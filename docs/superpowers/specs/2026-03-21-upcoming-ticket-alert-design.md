# 已购票临近发车提醒功能设计

## 概述

在火车订票系统中，当用户进入火车查询/购票页面时，如果用户已有的已购票在3小时内即将发车，显示红色警告横幅提醒用户。

## 背景

当前系统存在问题：
- 用户购买车票后没有专门的已购票页面
- 用户可能忘记自己购买的车票何时发车
- 没有用户体系，Ticket 未关联用户

## 功能需求

1. **用户体系** - 建立用户与车票的关联关系
2. **发车时间** - 记录每列火车的固定每日发车时间
3. **出发日期** - 购票时指定具体的出发日期
4. **临近提醒** - 进入页面时检查并显示3小时内即将发车的已购票

## 设计方案

### 1. 数据模型变更

#### train 表新增字段
```sql
ALTER TABLE train ADD COLUMN departure_time TIME NOT NULL DEFAULT '08:00:00';
```

#### ticket 表新增字段
```sql
ALTER TABLE ticket ADD COLUMN user_id BIGINT NOT NULL;
ALTER TABLE ticket ADD COLUMN travel_date DATE NOT NULL;
```

#### 新增 user 表
```sql
CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL
);
```

### 2. 后端 API

#### GET /api/tickets/my
获取当前用户的所有已购票列表。

**响应示例：**
```json
[
  {
    "id": 1,
    "trainName": "G102",
    "departureTime": "08:00:00",
    "travelDate": "2026-03-25",
    "fromStation": "北京南",
    "toStation": "上海虹桥"
  }
]
```

#### GET /api/tickets/upcoming
获取当前用户3小时内即将发车的已购票列表。

**响应示例：**
```json
[
  {
    "id": 1,
    "trainName": "G102",
    "departureTime": "08:00:00",
    "travelDate": "2026-03-21",
    "fromStation": "北京南",
    "toStation": "上海虹桥",
    "remainingMinutes": 45
  }
]
```

**判断逻辑：**
```
发车时间点 = travel_date + departure_time
当前时间距发车 = 发车时间点 - 当前时间
返回 remainingMinutes > 0 且 remainingMinutes <= 180 的票
```

#### POST /api/trains/{trainId}/tickets
购票接口新增参数。

**请求体：**
```json
{
  "from": 1,
  "to": 4,
  "travelDate": "2026-03-25"
}
```

### 3. 前端提醒组件

#### 警告横幅组件
- 位置：火车查询页面（Index.vue）顶部
- 组件：Element Plus `el-alert`，type="error"
- 标题：⚠️ 温馨提示
- 内容：已购票临近发车列表，每条显示：
  - 车次名称
  - 出发日期
  - 剩余分钟数（如"2小时后发车"）
  - 出发站 → 到达站
- 交互：右侧关闭按钮，点击隐藏横幅

#### 数据获取
- 页面挂载时（onMounted）调用 `/api/tickets/upcoming`
- 如果返回列表非空，显示警告横幅
- 如果为空，不显示任何内容

### 4. 购票流程调整

#### 日期选择器
- 在购票按钮旁增加日期选择器
- 默认选择今天
- 限制：只能选择今天及以后的日期

#### 购票请求
- 点击购票时，将选中的日期作为 `travelDate` 参数传递
- 后端创建 Ticket 时关联当前用户和出发日期

## 技术实现

### 后端

1. **新增 User 实体和 UserRepo**
2. **Train 实体新增 departureTime 字段**
3. **Ticket 实体新增 user 和 travelDate 字段**
4. **新建 TicketController 处理相关 API**
5. **TicketRepo 新增按用户查询和按时间范围查询方法**
6. **修改 TrainsController.buyTicket 接收 travelDate 参数**

### 前端

1. **新增 User 类型定义**
2. **新增 tickets API 服务**
3. **在 Index.vue 中调用 `/api/tickets/upcoming` 获取数据**
4. **添加 el-alert 组件显示警告横幅**
5. **购票表单增加日期选择器**

## 测试要点

1. 用户未登录时，访问页面不显示提醒
2. 用户已登录但无已购票，不显示提醒
3. 已购票发车时间在3小时外，不显示提醒
4. 已购票发车时间在3小时内，显示红色警告横幅
5. 关闭横幅后，刷新页面重新显示
6. 购票成功后，需要选择出发日期

## 优先级

1. **Phase 1（基础）**: 新增用户体系 + 数据模型变更 + 已购票 API
2. **Phase 2（提醒）**: 前端警告横幅 + 购票日期选择
