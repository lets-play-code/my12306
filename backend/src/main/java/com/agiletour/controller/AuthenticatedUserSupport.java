package com.agiletour.controller;

import com.agiletour.entity.User;
import com.agiletour.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Helper component to resolve current authenticated user from Authorization header.
 * Token format: Bearer <username>
 */
@Component
public class AuthenticatedUserSupport {

    @Autowired
    private UserRepo userRepo;

    public User requiredUser(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new UnauthorizedException("请先登录");
        }
        String username = authorization.substring("Bearer ".length()).trim();
        Optional<User> user = userRepo.findByUsername(username);
        if (user.isEmpty()) {
            throw new UnauthorizedException("请先登录");
        }
        return user.get();
    }
}
