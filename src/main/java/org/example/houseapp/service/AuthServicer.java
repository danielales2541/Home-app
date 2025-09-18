package org.example.houseapp.service;

import org.example.houseapp.Entity.User;
import org.example.houseapp.dto.RegistrationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import com.google.firebase.auth.FirebaseAuthException;
import java.util.Map;


public interface AuthServicer {

    public User register(RegistrationRequest request) throws FirebaseAuthException;

    public ResponseEntity<String> login(@RequestBody Map<String, String> credenciales);

    public ResponseEntity<String> verifyIdToken(@RequestHeader("Authorization") String authorizationHeader);

}
