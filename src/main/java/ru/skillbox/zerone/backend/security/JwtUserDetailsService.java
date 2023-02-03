package ru.skillbox.zerone.backend.security;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.model.entity.User;
import ru.skillbox.zerone.backend.security.jwt.JwtUserFactory;
import ru.skillbox.zerone.backend.service.UserService;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final UserService userService;


    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {


        User user = userService.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("User with email: " + email + " not found");
        }

      return JwtUserFactory.create(user);
    }
}