package org.example.houseapp.Controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.example.houseapp.Entity.User;
import org.example.houseapp.dto.RegistrationRequest;
import org.example.houseapp.service.AuthServicer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@RestController()
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthServicer authServicer;


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> credenciales) {
        ResponseEntity<String> response = authServicer.login(credenciales);
        return response;
    }

    @PostMapping("/api/login")
    public ResponseEntity<String> verifyIdToken(@RequestHeader("Authorization") String authorizationHeader) {
        ResponseEntity<String>  response;
        try {
          response = authServicer.verifyIdToken(authorizationHeader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return response;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequest request) {
        User user;
        try {
             user = authServicer.register(request);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok("Usuario registrado con éxito. UID: " + user.getUid());
    }
}