package net.noahk.takenlijst.controllers;

import net.noahk.takenlijst.dtos.UserDto;
import net.noahk.takenlijst.exceptions.UnmetPreconditionException;
import net.noahk.takenlijst.security.MyUserDetails;
import net.noahk.takenlijst.services.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
public class UserController {

    private final AuthenticationService service;

    public UserController(AuthenticationService service) {
        this.service = service;
    }

    @GetMapping("/users")
    public String getUser(@AuthenticationPrincipal MyUserDetails user) {
        if (user == null) {
            return "Not signed in!";
        }
        return user.getUsername();
    }

    @PostMapping("/users")
    public ResponseEntity<Object> createUser(@RequestBody UserDto userDto) {
        try {
            service.createUser(userDto);
        } catch (UnmetPreconditionException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/auth/").toUriString());
        return ResponseEntity.created(uri).body("User created!");
    }
}
