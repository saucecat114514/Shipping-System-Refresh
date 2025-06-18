package com.shipping.service;

import com.shipping.model.entity.User;
import com.shipping.model.dto.UserRequest;
import com.shipping.model.dto.UserQueryRequest;
import com.shipping.model.dto.ChangePasswordRequest;
import com.shipping.common.PageResult;
import java.util.List;

/**
 * 用户管理服务接口
 */
public interface UserService {

    /**
     * 创建用户
     */
    void createUser(UserRequest request);

    /**
     * 更新用户信息
     */
    void updateUser(Long id, UserRequest request);

    /**
     * 根据ID查询用户
     */
    User getUserById(Long id);

    /**
     * 根据用户名查询用户
     */
    User getUserByUsername(String username);

    /**
     * 分页查询用户
     */
    PageResult<User> getUserPage(UserQueryRequest query);

    /**
     * 查询所有用户
     */
    List<User> getAllUsers();

    /**
     * 删除用户
     */
    void deleteUser(Long id);

    /**
     * 批量删除用户
     */
    void deleteUserBatch(List<Long> ids);

    /**
     * 启用/禁用用户
     */
    void updateUserStatus(Long id, Integer status);

    /**
     * 修改密码
     */
    void changePassword(Long userId, ChangePasswordRequest request);

    /**
     * 重置密码
     */
    void resetPassword(Long id, String newPassword);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 根据角色查询用户
     */
    List<User> getUsersByRole(String role);

    /**
     * 获取用户统计信息
     */
    Object getUserStatistics();
} 