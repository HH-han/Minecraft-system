package com.minecraft.config;
import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.resource.ResourceStore;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.impl.LocalMemoryResourceStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CaptchaResourceConfiguration {

    @Bean
    public ResourceStore resourceStore() {
        LocalMemoryResourceStore resourceStore = new LocalMemoryResourceStore();
        
        resourceStore.addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "captcha/bg1.jpg", "default"));
        resourceStore.addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "captcha/bg2.jpg", "default"));
        resourceStore.addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "captcha/bg3.jpg", "default"));
        resourceStore.addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "captcha/bg4.jpg", "default"));
        resourceStore.addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "captcha/bg5.jpg", "default"));
        
        return resourceStore;
    }
}