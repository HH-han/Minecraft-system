package com.minecraft.excel.core;

import com.minecraft.exception.BusinessException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Set;

/**
 * 上传文件校验：大小、后缀、文件头魔数。
 */
public final class ExcelFileValidator {

    private static final Set<String> ALLOWED_EXT = Set.of("xls", "xlsx");
    private static final long MAX_SIZE = 50L * 1024 * 1024;

    private ExcelFileValidator() {
    }

    public static void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BusinessException("文件不能超过50MB");
        }
        String originalName = file.getOriginalFilename();
        if (originalName == null || !originalName.contains(".")) {
            throw new BusinessException("文件名不合法");
        }
        String ext = originalName.substring(originalName.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        if (!ALLOWED_EXT.contains(ext)) {
            throw new BusinessException("仅支持 xls/xlsx 格式");
        }
        try (InputStream in = file.getInputStream()) {
            byte[] head = in.readNBytes(8);
            boolean xlsx = head.length >= 4 && head[0] == 0x50 && head[1] == 0x4B;
            boolean xls = head.length >= 4 && (head[0] & 0xFF) == 0xD0 && (head[1] & 0xFF) == 0xCF
                    && (head[2] & 0xFF) == 0x11 && (head[3] & 0xFF) == 0xE0;
            if (!xlsx && !xls) {
                throw new BusinessException("文件格式非法，请上传真实的 Excel 文件");
            }
        } catch (IOException e) {
            throw new BusinessException("文件读取失败");
        }
    }
}
