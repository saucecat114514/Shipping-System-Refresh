package com.shipping.service;

import com.shipping.common.PageResult;
import com.shipping.common.Result;
import com.shipping.model.entity.Ship;
import com.shipping.model.dto.ShipRequest;
import com.shipping.model.dto.ShipQueryRequest;

import java.util.List;

/**
 * 船舶服务接口
 */
public interface ShipService {

    /**
     * 创建船舶
     * @param shipRequest 船舶请求信息
     * @return 操作结果
     */
    Result<Ship> createShip(ShipRequest shipRequest);

    /**
     * 根据ID删除船舶
     * @param id 船舶ID
     * @return 操作结果
     */
    Result<Void> deleteShip(Long id);

    /**
     * 更新船舶信息
     * @param id 船舶ID
     * @param shipRequest 更新的船舶信息
     * @return 操作结果
     */
    Result<Ship> updateShip(Long id, ShipRequest shipRequest);

    /**
     * 根据ID查询船舶
     * @param id 船舶ID
     * @return 船舶信息
     */
    Result<Ship> getShipById(Long id);

    /**
     * 根据MMSI查询船舶
     * @param mmsi MMSI
     * @return 船舶信息
     */
    Result<Ship> getShipByMmsi(String mmsi);

    /**
     * 根据IMO编号查询船舶
     * @param imoNumber IMO编号
     * @return 船舶信息
     */
    Result<Ship> getShipByImoNumber(String imoNumber);

    /**
     * 分页查询船舶列表
     * @param queryRequest 查询条件
     * @return 分页结果
     */
    Result<PageResult<Ship>> getShipPage(ShipQueryRequest queryRequest);

    /**
     * 获取所有船舶列表
     * @return 船舶列表
     */
    Result<List<Ship>> getAllShips();
} 