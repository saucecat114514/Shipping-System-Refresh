package com.shipping.service.impl;

import com.shipping.common.PageResult;
import com.shipping.common.Result;
import com.shipping.exception.BusinessException;
import com.shipping.mapper.RouteMapper;
import com.shipping.mapper.ShipMapper;
import com.shipping.mapper.VoyageMapper;
import com.shipping.model.entity.Route;
import com.shipping.model.entity.Ship;
import com.shipping.model.entity.Voyage;
import com.shipping.model.dto.VoyageRequest;
import com.shipping.model.dto.VoyageQueryRequest;
import com.shipping.service.VoyageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 航次服务实现类
 */
@Service
public class VoyageServiceImpl implements VoyageService {

    private static final Logger logger = LoggerFactory.getLogger(VoyageServiceImpl.class);

    @Autowired
    private VoyageMapper voyageMapper;

    @Autowired
    private RouteMapper routeMapper;

    @Autowired
    private ShipMapper shipMapper;

    @Override
    public Result<Voyage> createVoyage(VoyageRequest voyageRequest) {
        logger.info("创建航次: {}", voyageRequest.getVoyageNumber());

        // 检查航次编号是否已存在
        if (voyageMapper.existsByVoyageNumber(voyageRequest.getVoyageNumber(), null)) {
            throw new BusinessException("航次编号已存在：" + voyageRequest.getVoyageNumber());
        }

        // 检查航线是否存在
        Route route = routeMapper.selectById(voyageRequest.getRouteId());
        if (route == null) {
            throw new BusinessException("航线不存在，ID：" + voyageRequest.getRouteId());
        }

        // 检查船舶是否存在
        Ship ship = shipMapper.selectById(voyageRequest.getShipId());
        if (ship == null) {
            throw new BusinessException("船舶不存在，ID：" + voyageRequest.getShipId());
        }

        // 验证日期逻辑
        if (voyageRequest.getDepartureDate().isAfter(voyageRequest.getArrivalDate())) {
            throw new BusinessException("出发日期不能晚于到达日期");
        }

        // 如果有实际日期，验证实际日期逻辑
        if (voyageRequest.getActualDepartureDate() != null && 
            voyageRequest.getActualArrivalDate() != null) {
            if (voyageRequest.getActualDepartureDate().isAfter(voyageRequest.getActualArrivalDate())) {
                throw new BusinessException("实际出发日期不能晚于实际到达日期");
            }
        }

        // 创建航次实体
        Voyage voyage = new Voyage();
        BeanUtils.copyProperties(voyageRequest, voyage);
        
        // 设置默认状态
        if (voyage.getStatus() == null) {
            voyage.setStatus("PLANNED");
        }
        
        voyage.setCreatedAt(LocalDateTime.now());
        voyage.setUpdatedAt(LocalDateTime.now());

        // 插入数据库
        int result = voyageMapper.insert(voyage);
        if (result > 0) {
            logger.info("航次创建成功，ID: {}", voyage.getId());
            return Result.success(voyage);
        } else {
            throw new BusinessException("创建航次失败");
        }
    }

    @Override
    public Result<Void> deleteVoyage(Long id) {
        logger.info("删除航次，ID: {}", id);

        // 检查航次是否存在
        Voyage existingVoyage = voyageMapper.selectById(id);
        if (existingVoyage == null) {
            throw new BusinessException("航次不存在，ID：" + id);
        }

        // 检查是否有关联的订单
        // TODO: 检查是否有订单关联此航次，如果有则不能删除

        // 删除航次
        int result = voyageMapper.deleteById(id);
        if (result > 0) {
            logger.info("航次删除成功，ID: {}", id);
            return Result.success();
        } else {
            throw new BusinessException("删除航次失败");
        }
    }

    @Override
    public Result<Voyage> updateVoyage(Long id, VoyageRequest voyageRequest) {
        logger.info("更新航次，ID: {}", id);

        // 检查航次是否存在
        Voyage existingVoyage = voyageMapper.selectById(id);
        if (existingVoyage == null) {
            throw new BusinessException("航次不存在，ID：" + id);
        }

        // 检查航次编号是否被其他航次使用
        if (voyageMapper.existsByVoyageNumber(voyageRequest.getVoyageNumber(), id)) {
            throw new BusinessException("航次编号已存在：" + voyageRequest.getVoyageNumber());
        }

        // 检查航线是否存在
        if (routeMapper.selectById(voyageRequest.getRouteId()) == null) {
            throw new BusinessException("航线不存在，ID：" + voyageRequest.getRouteId());
        }

        // 检查船舶是否存在
        if (shipMapper.selectById(voyageRequest.getShipId()) == null) {
            throw new BusinessException("船舶不存在，ID：" + voyageRequest.getShipId());
        }

        // 验证日期逻辑
        if (voyageRequest.getDepartureDate().isAfter(voyageRequest.getArrivalDate())) {
            throw new BusinessException("出发日期不能晚于到达日期");
        }

        // 更新航次信息
        Voyage updateVoyage = new Voyage();
        BeanUtils.copyProperties(voyageRequest, updateVoyage);
        updateVoyage.setId(id);
        updateVoyage.setCreatedAt(existingVoyage.getCreatedAt());
        updateVoyage.setUpdatedAt(LocalDateTime.now());

        int result = voyageMapper.updateById(updateVoyage);
        if (result > 0) {
            logger.info("航次更新成功，ID: {}", id);
            return Result.success(voyageMapper.selectById(id));
        } else {
            throw new BusinessException("更新航次失败");
        }
    }

    @Override
    public Result<Voyage> getVoyageById(Long id) {
        Voyage voyage = voyageMapper.selectById(id);
        if (voyage == null) {
            throw new BusinessException("航次不存在，ID：" + id);
        }
        return Result.success(voyage);
    }

    @Override
    public Result<Voyage> getVoyageByIdWithDetails(Long id) {
        Voyage voyage = voyageMapper.selectByIdWithDetails(id);
        if (voyage == null) {
            throw new BusinessException("航次不存在，ID：" + id);
        }
        return Result.success(voyage);
    }

    @Override
    public Result<Voyage> getVoyageByVoyageNumber(String voyageNumber) {
        Voyage voyage = voyageMapper.selectByVoyageNumber(voyageNumber);
        if (voyage == null) {
            throw new BusinessException("航次不存在，编号：" + voyageNumber);
        }
        return Result.success(voyage);
    }

    @Override
    public Result<PageResult<Voyage>> getVoyagePage(VoyageQueryRequest queryRequest) {
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
        List<Voyage> voyages = voyageMapper.selectPageList(queryRequest);
        long total = voyageMapper.selectTotal(queryRequest);

        // 构建分页结果
        PageResult<Voyage> pageResult = new PageResult<>();
        pageResult.setRecords(voyages);
        pageResult.setTotal(total);
        pageResult.setPageNum(queryRequest.getPage());
        pageResult.setPageSize(queryRequest.getSize());
        pageResult.setPages((int) Math.ceil((double) total / queryRequest.getSize()));

        return Result.success(pageResult);
    }

    @Override
    public Result<PageResult<Voyage>> getVoyagePageWithDetails(VoyageQueryRequest queryRequest) {
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

        // 查询数据和总数（包含关联信息）
        List<Voyage> voyages = voyageMapper.selectPageListWithDetails(queryRequest);
        long total = voyageMapper.selectTotal(queryRequest);

        // 构建分页结果
        PageResult<Voyage> pageResult = new PageResult<>();
        pageResult.setRecords(voyages);
        pageResult.setTotal(total);
        pageResult.setPageNum(queryRequest.getPage());
        pageResult.setPageSize(queryRequest.getSize());
        pageResult.setPages((int) Math.ceil((double) total / queryRequest.getSize()));

        return Result.success(pageResult);
    }

    @Override
    public Result<List<Voyage>> getAllActiveVoyages() {
        List<Voyage> voyages = voyageMapper.selectAllActive();
        return Result.success(voyages);
    }

    @Override
    public Result<List<Voyage>> getVoyagesByRouteId(Long routeId) {
        List<Voyage> voyages = voyageMapper.selectByRouteId(routeId);
        return Result.success(voyages);
    }

    @Override
    public Result<List<Voyage>> getVoyagesByShipId(Long shipId) {
        List<Voyage> voyages = voyageMapper.selectByShipId(shipId);
        return Result.success(voyages);
    }

    @Override
    public Result<Void> updateVoyageStatus(Long id, String status) {
        // 检查航次是否存在
        Voyage existingVoyage = voyageMapper.selectById(id);
        if (existingVoyage == null) {
            throw new BusinessException("航次不存在，ID：" + id);
        }

        // 验证状态值
        if (!isValidStatus(status)) {
            throw new BusinessException("无效的航次状态：" + status);
        }

        int result = voyageMapper.updateStatus(id, status);
        if (result > 0) {
            logger.info("航次状态更新成功，ID: {}, 状态: {}", id, status);
            return Result.success();
        } else {
            throw new BusinessException("更新航次状态失败");
        }
    }

    @Override
    public Result<Voyage> generateVoyage(Long routeId, Long shipId) {
        logger.info("自动生成航次，航线ID: {}, 船舶ID: {}", routeId, shipId);

        // 检查航线是否存在
        Route route = routeMapper.selectById(routeId);
        if (route == null) {
            throw new BusinessException("航线不存在，ID：" + routeId);
        }

        // 检查船舶是否存在
        Ship ship = shipMapper.selectById(shipId);
        if (ship == null) {
            throw new BusinessException("船舶不存在，ID：" + shipId);
        }

        // 生成航次编号
        String voyageNumber = generateVoyageNumber(route.getRouteNumber());

        // 计算预计出发和到达时间（这里使用简单的逻辑，实际项目中可能更复杂）
        LocalDateTime departureDate = LocalDateTime.now().plusDays(7); // 一周后出发
        LocalDateTime arrivalDate = departureDate.plusHours(route.getEstimatedDuration() != null ? 
            route.getEstimatedDuration().longValue() : 240); // 默认10天

        // 创建航次
        Voyage voyage = new Voyage();
        voyage.setVoyageNumber(voyageNumber);
        voyage.setRouteId(routeId);
        voyage.setShipId(shipId);
        voyage.setDepartureDate(departureDate);
        voyage.setArrivalDate(arrivalDate);
        voyage.setStatus("PLANNED");
        voyage.setCreatedAt(LocalDateTime.now());
        voyage.setUpdatedAt(LocalDateTime.now());

        int result = voyageMapper.insert(voyage);
        if (result > 0) {
            logger.info("航次自动生成成功，编号: {}", voyageNumber);
            return Result.success(voyage);
        } else {
            throw new BusinessException("自动生成航次失败");
        }
    }

    /**
     * 验证状态值是否有效
     */
    private boolean isValidStatus(String status) {
        return "PLANNED".equals(status) || "IN_PROGRESS".equals(status) || 
               "COMPLETED".equals(status) || "CANCELLED".equals(status);
    }

    /**
     * 生成航次编号
     */
    private String generateVoyageNumber(String routeNumber) {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "V" + routeNumber + dateStr + String.format("%03d", System.currentTimeMillis() % 1000);
    }
} 