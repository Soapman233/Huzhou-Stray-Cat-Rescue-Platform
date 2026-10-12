# HZSCA 持续完善 —— 第二轮交付报告

**日期**：2026-10-12
**场景**：全流程交付（需求确认 → 实现 → 端到端验证 → 文档落盘 → 推送）
**执行方式**：主理人直接实现（本轮为用户确认清单后的连续改进）

---

## 📌 TL;DR（执行摘要）

- 整体结论：🟢 **通过** —— 用户勾选的 **7 项改进全部完成**，无阻塞项。
- 关键收益：仓库图片资源 **161MB → 6.3MB（-96%）**；前端首屏入口 chunk **1,141KB → 25KB**；ECharts **1,031KB → 505KB 且改为懒加载**；构建警告清零。
- 已推送 GitHub，`main` 分支同步。

---

## 🎯 核心结论卡片

| 项目 | 内容 |
|------|------|
| Go / No-Go | 🟢 **Go** |
| 完成度 | 7 / 7 |
| 构建与测试 | 后端编译 ✅ ｜ 前端构建无警告 ✅ ｜ 41 单测全绿 ✅ |
| 端到端验证 | 支付链路 ✅ ｜ 对账与审计 ✅ ｜ WebP 图片加载 ✅ ｜ 图表渲染 ✅ |

---

## 1. 交付清单

### ① 小程序端支付流程对齐
- `hzsca-miniapp/pages/donate/index.js`：由空 `Page({})` 实现为完整逻辑（配置读取、金额校验、下单、模拟支付、状态刷新、链接复制、重置）
- `index.wxml` / `index.wxss`：新增捐助卡片、订单状态卡片、四态样式（待支付/成功/关闭）
- **平台差异处理**：微信小程序无法直接跳转支付宝收银台，真实模式下提供「复制收银台链接」；Mock 模式下可一键模拟支付完成演示闭环

### ② 后端 Docker 容器化
- `hzsca-springboot/hzsca/Dockerfile`：多阶段构建（Maven 编译 → JRE 运行），非 root 用户运行，内置健康检查
- `hzsca-springboot/hzsca/.dockerignore`
- `docker-compose.yml`：新增 `app` 服务，通过 `profiles: ["full"]` 实现**向后兼容**——
  `docker compose up -d` 仍只起 MySQL+Redis，`docker compose --profile full up -d --build` 全栈一键启动
- 校验：`docker compose config` 通过，默认/完整 profile 服务清单符合预期

### ③ 前端分包优化
| 优化项 | 优化前 | 优化后 |
|--------|-------|--------|
| 入口 chunk | 1,141 KB | **25 KB** |
| Element Plus | 853 KB | **447 KB** |
| ECharts | 1,031 KB | **505 KB**（且仅管理端懒加载） |
- 手段：`vite.config.js` 增加 `manualChunks`；移除 `app.use(ElementPlus)` 改为 `unplugin-vue-components` 按需引入；新增 `src/utils/echarts.js` 只注册饼图/柱状图；`App.vue` 改用 `el-config-provider` 指定中文语言包
- **构建警告已消除**

### ④ 支付对账与审计日志
- 新增表 `payment_audit_log`（动作 / 状态变更 / 成功标记 / 详情）
- 新增 `PaymentAuditService`（审计写入失败绝不影响主流程）、`PaymentAuditLogMapper(+XML)`
- 新增 `PaymentReconcileTask`：每日 02:30 定时执行「主动查单补记 + 超时关单 + 审计一致性检查」
- 新增 `AdminPaymentController`：手动触发对账、查询订单审计轨迹（仅管理员）
- `HzscaApplication` 开启 `@EnableScheduling`

### ⑤ 图片转 WebP
| 资源 | 原始 | 压缩后 | 累计降幅 |
|------|------|--------|---------|
| 38 张猫咪图 | 145.8 MB | 5.6 MB | **-96.2%** |
| 15 张活动图 | 15.3 MB | 0.67 MB | **-95.6%** |
- 数据库 `animals.ImageURL`、`activity.imgurl` 已同步更新为 `.webp`

### ⑥ 算法局限写入文档
- 新增 `docs/算法说明与局限分析.md`：完整梳理两个算法的设计、5 项匹配局限、5 项评分局限及改进方向，可直接作为论文「不足与展望」素材

### ⑦ 标注两套领养流程技术债
- README 新增「📌 历史技术债：两套并存的领养流程」对照表（实体/接口/数据表/风险评分/使用方/权限），并给出收敛建议

---

## 2. 验证记录

| 验证项 | 结果 |
|--------|------|
| 后端 `mvnw -o compile` | ✅ EXIT=0 |
| 单元测试 | ✅ 41/41 通过 |
| 前端 `vite build` | ✅ 构建成功，**无 >500KB chunk 警告** |
| 支付链路（下单→模拟支付→PAID） | ✅ |
| 审计轨迹接口 | ✅ 返回 CREATE / PAY 两条记录 |
| 手动对账接口 | ✅ 正确识别出审计功能上线前的 2 笔历史订单（`missingAudit: 2`） |
| 越权防护 | ✅ 普通用户访问 `/api/admin/payment/reconcile` 返回 403 |
| WebP 图片 | ✅ 接口返回 `.webp`，浏览器加载成功（截图确认） |
| Element Plus / ECharts 按需引入 | ✅ 管理端仪表盘组件与图表正常渲染（截图确认） |
| `docker compose config` | ✅ 语法校验通过，profile 生效 |

---

## ⚠️ 待完善 / 已知局限

- **小程序真实支付**：受平台限制无法跳转支付宝，正式场景需接入微信支付（可复用 `PaymentService` 接口）
- **算法离线评估**：两个算法仍无 AUC / NDCG 等量化指标
- **Element Plus CSS**：样式仍为全量引入（约 326KB），可进一步按需引入
- **图片可上对象存储**：当前仍随 jar 打包

---

## 📌 运维提示（重要）

> **修改静态图片路径后必须清理 Redis 缓存**：`CatsServiceImpl` 对 `cats:all` / `cats:adoptable` / `cats:{id}` 有 24 小时缓存。
> 本次将图片从 `.jpeg` 改为 `.webp` 后，因缓存仍返回旧路径导致图片 404，清理缓存后恢复正常。
> 操作命令：`redis-cli -p 6379 DEL cats:all cats:adoptable`

---

> 本报告由软件工坊 AI 协作生成，关键决策请由工程负责人复核。
