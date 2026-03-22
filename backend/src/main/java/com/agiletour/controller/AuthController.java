package com.agiletour.controller;

import com.agiletour.dto.CurrentUserResponse;
import com.agiletour.dto.LoginRequest;
import com.agiletour.dto.TokenResponse;
import com.agiletour.entity.User;
import com.agiletour.repo.UserRepo;
import com.agiletour.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
public class AuthController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JwtUtil jwtUtil;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/sessions")
    public TokenResponse login(@RequestBody LoginRequest request) {
        User user = userRepo.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadRequestException("用户名或密码错误"));

        // 使用 BCrypt 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("用户名或密码错误");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        return new TokenResponse(token, user.getFullName());
    }

    @GetMapping("/sessions/current")
    public CurrentUserResponse getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        long userId = jwtUtil.getUserIdFromToken(token);
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new BadRequestException("用户不存在"));
        return new CurrentUserResponse(user.getFullName());
    }
}
