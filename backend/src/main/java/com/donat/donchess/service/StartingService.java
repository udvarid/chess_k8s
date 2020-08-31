package com.donat.donchess.service;

import com.donat.donchess.domain.Role;
import com.donat.donchess.domain.User;
import com.donat.donchess.repository.RoleRepository;
import com.donat.donchess.repository.UserRepository;
import net.bytebuddy.utility.RandomString;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Component
@Transactional
public class StartingService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public StartingService(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    void runMe() {
        if (dbIsEmpty()) {
            createRoles();

            createUser("udvarid@hotmail.com", "1234", "Udvari Donát", "ROLE_ADMIN");
            createUser("bot@bot.com", "1234", "Stupid Bot", "ROLE_BOT0");
            createUser("bot2@bot.com", "1234", "Plain Bot", "ROLE_BOT1");
        }
    }

    private boolean dbIsEmpty() {
        return roleRepository.findAll().isEmpty() && userRepository.findAll().isEmpty();
    }

    private void createUser(String email, String password, String fullName, String desiredRole) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFullname(fullName);
        Role role = roleRepository.findByRole(desiredRole);
        user.getRoles().add(role);
        user.setEnabled(true);
        user.setAuthenticationToken(RandomString.make(20));
        user.setTimeOfRegistration(LocalDateTime.now());
        userRepository.saveAndFlush(user);
    }

    private void createRoles() {
        Role roleUser = new Role();
        Role roleAdmin = new Role();
        Role roleBot0 = new Role();
        Role roleBot1 = new Role();

        roleUser.setRole("ROLE_USER");
        roleAdmin.setRole("ROLE_ADMIN");
        roleBot0.setRole("ROLE_BOT0");
        roleBot1.setRole("ROLE_BOT1");

        roleRepository.saveAndFlush(roleUser);
        roleRepository.saveAndFlush(roleAdmin);
        roleRepository.saveAndFlush(roleBot0);
        roleRepository.saveAndFlush(roleBot1);
    }
}
