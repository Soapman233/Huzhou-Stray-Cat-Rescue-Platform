package com.hzsca.service.impl;

import com.hzsca.service.FileService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    private static final String UPLOAD_DIR = "static/uploads";

    @Override
    public String uploadFile(MultipartFile file) throws Exception {
        // 获取resources目录的真实路径
        String resourcePath = ResourceUtils.getURL("classpath:").getPath();

        // 创建上传目录
        File uploadDir = new File(resourcePath + UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + extension;

        // 保存文件
        Path filePath = Paths.get(uploadDir.getPath(), filename);
        Files.copy(file.getInputStream(), filePath);

        // 返回文件访问路径
        return "/uploads/" + filename;
    }
}