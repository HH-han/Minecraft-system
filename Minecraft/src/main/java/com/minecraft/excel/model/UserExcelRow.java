package com.minecraft.excel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.minecraft.entity.User;
import com.minecraft.excel.core.ExcelImportRow;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@ColumnWidth(20)
public class UserExcelRow extends ExcelImportRow {

    @ExcelProperty("账号")
    private String account;

    @ExcelProperty("用户名")
    private String username;

    @ExcelProperty("昵称")
    private String nickname;

    @ExcelProperty("邮箱")
    private String email;

    @ExcelProperty("手机号")
    private String phone;

    @ExcelProperty("头像")
    private String avatar;

    @ExcelProperty("性别")
    private String gender;

    @ExcelProperty("年龄")
    private Integer age;

    @ExcelProperty("职业")
    private String occupation;

    @ExcelProperty("爱好")
    private String hobbies;

    @ExcelProperty("个人简介")
    private String bio;

    @ExcelProperty("在线状态")
    private Integer online;

    @ExcelProperty("账号状态")
    private Integer status;

    @ExcelProperty("个性签名")
    private String signature;

    @ExcelProperty("用户经验")
    private String experience;

    @ExcelProperty("积分")
    private Integer points;

    @ExcelProperty("权限")
    private String permissions;

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (account == null || account.isBlank()) {
            errors.add("账号不能为空");
        }
        if (username == null || username.isBlank()) {
            errors.add("用户名不能为空");
        }
        if (age != null && age < 0) {
            errors.add("年龄不能为负数");
        }
        if (points != null && points < 0) {
            errors.add("积分不能为负数");
        }
        return errors;
    }

    @Override
    public Object toEntity() {
        User entity = new User();
        entity.setAccount(account);
        entity.setUsername(username);
        entity.setNickname(nickname);
        entity.setEmail(email);
        entity.setPhone(phone);
        entity.setAvatar(avatar);
        entity.setGender(gender);
        entity.setAge(age);
        entity.setOccupation(occupation);
        entity.setHobbies(hobbies);
        entity.setBio(bio);
        entity.setOnline(online);
        entity.setStatus(status);
        entity.setSignature(signature);
        entity.setExperience(experience);
        entity.setPoints(points);
        entity.setPermissions(permissions);
        return entity;
    }

    public static UserExcelRow from(User entity) {
        UserExcelRow row = new UserExcelRow();
        row.setAccount(entity.getAccount());
        row.setUsername(entity.getUsername());
        row.setNickname(entity.getNickname());
        row.setEmail(entity.getEmail());
        row.setPhone(entity.getPhone());
        row.setAvatar(entity.getAvatar());
        row.setGender(entity.getGender());
        row.setAge(entity.getAge());
        row.setOccupation(entity.getOccupation());
        row.setHobbies(entity.getHobbies());
        row.setBio(entity.getBio());
        row.setOnline(entity.getOnline());
        row.setStatus(entity.getStatus());
        row.setSignature(entity.getSignature());
        row.setExperience(entity.getExperience());
        row.setPoints(entity.getPoints());
        row.setPermissions(entity.getPermissions());
        return row;
    }
}
