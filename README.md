# UUSHOP 电商平台

## 📚 项目简介

UUSHOP 是一个基于微服务架构的电商平台，采用前后端分离设计，提供完整的买家购物端和卖家管理端功能。系统支持商品管理、订单处理、用户认证、数据统计等核心电商功能。

## 🛠 技术栈

### 后端技术
- **核心框架**: Spring Boot 3.5.13
- **微服务框架**: Spring Cloud 2025.0.0
- **分布式解决方案**: Spring Cloud Alibaba 2023.0.3.2
- **ORM框架**: MyBatis-Plus 3.5
- **数据库**: MySQL 8.0
- **缓存**: Redis
- **服务注册与发现**: Nacos
- **API网关**: Spring Cloud Gateway
- **认证**: JWT (JSON Web Token)
- **构建工具**: Maven

### 前端技术
- **核心框架**: Vue 3.5
- **构建工具**: Vite 8.0
- **状态管理**: Pinia 3.0
- **HTTP客户端**: Axios
- **卖家后台UI**: Element Plus 2.13
- **买家移动端UI**: Vant 4.9
- **数据可视化**: ECharts 6.0

### 开发环境
- **JDK**: Java 21
- **Node.js**: 18+
- **数据库**: MySQL 8.0
- **注册中心**: Nacos 2.x

## 🏗 项目架构

```
uushop/
├── account-service/          # 账户服务 - 用户认证与授权
├── product-service/         # 商品服务 - 商品和类目管理
├── order-service/           # 订单服务 - 订单流程处理
├── sms-service/             # 短信服务 - 短信验证码发送
├── gateway-service/         # API网关 - 统一路由入口
├── common-service/          # 公共模块 - 通用工具和异常处理
├── base-service/            # 基础模块 - 公共依赖
├── repository-service/      # 数据访问层 - MyBatis-Plus配置
├── buyer/                   # 买家前端 - 移动端购物应用
├── seller/                  # 卖家前端 - 后台管理系统
└── src/                     # 单体架构版本（备用）
```

## 📦 服务模块说明

### 1. Account Service (端口: 8082)
用户账户管理服务，提供用户注册、登录、Token验证等功能。

**核心功能**:
- 用户注册（手机号注册）
- 用户登录（JWT Token认证）
- Token合法性校验
- 密码MD5加密存储

**接口**:
```
POST /account-service/user/register
GET  /account-service/user/login
GET  /account-service/user/checkToken
```

### 2. Product Service (端口: 8081)
商品管理服务，负责商品和类目的CRUD操作。

**核心功能**:
- 商品类目管理
- 商品信息管理
- 库存管理
- 商品Excel导入导出
- 商品图片管理

**接口**:
```
# 买家接口
GET  /product-service/buyer/product/list
GET  /product-service/buyer/product/findById/{id}
GET  /product-service/buyer/product/findPriceById/{id}
PUT  /product-service/buyer/product/subStockById/{id}/{quantity}

# 卖家接口
GET  /product-service/seller/product/list
POST /product-service/seller/product/add
PUT  /product-service/seller/product/update
DELETE /product-service/seller/product/delete/{id}
POST /product-service/seller/product/import
GET  /product-service/seller/product/export
```

### 3. Order Service (端口: 8083)
订单管理服务，处理订单创建、查询、取消、完成等业务流程。

**核心功能**:
- 订单创建
- 订单查询（分页）
- 订单取消（自动恢复库存）
- 订单完成
- 订单支付
- WebSocket实时通知
- 销售数据统计（柱状图、折线图）

**接口**:
```
# 买家接口
POST /order-service/buyer/order/create
GET  /order-service/buyer/order/list/{buyerId}/{page}/{size}
GET  /order-service/buyer/order/detail/{buyerId}/{orderId}
PUT  /order-service/buyer/order/cancel/{buyerId}/{orderId}
PUT  /order-service/buyer/order/finish/{orderId}
PUT  /order-service/buyer/order/pay/{buyerId}/{orderId}

# 卖家接口
GET  /order-service/seller/order/list
GET  /order-service/seller/order/detail/{orderId}
PUT  /order-service/seller/order/ship/{orderId}
```

### 4. Gateway Service (端口: 8086)
API网关服务，提供统一的路由转发和跨域处理。

**功能**:
- 动态路由
- 全局跨域配置
- 请求负载均衡

**配置端口**:
- 买家前端: http://localhost:5175
- 卖家前端: http://localhost:5176

### 5. SMS Service (端口: 8085)
短信服务，提供短信验证码发送功能。

**功能**:
- 随机验证码生成
- 短信发送（需配置短信服务商）

## 🗄 数据库设计

### 表结构

#### 1. admin - 管理员表
| 字段 | 类型 | 说明 |
|------|------|------|
| admin_id | INT | 主键，自增 |
| username | VARCHAR(11) | 管理员账号 |
| password | VARCHAR(64) | 密码（加密） |
| img_url | VARCHAR(300) | 头像URL |
| name | VARCHAR(11) | 管理员名称 |

#### 2. user - 用户表
| 字段 | 类型 | 说明 |
|------|------|------|
| user_id | INT | 主键，自增 |
| mobile | VARCHAR(32) | 手机号 |
| password | VARCHAR(64) | 密码（MD5加密） |
| create_time | TIMESTAMP | 创建时间 |
| update_time | TIMESTAMP | 更新时间 |

#### 3. product_category - 商品类目表
| 字段 | 类型 | 说明 |
|------|------|------|
| category_id | INT | 主键，自增 |
| category_name | VARCHAR(64) | 类目名称 |
| category_type | INT | 类目编号（唯一） |
| create_time | TIMESTAMP | 创建时间 |
| update_time | TIMESTAMP | 更新时间 |

#### 4. product_info - 商品信息表
| 字段 | 类型 | 说明 |
|------|------|------|
| product_id | INT | 主键，自增 |
| product_name | VARCHAR(64) | 商品名称 |
| product_price | DECIMAL(8,2) | 商品单价 |
| product_stock | INT | 库存数量 |
| product_description | VARCHAR(64) | 商品描述 |
| product_icon | VARCHAR(512) | 商品图片URL |
| category_type | INT | 所属类目 |
| product_status | INT | 状态（1正常/0下架） |
| create_time | TIMESTAMP | 创建时间 |
| update_time | TIMESTAMP | 更新时间 |

#### 5. order_master - 订单主表
| 字段 | 类型 | 说明 |
|------|------|------|
| order_id | VARCHAR(32) | 订单ID（主键） |
| buyer_name | VARCHAR(32) | 买家姓名 |
| buyer_phone | VARCHAR(32) | 买家电话 |
| buyer_address | VARCHAR(128) | 买家地址 |
| buyer_openid | INT | 买家ID |
| order_amount | DECIMAL(8,2) | 订单总金额 |
| order_status | TINYINT | 订单状态（0新下单/1完成/2取消） |
| pay_status | TINYINT | 支付状态（0未支付/1已支付） |
| create_time | TIMESTAMP | 创建时间 |
| update_time | TIMESTAMP | 更新时间 |

#### 6. order_detail - 订单详情表
| 字段 | 类型 | 说明 |
|------|------|------|
| detail_id | VARCHAR(32) | 详情ID（主键） |
| order_id | VARCHAR(32) | 订单ID |
| product_id | INT | 商品ID |
| product_name | VARCHAR(64) | 商品名称 |
| product_price | DECIMAL(8,2) | 商品单价 |
| product_quantity | INT | 商品数量 |
| product_icon | VARCHAR(512) | 商品图片 |
| create_time | TIMESTAMP | 创建时间 |
| update_time | TIMESTAMP | 更新时间 |

#### 7. login_record - 登录记录表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT | 主键，自增 |
| userid | INT | 用户ID |
| ipaddress | VARCHAR(64) | IP地址 |
| username | VARCHAR(64) | 账户名 |
| create_time | TIMESTAMP | 创建时间 |

## 🚀 快速开始

### 环境要求

- JDK 21
- Node.js 18+
- Maven 3.6+
- MySQL 8.0
- Redis
- Nacos Server 2.x

### 1. 环境准备

#### 安装 MySQL
```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE uushop DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE uushop;

# 执行SQL脚本
SOURCE src/main/resources/uushop.sql;
```

#### 安装 Nacos
```bash
# 下载 Nacos
wget https://github.com/alibaba/nacos/releases/download/2.2.3/nacos-server-2.2.3.tar.gz

# 解压
tar -xvf nacos-server-2.2.3.tar.gz
cd nacos/bin

# 单机模式启动（Linux/Mac）
./startup.sh -m standalone

# Windows启动
startup.cmd -m standalone
```

#### 安装 Redis
```bash
# Windows安装（使用Docker或其他方式）
docker run -d --name redis -p 6379:6379 redis:latest

# 或下载Windows版本
# https://github.com/tporadowski/redis/releases
```

### 2. 后端启动

```bash
# 进入项目目录
cd d:\Javawork\uushop

# 启动所有后端服务（使用Maven）
mvn clean install

# 依次启动各服务（推荐按顺序启动）

# 1. 启动 Common Service（无端口，被其他服务依赖）
# 2. 启动 Base Service（无端口，被其他服务依赖）
# 3. 启动 Repository Service（无端口，被其他服务依赖）

# 4. 启动 Account Service
cd account-service
mvn spring-boot:run

# 5. 启动 Product Service
cd ../product-service
mvn spring-boot:run

# 6. 启动 Order Service
cd ../order-service
mvn spring-boot:run

# 7. 启动 SMS Service
cd ../sms-service
mvn spring-boot:run

# 8. 启动 Gateway Service
cd ../gateway-service
mvn spring-boot:run
```

或使用 IDE（如 IntelliJ IDEA）直接运行各服务的 `Main.java` 或 `*Application.java`。

### 3. 前端启动

#### 启动买家前端（移动端）
```bash
# 新开终端
cd buyer
npm install
npm run dev
```
访问地址: http://localhost:5175

#### 启动卖家前端（后台管理）
```bash
# 新开终端
cd seller
npm install
npm run dev
```
访问地址: http://localhost:5176

### 4. 配置文件

各服务的配置文件位于 `src/main/resources/application.yml`。

#### Product Service 配置示例
```yaml
server:
  port: 8081

spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/uushop
    username: root
    password: 123456
  application:
    name: product-service
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848

mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  mapper-locations: classpath*:/mapper/**/*.xml
```

## 🎯 功能特性

### 买家端功能
1. **用户模块**
   - 手机号注册
   - 用户登录（JWT认证）
   - Token自动续期

2. **商品浏览**
   - 商品分类展示
   - 商品搜索
   - 商品详情查看
   - 价格查询

3. **购物车**
   - 加入购物车
   - 数量修改
   - 删除商品
   - 价格计算

4. **订单管理**
   - 创建订单
   - 订单列表查询
   - 订单详情查看
   - 取消订单
   - 订单支付

### 卖家端功能
1. **登录认证**
   - 管理员登录
   - 权限验证

2. **商品管理**
   - 商品列表（分页）
   - 添加商品
   - 编辑商品
   - 删除商品
   - Excel批量导入
   - Excel导出

3. **订单管理**
   - 订单列表
   - 订单详情
   - 订单发货
   - WebSocket实时通知

4. **数据统计**
   - 总销量柱状图
   - 日销量折线图
   - 销量明细分析
   - ECharts可视化

## 📡 API 调用示例

### 用户注册
```bash
curl -X POST http://localhost:8086/account-service/user/register \
  -H "Content-Type: application/json" \
  -d '{
    "mobile": "13800138000",
    "password": "password123",
    "code": "666666"
  }'
```

### 用户登录
```bash
curl -X GET "http://localhost:8086/account-service/user/login?mobile=13800138000&password=password123"
```

### 创建订单
```bash
curl -X POST http://localhost:8086/order-service/buyer/order/create \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "name": "张三",
    "phone": "13800138000",
    "address": "北京市朝阳区xxx",
    "items": [
      {
        "productId": 1,
        "productQuantity": 2
      }
    ]
  }'
```

### 商品查询
```bash
curl -X GET http://localhost:8086/product-service/buyer/product/list
curl -X GET http://localhost:8086/product-service/buyer/product/findById/1
```

## 🛡 安全机制

### JWT认证
- 使用 JJWT 0.12.6 实现
- Token包含用户ID和手机号
- Token校验通过过滤器实现
- 密码使用MD5加盐加密存储

### 密码安全
- 使用盐值MD5加密
- 登录时验证加密后的密码
- 密码不在前端明文传输

### 接口安全
- 统一异常处理
- 参数校验
- SQL注入防护（MyBatis-Plus）

## 📊 数据统计

系统提供丰富的数据统计功能：

1. **柱状图** - 各类目商品总销量统计
2. **基础折线图** - 日销量趋势分析
3. **堆叠折线图** - 商品销量明细对比

使用 ECharts 6.0 实现数据可视化，支持图表导出。

## 🔧 常见问题

### 1. Nacos 连接失败
确保 Nacos 服务已启动，默认端口 8848。
检查 `application.yml` 中的 Nacos 配置地址是否正确。

### 2. 数据库连接失败
检查 MySQL 服务是否运行。
验证数据库用户名、密码是否正确。
确认数据库 `uushop` 已创建。

### 3. Redis 连接失败
确认 Redis 服务已启动。
检查 Redis 端口（默认6379）是否被占用。

### 4. 前端跨域问题
网关已配置 CORS，支持配置的源地址。
买家端: http://localhost:5175
卖家端: http://localhost:5176

### 5. 短信服务无法使用
短信服务需要配置实际的短信服务商（如阿里云短信）。
当前为模拟实现，验证码为 "666666"。

## 📝 开发指南

### 项目结构规范
- Controller: 处理请求和响应
- Service: 业务逻辑处理
- Mapper/DAO: 数据访问层
- Entity: 数据库实体
- VO: 视图对象
- Form: 表单数据对象
- Config: 配置类

### 代码风格
- 使用 `@Resource` 进行依赖注入
- 使用 MyBatis-Plus 的 CRUD 接口
- 使用 ResultVO 统一响应格式
- 使用自定义异常处理错误

### Git 提交规范
```
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式调整
refactor: 重构
test: 测试相关
chore: 构建或辅助工具变动
```

## 📄 许可证

本项目仅供学习交流使用。

## 👨‍💻 作者

UUshop Development Team

## 🙏 致谢

感谢以下开源项目：
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring Cloud](https://spring.io/projects/spring-cloud)
- [MyBatis-Plus](https://baomidou.com/)
- [Vue.js](https://vuejs.org/)
- [Element Plus](https://element-plus.org/)
- [Vant](https://vant-contrib.gitee.io/vant/)
- [ECharts](https://echarts.apache.org/)

---

如有问题或建议，请提交 Issue 或 Pull Request。
