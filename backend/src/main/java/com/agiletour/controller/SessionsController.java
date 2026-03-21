package com.agiletour.controller;

import com.agiletour.entity.User;
import com.agiletour.repo.UserRepo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/sessions")
public class SessionsController {

    @Autowired
    private UserRepo userRepo;

    @PostMapping
    public TokenResponse login(@RequestBody LoginRequest loginRequest) {
        Optional<User> user = userRepo.findByUsername(loginRequest.getUsername());
        if (user.isPresent() && user.get().getPassword().equals(loginRequest.getPassword())) {
            return new TokenResponse("test-token-" + user.get().getId());
        }
        throw new BadRequestException("Invalid username or password");
    }

    @GetMapping
    public CurrentUserResponse currentUser(@RequestHeader(value = "Authorization", required = false) String auth) {
        if (auth == null || !auth.startsWith("Bearer ")) {
            throw new BadRequestException("Not authenticated");
        }
        String token = auth.substring(7);
        if (!token.startsWith("test-token-")) {
            throw new BadRequestException("Invalid token");
        }
        long userId = Long.parseLong(token.substring("test-token-".length()));
        Optional<User> user = userRepo.findById(userId);
        if (user.isEmpty()) {
            throw new BadRequestException("User not found");
        }
        return new CurrentUserResponse(user.get().getFullName());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenResponse {
        private String token;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrentUserResponse {
        private String fullName;
    }
}
