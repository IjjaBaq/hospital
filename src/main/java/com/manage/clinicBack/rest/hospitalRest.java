package com.manage.clinicBack.rest;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@RequestMapping(path = "/hospital")
public interface hospitalRest {

    @PostMapping(path ="/signup")
    public ResponseEntity<String> signUp(@RequestBody(required = true) Map<String,String> requestMap );

    @PostMapping(path ="/login")
    public ResponseEntity<String> login(@RequestBody(required = true) Map<String,String> requestMap);
}
