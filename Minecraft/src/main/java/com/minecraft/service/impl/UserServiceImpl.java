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
import com.minecraft.utils.ImageUtils;
import com.minecraft.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private ImageUtils imageUtils;

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
        response.setSignature(user.getSignature());
        response.setNickname(user.getNickname());
        response.setExperience(user.getExperience());

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
        // 排除生成列，只更新允许更新的字段
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setAccount(user.getAccount());
        updateUser.setUsername(user.getUsername());
        updateUser.setPassword(user.getPassword());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setAvatar(user.getAvatar());
        updateUser.setGender(user.getGender());
        updateUser.setAge(user.getAge());
        updateUser.setOccupation(user.getOccupation());
        updateUser.setHobbies(user.getHobbies());
        updateUser.setBio(user.getBio());
        updateUser.setOnline(user.getOnline());
        updateUser.setStatus(user.getStatus());
        updateUser.setUpdateTime(user.getUpdateTime());
        updateUser.setSignature(user.getSignature());
        updateUser.setNickname(user.getNickname());
        updateUser.setExperience(user.getExperience());
        
        updateById(updateUser);
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

    @Override
    public String uploadAvatar(Long userId, MultipartFile file) {
        try {
            // 处理上传的文件
            String avatarUrl = imageUtils.processMultipartFile(file);
            
            // 更新用户头像
            User user = getById(userId);
            if (user != null) {
                // 删除旧头像
                if (user.getAvatar() != null) {
                    try {
                        imageUtils.deleteImage(user.getAvatar());
                    } catch (Exception e) {
                        // 旧头像删除失败不影响新头像上传
                        e.printStackTrace();
                    }
                }
                user.setAvatar(avatarUrl);
                updateById(user);
            }
            
            return avatarUrl;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("头像上传失败: " + e.getMessage());
        }
    }

    @Override
    public void deleteUser(Long userId) {
        // 获取用户信息
        User user = getById(userId);
        if (user != null) {
            // 删除用户头像
            if (user.getAvatar() != null) {
                try {
                    imageUtils.deleteImage(user.getAvatar());
                } catch (Exception e) {
                    // 头像删除失败不影响用户删除
                    e.printStackTrace();
                }
            }
            // 删除用户
            removeById(userId);
        }
    }

    @Override
    public List<User> getAllUsers() {
        return list();
    }

    @Override
    public User getUserByAccount(String account) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getAccount, account);
        return getOne(wrapper);
    }
}
