package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.request.LoginRequest;
import com.minecraft.dto.request.RegisterRequest;
import com.minecraft.dto.response.LoginResponse;
import com.minecraft.entity.User;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.UserMapper;
import com.minecraft.service.UserService;
import com.minecraft.utils.AccountGenerator;
import com.minecraft.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public LoginResponse login(LoginRequest request) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getAccount, request.getAccount());
        User user = getOne(wrapper);

        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("密码错误");
        }

        // 确保username不为空，如果为空则使用account作为username
        String username = user.getUsername();
        if (username == null || username.trim().isEmpty()) {
            username = user.getAccount();
        }

        String token = jwtUtil.generateToken(user.getId(), username);

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(username);
        response.setEmail(user.getEmail());
        response.setAvatar(user.getAvatar());
        response.setAccount(user.getAccount());
        response.setPhone(user.getPhone());

        return response;
    }

    @Override
    public void register(RegisterRequest request) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        if (getOne(wrapper) != null) {
            throw new BusinessException("用户名已存在");
        }

        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, request.getEmail());
        if (getOne(wrapper) != null) {
            throw new BusinessException("邮箱已被注册");
        }

        User user = new User();
        user.setAccount(AccountGenerator.generateAccount());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setStatus(1);

        save(user);
    }

    @Override
    public User getUserInfo(Long userId) {
        return getById(userId);
    }

    @Override
    public void updateUserInfo(User user) {
        updateById(user);
    }

    @Override
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = getById(userId);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        updateById(user);
    }

    @Override
    public void updateOnlineStatus(Long userId, boolean online) {
        User user = getById(userId);
        if (user != null) {
            user.setOnline(online ? 1 : 0);
            updateById(user);
        }
    }
}
