package com.shipping.mapper;

import com.shipping.model.entity.AisData;
import com.shipping.model.dto.AisDataQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * AIS数据Mapper接口
 */
@Mapper
public interface AisDataMapper {

    /**
     * 插入AIS数据
     */
    void insert(AisData aisData);

    /**
     * 批量插入AIS数据
     */
    void insertBatch(@Param("list") List<AisData> aisDataList);

    /**
     * 根据ID查询AIS数据
     */
    AisData selectById(Long id);

    /**
     * 分页查询AIS数据
     */
    List<AisData> selectPage(@Param("query") AisDataQueryRequest query, 
                            @Param("offset") int offset, 
                            @Param("limit") int limit);

    /**
     * 查询AIS数据总数
     */
    int selectCount(@Param("query") AisDataQueryRequest query);

    /**
     * 根据MMSI查询最新的AIS数据
     */
    AisData selectLatestByMmsi(@Param("mmsi") String mmsi);

    /**
     * 根据船舶ID查询最新的AIS数据
     */
    AisData selectLatestByShipId(@Param("shipId") Long shipId);

    /**
     * 根据船舶ID查询轨迹数据
     */
    List<AisData> selectTrackByShipId(@Param("shipId") Long shipId, 
                                     @Param("startTime") String startTime, 
                                     @Param("endTime") String endTime);

    /**
     * 根据MMSI查询轨迹数据
     */
    List<AisData> selectTrackByMmsi(@Param("mmsi") String mmsi, 
                                   @Param("startTime") String startTime, 
                                   @Param("endTime") String endTime);

    /**
     * 根据ID删除AIS数据
     */
    void deleteById(Long id);

    /**
     * 批量删除AIS数据
     */
    void deleteBatch(@Param("ids") List<Long> ids);

    /**
     * 清理过期的AIS数据
     */
    void deleteExpiredData(@Param("beforeTime") String beforeTime);
} 