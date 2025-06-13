# 航运管理系统 (Shipping Management System)

## 项目简介

航运管理系统是一个现代化的航运业务管理平台，采用前后端分离架构，旨在提高航运业务的数字化管理水平。系统集成了用户管理、港口管理、船舶管理、航线管理、航次管理、订单管理等核心功能模块，并支持AIS船舶动态监控和地图可视化。

## 技术架构

### 后端技术栈
- **Java 17** - 开发语言
- **Spring Boot 3.2.0** - 核心框架
- **MyBatis 3.0.3** - 数据持久化框架
- **Spring Security** - 安全框架
- **JWT** - 身份认证
- **MySQL** - 数据库
- **Maven** - 项目管理工具

### 前端技术栈
- **Vue 3** - 前端框架
- **TypeScript** - 开发语言
- **Vite 5** - 构建工具
- **Element Plus** - UI组件库
- **Vue Router 4** - 路由管理
- **Pinia** - 状态管理
- **Axios** - HTTP客户端
- **ECharts** - 数据可视化
- **高德地图API** - 地图服务

## 项目结构

```
shipping-system/
├── backend/                    # Spring Boot后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/shipping/
│   │   │   │   ├── common/        # 通用工具类
│   │   │   │   ├── config/        # 配置类
│   │   │   │   ├── controller/    # 控制器层
│   │   │   │   ├── service/       # 业务逻辑层
│   │   │   │   ├── mapper/        # 数据访问层
│   │   │   │   ├── model/         # 实体和DTO类
│   │   │   │   └── ShippingApplication.java
│   │   │   └── resources/
│   │   │       ├── application-dev.yml
│   │   │       ├── schema.sql
│   │   │       └── mapper/        # MyBatis映射文件
│   │   └── test/
│   └── pom.xml
├── frontend/                   # Vue前端项目
│   ├── src/
│   │   ├── api/               # API接口
│   │   ├── components/        # 通用组件
│   │   ├── views/             # 页面组件
│   │   ├── router/            # 路由配置
│   │   ├── stores/            # 状态管理
│   │   ├── utils/             # 工具函数
│   │   ├── types/             # TypeScript类型定义
│   │   ├── styles/            # 样式文件
│   │   └── main.ts
│   ├── package.json
│   ├── vite.config.ts
│   ├── tsconfig.json
│   └── index.html
├── pom.xml                     # 父项目Maven配置
└── README.md
```

## 系统功能模块

### 1. 用户管理模块
- 用户注册、登录、权限控制
- 角色管理：管理员、调度员、客户
- 用户信息维护

### 2. 港口管理模块
- 港口信息维护（中英文名称、代码、坐标）
- 港口查询和搜索
- 地理位置可视化

### 3. 船舶管理模块
- 船舶档案管理（基本信息、MMSI、IMO等）
- 船舶状态跟踪
- AIS数据集成预留

### 4. 航线管理模块
- 航线规划和维护
- 自动距离计算（基于高德地图API）
- 航线可视化展示

### 5. 航次管理模块
- 航次计划制定
- 航次状态跟踪
- 船舶调度管理

### 6. 订单管理模块
- 货运订单创建和处理
- 自动运价计算
- 订单状态跟踪
- 附加费用管理

### 7. AIS动态监控模块
- 船舶实时位置查询
- 历史轨迹回放（规划中）
- 动态数据存储和处理

### 8. 地图集成模块
- 高德地图API集成
- 港口和航线可视化
- 船舶位置标注
- 距离计算服务

## 环境要求

### 开发环境
- **JDK 17+**
- **Node.js 16+**
- **MySQL 8.0+**
- **Maven 3.8+**

### 推荐工具
- **IDE**: IntelliJ IDEA / VS Code
- **数据库管理**: MySQL Workbench / Navicat
- **API测试**: Postman / Apifox

## 快速开始

### 1. 数据库准备
```sql
-- 创建数据库
CREATE DATABASE shipping_system DEFAULT CHARACTER SET utf8mb4;

-- 导入数据库结构
mysql -u root -p shipping_system < backend/src/main/resources/schema.sql
```

### 2. 后端启动
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### 3. 前端启动
```bash
cd frontend
npm install
npm run dev
```

### 4. 访问系统
- 前端地址：http://localhost:3000
- 后端API：http://localhost:8080/api
- API文档：http://localhost:8080/api/swagger-ui.html

## 配置说明

### 后端配置
在 `backend/src/main/resources/application-dev.yml` 中配置：
- 数据库连接信息
- 高德地图API密钥
- JWT密钥等

### 前端配置
在 `frontend/vite.config.ts` 中配置：
- API代理地址
- 构建输出目录等

## 开发规范

### 后端开发规范
- 遵循RESTful API设计原则
- 使用统一的返回格式
- 采用三层架构：Controller -> Service -> Mapper
- 使用@Valid进行参数校验
- 统一异常处理

### 前端开发规范
- 使用TypeScript编写
- 组件化开发
- 统一的API封装
- 响应式设计
- 国际化支持预留

## 部署说明

### 生产环境部署
1. 修改配置文件中的数据库连接等信息
2. 后端打包：`mvn clean package`
3. 前端打包：`npm run build`
4. 部署到服务器

### Docker部署（规划中）
- 提供Docker Compose配置
- 支持一键部署
- 包含数据库初始化

## 项目特色

1. **现代化架构**：采用最新的Spring Boot 3和Vue 3技术栈
2. **专业化设计**：针对航运业务特点定制的功能模块
3. **可视化支持**：集成地图服务和图表展示
4. **扩展性良好**：支持AIS数据接入和第三方平台对接
5. **文档完善**：提供详细的API文档和开发指南

## 贡献指南

1. Fork 项目
2. 创建特性分支
3. 提交变更
4. 推送到分支
5. 创建 Pull Request

## 许可证

本项目仅供学习和研究使用。

## 联系方式

如有问题或建议，请通过Issue或邮件联系。 