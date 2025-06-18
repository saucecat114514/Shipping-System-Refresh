package com.shipping.service.impl;

import com.shipping.service.AisDataService;
import com.shipping.mapper.AisDataMapper;
import com.shipping.mapper.ShipMapper;
import com.shipping.model.entity.AisData;
import com.shipping.model.entity.Ship;
import com.shipping.model.dto.AisDataRequest;
import com.shipping.model.dto.AisDataQueryRequest;
import com.shipping.common.PageResult;
import com.shipping.exception.BusinessException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AIS数据服务实现类
 */
@Service
public class AisDataServiceImpl implements AisDataService {

    private static final Logger logger = LoggerFactory.getLogger(AisDataServiceImpl.class);

    @Autowired
    private AisDataMapper aisDataMapper;

    @Autowired
    private ShipMapper shipMapper;

    @Override
    @Transactional
    public void addAisData(AisDataRequest request) {
        logger.info("添加AIS数据：{}", request.getMmsi());
        
        // 验证船舶是否存在
        if (request.getShipId() != null) {
            Ship ship = shipMapper.selectById(request.getShipId());
            if (ship == null) {
                throw new BusinessException("船舶不存在");
            }
        }

        AisData aisData = convertToEntity(request);
        aisData.setCreatedAt(LocalDateTime.now());
        
        aisDataMapper.insert(aisData);
        logger.info("AIS数据添加成功，ID：{}", aisData.getId());
    }

    @Override
    @Transactional
    public void addAisDataBatch(List<AisDataRequest> requests) {
        logger.info("批量添加AIS数据，数量：{}", requests.size());
        
        List<AisData> aisDataList = requests.stream()
            .map(this::convertToEntity)
            .collect(Collectors.toList());
        
        LocalDateTime now = LocalDateTime.now();
        aisDataList.forEach(data -> data.setCreatedAt(now));
        
        aisDataMapper.insertBatch(aisDataList);
        logger.info("批量添加AIS数据成功");
    }

    @Override
    public AisData getAisDataById(Long id) {
        logger.info("查询AIS数据，ID：{}", id);
        AisData aisData = aisDataMapper.selectById(id);
        if (aisData == null) {
            throw new BusinessException("AIS数据不存在");
        }
        return aisData;
    }

    @Override
    public PageResult<AisData> getAisDataPage(AisDataQueryRequest query) {
        logger.info("分页查询AIS数据：页码={}，每页大小={}", query.getPage(), query.getSize());
        
        int offset = (query.getPage() - 1) * query.getSize();
        List<AisData> aisDataList = aisDataMapper.selectPage(query, offset, query.getSize());
        int total = aisDataMapper.selectCount(query);
        
        return new PageResult<AisData>((long) total, aisDataList, query.getPage(), query.getSize());
    }

    @Override
    public AisData getLatestAisDataByMmsi(String mmsi) {
        logger.info("查询最新AIS数据，MMSI：{}", mmsi);
        return aisDataMapper.selectLatestByMmsi(mmsi);
    }

    @Override
    public AisData getLatestAisDataByShipId(Long shipId) {
        logger.info("查询最新AIS数据，船舶ID：{}", shipId);
        return aisDataMapper.selectLatestByShipId(shipId);
    }

    @Override
    public List<AisData> getShipTrack(Long shipId, String startTime, String endTime) {
        logger.info("查询船舶轨迹，船舶ID：{}，时间范围：{} - {}", shipId, startTime, endTime);
        return aisDataMapper.selectTrackByShipId(shipId, startTime, endTime);
    }

    @Override
    public List<AisData> getShipTrackByMmsi(String mmsi, String startTime, String endTime) {
        logger.info("查询船舶轨迹，MMSI：{}，时间范围：{} - {}", mmsi, startTime, endTime);
        return aisDataMapper.selectTrackByMmsi(mmsi, startTime, endTime);
    }

    @Override
    @Transactional
    public void deleteAisData(Long id) {
        logger.info("删除AIS数据，ID：{}", id);
        
        AisData existing = aisDataMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("AIS数据不存在");
        }
        
        aisDataMapper.deleteById(id);
        logger.info("AIS数据删除成功");
    }

    @Override
    @Transactional
    public void deleteAisDataBatch(List<Long> ids) {
        logger.info("批量删除AIS数据，数量：{}", ids.size());
        aisDataMapper.deleteBatch(ids);
        logger.info("批量删除AIS数据成功");
    }

    @Override
    @Transactional
    public void cleanExpiredData(int daysBefore) {
        logger.info("清理过期AIS数据，保留最近{}天", daysBefore);
        
        LocalDateTime beforeTime = LocalDateTime.now().minusDays(daysBefore);
        String beforeTimeStr = beforeTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        aisDataMapper.deleteExpiredData(beforeTimeStr);
        logger.info("过期AIS数据清理完成");
    }

    @Override
    public List<AisData> getRealTimePositions(List<Long> shipIds) {
        logger.info("获取船舶实时位置，船舶数量：{}", shipIds.size());
        
        return shipIds.stream()
            .map(this::getLatestAisDataByShipId)
            .filter(data -> data != null)
            .collect(Collectors.toList());
    }

    /**
     * 转换DTO为实体
     */
    private AisData convertToEntity(AisDataRequest request) {
        AisData aisData = new AisData();
        BeanUtils.copyProperties(request, aisData);
        return aisData;
    }
} 