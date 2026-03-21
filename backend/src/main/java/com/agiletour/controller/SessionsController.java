package com.agiletour.controller;

import com.agiletour.entity.User;
import com.agiletour.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for session (login) management.
 * Provides login and current user lookup endpoints.
 */
@RestController
@RequestMapping("api/sessions")
public class SessionsController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AuthenticatedUserSupport authenticatedUserSupport;

    public static class LoginRequest {
        public String username;
        public String password;
    }

    @PostMapping
    public Map<String, String> login(@RequestBody LoginRequest request) {
        User user = userRepo.findByUsername(request.username)
                .filter(found -> found.getPassword().equals(request.password))
                .orElseThrow(() -> new UnauthorizedException("用户名或密码错误"));
        return Map.of("token", user.getUsername());
    }

    @GetMapping
    public Map<String, String> currentUser(@RequestHeader(value = "Authorization", required = false) String authorization) {
        User user = authenticatedUserSupport.requiredUser(authorization);
        return Map.of("fullName", user.getFullName());
    }
}
