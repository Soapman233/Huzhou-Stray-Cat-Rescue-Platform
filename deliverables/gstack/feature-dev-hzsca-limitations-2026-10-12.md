# HZSCA 毕设局限完善 —— 交付评审报告

**日期**：2026-10-12
**场景**：全流程交付（方案设计 → 代码实现 → 测试验证 → 文档落盘 → 仓库瘦身发布）
**参与成员**：产品官（product-reviewer） + 质量门神（qa-lead） + 设计师（designer） + 主理人（lead）

---

## 📌 TL;DR（执行摘要）

- 整体结论：🟢 **通过** —— 用户提出的 5 项局限**全部闭环**，并额外发现并修复了 1 个 P0 级可复现性缺口（仓库缺少基础表 DDL）。
- 阻塞项数量：**0**（后端编译通过、前端构建通过、41 个单元测试全绿、支付链路端到端实测通过）。
- 下一步：已重写 Git 历史并推送至 GitHub，仓库体积从 **155MB 降至约 22MB**。

---

## 🎯 核心结论卡片

| 项目 | 内容 |
|------|------|
| Go / No-Go | 🟢 **Go** |
| 局限完成度 | 5 / 5 闭环（+ 1 项额外修复） |
| 新增测试 | 41 个单元测试，全部通过 |
| 仓库瘦身 | 历史 155MB → 22MB（-86%）；工作区 162MB → 25MB（-85%） |
| 关键行动项 | 6 条（全部完成） |

---

## 1. 各成员核心结论

### 🔍 产品官（支付方案设计）
- 核心判断：确认「零第三方 SDK」路线可行——支付宝沙箱的 `alipay.trade.page.pay` 与 `alipay.trade.query` 仅依赖 RSA2 签名，用 JDK 内置 `java.security` 即可完整实现，避免了离线构建下无法拉取 `alipay-sdk-java` 的问题。
- 关键建议：金额统一以「分」存储规避浮点误差；异步通知必须「验签 + 金额校验 + 幂等置位」三件套；未配置密钥时降级为 Mock 模式，保证无账号也能演示完整链路。以上均已落地。

### ✅ 质量门神（测试与部署方案）
- 核心判断：给出了 **41 条可执行测试用例**（风险评分 22 条 + 匹配算法 19 条），覆盖各单项加减分、档位边界（80/79/60/59）、上下界截断、5 条硬性排除、limit 边界与排序。
- 关键建议：**独立发现 P0 缺口——仓库缺少 17 张基础表的 DDL**，新克隆环境无法建库，README 的「快速开始」实际跑不通。此项已由主理人补齐 `db/schema.sql`。
- 另指出：`RiskScoringServiceImpl` 中 `if (score < 0) score = 0` 为**死代码**（最大扣分 85，理论最低分 15），`matchSpace` 维度因空间不足会先被硬性排除而**不具区分度**——两条均已记入论文可讨论的「算法局限」。

### 🎨 设计师（捐助支付 UI 设计）
- 核心判断：捐助页应保留原有公益内容，在顶部新增「捐助卡片」；结果页采用「加载中 / 支付成功 / 处理中 / 已关闭」四态设计。
- 关键建议：Mock 模式必须**显式提示用户当前未接入真实支付**（避免误导）；轮询每 3 秒、最长 60 秒，超时后转为手动刷新；`outTradeNo` 通过 query 参数传递以便支付宝同步回跳复用同一页面。均已实现。

### 🔧 主理人（实现与交付）
- 核心判断：本轮为「全流程交付」，由主理人负责全部代码实现与仓库收口。
- 关键建议：历史重写前先做 bundle 备份，并先验证「当前工作区无任何 >1MB 文件」再执行 `--strip-blobs-bigger-than 1M`，确保不会误删现存资源。

---

## 2. 交付清单

### 2.1 代码变更

| 模块 | 新增/修改 | 说明 |
|------|----------|------|
| 测试 | `RiskScoringServiceImplTest`（新增） | 22 个用例，纯单元测试 |
| 测试 | `MatchServiceImplTest`（新增） | 19 个用例，Mockito 隔离 Mapper |
| 测试 | `HzscaApplicationTests`（修改） | 标记 `@Tag("integration")` |
| 口令 | `PasswordMigrationRunner`（新增） | 启动时幂等迁移明文口令 |
| 口令 | `seed_admin_demo_data.sql`（修改） | 种子口令改为 BCrypt 哈希 |
| 数据库 | `db/schema.sql`（新增） | 17 张基础表 DDL，`CREATE TABLE IF NOT EXISTS` |
| 数据库 | `db/payment.sql`、`db/seed_admin_account.sql`（新增） | 支付表 + 演示管理员 |
| 数据库 | `DbBootstrap`（修改） | `@Order(10)`、自动建库、建支付表、建管理员 |
| 支付 | `PaymentController` / `PaymentService` / `PaymentServiceImpl`（新增） | 下单、查单、同步、Mock 支付、回调 |
| 支付 | `AlipayProperties` / `AlipaySignUtils`（新增） | 配置绑定 + RSA2 签名验签 |
| 支付 | `PaymentOrder` / `PaymentOrderMapper(.xml)`（新增） | 订单实体与持久层 |
| 安全 | `SecurityConfig`（修改） | 放行支付回调与配置接口 |
| 前端 | `donate.vue`（重写）、`PayResult.vue`（新增）、`router/index.js`（修改） | 捐助流程与结果页 |
| 资源 | 38 张猫咪图 + 15 张活动图（重新压缩） | 146MB → 9.4MB |
| 部署 | `docker-compose.yml`、`.env.example`（新增） | MySQL + Redis 一键启动 |

### 2.2 测试覆盖

| 测试类 | 用例数 | 结果 |
|--------|-------|------|
| `RiskScoringServiceImplTest` | 22 | ✅ 全通过 |
| `MatchServiceImplTest` | 19 | ✅ 全通过 |
| **合计** | **41** | **Failures: 0, Errors: 0** |

### 2.3 发布检查清单

| # | 检查项 | 结果 |
|---|--------|------|
| 1 | 后端 `mvnw -o compile` | ✅ EXIT=0 |
| 2 | 后端单元测试 | ✅ 41/41 通过 |
| 3 | 前端 `vite build` | ✅ 2209 模块，7.79s |
| 4 | 支付下单接口 | ✅ 返回 `outTradeNo`，`mock=true` |
| 5 | 支付查单接口 | ✅ 状态 `CREATED` |
| 6 | 模拟支付接口 | ✅ 状态置为 `PAID` 且写入 `pay_time` |
| 7 | 金额越界校验 | ✅ 返回 `400 捐助金额需在 1 ~ 50000 元之间` |
| 8 | 未登录下单 | ✅ 返回 403 |
| 9 | 口令迁移 | ✅ 13 个用户全部 BCrypt，明文残留 0 |
| 10 | 自动建表 | ✅ `payment_order` 已创建 |
| 11 | 历史重写后工作区 | ✅ `git status` 干净，348 个跟踪文件完好 |

### 2.4 回滚预案

| 场景 | 回滚方式 |
|------|---------|
| 历史重写结果不符合预期 | 使用备份 `C:/Users/SOAP/Desktop/hzsca-repo-backup-20261012.bundle` 恢复：`git clone <bundle> hzsca-restore` |
| 远端需要回退 | GitHub 上仍可查看 push 前的 reflog；如需彻底回退可重新推送备份 bundle |
| 图片压缩后画质不满意 | 原始图备份于 `C:/Users/SOAP/Desktop/HZSCA-images-original-backup/` |
| 数据库口令迁移需回退 | 迁移不可逆（哈希单向），但所有演示账号口令均为 `123456`，可用 `PasswordMigrationRunner` 逻辑重新设置 |
| 支付模块需下线 | `alipay.enabled=false` 即降级 Mock；删除 `payment_order` 表与 `PaymentController` 不影响其他模块 |

---

## ✅ 行动清单

| # | 行动 | 负责方 | 紧急度 | 状态 |
|---|------|--------|--------|------|
| 1 | 补齐核心算法单元测试（41 例） | 主理人 | P0 | ✅ 完成 |
| 2 | 明文口令一次性迁移（启动自动 + 种子改哈希） | 主理人 | P0 | ✅ 完成 |
| 3 | 补齐基础表 DDL，修复可复现性缺口 | 主理人 | P0 | ✅ 完成 |
| 4 | 压缩图片资源并同步数据库引用 | 主理人 | P1 | ✅ 完成 |
| 5 | 支付宝沙箱支付接口 + 前端流程 + 接入文档 | 主理人 | P1 | ✅ 完成 |
| 6 | Redis/Docker 部署方案 + 首次运行检查清单 | 主理人 | P1 | ✅ 完成 |
| 7 | 重写 Git 历史并推送 GitHub | 主理人 | P1 | ✅ 完成 |

---

## ⚠️ 待完善 / 已知局限

- **小程序端支付未对齐**：`hzsca-miniapp/pages/donate` 仍为静态展示，后续可复用同一套 `/api/payment/**` 接口；
- **后端未容器化**：`docker-compose.yml` 目前只托管 MySQL 与 Redis，后端 jar 仍为本地运行；
- **无支付对账**：缺少每日对账定时任务与支付审计日志表；
- **算法局限**：`matchSpace` 维度不具区分度、风险评分下界为死代码（详见质量门神结论）；
- **图片仍可再瘦身**：当前为 1600px JPEG，转 WebP 可进一步减小；
- **前端分包**：仍有 2 个 >500KB 的 chunk 未拆分。

---

## 📚 成员产出索引

- gstack-product-reviewer（产品官）：支付宝沙箱支付接口契约、订单状态机、签名规则与安全要点设计
- gstack-qa-lead（质量门神）：41 条测试用例设计（含边界值）、docker-compose 方案、首次运行检查清单；**独立发现 P0 可复现性缺口**
- gstack-designer（设计师）：捐助页与结果页的信息架构、四态设计、轮询策略、Mock 模式文案规范
- gstack-investigator（排障手，上一轮）：越权/鉴权缺陷清单（本轮未上场）

---

> 本报告由软件工坊 AI 协作生成，关键决策请由工程负责人复核。
