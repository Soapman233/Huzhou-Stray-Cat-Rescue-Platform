# HZSCA 流浪猫救助与领养平台 —— 毕业设计归档评审报告

**日期**：2026-10-12
**场景**：全流程交付（架构梳理 + 代码审查 + 安全审计 + 修复验证 + 仓库归档）
**参与成员**：产品官（product-reviewer） + 安全卫士（security-officer 视角，由排障手覆盖 OWASP 检查） + 排障手（investigator） + 设计师（designer） + 主理人（lead）

---

## 📌 TL;DR（执行摘要）

- 整体结论：🟡 **有条件通过** —— 项目功能完整、架构清晰，可作为毕业设计归档；已修复 12 处确认缺陷（含 4 处越权/鉴权类安全问题）。
- 阻塞项数量：**0**（修复后后端 `mvn compile` 与前端 `vite build` 均通过，运行时接口验证正常）。
- 下一步：推送至 GitHub `Huzhou-Stray-Cat-Rescue-Platform`，并以本报告 + README 作为归档说明。

---

## 🎯 核心结论卡片

| 项目 | 内容 |
|------|------|
| Go / No-Go | 🟢 **Go**（修复后） |
| 严重度分布 | 🔴 4 / 🟠 5 / 🟡 6 |
| 关键行动项 | 6 条（4 条已闭环） |
| 建议负责人 | 后端负责人（鉴权/密码）、前端负责人（配置化）、运维（部署文档） |

---

## 1. 各成员核心结论

### 🔍 产品官（架构与功能评审）
- 核心判断：三端（Spring Boot 3 + Vue 3 + 微信小程序）职责清晰、分层规范，`controller / service / mapper / entity / security` 分包完整；业务闭环覆盖「浏览猫咪 → 智能匹配推荐 → 提交领养申请 → 风险评分 → 管理员审核 → 活动/志愿者/救助上报 → 数据导出」，达到本科毕设的完整度要求。
- 关键建议：两个算法（智能匹配、风险评分）是本项目最亮的加分项，建议在论文与 README 中单独成节阐述权重设计与硬性排除规则；补齐单元测试与部署文档可显著提升答辩评价。

### 🛡️ 安全卫士（OWASP + 鉴权审计）
- 核心判断：原实现存在 **4 处高危越权/鉴权缺陷**：普通登录用户可增删改猫咪、可拉取全量用户列表、可查看/修改/删除任意用户资料（IDOR）、网页用户口令明文存储。
- 关键建议：已在 `SecurityConfig` 收敛写操作权限，在 `UserController` 增加「本人或管理员」归属校验，并将用户口令统一升级为 BCrypt（兼容历史明文、首次登录自动迁移）。均已运行时验证。

### ✅ 质量门神（构建与运行时验证）
- 核心判断：后端 `mvnw -o -DskipTests compile` 通过；前端 `vite build` 通过（2209 模块，7.93s）；后端实际启动后 `/api/cats/adoptable`、`/api/activity/list` 返回 200 且数据正确。
- 关键建议：前端构建存在 2 个 >500KB 的 chunk（Element Plus + ECharts 未拆分），建议后续用 `manualChunks` 优化首屏体积（非阻塞）。

### 🔧 排障手（代码质量与根因）
- 核心判断：定位并确认 15 处问题，其中功能性缺陷集中在「鉴权链路不一致」「配置层级错误」「空值未防御」三类；`application.yml` 中 `jackson` 与 `main` 节点误置于根级，导致日期格式与循环引用配置**实际从未生效**，属典型隐性 Bug。
- 关键建议：统一前后端接口返回约定（`Result.code` 为字符串 `"200"`），避免后续维护踩坑；为上传接口增加扩展名白名单（已实施）。

### 🎨 设计师（README 视觉方案）
- 核心判断：采用「真实界面截图为主 + 暖色调米咖主题 + Mermaid 架构图」的组合，比纯插画更能体现工程真实度。
- 关键建议：GitHub 明暗主题下统一使用浅色卡片底 + 深色文字的截图；徽章克制使用；架构图用 Mermaid 保证可维护。

---

## 2. 综合审查发现（按严重度排序）

| # | 严重度 | 类别 | 位置 | 问题描述 | 建议/处理 | 来源 |
|---|--------|------|------|---------|-----------|------|
| 1 | 🔴 | 安全/越权 | `SecurityConfig.java:27-42` | 普通登录用户可 `POST/PUT/DELETE /api/cats/**`、`/api/activity/add|update|delete|upload` | 已收敛为 `hasRole("admin")` | 安全卫士 |
| 2 | 🔴 | 安全/越权 | `UserController.java` | `GET /api/users` 任意登录用户可拉全量用户；`/update`、`/delete`、`/current` 可操作他人账号（IDOR） | 列表限管理员；三接口增加「本人或管理员」校验 | 安全卫士 |
| 3 | 🔴 | 安全/口令 | `AuthController.java` | 网页用户口令明文比较与明文入库 | 改为 BCrypt，兼容历史明文并自动迁移 | 安全卫士 |
| 4 | 🔴 | 安全/鉴权 | `AuthController.java:63-78` | `role=admin` 分支登录成功但**不签发 token**，后续请求必 401 | 补充 token 签发，与 `/api/admin/login` 对齐 | 排障手 |
| 5 | 🟠 | 配置 | `application.yml` | `jackson` / `main` 位于根级，应为 `spring.jackson` / `spring.main`，配置静默失效 | 已移至 `spring` 下 | 排障手 |
| 6 | 🟠 | 安全/配置 | `application.yml` | 数据库账号口令硬编码 | 改为 `${DB_USERNAME:root}` / `${DB_PASSWORD:1234}` 环境变量注入 | 安全卫士 |
| 7 | 🟠 | 功能性 | `FileServiceImpl.java:34` | `getOriginalFilename()` 无后缀时 `substring` 抛 `StringIndexOutOfBoundsException`；且无类型校验 | 增加空值防御 + 图片扩展名白名单 | 排障手 |
| 8 | 🟠 | 功能性 | `WebConfig.java:17` | 上传路径用字符串拼接 `"file:" + Windows 路径`，反斜杠导致静态资源 404 | 改用 `Path.toUri()` | 排障手 |
| 9 | 🟠 | 健壮性 | `AuthController.java:111` | `Integer.parseInt(request.getGender())` 未判空，前端不传时 NPE/格式异常 | 新增 `parseGender()` 兜底为 1 | 排障手 |
| 10 | 🟡 | 仓库规范 | `.gitignore` | 仅忽略 `node_modules`，导致 `target/`(162MB)、`dist/`、`.idea/`、`Redis/` 二进制被提交 | 重写 `.gitignore` 并取消跟踪 | 排障手 |
| 11 | 🟡 | 前端质量 | `utils/request.js` | 生产环境打印完整请求体与响应体到控制台 | 已精简日志 | 排障手 |
| 12 | 🟡 | 可维护性 | `hzsca-miniapp/utils/request.js:1` | 后端地址硬编码 `127.0.0.1`，真机不可用 | 抽出 `utils/config.js` 并注释真机需替换 HTTPS 域名 | 排障手 |
| 13 | 🟡 | 性能 | `hzsca-vue` 构建产物 | 2 个 chunk > 500KB | 建议 `manualChunks` 拆分（未实施） | 质量门神 |
| 14 | 🟡 | 性能 | `static/cats/*.jpeg` | 38 张猫咪原图共 146MB（单张最大 8.2MB），Web 端加载偏慢 | 建议压缩为 300KB 级 WebP（未实施，保留原图） | 排障手 |
| 15 | 🟡 | 工程规范 | 根目录 `package.json` | 与 `hzsca-vue/hzsca/package.json` 重复的 axios/element-plus 依赖，疑为误建 | 建议清理（未实施） | 排障手 |

---

## 3. 修复验证记录（运行时实测）

| 验证项 | 期望 | 实测 |
|--------|------|------|
| 未登录 `POST /api/cats` | 拒绝 | **403** ✅ |
| 普通用户 token `POST /api/cats` | 拒绝 | **403** ✅ |
| 普通用户 token `GET /api/users` | 拒绝 | **403** ✅ |
| 普通用户 `GET /api/users/current?username=自己` | 允许 | **200** ✅ |
| 普通用户 `GET /api/users/current?username=他人` | 拒绝 | **403「无权查看其他用户信息」** ✅ |
| 明文口令首次登录 | 自动升级 BCrypt | user1 口令已变为 `$2a$10$...`（60 位） ✅ |
| 后端编译 | 通过 | `mvnw -o compile` EXIT=0 ✅ |
| 前端构建 | 通过 | `vite build` 2209 模块 / 7.93s ✅ |

---

## ✅ 行动清单

| # | 行动 | 负责方 | 紧急度 | 状态 |
|---|------|--------|--------|------|
| 1 | 收敛管理端写操作权限（猫咪/活动） | 后端 | P0 | ✅ 已完成 |
| 2 | 用户接口增加归属校验，列表限管理员 | 后端 | P0 | ✅ 已完成 |
| 3 | 用户口令 BCrypt 化 + 历史明文自动迁移 | 后端 | P0 | ✅ 已完成 |
| 4 | 修复配置层级 / 上传路径 / 空值防御 | 后端 | P0 | ✅ 已完成 |
| 5 | 清理构建产物并重写 `.gitignore`，归档推送 GitHub | 主理人 | P1 | ✅ 已完成 |
| 6 | 压缩猫咪原图、拆分前端 chunk、补充单元测试 | 后续迭代 | P2 | ⏳ 待办 |

---

## ⚠️ 待完善 / 已知局限

- **无自动化测试**：项目未包含单元/集成测试，建议后续补充 `spring-boot-starter-test` 用例。
- **明文密码兼容期**：历史明文口令在用户首次登录前仍以明文存放于数据库，建议上线前统一执行一次迁移脚本。
- **Redis 便携版已移出仓库**：`Redis/` 目录（含 30MB 调试符号）不再入库，运行前需自行安装 Redis。
- **图片资源体积偏大**：38 张猫咪原图共约 146MB，本次为保留原始画质未做压缩。
- **支付功能为展示性质**：`donate` 页面为静态展示，未接入真实支付通道。

---

## 📚 成员产出索引

- gstack-product-reviewer（产品官）：系统分层架构、三端职责、核心功能清单、毕设完成度评审（已并入 README「系统架构」「核心功能」章节）
- gstack-security-officer（安全卫士，排障手兼）：OWASP 视角的越权/鉴权/口令检查项 15 条（见本报告第 2 节）
- gstack-qa-lead（质量门神，主理人代验）：后端编译 + 前端构建 + 运行时接口验证记录（见第 3 节）
- gstack-designer（设计师）：README 信息架构、配色规范、配图清单、Mermaid 架构图方案（已并入 `README.md`）
- gstack-investigator（排障手）：15 条 file:line 级缺陷清单与修复补丁

---

> 本报告由软件工坊 AI 协作生成，关键决策请由工程负责人复核。
