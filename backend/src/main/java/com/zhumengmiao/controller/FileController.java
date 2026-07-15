package com.zhumengmiao.controller;

import com.zhumengmiao.dto.ApiResponse;
import com.zhumengmiao.config.FileUploadConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FileController {

    private final FileUploadConfig fileUploadConfig;

    @PostMapping("/admin/file/upload")
    public ApiResponse<String> uploadFile(@RequestParam("file") MultipartFile file,
                                          @RequestParam(value = "type", defaultValue = "files") String type) {
        if (file.isEmpty()) {
            return ApiResponse.error("文件不能为空");
        }

        try {
            String originalName = file.getOriginalFilename();
            String extension = "";
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + extension;

            String uploadDir = fileUploadConfig.getAbsoluteUploadDir();
            String dirPath = uploadDir + type;
            new File(dirPath).mkdirs();

            File dest = new File(dirPath + File.separator + fileName);
            file.transferTo(dest);

            String url = "/uploads/" + type + "/" + fileName;
            System.out.println("文件上传成功: " + dest.getAbsolutePath());
            return ApiResponse.success("上传成功", url);
        } catch (IOException e) {
            e.printStackTrace();
            return ApiResponse.error("上传失败: " + e.getMessage());
        }
    }
}
