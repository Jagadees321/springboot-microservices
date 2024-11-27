package com.example.auth.controller;

import com.example.auth.config.CustomUserDetailsService;
import com.example.auth.helper.JwtHelper;
import com.example.auth.model.JwtRequest;
import com.example.auth.model.JwtResponse;
import com.example.auth.model.UserData;
import com.example.auth.repository.UserDataRepository;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserDataRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtHelper jwtHelper;

    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@RequestBody UserData userData) {
        userData.setPassword(passwordEncoder.encode(userData.getPassword()));
        repository.save(userData);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!!!");
    }

    @PostMapping("/token")
    public ResponseEntity<Object> generateToken(@RequestBody JwtRequest jwtRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(jwtRequest.getUsername(), jwtRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(jwtRequest.getUsername());
            String token = jwtHelper.generateToken(userDetails.getUsername());

            JwtResponse jwtResponse = JwtResponse.builder()
                    .username(userDetails.getUsername())
                    .jwtToken(token)
                    .build();

            return ResponseEntity.status(HttpStatus.FOUND).body(jwtResponse);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied...Better luck next time");
        }
    }
}
