package com.shipping.service.impl;

import com.shipping.common.PageResult;
import com.shipping.common.Result;
import com.shipping.exception.BusinessException;
import com.shipping.mapper.PortMapper;
import com.shipping.model.entity.Port;
import com.shipping.model.dto.PortRequest;
import com.shipping.model.dto.PortQueryRequest;
import com.shipping.service.PortService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 港口服务实现类
 */
@Service
public class PortServiceImpl implements PortService {

    @Autowired
    private PortMapper portMapper;

    @Override
    public Result<Port> createPort(PortRequest portRequest) {
        // 检查港口代码是否已存在
        if (portMapper.existsByCode(portRequest.getCode(), null)) {
            throw new BusinessException("港口代码已存在：" + portRequest.getCode());
        }

        // 创建港口实体
        Port port = new Port();
        BeanUtils.copyProperties(portRequest, port);
        port.setCreatedAt(LocalDateTime.now());
        port.setUpdatedAt(LocalDateTime.now());

        // 插入数据库
        int result = portMapper.insert(port);
        if (result > 0) {
            return Result.success(port);
        } else {
            throw new BusinessException("创建港口失败");
        }
    }

    @Override
    public Result<Void> deletePort(Long id) {
        // 检查港口是否存在
        Port existingPort = portMapper.selectById(id);
        if (existingPort == null) {
            throw new BusinessException("港口不存在，ID：" + id);
        }

        // TODO: 检查港口是否被航线使用，如果被使用则不能删除
        // 可以在这里添加检查逻辑

        // 删除港口
        int result = portMapper.deleteById(id);
        if (result > 0) {
            return Result.success();
        } else {
            throw new BusinessException("删除港口失败");
        }
    }

    @Override
    public Result<Port> updatePort(Long id, PortRequest portRequest) {
        // 检查港口是否存在
        Port existingPort = portMapper.selectById(id);
        if (existingPort == null) {
            throw new BusinessException("港口不存在，ID：" + id);
        }

        // 检查港口代码是否被其他港口使用
        if (portMapper.existsByCode(portRequest.getCode(), id)) {
            throw new BusinessException("港口代码已存在：" + portRequest.getCode());
        }

        // 更新港口信息
        Port updatePort = new Port();
        BeanUtils.copyProperties(portRequest, updatePort);
        updatePort.setId(id);
        updatePort.setCreatedAt(existingPort.getCreatedAt());
        updatePort.setUpdatedAt(LocalDateTime.now());

        int result = portMapper.updateById(updatePort);
        if (result > 0) {
            return Result.success(portMapper.selectById(id));
        } else {
            throw new BusinessException("更新港口失败");
        }
    }

    @Override
    public Result<Port> getPortById(Long id) {
        Port port = portMapper.selectById(id);
        if (port == null) {
            throw new BusinessException("港口不存在，ID：" + id);
        }
        return Result.success(port);
    }

    @Override
    public Result<Port> getPortByCode(String code) {
        Port port = portMapper.selectByCode(code);
        if (port == null) {
            throw new BusinessException("港口不存在，代码：" + code);
        }
        return Result.success(port);
    }

    @Override
    public Result<PageResult<Port>> getPortPage(PortQueryRequest queryRequest) {
        // 参数校验和默认值设置
        if (queryRequest.getPage() < 1) {
            queryRequest.setPage(1);
        }
        if (queryRequest.getSize() < 1) {
            queryRequest.setSize(10);
        }
        if (queryRequest.getSize() > 100) {
            queryRequest.setSize(100);
        }

        // 查询数据和总数
        List<Port> ports = portMapper.selectPageList(queryRequest);
        long total = portMapper.selectTotal(queryRequest);

        // 构建分页结果
        PageResult<Port> pageResult = new PageResult<>();
        pageResult.setRecords(ports);
        pageResult.setTotal(total);
        pageResult.setPageNum(queryRequest.getPage());
        pageResult.setPageSize(queryRequest.getSize());
        pageResult.setPages((int) Math.ceil((double) total / queryRequest.getSize()));

        return Result.success(pageResult);
    }

    @Override
    public Result<List<Port>> getAllPorts() {
        List<Port> ports = portMapper.selectAll();
        return Result.success(ports);
    }
} 