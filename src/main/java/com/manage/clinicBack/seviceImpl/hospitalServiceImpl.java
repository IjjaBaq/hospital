package com.manage.clinicBack.seviceImpl;


import com.manage.clinicBack.service.hospitalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class hospitalServiceImpl implements hospitalService {
    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        return null;
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        return null;
    }
}
