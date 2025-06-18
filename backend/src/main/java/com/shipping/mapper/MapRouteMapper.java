package com.shipping.mapper;

import com.shipping.model.entity.MapRoute;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 地图路径数据访问层接口
 */
@Mapper
public interface MapRouteMapper {

    /**
     * 插入路径
     */
    int insert(MapRoute route);

    /**
     * 根据ID查询路径
     */
    @Select("SELECT * FROM map_routes WHERE id = #{id}")
    MapRoute selectById(@Param("id") Long id);

    /**
     * 查询所有路径
     */
    @Select("SELECT * FROM map_routes ORDER BY created_at DESC")
    List<MapRoute> selectAll();

    /**
     * 根据ID删除路径
     */
    int deleteById(@Param("id") Long id);

    /**
     * 更新路径
     */
    int update(MapRoute route);

    /**
     * 根据创建人查询路径
     */
    @Select("SELECT * FROM map_routes WHERE created_by = #{createdBy} ORDER BY created_at DESC")
    List<MapRoute> selectByCreatedBy(@Param("createdBy") String createdBy);

    /**
     * 根据车辆类型查询路径
     */
    @Select("SELECT * FROM map_routes WHERE vehicle_type = #{vehicleType} ORDER BY created_at DESC")
    List<MapRoute> selectByVehicleType(@Param("vehicleType") String vehicleType);

    /**
     * 根据状态查询路径
     */
    @Select("SELECT * FROM map_routes WHERE status = #{status} ORDER BY created_at DESC")
    List<MapRoute> selectByStatus(@Param("status") String status);
} 