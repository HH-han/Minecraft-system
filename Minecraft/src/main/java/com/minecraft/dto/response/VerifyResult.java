package com.minecraft.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerifyResult {
    private Boolean passed;
    private String message;
    
    public static VerifyResult fail(String message) {
        return VerifyResult.builder().passed(false).message(message).build();
    }
}