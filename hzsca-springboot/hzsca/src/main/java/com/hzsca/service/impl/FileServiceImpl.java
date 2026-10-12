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
import java.util.Set;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    @Value("${file.upload.path:uploads}")
    private String uploadBasePath;

    private static final String ACTIVITY_DIR = "activity";

    /** 允许上传的图片扩展名白名单，避免任意文件写入 */
    private static final Set<String> ALLOWED_EXTENSIONS =
            Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp");

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }

        // 获取项目根目录的绝对路径
        String projectPath = System.getProperty("user.dir");

        // 构建完整的上传路径
        Path uploadPath = Paths.get(projectPath, uploadBasePath, ACTIVITY_DIR);

        // 确保目录存在
        Files.createDirectories(uploadPath);

        // 解析并校验扩展名（原实现未判空，文件名无后缀时会抛 StringIndexOutOfBoundsException）
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.lastIndexOf('.') >= 0) {
            extension = originalFilename.substring(originalFilename.lastIndexOf('.')).toLowerCase();
        }
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("仅支持上传图片文件（jpg/jpeg/png/gif/webp/bmp）");
        }

        // 生成唯一文件名
        String filename = UUID.randomUUID().toString() + extension;

        // 构建文件完整路径
        Path filePath = uploadPath.resolve(filename);

        // 保存文件
        Files.copy(file.getInputStream(), filePath);

        // 返回相对路径（用于访问文件）
        return uploadBasePath + "/" + ACTIVITY_DIR + "/" + filename;
    }
}