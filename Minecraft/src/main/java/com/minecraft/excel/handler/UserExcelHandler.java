package com.minecraft.excel.handler;

import com.minecraft.entity.User;
import com.minecraft.excel.core.AbstractExcelBizHandler;
import com.minecraft.excel.enums.ExcelBizType;
import com.minecraft.excel.model.UserExcelRow;
import com.minecraft.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class UserExcelHandler extends AbstractExcelBizHandler<UserExcelRow, User> {

    public UserExcelHandler(UserService service) {
        super(service, UserExcelRow.class, "用户");
    }

    @Override
    public ExcelBizType bizType() {
        return ExcelBizType.USER;
    }

    @Override
    protected String uniqueColumn() {
        return "account";
    }

    @Override
    protected String uniqueValue(UserExcelRow row) {
        return row.getAccount();
    }

    @Override
    protected String uniqueValue(User entity) {
        return entity.getAccount();
    }

    @Override
    protected Long idOf(User entity) {
        return entity.getId();
    }

    @Override
    protected void assignId(User entity, Long id) {
        entity.setId(id);
    }

    @Override
    protected UserExcelRow toRow(User entity) {
        return UserExcelRow.from(entity);
    }
}
