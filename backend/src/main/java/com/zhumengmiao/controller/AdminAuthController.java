package com.zhumengmiao.controller;

import com.zhumengmiao.dto.ApiResponse;
import com.zhumengmiao.dto.LoginRequest;
import com.zhumengmiao.entity.Admin;
import com.zhumengmiao.repository.AdminRepository;
import com.zhumengmiao.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ApiResponse<Map<String, String>> login(@RequestBody LoginRequest request) {
        return adminRepository.findByUsername(request.getUsername())
                .filter(admin -> passwordEncoder.matches(request.getPassword(), admin.getPassword()))
                .map(admin -> {
                    String token = jwtUtil.generateToken(admin.getUsername());
                    return ApiResponse.success(Map.of("token", token, "username", admin.getUsername()));
                })
                .orElse(ApiResponse.error(401, "用户名或密码错误"));
    }

    @GetMapping("/info")
    public ApiResponse<Map<String, String>> info(@RequestHeader("Authorization") String auth) {
        String token = auth.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(token);
        return ApiResponse.success(Map.of("username", username));
    }
}
