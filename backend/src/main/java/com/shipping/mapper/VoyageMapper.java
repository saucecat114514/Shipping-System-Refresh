package com.shipping.mapper;

import com.shipping.model.entity.Voyage;
import com.shipping.model.dto.VoyageQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 航次数据访问接口
 */
@Mapper
public interface VoyageMapper {

    /**
     * 插入航次
     */
    int insert(Voyage voyage);

    /**
     * 根据ID删除航次
     */
    int deleteById(Long id);

    /**
     * 根据ID更新航次
     */
    int updateById(Voyage voyage);

    /**
     * 根据ID查询航次
     */
    Voyage selectById(Long id);

    /**
     * 根据ID查询航次详情（包含航线和港口信息）
     */
    Voyage selectByIdWithDetails(Long id);

    /**
     * 根据航次编号查询航次
     */
    Voyage selectByVoyageNumber(String voyageNumber);

    /**
     * 检查航次编号是否存在（排除指定ID）
     */
    boolean existsByVoyageNumber(@Param("voyageNumber") String voyageNumber, @Param("excludeId") Long excludeId);

    /**
     * 分页查询航次列表
     */
    List<Voyage> selectPageList(VoyageQueryRequest queryRequest);

    /**
     * 分页查询航次列表（包含关联信息）
     */
    List<Voyage> selectPageListWithDetails(VoyageQueryRequest queryRequest);

    /**
     * 查询总数
     */
    long selectTotal(VoyageQueryRequest queryRequest);

    /**
     * 获取所有启用的航次
     */
    List<Voyage> selectAllActive();

    /**
     * 根据航线ID查询航次列表
     */
    List<Voyage> selectByRouteId(Long routeId);

    /**
     * 根据船舶ID查询航次列表
     */
    List<Voyage> selectByShipId(Long shipId);

    /**
     * 更新航次状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") String status);
} 