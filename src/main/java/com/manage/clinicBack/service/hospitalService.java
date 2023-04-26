package com.manage.clinicBack.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface hospitalService {

    ResponseEntity<String> signUp(Map<String,String> requestMap);
    ResponseEntity<String> login(Map<String,String> requestMap);
}
