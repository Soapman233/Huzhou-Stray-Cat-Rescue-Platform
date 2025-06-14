package com.hzsca.service.impl;

import com.hzsca.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    @Value("${file.upload.path:uploads}")
    private String uploadBasePath;

    private static final String ACTIVITY_DIR = "activity";

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        // 获取项目根目录的绝对路径
        String projectPath = System.getProperty("user.dir");

        // 构建完整的上传路径
        Path uploadPath = Paths.get(projectPath, uploadBasePath, ACTIVITY_DIR);

        // 确保目录存在
        Files.createDirectories(uploadPath);

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + extension;

        // 构建文件完整路径
        Path filePath = uploadPath.resolve(filename);

        // 保存文件
        Files.copy(file.getInputStream(), filePath);

        // 返回相对路径（用于访问文件）
        return uploadBasePath + "/" + ACTIVITY_DIR + "/" + filename;
    }
}