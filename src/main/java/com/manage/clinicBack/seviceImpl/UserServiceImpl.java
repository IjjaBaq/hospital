package com.manage.clinicBack.seviceImpl;

import com.google.common.base.Strings;
import com.manage.clinicBack.Dao.UserDao;
import com.manage.clinicBack.JWT.CustomerUserDetailsService;
import com.manage.clinicBack.JWT.JwtFilter;
import com.manage.clinicBack.JWT.JwtUtil;
import com.manage.clinicBack.constents.cliniqueConstants;
import com.manage.clinicBack.module.User;
import com.manage.clinicBack.service.UserService;
import com.manage.clinicBack.utils.ClinicUtils;
import com.manage.clinicBack.utils.EmailUtils;
import com.manage.clinicBack.wrapper.UserWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    JwtFilter jwtFilter ;

    @Autowired
    UserDao userDao;
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    CustomerUserDetailsService customerUserDetailsService;

    @Autowired
    EmailUtils emailUtils ;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Inside signup{}", requestMap);
        try {
            if (validateSignUpMap(requestMap)) {
                User user = userDao.findByEmailId(requestMap.get("email"));
                if (Objects.isNull(user)) {

                    userDao.save(getUserFromMap(requestMap));
                    return ClinicUtils.getResponseEntity("Compte bien créer", HttpStatus.OK);
                } else {
                    return ClinicUtils.getResponseEntity("Email existe déja", HttpStatus.BAD_REQUEST);
                }

            } else {
                return ClinicUtils.getResponseEntity(cliniqueConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ClinicUtils.getResponseEntity(cliniqueConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private boolean validateSignUpMap(Map<String, String> requestMap) {
        if (requestMap.containsKey("name") && requestMap.containsKey("contactNumber") &&
                requestMap.containsKey("email") && requestMap.containsKey("password")) {
            return true;
        }
        return false;
    }

    private User getUserFromMap(Map<String, String> requestMap) {
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setStatus("true");
        user.setRole("user");
        return user;
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Inside login{} ", requestMap);
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password"))
            );
            if (auth.isAuthenticated()) {
                if (customerUserDetailsService.getUserDetails().getStatus().equalsIgnoreCase("true")) {
                    return new ResponseEntity<String>("{\"token\":\"" +
                            jwtUtil.generateToken(customerUserDetailsService.getUserDetails().getEmail()
                                    , customerUserDetailsService.getUserDetails().getRole()) + "\"}",
                            HttpStatus.OK);
                } else {
                    return new ResponseEntity<String>("{\"message\":\"" + "Attendez authorisation de l'admin." + "\"}",
                            HttpStatus.BAD_REQUEST);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<String>("{\"message\":\"" + "Données fausses" + "\"}",
                HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUser() {
        try {
               if(jwtFilter.isAdmin()){

                   return new ResponseEntity<>(userDao.getAllUser(),HttpStatus.OK);

               }else {
                   return new ResponseEntity<>(new ArrayList<>(),HttpStatus.UNAUTHORIZED);
               }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
              Optional<User>  optional= userDao.findById(Integer.parseInt(requestMap.get("id")));
              if(!optional.isEmpty()){
                  userDao.updateStatus(requestMap.get("status"),Integer.parseInt(requestMap.get("id")));
                  sendMailToUser(requestMap.get("status"),optional.get().getEmail());
                  return ClinicUtils.getResponseEntity("le statut d'utilisateur est bien modifier",HttpStatus.OK);
              }else {
                return   ClinicUtils.getResponseEntity("id d'utilisateur n'existe pas",HttpStatus.OK);
              }
            }else {
                return ClinicUtils.getResponseEntity(cliniqueConstants.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);

            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return ClinicUtils.getResponseEntity(cliniqueConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);

    }

    private void sendMailToUser(String status, String user) {
      if(status!=null && status.equalsIgnoreCase("true")){
      emailUtils.sendSimpleMessage(user,"compte activé","Utilisateur:- "+user+" \n ce compte est activé ");
      }else {

          emailUtils.sendSimpleMessage(user,"compte désactivé","Utilisateur:- "+user+" \n ce compte est désactivé ");
      }
    }

    @Override
    public ResponseEntity<String> checkToken() {
        return  ClinicUtils.getResponseEntity("true",HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try{
            User userObj = userDao.findByEmail(jwtFilter.getCurrentUser());
            if(!userObj.equals(null)){
                if(userObj.getPassword().equals(requestMap.get("oldPassword"))){
                 userObj.setPassword(requestMap.get("newPassword"));
                 userDao.save(userObj);
                 return ClinicUtils.getResponseEntity("mot de passe mise à jour",HttpStatus.OK);
                }
                return ClinicUtils.getResponseEntity("mot de passe incorrect",HttpStatus.BAD_REQUEST);
            }
            return ClinicUtils.getResponseEntity(cliniqueConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return  ClinicUtils.getResponseEntity(cliniqueConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        try {
            User user = userDao.findByEmail(requestMap.get("email"));
            if(!Objects.isNull(user)&& !Strings.isNullOrEmpty(user.getEmail()))
               emailUtils.forgotMail(user.getEmail(),"Connexion à CloseMed",user.getPassword());
               return ClinicUtils.getResponseEntity("Vérifier votre boite email pour récupérer vos données",HttpStatus.OK);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return  ClinicUtils.getResponseEntity(cliniqueConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);

    }


}
