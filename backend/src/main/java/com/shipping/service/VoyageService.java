package com.shipping.service;

import com.shipping.common.PageResult;
import com.shipping.common.Result;
import com.shipping.model.entity.Voyage;
import com.shipping.model.dto.VoyageRequest;
import com.shipping.model.dto.VoyageQueryRequest;

import java.util.List;

/**
 * 航次服务接口
 */
public interface VoyageService {

    /**
     * 创建航次
     */
    Result<Voyage> createVoyage(VoyageRequest voyageRequest);

    /**
     * 删除航次
     */
    Result<Void> deleteVoyage(Long id);

    /**
     * 更新航次信息
     */
    Result<Voyage> updateVoyage(Long id, VoyageRequest voyageRequest);

    /**
     * 根据ID查询航次
     */
    Result<Voyage> getVoyageById(Long id);

    /**
     * 根据航次编号查询航次
     */
    Result<Voyage> getVoyageByVoyageNumber(String voyageNumber);

    /**
     * 分页查询航次列表
     */
    Result<PageResult<Voyage>> getVoyagePage(VoyageQueryRequest queryRequest);

    /**
     * 分页查询航次列表（包含关联信息）
     */
    Result<PageResult<Voyage>> getVoyagePageWithDetails(VoyageQueryRequest queryRequest);

    /**
     * 获取所有启用的航次
     */
    Result<List<Voyage>> getAllActiveVoyages();

    /**
     * 根据航线ID查询航次列表
     */
    Result<List<Voyage>> getVoyagesByRouteId(Long routeId);

    /**
     * 根据船舶ID查询航次列表
     */
    Result<List<Voyage>> getVoyagesByShipId(Long shipId);

    /**
     * 更新航次状态
     */
    Result<Void> updateVoyageStatus(Long id, String status);

    /**
     * 自动生成航次（基于航线和固定时间表）
     */
    Result<Voyage> generateVoyage(Long routeId, Long shipId);
} 