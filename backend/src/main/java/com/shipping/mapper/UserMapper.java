package com.shipping.mapper;

import com.shipping.model.entity.User;
import com.shipping.model.dto.UserQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 用户数据访问层接口
 */
@Mapper
public interface UserMapper {

    /**
     * 根据用户名查找用户
     */
    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    /**
     * 根据ID查找用户
     */
    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(@Param("id") Long id);

    /**
     * 根据邮箱查找用户
     */
    @Select("SELECT * FROM users WHERE email = #{email}")
    User findByEmail(@Param("email") String email);

    /**
     * 查询所有用户
     */
    @Select("SELECT * FROM users ORDER BY created_at DESC")
    List<User> findAll();

    /**
     * 插入用户
     */
    int insert(User user);

    /**
     * 更新用户
     */
    int update(User user);

    /**
     * 删除用户
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据角色查询用户
     */
    @Select("SELECT * FROM users WHERE role = #{role} ORDER BY created_at DESC")
    List<User> findByRole(@Param("role") String role);

    /**
     * 统计用户数量
     */
    @Select("SELECT COUNT(*) FROM users")
    long count();

    // 为UserService添加的新方法
    
    /**
     * 根据ID查询用户 (selectById别名)
     */
    default User selectById(Long id) {
        return findById(id);
    }

    /**
     * 根据用户名查询用户 (selectByUsername别名)
     */
    default User selectByUsername(String username) {
        return findByUsername(username);
    }

    /**
     * 根据邮箱查询用户 (selectByEmail别名)
     */
    default User selectByEmail(String email) {
        return findByEmail(email);
    }

    /**
     * 查询所有用户 (selectAll别名)
     */
    default List<User> selectAll() {
        return findAll();
    }

    /**
     * 根据角色查询用户 (selectByRole别名)
     */
    default List<User> selectByRole(String role) {
        return findByRole(role);
    }

    /**
     * 分页查询用户
     */
    List<User> selectPage(@Param("query") UserQueryRequest query, 
                         @Param("offset") int offset, 
                         @Param("limit") int limit);

    /**
     * 查询用户总数
     */
    int selectCount(@Param("query") UserQueryRequest query);

    /**
     * 批量删除用户
     */
    void deleteBatch(@Param("ids") List<Long> ids);

    /**
     * 更新用户状态
     */
    @Update("UPDATE users SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    void updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 更新用户密码
     */
    @Update("UPDATE users SET password = #{password}, updated_at = NOW() WHERE id = #{id}")
    void updatePassword(@Param("id") Long id, @Param("password") String password);

    /**
     * 查询启用的管理员
     */
    @Select("SELECT * FROM users WHERE role = 'ADMIN' AND status = 1")
    List<User> selectActiveAdmins();

    /**
     * 统计总用户数
     */
    @Select("SELECT COUNT(*) FROM users")
    int selectTotalCount();

    /**
     * 根据角色统计用户数
     */
    @Select("SELECT COUNT(*) FROM users WHERE role = #{role}")
    int selectCountByRole(@Param("role") String role);

    /**
     * 根据状态统计用户数
     */
    @Select("SELECT COUNT(*) FROM users WHERE status = #{status}")
    int selectCountByStatus(@Param("status") Integer status);
} 