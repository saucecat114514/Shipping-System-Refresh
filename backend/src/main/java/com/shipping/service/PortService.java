package com.shipping.service;

import com.shipping.common.PageResult;
import com.shipping.common.Result;
import com.shipping.model.entity.Port;
import com.shipping.model.dto.PortRequest;
import com.shipping.model.dto.PortQueryRequest;

import java.util.List;

/**
 * 港口服务接口
 */
public interface PortService {

    /**
     * 创建港口
     * @param portRequest 港口请求信息
     * @return 操作结果
     */
    Result<Port> createPort(PortRequest portRequest);

    /**
     * 根据ID删除港口
     * @param id 港口ID
     * @return 操作结果
     */
    Result<Void> deletePort(Long id);

    /**
     * 更新港口信息
     * @param id 港口ID
     * @param portRequest 更新的港口信息
     * @return 操作结果
     */
    Result<Port> updatePort(Long id, PortRequest portRequest);

    /**
     * 根据ID查询港口
     * @param id 港口ID
     * @return 港口信息
     */
    Result<Port> getPortById(Long id);

    /**
     * 根据代码查询港口
     * @param code 港口代码
     * @return 港口信息
     */
    Result<Port> getPortByCode(String code);

    /**
     * 分页查询港口列表
     * @param queryRequest 查询条件
     * @return 分页结果
     */
    Result<PageResult<Port>> getPortPage(PortQueryRequest queryRequest);

    /**
     * 获取所有港口列表
     * @return 港口列表
     */
    Result<List<Port>> getAllPorts();
} 