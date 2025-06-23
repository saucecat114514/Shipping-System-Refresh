package com.shipping.common;

/**
 * 角色权限配置
 * 定义系统中三个角色的权限范围
 */
public class RolePermission {

    /**
     * 角色枚举
     */
    public enum Role {
        ADMIN("ADMIN", "系统管理员"),
        DISPATCHER("DISPATCHER", "航运调度员"),
        CUSTOMER("CUSTOMER", "客户");

        private final String code;
        private final String name;

        Role(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * 权限常量
     */
    public static class Permission {
        // 系统管理员权限
        public static final String[] ADMIN_ALL = {"ADMIN"};
        
        // 航运调度员权限
        public static final String[] ADMIN_DISPATCHER = {"ADMIN", "DISPATCHER"};
        
        // 客户权限（只读）
        public static final String[] ALL_ROLES = {"ADMIN", "DISPATCHER", "CUSTOMER"};
        
        // 特定模块权限
        public static final String[] USER_MANAGEMENT = {"ADMIN"};
        public static final String[] SYSTEM_CONFIG = {"ADMIN"};
        public static final String[] PORT_MANAGEMENT = {"ADMIN", "DISPATCHER"};
        public static final String[] SHIP_MANAGEMENT = {"ADMIN", "DISPATCHER"};
        public static final String[] ROUTE_MANAGEMENT = {"ADMIN", "DISPATCHER"};
        public static final String[] VOYAGE_MANAGEMENT = {"ADMIN", "DISPATCHER"};
        public static final String[] ORDER_MANAGEMENT = {"ADMIN", "DISPATCHER"};
        public static final String[] ORDER_VIEW = {"ADMIN", "DISPATCHER", "CUSTOMER"};
    }

    /**
     * 检查角色是否有权限
     */
    public static boolean hasPermission(String userRole, String[] allowedRoles) {
        if (userRole == null || allowedRoles == null) {
            return false;
        }
        
        for (String role : allowedRoles) {
            if (role.equals(userRole)) {
                return true;
            }
        }
        return false;
    }
} 