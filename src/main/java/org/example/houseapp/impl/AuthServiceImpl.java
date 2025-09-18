package org.example.houseapp.impl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.example.houseapp.dto.RegistrationRequest;
import org.example.houseapp.Entity.User;
import org.example.houseapp.repository.UserRepository;
import org.example.houseapp.service.AuthServicer;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@Service
public class AuthServiceImpl  implements AuthServicer {

    private  UserRepository userRepository;
    private final String FIREBASE_API_KEY = "AIzaSyAocBauAkwKTZmYgaqCJYFzv8qXvZ3grx8";
    private final String FIREBASE_SIGNIN_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + FIREBASE_API_KEY;

    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(RegistrationRequest request) throws FirebaseAuthException {
        // 1. Crea el usuario en Firebase Authentication
        UserRecord.CreateRequest firebaseRequest = new UserRecord.CreateRequest()
                .setEmail(request.getEmail())
                .setPassword(request.getPassword())
                .setDisplayName(request.getName());

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(firebaseRequest);

        // 2. Guarda la información del usuario en tu base de datos MySQL
        User user = new User();
        user.setUid(userRecord.getUid()); // Usa el UID de Firebase
        user.setEmail(request.getEmail());
        user.setName(request.getName());

        return userRepository.save(user);
    }


    public ResponseEntity<String> login(@RequestBody Map<String, String> credenciales) {
        String email = credenciales.get("email");
        String password = credenciales.get("password");

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = String.format("{\"email\":\"%s\", \"password\":\"%s\", \"returnSecureToken\":true}", email, password);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {

            ResponseEntity<String> firebaseResponse = restTemplate.exchange(
                    FIREBASE_SIGNIN_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> responseMap = mapper.readValue(firebaseResponse.getBody(), Map.class);
            String idToken = (String) responseMap.get("idToken");
            ResponseEntity<String> response = verifyIdToken(idToken);
            return response;
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body("Error de la API interna: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error inesperado: " + e.getMessage());
        }
    }

    public ResponseEntity<String> verifyIdToken(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String idToken = authorizationHeader.replace("Bearer ", "");
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String uid = decodedToken.getUid();
            return ResponseEntity.ok("Usuario autenticado. UID: " + uid);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.badRequest().body("Token de Firebase inválido.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Encabezado de autorización no proporcionado o inválido.");
        }
    }

}
