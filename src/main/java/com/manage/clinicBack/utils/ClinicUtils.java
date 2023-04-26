package com.manage.clinicBack.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ClinicUtils {

    private  ClinicUtils(){

    }
    public static ResponseEntity<String> getResponseEntity(String responseMessage , HttpStatus httpStatus){
        return new ResponseEntity<String>("{\"message\":\""+responseMessage+"\"}", httpStatus);
    }

}