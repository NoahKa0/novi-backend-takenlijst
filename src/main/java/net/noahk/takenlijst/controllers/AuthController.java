package net.noahk.takenlijst.controllers;

import net.noahk.takenlijst.dtos.AuthDto;
import net.noahk.takenlijst.services.AuthenticationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthenticationService service;

    public AuthController(AuthenticationService service) {
        this.service = service;
    }

    @PostMapping("/auth")
    public ResponseEntity<Object> signIn(@RequestBody AuthDto authDto) {
        try {
            var token = service.getToken(authDto);
            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .body("Token generated");
        } catch (AuthenticationException ex) {
            return new ResponseEntity(ex.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
}
