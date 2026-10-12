<div align="center">

# 🐱 HZSCA · 湖州流浪猫救助与领养平台

**一个面向流浪猫救助公益场景的「微信小程序 + 管理后台 + 后端服务」三端一体化平台**

用技术连接救助者、领养人与流浪猫，让每一次相遇都有依据。

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.12-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.4-4FC08D?logo=vuedotjs&logoColor=white)](https://vuejs.org/)
[![Vite](https://img.shields.io/badge/Vite-5.1-646CFF?logo=vite&logoColor=white)](https://vitejs.dev/)
[![WeChat MiniProgram](https://img.shields.io/badge/微信小程序-原生-07C160?logo=wechat&logoColor=white)](#)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-7.x-DC382D?logo=redis&logoColor=white)](https://redis.io/)
[![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)](#)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

</div>

---

## 📖 项目简介

**HZSCA（Huzhou Stray Cat Association）** 是一个面向流浪猫救助公益组织的综合管理平台，也是本人的本科毕业设计作品。项目围绕流浪猫救助的真实业务链路，解决三个核心问题：

- **救助侧**：志愿者与热心市民可以上报流浪猫线索（含位置、紧急度、图片），由管理员统一调度跟进；
- **领养侧**：用户填写领养偏好问卷后，系统基于**多维度加权匹配算法**推荐适配的猫咪，并对领养申请进行**风险评分**辅助审核；
- **运营侧**：管理员通过后台完成猫咪档案、猫咪特征、活动、志愿者审核、领养申请审核、救助上报处理与数据导出。

平台采用**一套后端、两个前端**的形态：微信小程序承载 C 端用户体验，Vue 管理后台承载 B 端运营工作台。

> 💡 **设计亮点**：不是简单的「信息展示 + 增删改查」，而是把**推荐算法**与**风险评估**引入领养决策，让「谁适合养哪只猫」这件事有量化依据。

---

## 🖼️ 界面预览

### 用户端 · 微信小程序（H5 版界面）

<div align="center">

| 首页 · 待领养猫咪 | 领养列表 | 猫咪详情 |
| :---: | :---: | :---: |
| ![首页](docs/images/01-home.webp) | ![领养列表](docs/images/02-adopt-list.webp) | ![猫咪详情](docs/images/03-cat-detail.webp) |

| 活动列表 | 全部活动 | 捐助我们 |
| :---: | :---: | :---: |
| ![活动](docs/images/04-activity.webp) | ![全部活动](docs/images/05-activity-all.webp) | ![捐助](docs/images/06-donate.webp) |

| 工作报告 | 账号登录 |
| :---: | :---: |
| ![工作报告](docs/images/07-work-report.webp) | ![登录](docs/images/08-login.webp) |

</div>

### 💰 爱心捐助 · 支付宝支付流程

<div align="center">

| 捐助页（金额选择 / Mock 模式提示） | 支付结果（处理中） | 支付结果（成功） |
| :---: | :---: | :---: |
| ![捐助页](docs/images/20-donate.webp) | ![支付处理中](docs/images/21-payresult-pending.webp) | ![支付成功](docs/images/22-payresult-success.webp) |

</div>

### 管理端 · Vue 运营后台

<div align="center">

| 数据仪表盘（ECharts 可视化） | 猫咪档案管理 |
| :---: | :---: |
| ![仪表盘](docs/images/10-admin-dashboard.webp) | ![猫咪管理](docs/images/11-admin-cats.webp) |

| 猫咪特征标签管理 | 领养申请审核 |
| :---: | :---: |
| ![猫咪特征](docs/images/12-admin-cat-traits.webp) | ![领养申请](docs/images/13-admin-adoptions.webp) |

| 志愿者审核 | 救助上报处理 |
| :---: | :---: |
| ![志愿者审核](docs/images/14-admin-volunteers.webp) | ![救助上报](docs/images/15-admin-rescues.webp) |

| 实验数据导出 | 用户管理 |
| :---: | :---: |
| ![数据导出](docs/images/16-admin-dataset.webp) | ![用户管理](docs/images/17-admin-users.webp) |

</div>

---

## ✨ 核心功能

### 🧭 用户端（微信小程序 · 16 个页面）

| 模块 | 功能说明 |
|------|---------|
| 微信登录 | 通过 `wx.login` 换取 code，后端调用微信 `jscode2session` 完成静默登录并签发 JWT |
| 猫咪浏览 | 首页推荐位 + 领养列表 + 猫咪详情（品种、年龄、性别、健康状况、图片） |
| **智能匹配** | 填写领养偏好档案后，一键获取按匹配度排序的推荐猫咪及推荐理由 |
| 领养申请 | 填写家庭情况问卷提交申请，系统自动生成风险评分；支持查看申请进度与撤销 |
| 活动报名 | 浏览救助活动，一键报名参与（需审核） |
| 志愿者申请 | 提交志愿者申请，等待管理员审核 |
| 救助上报 | 上报流浪猫线索：定位、地址、描述、图片、紧急度 |
| 我的 | 个人资料、我的申请、领养档案维护 |

### 🖥️ 管理端（Vue 3 后台 · 24 个视图）

| 模块 | 功能说明 |
|------|---------|
| 数据仪表盘 | 领养申请总数/待审核/已通过/已拒绝 KPI，申请状态分布环形图、月度趋势柱状图（ECharts） |
| 猫咪管理 | 猫咪档案增删改查 + 图片上传 |
| 猫咪特征管理 | 维护每只猫的性格、活动量、亲人度、掉毛等级、是否适合儿童/宠物等 11 维特征标签 |
| 活动管理 | 活动发布、编辑、删除、封面图上传 |
| 志愿者审核 | 志愿者申请审核、活动报名审核 |
| 领养申请审核 | 查看风险评分与理由，通过 / 驳回并记录审核意见 |
| 救助上报管理 | 查看上报线索、指派处理人、关闭工单 |
| 数据导出 | 领养数据与用户行为数据查询导出（用于实验分析） |
| 用户管理 | 用户列表、新增、编辑、删除 |

### 💰 捐助与支付（支付宝沙箱）

| 能力 | 说明 |
|------|------|
| 捐助下单 | 预设 ¥10/30/50/100 或自定义金额，可填写留言，金额以「分」存储避免浮点误差 |
| 支付宝沙箱支付 | 通过 `alipay.trade.page.pay` 生成收银台跳转地址，签名采用 **RSA2（JDK 内置实现，零第三方 SDK）** |
| 异步通知 | `notify_url` 回调**验签 + 金额校验 + 幂等置位**，重复通知不会重复处理 |
| 主动查单 | `alipay.trade.query` 兜底同步；结果页每 3 秒轮询、最长 60 秒，超时后支持手动刷新 |
| Mock 降级 | 未配置密钥时自动进入 Mock 演示模式，无需支付宝账号即可演示完整「下单 → 支付 → 结果」链路 |
| 安全约束 | 订单归属校验（防越权查他人订单）、回调接口匿名放行、商户私钥不入库不入前端 |

> 完整接入流程（沙箱申请 → 密钥生成 → 回调调试 → 内网穿透 → 常见问题）见 [`docs/支付接入指南.md`](docs/支付接入指南.md)。

### ⚙️ 公共能力

- **JWT 无状态鉴权**：统一签发、`Bearer` 头 / `access_token` 双通道解析，支持 `web` / `wx` / `admin` 三种主体类型；
- **角色权限模型**：`user` / `volunteer` / `admin` 三级角色，接口级权限收敛；
- **口令安全**：BCrypt 加盐哈希存储，兼容历史明文并支持首次登录自动迁移；
- **统一响应体**：`Result{code, msg, data}` 全站统一；
- **接口文档**：SpringDoc OpenAPI 自动生成 Swagger UI；
- **文件服务**：活动/猫咪图片上传，扩展名白名单校验，静态资源映射访问；
- **缓存**：Redis 支持（Lettuce 连接池）。

---

## 🏗️ 系统架构

```mermaid
graph TB
    subgraph Client["🖥️ 客户端层"]
        MP["微信小程序<br/>(原生 · 16 页面)"]
        WEB["Vue 3 管理后台<br/>(Element Plus + ECharts)"]
    end

    subgraph Gateway["🌐 接入层"]
        NGINX["HTTP / JSON<br/>CORS + JWT 鉴权过滤器"]
    end

    subgraph Server["⚙️ 应用层 · Spring Boot 3"]
        CTRL["Controller 层<br/>21 个 REST 控制器"]
        SEC["Security 层<br/>JwtService / Filter / SecurityUtils"]
        SVC["Service 层<br/>业务逻辑 + 算法"]
        MAPPER["Mapper 层<br/>MyBatis + XML"]
        ALGO["核心算法<br/>智能匹配 / 风险评分"]
    end

    subgraph Data["🗄️ 数据层"]
        MYSQL[("MySQL 8<br/>17 张业务表")]
        REDIS[("Redis<br/>缓存 / 会话")]
        FS[("本地文件系统<br/>图片上传")]
    end

    MP --> NGINX
    WEB --> NGINX
    NGINX --> SEC --> CTRL
    CTRL --> SVC
    SVC --> ALGO
    SVC --> MAPPER
    MAPPER --> MYSQL
    SVC --> REDIS
    SVC --> FS

    style Client fill:#FDF6EC,stroke:#D9A86C,color:#4A3728
    style Gateway fill:#F5EFE6,stroke:#D9A86C,color:#4A3728
    style Server fill:#EAF4EE,stroke:#6B9E7A,color:#2C3E33
    style Data fill:#EAF0F7,stroke:#7A94B8,color:#2C3E4A
```

### 请求鉴权流程

```mermaid
sequenceDiagram
    participant C as 客户端
    participant F as JwtAuthenticationFilter
    participant S as SecurityFilterChain
    participant API as Controller

    C->>F: 携带 Authorization: Bearer <token>
    F->>F: 解析 JWT（校验签名 + issuer + 过期）
    alt Token 有效
        F->>F: 注入 AuthPrincipal(id, type) + ROLE_xxx
        F->>S: 放行
        S->>S: 匹配接口权限规则
        alt 权限满足
            S->>API: 执行业务
            API-->>C: Result{code:200, data}
        else 权限不足
            S-->>C: 403 Forbidden
        end
    else Token 无效/缺失
        F->>S: 匿名放行
        S-->>C: 401 / 403
    end
```

---

## 🛠️ 技术栈

### 后端 `hzsca-springboot`

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 3.3.12 | 应用框架 |
| Java | 17 | 语言版本 |
| Spring Security | 6.x | 认证授权 |
| JJWT | 0.12.6 | JWT 签发与校验（HS384） |
| MyBatis | 3.0.4 | ORM 持久层 |
| MySQL Connector/J | 8.0.33 | 数据库驱动 |
| Spring Data Redis | — | 缓存（Lettuce 连接池） |
| PageHelper | 1.4.6 | 物理分页 |
| SpringDoc OpenAPI | 2.3.0 | 接口文档 |
| Lombok | — | 简化实体代码 |
| **支付宝沙箱** | RSA2（JDK 内置） | **零第三方 SDK**，手写签名与验签 |

### 管理后台 `hzsca-vue`

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | 3.4.15 | 前端框架（组合式 API） |
| Vite | 5.1 | 构建工具 |
| Vue Router | 4.2.5 | 路由 + 守卫 |
| Element Plus | 2.5.6 | UI 组件库 |
| ECharts | 5.5.1 | 数据可视化 |
| axios | 1.6.7 | HTTP 请求封装 |
| wangEditor | 5.1.23 | 富文本编辑器 |
| Sass | 1.71 | 样式预处理 |

### 微信小程序 `hzsca-miniapp`

| 技术 | 说明 |
|------|------|
| 原生小程序框架 | 16 个页面，自定义 `custom-tab-bar` |
| 统一请求封装 | `utils/request.js` 自动注入 token 与 `access_token` |
| 集中配置 | `utils/config.js` 管理后端地址，便于真机部署切换 |

---

## 📁 目录结构

```
HZSCA-WEB/
├── hzsca-springboot/hzsca/          # 🔧 后端服务（Spring Boot）
│   ├── src/main/java/com/hzsca/
│   │   ├── common/                  #   统一响应 Result、跨域配置
│   │   ├── config/                  #   数据库自举、口令迁移、支付宝配置、静态资源、Redis
│   │   ├── controller/              #   22 个 REST 控制器（含 PaymentController）
│   │   ├── entity/                  #   实体 / DTO
│   │   ├── exception/               #   全局异常处理
│   │   ├── mapper/                  #   MyBatis Mapper 接口
│   │   ├── security/                #   JWT 与 Spring Security
│   │   ├── service/                 #   业务服务、匹配算法、风险评分、支付服务
│   │   └── utils/                   #   工具类（RedisUtil、AlipaySignUtils）
│   ├── src/main/resources/
│   │   ├── db/                      #   schema.sql 建表 / 增量脚本 / 种子数据 / payment.sql
│   │   ├── mapper/                  #   MyBatis XML 映射
│   │   ├── static/                  #   猫咪与活动静态图片
│   │   └── application.yml          #   应用配置（支持环境变量覆盖）
│   └── src/test/java/com/hzsca/     #   ✅ 单元测试（匹配算法 19 例 + 风险评分 22 例）
│
├── hzsca-vue/hzsca/                 # 🖥️ 管理后台（Vue 3 + Vite）
│   └── src/
│       ├── views/                   #   前台页面 + admin/ 后台页面（25 个）
│       ├── router/                  #   路由与权限守卫
│       ├── utils/request.js         #   axios 封装与拦截器
│       └── config/config.js         #   接口地址配置
│
├── hzsca-miniapp/                   # 📱 微信小程序（原生）
│   ├── pages/                       #   16 个业务页面
│   ├── custom-tab-bar/              #   自定义底部导航
│   └── utils/                       #   请求封装与配置
│
├── docs/                            # 📚 文档
│   ├── images/                      #   README 界面截图（20 张）
│   ├── 部署指南.md                   #   环境搭建 / 数据库初始化 / 检查清单
│   └── 支付接入指南.md               #   支付宝沙箱接入全流程
├── deliverables/gstack/             # 📋 毕业设计归档评审报告
├── docker-compose.yml               # 🐳 一键启动 MySQL + Redis
├── .env.example                     # 🔑 环境变量示例
├── 毕业设计论文.md                   # 📄 毕业论文（Markdown 版）
├── start-backend.bat                # ▶️ 后端一键启动脚本
└── LICENSE                          # Apache License 2.0
```

---

## 🚀 快速开始

### 环境要求

| 依赖 | 版本要求 |
|------|---------|
| JDK | 17+ |
| MySQL | 8.0+ |
| Redis | 6.x / 7.x |
| Node.js | 18+ |
| 微信开发者工具 | 最新稳定版 |

### 1️⃣ 启动依赖服务（MySQL + Redis）

**方式 A：Docker Compose（推荐）**

```bash
docker compose up -d        # 启动 MySQL 8 + Redis 7，参数与 application.yml 默认值一致
docker compose ps           # 等待两个服务变为 healthy
```

**方式 B：手动安装** — 参见 [`docs/部署指南.md`](docs/部署指南.md)（含 Windows / macOS / Linux 三种平台的安装步骤）。

### 2️⃣ 初始化数据库

```bash
# 只需创建一个空库，表结构由应用启动时自动创建
mysql -uroot -p -e "CREATE DATABASE IF NOT EXISTS hzsca DEFAULT CHARACTER SET utf8mb4;"
```

启动后端时 `DbBootstrap` 会**自动完成**：

| 步骤 | 说明 |
|------|------|
| 建表 | 检测核心表缺失 → 执行 `db/schema.sql`（17 张表，全部 `CREATE TABLE IF NOT EXISTS`，**不会删改已有数据**） |
| 增量表 | 补齐匹配算法表、志愿者表（`matching.sql` / `volunteer.sql`） |
| 种子数据 | 写入猫咪特征、演示申请/活动/救助数据 |
| 管理员 | `admins` 表为空时创建演示账号 **`admin / admin123456`** |
| 支付表 | 创建 `payment_order`（`db/payment.sql`） |
| 口令迁移 | 把残留的**明文口令**统一升级为 BCrypt（幂等，可重复执行） |

> 演示网页用户：`user1` ~ `user10`，口令均为 `123456`。

### 3️⃣ 启动后端

```bash
cd hzsca-springboot/hzsca

# Windows 一键启动（自动注入 JWT 密钥等环境变量）
../../start-backend.bat

# 或手动启动
export JWT_SECRET="请替换为不少于32字节的随机密钥"
export WECHAT_APPID="你的小程序AppID"
export WECHAT_SECRET="你的小程序AppSecret"
export DB_USERNAME=root
export DB_PASSWORD=你的数据库密码
./mvnw spring-boot:run
```

后端启动后：
- 接口地址：`http://localhost:8080`
- 接口文档：`http://localhost:8080/swagger-ui.html`

### 4️⃣ 启动管理后台

```bash
cd hzsca-vue/hzsca
npm install
npm run dev          # 开发模式，默认 http://localhost:5173
npm run build        # 生产构建
```

> 若后端地址非 `localhost:8080`，请修改 `src/config/config.js` 中的 `baseURL` 与 `imageBaseURL`。

### 5️⃣ 运行微信小程序

1. 用微信开发者工具导入 `hzsca-miniapp` 目录；
2. 修改 `utils/config.js` 中的 `BASE_URL` 为你的后端地址；
3. 本地调试时勾选「详情 → 本地设置 → 不校验合法域名」；
4. 真机预览 / 上线需将 `BASE_URL` 替换为已备案的 **HTTPS 合法域名**。

---

## 🧠 核心算法

### 1. 猫咪智能匹配算法

> 实现位置：`service/impl/MatchServiceImpl.java`

采用**「硬性排除 + 多维加权打分」**两阶段模型：

**第一阶段 · 硬性排除（一票否决）**

| 条件 | 排除规则 |
|------|---------|
| 用户过敏 且 猫咪掉毛等级 > 用户容忍上限 | 排除 |
| 家中有孩子 且 猫咪不适合与儿童相处 | 排除 |
| 家中有其他宠物 且 猫咪不适合与宠物共处 | 排除 |
| 用户不接受特殊照护 且 猫咪需要特殊照护 | 排除 |
| 居住空间等级 < 猫咪空间需求等级 | 排除 |

**第二阶段 · 六维加权打分**

| 维度 | 权重（默认，可由 `match_weights` 表动态覆盖） | 计算方式 |
|------|------|---------|
| 活动量匹配 | 1.0 | 依据每日陪伴时长映射用户活跃度，与猫咪活跃度做归一化差值 |
| 亲人度匹配 | 0.6 | 按用户偏好（黏人 / 独立 / 不限）对亲人度做正向或反向映射 |
| 掉毛容忍度 | 1.2 | 掉毛等级低于容忍上限得满分，超出按比例衰减 |
| 空间需求 | 1.0 | 居住空间满足需求得满分，不足按差值衰减 |
| 新手友好度 | 1.0 | 零经验用户 × 非新手友好猫 → 0 分 |
| 作息匹配 | 1.0 | 夜班用户 × 夜行猫 得满分，否则 0.6 |

最终得分：`score = Σ(权重ᵢ × 匹配度ᵢ) / Σ权重ᵢ × 100`，按得分降序返回 Top-N（默认 10，上限 50），并为高分/低分维度生成「优势 / 需磨合」的推荐理由。

### 2. 领养风险评分算法

> 实现位置：`service/impl/RiskScoringServiceImpl.java`

以 100 分为基准，按问卷项加减分，输出 `LOW / MEDIUM / HIGH` 三档风险：

| 因素 | 分值变化 |
|------|---------|
| 家庭成员过敏 | −25 |
| 家庭成员哮喘 | −15 |
| 家中有人吸烟 | −10 |
| 工作时长 ≥ 12h / ≥ 10h | −12 / −8 |
| 工作时长 ≤ 6h | +3 |
| 家庭月收入 < 3000 / ≥ 8000 | −10 / +5 |
| 有养宠经验 / 无经验 | +2 / −5 |
| 接受定期回访 / 拒绝回访 | +5 / −8 |

评分归一化到 `[0, 100]`：`≥80 → LOW`，`60~79 → MEDIUM`，`<60 → HIGH`，并保留每一项的加减分明细作为审核依据。

---

## 🔌 接口概览

| 分组 | 路径前缀 | 权限 |
|------|---------|------|
| 网页用户认证 | `/api/auth/**` | 公开 |
| 管理员认证 | `/api/admin/login` | 公开 |
| 微信小程序登录 | `/api/wx/login` | 公开 |
| 猫咪浏览 | `GET /api/cats/**` | 公开 |
| 活动浏览 | `GET /api/activity/**` | 公开 |
| 领养申请 / 档案 | `/api/adoption-requests`、`/api/adoption-profile` | 登录用户 |
| 智能匹配推荐 | `/api/match/recommendations` | 登录用户 |
| 救助上报 | `/api/rescue-reports` | 登录用户 |
| 志愿者申请 | `/api/volunteer/apply` | 登录用户 |
| 用户资料 | `/api/users/current`、`/api/users/update` | 本人或管理员 |
| 捐助支付 | `/api/payment/config` | 公开 |
| 捐助支付 | `/api/payment/orders`、`/orders/{no}`、`/orders/{no}/sync`、`/orders/{no}/mock/pay` | 登录用户（限本人订单） |
| 支付宝回调 | `/api/payment/alipay/notify`、`/api/payment/alipay/return` | 公开（验签保护） |
| 猫咪/活动写操作 | `POST/PUT/DELETE /api/cats/**`、`/api/activity/add\|update\|delete\|upload` | 仅管理员 |
| 后台管理 | `/api/admin/**` | 仅管理员 |

> 完整接口清单可在启动后访问 Swagger UI 查看：`http://localhost:8080/swagger-ui.html`

---

## 🗄️ 数据库设计（18 张表）

| 分类 | 表名 | 说明 |
|------|------|------|
| 用户 | `wx_user` | 微信小程序用户 |
| 用户 | `users` | 网页注册用户 |
| 用户 | `admins` | 管理员账号 |
| 用户 | `adopters` | 领养人档案 |
| 猫咪 | `animals` | 猫咪基础档案 |
| 猫咪 | `cat_traits` | 猫咪 11 维特征标签 |
| 领养 | `user_adoption_profile` | 用户领养偏好档案 |
| 领养 | `adoption_request` | 领养申请（含风险评分） |
| 领养 | `adoption_form` | 领养问卷明细 |
| 领养 | `adoption_assessment` | 领养评估记录 |
| 领养 | `adoption_record` | 领养成交记录 |
| 算法 | `match_weights` | 匹配算法权重配置 |
| 运营 | `activity` | 救助活动 |
| 运营 | `activity_participation` | 活动报名 |
| 运营 | `volunteer_application` | 志愿者申请 |
| 运营 | `rescue_report` | 流浪猫救助上报 |
| 行为 | `user_behavior` | 用户行为埋点日志 |
| 支付 | `payment_order` | 支付订单（金额以分存储，含状态机与回调报文） |

> 完整表结构见 [`schema.sql`](hzsca-springboot/hzsca/src/main/resources/db/schema.sql)（由数据库实际结构导出）。

---

## ✅ 质量保障与测试

后端已补齐针对两个核心算法的**单元测试**（JUnit 5 + Mockito + AssertJ），不依赖数据库，可离线运行：

```bash
cd hzsca-springboot/hzsca
./mvnw -o test -Dtest='RiskScoringServiceImplTest,MatchServiceImplTest'
```

| 测试类 | 用例数 | 覆盖要点 |
|--------|-------|---------|
| `RiskScoringServiceImplTest` | 22 | 各单项加减分、满分/零分边界、80/79/60/59 分风险档位边界、上下界截断、null 表单 |
| `MatchServiceImplTest` | 19 | 5 条硬性排除规则、六维全优/全劣打分、权重表覆盖与全零权重、limit 边界（0/负数/超上限）、缺失特征兜底、结果降序排序 |

```
Tests run: 41, Failures: 0, Errors: 0, Skipped: 0
```

> 需要真实数据库的 `HzscaApplicationTests` 已标记 `@Tag("integration")`，可用 `-DexcludedGroups=integration` 排除。

---

## ⚠️ 已知限制与后续计划

**已完善**：✅ 自动化测试 ｜ ✅ 明文口令一次性迁移 ｜ ✅ Redis/MySQL 一键部署方案 ｜ ✅ 图片资源压缩 ｜ ✅ 支付宝沙箱支付接口与接入文档

**仍待改进**：

- [ ] **图片进一步瘦身**：猫咪原图已从 146MB 压缩至约 8.4MB（1600px JPEG），如需更小可转 WebP 并接入对象存储；
- [ ] **前端分包**：构建产物仍有 2 个 >500KB 的 chunk，计划通过 `manualChunks` 拆分；
- [ ] **后端容器化**：当前 `docker-compose.yml` 只托管 MySQL 与 Redis，后端 jar 尚未容器化；
- [ ] **支付对账**：缺少每日对账定时任务与支付审计日志表；
- [ ] **小程序端支付**：小程序 `pages/donate` 仍为静态展示，未对齐 Web 端支付流程；
- [ ] **两套领养流程并存**：`adoption_assessment`（旧）与 `adoption_request`（新，含风险评分）功能重叠，属历史技术债。

---

## 📄 文档与归档

| 文件 | 说明 |
|------|------|
| [`毕业设计论文.md`](毕业设计论文.md) | 本科毕业设计论文全文 |
| [`docs/部署指南.md`](docs/部署指南.md) | 环境搭建、数据库初始化、首次运行检查清单 |
| [`docs/支付接入指南.md`](docs/支付接入指南.md) | 支付宝沙箱申请、密钥生成、回调调试与常见问题 |
| [`deliverables/gstack/grad-archive-hzsca-2026-10-12.md`](deliverables/gstack/grad-archive-hzsca-2026-10-12.md) | 项目归档评审报告（架构梳理 + 缺陷审计 + 修复验证） |

---

## 🙏 致谢

感谢湖州流浪猫救助公益场景带来的真实需求，让这个毕业设计不止是一次技术练习，也能对真实的公益救助工作有所助益。

愿每一只流浪猫，都能被温柔以待。🐾

---

<div align="center">

**License · [Apache 2.0](LICENSE)**

<sub>本项目为本科毕业设计作品，仅用于学习与交流。</sub>

</div>
