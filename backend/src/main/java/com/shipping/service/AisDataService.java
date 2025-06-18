package com.shipping.service;

import com.shipping.model.entity.AisData;
import com.shipping.model.dto.AisDataRequest;
import com.shipping.model.dto.AisDataQueryRequest;
import com.shipping.common.PageResult;
import java.util.List;

/**
 * AIS数据服务接口
 */
public interface AisDataService {

    /**
     * 添加AIS数据
     */
    void addAisData(AisDataRequest request);

    /**
     * 批量添加AIS数据
     */
    void addAisDataBatch(List<AisDataRequest> requests);

    /**
     * 根据ID查询AIS数据
     */
    AisData getAisDataById(Long id);

    /**
     * 分页查询AIS数据
     */
    PageResult<AisData> getAisDataPage(AisDataQueryRequest query);

    /**
     * 根据MMSI查询最新的AIS数据
     */
    AisData getLatestAisDataByMmsi(String mmsi);

    /**
     * 根据船舶ID查询最新的AIS数据
     */
    AisData getLatestAisDataByShipId(Long shipId);

    /**
     * 根据船舶ID查询轨迹数据
     */
    List<AisData> getShipTrack(Long shipId, String startTime, String endTime);

    /**
     * 根据MMSI查询轨迹数据
     */
    List<AisData> getShipTrackByMmsi(String mmsi, String startTime, String endTime);

    /**
     * 删除AIS数据
     */
    void deleteAisData(Long id);

    /**
     * 批量删除AIS数据
     */
    void deleteAisDataBatch(List<Long> ids);

    /**
     * 清理过期的AIS数据
     */
    void cleanExpiredData(int daysBefore);

    /**
     * 获取船舶实时位置
     */
    List<AisData> getRealTimePositions(List<Long> shipIds);
} 