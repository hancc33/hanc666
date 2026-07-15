package com.zhumengmiao.config;

import com.zhumengmiao.entity.Admin;
import com.zhumengmiao.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (adminRepository.count() == 0) {
            adminRepository.save(Admin.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .nickname("管理员")
                    .build());
            log.info("Default admin created: admin/admin123");
        }
    }
}
