package com.shorgov.tokens.service;

import com.shorgov.tokens.model.Role;
import com.shorgov.tokens.model.User;
import com.shorgov.tokens.repo.RolesRepository;
import com.shorgov.tokens.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepo;
    private final RolesRepository rolesRepo;

    public User getUser(String email) {
        log.info("getUser with email {}", email);
        return userRepo.findByEmail(email).orElseThrow();
    }

    public void saveUser(User user) {
        log.info("saveUser with email {}", user.email());
        userRepo.saveUser(user);
    }

    public Role getRole(String name) {
        log.info("getRole with email {}", name);
        return rolesRepo.findByName(name).orElseThrow();
    }

    public void saveRole(Role role) {
        log.info("saveRole with name {}", role.name());
        rolesRepo.saveRole(role);
    }

    public void addRoleToUser(String email, String roleName) {
        User user = userRepo.findByEmail(email).orElseThrow();
        Role role = rolesRepo.findByName(roleName).orElseThrow();

        log.info("{} assigned to {}", email, roleName);
        user.roles().add(role);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getUser(username);
        List<SimpleGrantedAuthority> authorities = user.roles().stream().map(r -> new SimpleGrantedAuthority(r.name())).toList();
        return new org.springframework.security.core.userdetails.User(user.email(), user.password(), authorities);
    }
}
