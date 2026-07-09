package com.minecraft.config;
import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.resource.ResourceStore;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.impl.LocalMemoryResourceStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class CaptchaResourceConfiguration {

    private static final String IMAGE_DIR = "G:/slidingverification";

    @Bean
    public ResourceStore resourceStore() {
        LocalMemoryResourceStore resourceStore = new LocalMemoryResourceStore();
        
        List<String> imageFiles = getImageFiles(IMAGE_DIR);
        
        for (String imageFile : imageFiles) {
            resourceStore.addResource(CaptchaTypeConstant.SLIDER, new Resource("file", imageFile, "default"));
        }
        
        return resourceStore;
    }
    
    private List<String> getImageFiles(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            return Arrays.asList();
        }
        
        return Arrays.stream(dir.listFiles())
                .filter(file -> file.isFile() && isImageFile(file.getName()))
                .map(File::getAbsolutePath)
                .limit(10)
                .collect(Collectors.toList());
    }
    
    private boolean isImageFile(String fileName) {
        String lowerName = fileName.toLowerCase();
        return lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg") || 
               lowerName.endsWith(".png") || lowerName.endsWith(".webp");
    }
}