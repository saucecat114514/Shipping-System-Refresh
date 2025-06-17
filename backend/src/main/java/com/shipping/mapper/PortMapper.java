package com.shipping.mapper;

import com.shipping.model.entity.Port;
import com.shipping.model.dto.PortQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 港口Mapper接口
 */
@Mapper
public interface PortMapper {

    /**
     * 插入港口信息
     * @param port 港口实体
     * @return 影响行数
     */
    int insert(Port port);

    /**
     * 根据ID删除港口
     * @param id 港口ID
     * @return 影响行数
     */
    int deleteById(Long id);

    /**
     * 更新港口信息
     * @param port 港口实体
     * @return 影响行数
     */
    int updateById(Port port);

    /**
     * 根据ID查询港口
     * @param id 港口ID
     * @return 港口实体
     */
    Port selectById(Long id);

    /**
     * 根据代码查询港口
     * @param code 港口代码
     * @return 港口实体
     */
    Port selectByCode(String code);

    /**
     * 分页查询港口列表
     * @param queryRequest 查询请求参数
     * @return 港口列表
     */
    List<Port> selectPageList(PortQueryRequest queryRequest);

    /**
     * 查询港口总数
     * @param queryRequest 查询请求参数
     * @return 总数
     */
    long selectTotal(PortQueryRequest queryRequest);

    /**
     * 查询所有港口（用于下拉框等）
     * @return 港口列表
     */
    List<Port> selectAll();

    /**
     * 检查港口代码是否存在
     * @param code 港口代码
     * @param excludeId 排除的ID（用于更新时检查）
     * @return 是否存在
     */
    boolean existsByCode(@Param("code") String code, @Param("excludeId") Long excludeId);
} 