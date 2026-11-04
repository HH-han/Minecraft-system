package com.minecraft.excel.core;

import com.minecraft.excel.enums.ExcelBizType;
import com.minecraft.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class ExcelBizHandlerRegistry {

    private final Map<ExcelBizType, ExcelBizHandler<?>> handlers = new EnumMap<>(ExcelBizType.class);

    public ExcelBizHandlerRegistry(List<ExcelBizHandler<?>> handlerList) {
        for (ExcelBizHandler<?> handler : handlerList) {
            handlers.put(handler.bizType(), handler);
        }
    }

    public ExcelBizHandler<?> get(ExcelBizType bizType) {
        ExcelBizHandler<?> handler = handlers.get(bizType);
        if (handler == null) {
            throw new BusinessException("暂不支持该业务类型的 Excel 操作：" + bizType.getDesc());
        }
        return handler;
    }
}
