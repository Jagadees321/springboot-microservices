package com.example.auth.config;

import com.example.auth.model.UserData;
import com.example.auth.repository.UserDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserDataRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserData> userDataOptional = repository.findByName(username);
        return userDataOptional.map(CustomUserDetails::new).orElseThrow(
                () -> new UsernameNotFoundException("Username not found: " + username));

    }
}
