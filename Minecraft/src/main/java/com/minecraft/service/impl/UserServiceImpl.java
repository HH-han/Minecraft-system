package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.request.LoginRequest;
import com.minecraft.dto.request.RegisterRequest;
import com.minecraft.dto.response.LoginResponse;
import com.minecraft.entity.LoginLog;
import com.minecraft.entity.User;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.UserMapper;
import com.minecraft.service.LoginLogService;
import com.minecraft.service.UserService;
import com.minecraft.utils.AccountGenerator;
import com.minecraft.utils.DeviceInfoUtils;
import com.minecraft.utils.ImageUtils;
import com.minecraft.utils.IpLocationUtils;
import com.minecraft.utils.JwtUtil;
import com.minecraft.utils.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private ImageUtils imageUtils;
    
    @Autowired
    private RedisUtil redisUtil;
    
    @Autowired
    private DeviceInfoUtils deviceInfoUtils;
    
    @Autowired
    private IpLocationUtils ipLocationUtils;
    
    @Autowired
    private LoginLogService loginLogService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        String account = request.getAccount();
        
        // 获取用户IP地址
        String ip = deviceInfoUtils.getClientIP(httpRequest);
        
        // 检查IP是否被封禁
        if (loginLogService.isIPBlocked(ip)) {
            String blockReason = loginLogService.getIPBlockReason(ip);
            String msg = "IP已被封禁：" + blockReason;
            // 记录登录失败日志
            recordLoginLog(httpRequest, account, "0", msg);
            throw new BusinessException(msg);
        }
        
        String cacheKey = "login:user:" + account;
        
        // 尝试从Redis获取缓存的登录响应
        Object cachedValue = redisUtil.get(cacheKey);
        if (cachedValue != null && cachedValue instanceof LoginResponse) {
            LoginResponse cachedResponse = (LoginResponse) cachedValue;
            // 记录登录日志
            recordLoginLog(httpRequest, account, "1", "登录成功（缓存）");
            return cachedResponse;
        }
        
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getAccount, account);
        User user = getOne(wrapper);

        if (user == null) {
            // 记录登录失败日志
            recordLoginLog(httpRequest, account, "0", "用户不存在");
            throw new BusinessException("用户不存在");
        }

        if (user.getStatus() != 1) {
            // 记录登录失败日志
            recordLoginLog(httpRequest, account, "0", "账号已被禁用");
            throw new BusinessException("账号已被禁用");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            // 记录登录失败日志
            recordLoginLog(httpRequest, account, "0", "密码错误");
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
        
        // 将登录响应缓存到Redis，设置30分钟过期
        redisUtil.set(cacheKey, response, 30, TimeUnit.MINUTES);

        // 记录登录成功日志
        recordLoginLog(httpRequest, account, "1", "登录成功");

        return response;
    }

    /**
     * 记录登录日志
     * @param request HttpServletRequest
     * @param account 用户账号
     * @param status 登录状态（1-成功，0-失败）
     * @param msg 登录消息
     */
    private void recordLoginLog(HttpServletRequest request, String account, String status, String msg) {
        // 获取用户IP地址
        String ip = deviceInfoUtils.getClientIP(request);
        // 获取地理位置
        String loginLocation = ipLocationUtils.getLocation(ip);
        // 获取用户代理
        String userAgent = deviceInfoUtils.getUserAgent(request);
        // 获取浏览器信息
        String browser = deviceInfoUtils.getBrowser(userAgent);
        // 获取操作系统信息
        String os = deviceInfoUtils.getOS(userAgent);

        // 构建登录日志
        LoginLog loginLog = new LoginLog();
        // 尝试获取用户ID
        try {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getAccount, account);
            User user = getOne(wrapper);
            if (user != null) {
                loginLog.setUserId(user.getId());
                loginLog.setUsername(user.getUsername());
            }
        } catch (Exception e) {
            // 获取用户信息失败，不影响日志记录
        }
        loginLog.setAccount(account);
        loginLog.setIpaddr(ip);
        loginLog.setLoginLocation(loginLocation);
        loginLog.setBrowser(browser);
        loginLog.setOs(os);
        loginLog.setStatus(status);
        loginLog.setMsg(msg);
        loginLog.setLoginTime(LocalDateTime.now());

        // 异步保存登录日志
        new Thread(() -> {
            try {
                loginLogService.save(loginLog);
            } catch (Exception e) {
                // 日志保存失败，不影响登录流程
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public String register(RegisterRequest request) {
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
        String account = AccountGenerator.generateAccount();
        user.setAccount(account);
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setStatus(1);
        
        // 处理头像上传
        if (request.getAvatar() != null && request.getAvatar().startsWith("data:image")) {
            try {
                String processedAvatar = imageUtils.processBase64Image(request.getAvatar());
                user.setAvatar(processedAvatar);
            } catch (Exception e) {
                throw new BusinessException("头像处理失败: " + e.getMessage());
                // 头像处理失败不影响注册流程，继续执行
            }
        }

        save(user);
        
        // 构建登录响应并缓存到Redis
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
        
        String cacheKey = "login:user:" + account;
        redisUtil.set(cacheKey, response, 30, TimeUnit.MINUTES);
        
        return account;
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
        
        // 清除用户登录缓存
        String cacheKey = "login:user:" + user.getAccount();
        redisUtil.delete(cacheKey);
    }

    @Override
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = getById(userId);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        updateById(user);
        
        // 清除用户登录缓存
        String cacheKey = "login:user:" + user.getAccount();
        redisUtil.delete(cacheKey);
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
