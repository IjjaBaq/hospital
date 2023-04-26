package com.manage.clinicBack.JWT;

import com.manage.clinicBack.Dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;

@Service
public class CustomerUserDetailsService implements UserDetailsService {

    @Autowired
    UserDao userDao ;

    private com.manage.clinicBack.module.User userDetails ; // to distinguish from user of spring

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        userDetails = userDao.findByEmailId(email);
        if(!Objects.isNull(userDetails))
            return  new User(userDetails.getEmail(),userDetails.getPassword(),new ArrayList<>());
            // arraylist for the role
        else throw new UsernameNotFoundException("Utilisateur n'existe pas ");
    }

    public com.manage.clinicBack.module.User getUserDetails(){
        return userDetails ;

    }

}