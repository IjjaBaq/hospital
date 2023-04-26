package com.manage.clinicBack.restImpl;


import com.manage.clinicBack.constents.cliniqueConstants;
import com.manage.clinicBack.rest.hospitalRest;
import com.manage.clinicBack.service.UserService;
import com.manage.clinicBack.utils.ClinicUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class hospitalRestImpl implements hospitalRest {

    @Autowired
    UserService hospitalService;
    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        try {
            return hospitalService.signUp(requestMap);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ClinicUtils.getResponseEntity(cliniqueConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        try {
            return hospitalService.login(requestMap);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ClinicUtils.getResponseEntity(cliniqueConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

    }
}
