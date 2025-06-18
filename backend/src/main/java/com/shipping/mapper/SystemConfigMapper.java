package com.shipping.mapper;

import com.shipping.model.entity.SystemConfig;
import com.shipping.model.dto.SystemConfigQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 系统配置Mapper接口
 */
@Mapper
public interface SystemConfigMapper {

    /**
     * 插入系统配置
     */
    void insert(SystemConfig systemConfig);

    /**
     * 更新系统配置
     */
    void update(SystemConfig systemConfig);

    /**
     * 根据ID查询系统配置
     */
    SystemConfig selectById(Long id);

    /**
     * 根据配置键查询系统配置
     */
    SystemConfig selectByKey(@Param("configKey") String configKey);

    /**
     * 分页查询系统配置
     */
    List<SystemConfig> selectPage(@Param("query") SystemConfigQueryRequest query, 
                                 @Param("offset") int offset, 
                                 @Param("limit") int limit);

    /**
     * 查询系统配置总数
     */
    int selectCount(@Param("query") SystemConfigQueryRequest query);

    /**
     * 查询所有系统配置
     */
    List<SystemConfig> selectAll();

    /**
     * 根据ID删除系统配置
     */
    void deleteById(Long id);

    /**
     * 根据配置键删除系统配置
     */
    void deleteByKey(@Param("configKey") String configKey);

    /**
     * 批量删除系统配置
     */
    void deleteBatch(@Param("ids") List<Long> ids);

    /**
     * 根据配置键更新配置值
     */
    void updateValueByKey(@Param("configKey") String configKey, 
                         @Param("configValue") String configValue);

    /**
     * 批量更新系统配置
     */
    void updateBatch(@Param("list") List<SystemConfig> configList);
} 