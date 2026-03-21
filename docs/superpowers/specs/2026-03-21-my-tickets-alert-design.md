# 我的车票临近发车提醒设计

**日期：** 2026-03-21  
**状态：** 已确认，待实现计划

## 1. 目标

为系统新增独立的“我的车票”页面。登录用户进入该页面后，可以查看自己已购买的车票；当某张票的发车时间距离当前时间在 3 小时内时，页面顶部显示红色汇总提醒，并将对应条目标红；已经发车的车票继续显示，并标记为“已发车”。

## 2. 已确认需求

- 新建独立页面，而不是把功能塞进现有查询/购票页
- 临近规则按**发车时间**判断
- 临近阈值为**3 小时内**
- 页面顶部显示**红色汇总提醒**
- 对应车票条目本身也**标红**
- “我的车票”基于**真正的登录用户**，不是全局票池，也不是浏览器本地缓存
- 本次只做**最小用户体系**：登录、当前用户、车票归属，不做注册
- 已经发车的车票仍然显示，并单独标记为**已发车**

## 3. 范围

### 3.1 本次会做

1. 后端新增最小用户体系
2. 登录接口与当前用户接口
3. 车票绑定到登录用户
4. 停靠站新增发车时间字段
5. 新增“我的车票”查询接口
6. 前端新增“我的车票”页面和路由入口
7. 页面顶部红色汇总提醒
8. 列表中即将发车条目标红
9. 已发车条目显示“已发车”标签

### 3.2 本次不做

- 用户注册
- 找回密码
- 完整 JWT / refresh token 体系
- 退票、改签、订单管理
- 短信/站内消息推送
- 复杂班次日历、多天时刻表引擎

## 4. 设计方案

### 4.1 推荐方案

采用“**后端负责用户归属、我的车票聚合和状态计算，前端只负责展示**”的方式。

原因：
- 时间状态规则只有一份，避免前后端口径不一致
- API 验收测试更容易覆盖
- 前端实现更直接，只处理颜色和文案渲染
- 后续若要扩展提醒规则，可继续在后端统一演进

### 4.2 非推荐备选

1. 后端只返回原始票据，前端自己计算“3 小时内 / 已发车”
   - 缺点：规则散落在前端，不利于测试和复用
2. 单独拆一个提醒接口，与我的车票列表接口分离
   - 缺点：接口偏重，对当前需求收益不高

## 5. 页面交互设计

### 5.1 页面结构

采用用户确认的 **A 方案：单页分组 + 顶部警告**。

页面结构：

1. 页面标题：我的车票
2. 顶部红色提醒条
   - 当存在 1 张及以上“3 小时内发车”的票时显示
   - 文案示例：`你有 2 张车票将在 3 小时内发车，请留意出行时间`
3. 车票列表
   - 即将发车：整行红色高亮 + 状态文案“即将发车”
   - 未发车：普通样式
   - 已发车：保留显示 + 灰色或弱化样式 + 状态文案“已发车”
4. 空状态
   - 当前用户没有任何已购车票时，显示空状态文案

### 5.2 页面流转

1. 用户登录
2. 在火车查询页购票
3. 购票接口将车票归属到当前登录用户
4. 用户进入“我的车票”页面
5. 前端请求当前用户的车票列表接口
6. 后端返回当前用户的已购票及其状态
7. 前端根据状态渲染顶部提醒和条目样式

## 6. 数据模型设计

### 6.1 User

新增 `User` 实体，字段保持最小化：

- `id`
- `username`
- `password`
- `fullName`

用途：
- 登录认证
- 当前用户识别
- 车票归属

### 6.2 Stop

在 `Stop` 上新增发车时间字段：

- `departureTime`

建议类型：`LocalDateTime` / 数据库 `datetime`

原因：
- 本次需求只需要按“当前时间 vs 发车时间”做判断
- 直接存日期时间，后端计算最简单
- 不引入额外的班次日历和时区复杂度

### 6.3 Ticket

在 `Ticket` 上新增：

- `user`

说明：
- 每张票属于一个登录用户
- 车票状态不落库，查询时动态计算

## 7. 状态计算规则

以车票出发站 `from` 对应的 `departureTime` 作为该票的发车时间。

状态规则：

- `DEPARTED`：`departureTime < now`
- `UPCOMING_SOON`：`now <= departureTime <= now + 3 hours`
- `UPCOMING`：`departureTime > now + 3 hours`

顶部红色提醒显示规则：

- 统计 `UPCOMING_SOON` 数量
- 数量大于 0 时显示提醒条
- 数量等于 0 时不显示提醒条

## 8. API 设计

### 8.1 登录接口

`POST /api/sessions`

请求示例：

```json
{
  "username": "zhangsan@example.com",
  "password": "123456"
}
```

响应示例：

```json
{
  "token": "<minimal-token>"
}
```

说明：
- 本次采用最小可用 token 方案，不引入完整 JWT 能力
- 目标是支撑登录态与当前用户识别

### 8.2 当前用户接口

`GET /api/sessions`

响应示例：

```json
{
  "fullName": "张三"
}
```

### 8.3 购票接口改造

现有接口：`POST /api/trains/{trainId}/tickets`

改造点：
- 从请求头中的 token 识别当前用户
- 购票成功时写入 `ticket.user = currentUser`
- 未登录购票应明确返回认证错误

### 8.4 我的车票接口

新增：`GET /api/tickets/me`

返回当前登录用户所有车票，示例：

```json
[
  {
    "id": 1,
    "trainName": "G102",
    "fromStation": "北京南",
    "toStation": "上海虹桥",
    "departureTime": "2026-03-21T18:30:00",
    "status": "UPCOMING_SOON",
    "statusText": "即将发车"
  },
  {
    "id": 2,
    "trainName": "G103",
    "fromStation": "上海虹桥",
    "toStation": "北京南",
    "departureTime": "2026-03-21T10:00:00",
    "status": "DEPARTED",
    "statusText": "已发车"
  }
]
```

说明：
- 后端直接返回状态枚举和展示文案
- 前端不负责时间状态计算

## 9. 前端设计

### 9.1 路由

新增独立页面路由，例如：

- `/my-tickets`

### 9.2 页面数据结构

前端页面展示需要的字段：

- `trainName`
- `fromStation`
- `toStation`
- `departureTime`
- `status`
- `statusText`

前端仅根据 `status` 做展示：

- `UPCOMING_SOON`：红色高亮
- `UPCOMING`：普通样式
- `DEPARTED`：灰色弱化 + “已发车”

### 9.3 入口

建议在页面头部导航中新增“我的车票”入口，便于从查询页进入。

## 10. 测试策略

### 10.1 API / 后端验收测试

需要覆盖：

1. 用户登录成功
2. 未登录访问 `/api/tickets/me` 被拒绝
3. 已登录购票后，车票归属当前用户
4. A 用户买的票不会出现在 B 用户的“我的车票”中
5. `/api/tickets/me` 返回 `DEPARTED`
6. `/api/tickets/me` 返回 `UPCOMING_SOON`
7. `/api/tickets/me` 返回 `UPCOMING`

### 10.2 UI 验收测试

需要覆盖：

1. 登录后可以进入“我的车票”页面
2. 没有已购车票时显示空状态
3. 存在 3 小时内车票时，顶部显示红色提醒
4. 即将发车车票条目标红
5. 已发车车票显示“已发车”标签
6. 普通未发车车票正常显示
7. “我的车票”页面只展示当前用户自己的票

## 11. 预计改动文件

### 11.1 后端

可能新增：

- `backend/src/main/java/com/agiletour/entity/User.java`
- `backend/src/main/java/com/agiletour/repo/UserRepo.java`
- `backend/src/main/java/com/agiletour/controller/SessionsController.java`
- `backend/src/main/java/com/agiletour/controller/TicketsController.java`
- `backend/src/main/java/com/agiletour/dto/MyTicketResponse.java`

可能修改：

- `backend/src/main/java/com/agiletour/entity/Ticket.java`
- `backend/src/main/java/com/agiletour/entity/Stop.java`
- `backend/src/main/java/com/agiletour/controller/TrainsController.java`
- `backend/src/main/resources/db/migration/R__0_init_db.sql`
- `backend/src/main/resources/db/migration/R__1_seed_demo_data.sql`
- 相关 API / UI 测试文件

### 11.2 前端

可能新增：

- `frontend/src/views/my-tickets/Index.vue`
- 票据 API service 文件（如需要）

可能修改：

- `frontend/src/router/index.ts`
- `frontend/src/components/layout/Header.vue`
- `frontend/src/api/services/session.ts`
- `frontend/src/services/authenticationService.ts`
- 相关 UI 测试支持代码

## 12. 验收标准

实现完成后应满足：

1. 登录用户购票后，车票归属到该用户
2. 当前用户进入“我的车票”页面时，只能看到自己的已购车票
3. 存在 3 小时内发车的票时：
   - 页面顶部显示红色汇总提醒
   - 对应条目标红
4. 已发车票继续显示，并带“已发车”标记
5. 没有临近发车票时，不显示顶部红色提醒
6. 相关单个 API / UI 验收测试通过

## 13. 风险与约束

1. 当前项目后端尚无现成用户体系，需要补最小实现
2. 当前 `Stop` 尚无时间字段，数据库结构和种子数据需要同步调整
3. 认证方案本次故意保持最小，不扩展到完整安全体系
4. 时间判断依赖服务端当前时间，测试中需要使用明确的种子时间或可控时间数据，避免脆弱断言

## 14. 结论

该需求可以在当前项目中以最小增量实现，核心路径是：

- 补最小用户体系
- 给停靠站补发车时间
- 购票绑定用户
- 新增“我的车票”接口和页面
- 由后端统一计算票据状态，前端负责提醒和样式展示

该方案满足用户确认的范围，且便于下一步拆分为可执行的实现计划。
