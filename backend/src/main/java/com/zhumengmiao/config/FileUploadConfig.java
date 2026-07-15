package com.zhumengmiao.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileUploadConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:./uploads/}")
    private String uploadDir;

    private String absoluteUploadDir;

    @PostConstruct
    public void init() throws IOException {
        // 使用绝对路径，避免相对路径导致的问题
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        absoluteUploadDir = uploadPath.toString() + File.separator;

        File dir = new File(absoluteUploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        new File(absoluteUploadDir + "videos").mkdirs();
        new File(absoluteUploadDir + "images").mkdirs();
        new File(absoluteUploadDir + "files").mkdirs();
        new File(absoluteUploadDir + "covers").mkdirs();

        System.out.println("=== 文件上传目录 ===");
        System.out.println("绝对路径: " + absoluteUploadDir);
        System.out.println("====================");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + absoluteUploadDir);
    }

    public String getAbsoluteUploadDir() {
        return absoluteUploadDir;
    }
}
