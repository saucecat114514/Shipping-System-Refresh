package com.shipping.mapper;

import com.shipping.model.entity.Ship;
import com.shipping.model.dto.ShipQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 船舶Mapper接口
 */
@Mapper
public interface ShipMapper {

    /**
     * 插入船舶信息
     * @param ship 船舶实体
     * @return 影响行数
     */
    int insert(Ship ship);

    /**
     * 根据ID删除船舶
     * @param id 船舶ID
     * @return 影响行数
     */
    int deleteById(Long id);

    /**
     * 更新船舶信息
     * @param ship 船舶实体
     * @return 影响行数
     */
    int updateById(Ship ship);

    /**
     * 根据ID查询船舶
     * @param id 船舶ID
     * @return 船舶实体
     */
    Ship selectById(Long id);

    /**
     * 根据MMSI查询船舶
     * @param mmsi MMSI
     * @return 船舶实体
     */
    Ship selectByMmsi(String mmsi);

    /**
     * 根据IMO编号查询船舶
     * @param imoNumber IMO编号
     * @return 船舶实体
     */
    Ship selectByImoNumber(String imoNumber);

    /**
     * 分页查询船舶列表
     * @param queryRequest 查询请求参数
     * @return 船舶列表
     */
    List<Ship> selectPageList(ShipQueryRequest queryRequest);

    /**
     * 查询船舶总数
     * @param queryRequest 查询请求参数
     * @return 总数
     */
    long selectTotal(ShipQueryRequest queryRequest);

    /**
     * 查询所有船舶（用于下拉框等）
     * @return 船舶列表
     */
    List<Ship> selectAll();

    /**
     * 检查MMSI是否存在
     * @param mmsi MMSI
     * @param excludeId 排除的ID（用于更新时检查）
     * @return 是否存在
     */
    boolean existsByMmsi(@Param("mmsi") String mmsi, @Param("excludeId") Long excludeId);

    /**
     * 检查IMO编号是否存在
     * @param imoNumber IMO编号
     * @param excludeId 排除的ID（用于更新时检查）
     * @return 是否存在
     */
    boolean existsByImoNumber(@Param("imoNumber") String imoNumber, @Param("excludeId") Long excludeId);
} 