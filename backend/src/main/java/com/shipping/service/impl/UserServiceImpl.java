package com.shipping.service.impl;

import com.shipping.service.UserService;
import com.shipping.mapper.UserMapper;
import com.shipping.model.entity.User;
import com.shipping.model.dto.UserRequest;
import com.shipping.model.dto.UserQueryRequest;
import com.shipping.model.dto.ChangePasswordRequest;
import com.shipping.common.PageResult;
import com.shipping.exception.BusinessException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional
    public void createUser(UserRequest request) {
        logger.info("创建用户：{}", request.getUsername());
        
        // 检查用户名是否已存在
        if (existsByUsername(request.getUsername())) {
            throw new BusinessException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (existsByEmail(request.getEmail())) {
            throw new BusinessException("邮箱已存在");
        }

        User user = convertToEntity(request);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        // 加密密码
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        } else {
            // 默认密码
            user.setPassword(passwordEncoder.encode("123456"));
        }
        
        userMapper.insert(user);
        logger.info("用户创建成功，ID：{}", user.getId());
    }

    @Override
    @Transactional
    public void updateUser(Long id, UserRequest request) {
        logger.info("更新用户信息，ID：{}", id);
        
        User existing = userMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("用户不存在");
        }

        // 检查用户名是否与其他用户冲突
        if (!existing.getUsername().equals(request.getUsername()) && existsByUsername(request.getUsername())) {
            throw new BusinessException("用户名已被其他用户使用");
        }
        
        // 检查邮箱是否与其他用户冲突
        if (!existing.getEmail().equals(request.getEmail()) && existsByEmail(request.getEmail())) {
            throw new BusinessException("邮箱已被其他用户使用");
        }

        User user = convertToEntity(request);
        user.setId(id);
        user.setPassword(existing.getPassword()); // 保持原密码
        user.setCreatedAt(existing.getCreatedAt());
        user.setUpdatedAt(LocalDateTime.now());
        
        userMapper.update(user);
        logger.info("用户信息更新成功");
    }

    @Override
    public User getUserById(Long id) {
        logger.info("查询用户，ID：{}", id);
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        // 清除敏感信息
        user.setPassword(null);
        return user;
    }

    @Override
    public User getUserByUsername(String username) {
        logger.info("根据用户名查询用户：{}", username);
        User user = userMapper.selectByUsername(username);
        if (user != null) {
            // 清除敏感信息
            user.setPassword(null);
        }
        return user;
    }

    @Override
    public PageResult<User> getUserPage(UserQueryRequest query) {
        logger.info("分页查询用户：页码={}，每页大小={}", query.getPage(), query.getSize());
        
        int offset = (query.getPage() - 1) * query.getSize();
        List<User> userList = userMapper.selectPage(query, offset, query.getSize());
        int total = userMapper.selectCount(query);
        
        // 清除敏感信息
        userList.forEach(user -> user.setPassword(null));
        
        return new PageResult<User>((long) total, userList, query.getPage(), query.getSize());
    }

    @Override
    public List<User> getAllUsers() {
        logger.info("查询所有用户");
        List<User> users = userMapper.selectAll();
        // 清除敏感信息
        users.forEach(user -> user.setPassword(null));
        return users;
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        logger.info("删除用户，ID：{}", id);
        
        User existing = userMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 检查是否为管理员
        if ("ADMIN".equals(existing.getRole())) {
            // 检查是否还有其他管理员
            List<User> admins = getUsersByRole("ADMIN");
            if (admins.size() <= 1) {
                throw new BusinessException("至少需要保留一个管理员用户");
            }
        }
        
        userMapper.deleteById(id);
        logger.info("用户删除成功");
    }

    @Override
    @Transactional
    public void deleteUserBatch(List<Long> ids) {
        logger.info("批量删除用户，数量：{}", ids.size());
        
        // 检查管理员数量
        long adminCount = ids.stream()
            .map(id -> userMapper.selectById(id))
            .filter(user -> user != null && "ADMIN".equals(user.getRole()))
            .count();
            
        List<User> allAdmins = getUsersByRole("ADMIN");
        if (allAdmins.size() - adminCount < 1) {
            throw new BusinessException("至少需要保留一个管理员用户");
        }
        
        userMapper.deleteBatch(ids);
        logger.info("批量删除用户成功");
    }

    @Override
    @Transactional
    public void updateUserStatus(Long id, Integer status) {
        logger.info("更新用户状态，ID：{}，状态：{}", id, status);
        
        User existing = userMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 如果禁用管理员，检查是否还有其他启用的管理员
        if ("ADMIN".equals(existing.getRole()) && status == 0) {
            List<User> activeAdmins = userMapper.selectActiveAdmins();
            if (activeAdmins.size() <= 1) {
                throw new BusinessException("至少需要保留一个启用的管理员用户");
            }
        }
        
        userMapper.updateStatus(id, status);
        logger.info("用户状态更新成功");
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        logger.info("修改用户密码，用户ID：{}", userId);
        
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 验证旧密码
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("旧密码不正确");
        }
        
        // 验证新密码确认
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("新密码确认不一致");
        }
        
        // 加密新密码
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        userMapper.updatePassword(userId, encodedPassword);
        
        logger.info("用户密码修改成功");
    }

    @Override
    @Transactional
    public void resetPassword(Long id, String newPassword) {
        logger.info("重置用户密码，用户ID：{}", id);
        
        User existing = userMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("用户不存在");
        }
        
        String encodedPassword = passwordEncoder.encode(newPassword);
        userMapper.updatePassword(id, encodedPassword);
        
        logger.info("用户密码重置成功");
    }

    @Override
    public boolean existsByUsername(String username) {
        return userMapper.selectByUsername(username) != null;
    }

    @Override
    public boolean existsByEmail(String email) {
        return userMapper.selectByEmail(email) != null;
    }

    @Override
    public List<User> getUsersByRole(String role) {
        logger.info("根据角色查询用户：{}", role);
        List<User> users = userMapper.selectByRole(role);
        // 清除敏感信息
        users.forEach(user -> user.setPassword(null));
        return users;
    }

    @Override
    public Object getUserStatistics() {
        logger.info("获取用户统计信息");
        
        Map<String, Object> statistics = new HashMap<>();
        
        // 总用户数
        statistics.put("totalUsers", userMapper.selectTotalCount());
        
        // 按角色统计
        statistics.put("adminCount", userMapper.selectCountByRole("ADMIN"));
        statistics.put("dispatcherCount", userMapper.selectCountByRole("DISPATCHER"));
        statistics.put("customerCount", userMapper.selectCountByRole("CUSTOMER"));
        
        // 按状态统计
        statistics.put("activeUsers", userMapper.selectCountByStatus(1));
        statistics.put("inactiveUsers", userMapper.selectCountByStatus(0));
        
        return statistics;
    }

    /**
     * 转换DTO为实体
     */
    private User convertToEntity(UserRequest request) {
        User user = new User();
        BeanUtils.copyProperties(request, user);
        return user;
    }
} 