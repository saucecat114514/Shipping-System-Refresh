package com.shipping.service.impl;

import com.shipping.common.PageResult;
import com.shipping.common.Result;
import com.shipping.exception.BusinessException;
import com.shipping.mapper.ShipMapper;
import com.shipping.model.entity.Ship;
import com.shipping.model.dto.ShipRequest;
import com.shipping.model.dto.ShipQueryRequest;
import com.shipping.service.ShipService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 船舶服务实现类
 */
@Service
public class ShipServiceImpl implements ShipService {

    @Autowired
    private ShipMapper shipMapper;

    @Override
    public Result<Ship> createShip(ShipRequest shipRequest) {
        // 检查MMSI是否已存在
        if (StringUtils.hasText(shipRequest.getMmsi()) && 
            shipMapper.existsByMmsi(shipRequest.getMmsi(), null)) {
            throw new BusinessException("MMSI已存在：" + shipRequest.getMmsi());
        }

        // 检查IMO编号是否已存在
        if (StringUtils.hasText(shipRequest.getImoNumber()) && 
            shipMapper.existsByImoNumber(shipRequest.getImoNumber(), null)) {
            throw new BusinessException("IMO编号已存在：" + shipRequest.getImoNumber());
        }

        // 创建船舶实体
        Ship ship = new Ship();
        BeanUtils.copyProperties(shipRequest, ship);
        ship.setCreatedAt(LocalDateTime.now());
        ship.setUpdatedAt(LocalDateTime.now());

        // 插入数据库
        int result = shipMapper.insert(ship);
        if (result > 0) {
            return Result.success(ship);
        } else {
            throw new BusinessException("创建船舶失败");
        }
    }

    @Override
    public Result<Void> deleteShip(Long id) {
        // 检查船舶是否存在
        Ship existingShip = shipMapper.selectById(id);
        if (existingShip == null) {
            throw new BusinessException("船舶不存在，ID：" + id);
        }

        // TODO: 检查船舶是否被航次使用，如果被使用则不能删除
        // 可以在这里添加检查逻辑

        // 删除船舶
        int result = shipMapper.deleteById(id);
        if (result > 0) {
            return Result.success();
        } else {
            throw new BusinessException("删除船舶失败");
        }
    }

    @Override
    public Result<Ship> updateShip(Long id, ShipRequest shipRequest) {
        // 检查船舶是否存在
        Ship existingShip = shipMapper.selectById(id);
        if (existingShip == null) {
            throw new BusinessException("船舶不存在，ID：" + id);
        }

        // 检查MMSI是否被其他船舶使用
        if (StringUtils.hasText(shipRequest.getMmsi()) && 
            shipMapper.existsByMmsi(shipRequest.getMmsi(), id)) {
            throw new BusinessException("MMSI已存在：" + shipRequest.getMmsi());
        }

        // 检查IMO编号是否被其他船舶使用
        if (StringUtils.hasText(shipRequest.getImoNumber()) && 
            shipMapper.existsByImoNumber(shipRequest.getImoNumber(), id)) {
            throw new BusinessException("IMO编号已存在：" + shipRequest.getImoNumber());
        }

        // 更新船舶信息
        Ship updateShip = new Ship();
        BeanUtils.copyProperties(shipRequest, updateShip);
        updateShip.setId(id);
        updateShip.setCreatedAt(existingShip.getCreatedAt());
        updateShip.setUpdatedAt(LocalDateTime.now());

        int result = shipMapper.updateById(updateShip);
        if (result > 0) {
            return Result.success(shipMapper.selectById(id));
        } else {
            throw new BusinessException("更新船舶失败");
        }
    }

    @Override
    public Result<Ship> getShipById(Long id) {
        Ship ship = shipMapper.selectById(id);
        if (ship == null) {
            throw new BusinessException("船舶不存在，ID：" + id);
        }
        return Result.success(ship);
    }

    @Override
    public Result<Ship> getShipByMmsi(String mmsi) {
        Ship ship = shipMapper.selectByMmsi(mmsi);
        if (ship == null) {
            throw new BusinessException("船舶不存在，MMSI：" + mmsi);
        }
        return Result.success(ship);
    }

    @Override
    public Result<Ship> getShipByImoNumber(String imoNumber) {
        Ship ship = shipMapper.selectByImoNumber(imoNumber);
        if (ship == null) {
            throw new BusinessException("船舶不存在，IMO编号：" + imoNumber);
        }
        return Result.success(ship);
    }

    @Override
    public Result<PageResult<Ship>> getShipPage(ShipQueryRequest queryRequest) {
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
        List<Ship> ships = shipMapper.selectPageList(queryRequest);
        long total = shipMapper.selectTotal(queryRequest);

        // 构建分页结果
        PageResult<Ship> pageResult = new PageResult<>();
        pageResult.setRecords(ships);
        pageResult.setTotal(total);
        pageResult.setPageNum(queryRequest.getPage());
        pageResult.setPageSize(queryRequest.getSize());
        pageResult.setPages((int) Math.ceil((double) total / queryRequest.getSize()));

        return Result.success(pageResult);
    }

    @Override
    public Result<List<Ship>> getAllShips() {
        List<Ship> ships = shipMapper.selectAll();
        return Result.success(ships);
    }
} 