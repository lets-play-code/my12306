# my12306 项目文档

## 项目概述

my12306 是一个火车票务管理系统，模拟中国铁路12306系统的核心功能。该项目采用前后端分离架构，后端使用Spring Boot提供REST API，前端使用Vue.js构建用户界面。项目支持火车查询、票务购买等基本功能，旨在演示敏捷开发和现代Web应用架构。

**技术栈**：
- **后端**：Java 17, Spring Boot, Gradle, JPA/Hibernate, MySQL
- **前端**：Vue.js 3, TypeScript, Vite, Tailwind CSS, Vitest
- **其他**：Docker Compose (环境配置), Flyway (数据库迁移)

**关键特征**：
- RESTful API设计
- 火车和票务数据管理
- 用户认证和会话管理
- 响应式前端界面
- 单元测试和集成测试
- 容器化部署支持

## 整体架构

项目遵循经典的三层架构：

1. **表现层**：前端Vue.js应用，负责用户交互和数据展示。
2. **业务逻辑层**：后端Spring Boot控制器和服务，处理业务规则。
3. **数据访问层**：JPA仓库类，负责数据库操作。

```
[前端 Vue.js] <--- HTTP API ---> [后端 Spring Boot]
     |                                   |
     |                                   |
[用户界面]                          [控制器] --> [服务] --> [仓库] --> [数据库]
```

## 后端结构 (Spring Boot)

位于 `backend/` 目录，使用Gradle构建。

### 核心包结构
```
src/main/java/com/agiletour/
├── controller/          # REST控制器
│   └── TrainsController.java  # 火车和票务API
├── entity/              # JPA实体类
│   ├── Train.java       # 火车实体
│   ├── Ticket.java      # 票务实体
│   └── ...              # 其他实体
├── repo/                # 数据仓库接口
│   ├── TrainRepo.java   # 火车仓库
│   └── TicketRepo.java  # 票务仓库
└── service/             # 业务服务类 (如有)
```

### 关键组件

#### TrainsController.java
- **端点**：
  - `POST /api/trains/{trainId}/tickets`：购买票务
  - `GET /api/trains`：查询火车列表
- **逻辑**：处理票务购买，包括座位可用性检查和异常处理
- **依赖**：注入 `TrainRepo` 和 `TicketRepo`

#### 数据模型
- **Train**：包含座位、站点等信息
- **Ticket**：关联座位、出发/到达站点
- **FromAndTo**：请求体POJO，用于传递站点索引

#### 配置
- `application.yaml`：数据库连接、端口等配置
- `db/migration/`：Flyway脚本，用于数据库初始化

## 前端结构 (Vue.js)

位于 `frontend/` 目录，使用Vite构建。

### 核心目录结构
```
src/
├── api/                 # API客户端
│   ├── services/
│   │   └── session.ts   # 会话管理
│   ├── request.ts       # HTTP请求封装
│   └── index.ts         # API入口
├── components/          # 可复用组件
│   ├── layout/          # 布局组件 (Header, Footer, Logo)
│   ├── Loader.vue       # 加载器
│   ├── Notification.vue # 通知组件
│   └── Pagination.vue   # 分页组件
├── views/               # 页面视图
│   ├── login/           # 登录页面
│   └── train/           # 火车查询页面
├── router/              # 路由配置
├── services/            # 业务服务
│   └── authenticationService.ts  # 认证服务
├── model/               # 数据模型 (TypeScript接口)
└── assets/              # 静态资源
```

### 关键特征

#### 组件化架构
- 使用Vue 3 Composition API
- TypeScript提供类型安全
- Tailwind CSS用于样式

#### 路由和视图
- Vue Router管理页面导航
- 登录和火车查询页面

#### API集成
- Axios封装HTTP请求
- 服务层抽象业务逻辑

#### 测试
- Vitest用于单元测试
- 组件快照测试

## 关键特征详解

### 1. 票务购买流程
- 用户选择火车和站点
- 后端检查座位可用性
- 创建票务记录或抛出异常

### 2. 数据一致性
- 使用JPA乐观锁或事务确保数据完整性
- 异常处理防止无效操作

### 3. 前端用户体验
- 响应式设计，支持移动端
- 加载状态和错误通知
- 分页组件处理大数据

### 4. 开发和部署
- Gradle Wrapper和pnpm-lock.yaml确保依赖一致性
- Docker Compose简化本地环境
- 热重载支持快速开发

## 运行指南

### 后端
```bash
cd backend
./gradlew bootRun
```

### 前端
```bash
cd frontend
pnpm install
pnpm dev
```

### 数据库
使用Docker Compose启动MySQL：
```bash
cd env
docker-compose up
```

## 扩展建议

- 添加用户认证和授权
- 实现实时座位更新
- 集成支付系统
- 添加更多测试覆盖

## 首页火车信息列表实现

### 需求分析
领导要求在首页上加入火车信息列表，用于展示所有可用火车的基本信息（如火车ID、路线、时间等），方便用户快速浏览和选择。

### 实现步骤

#### 1. 后端确认
- 后端已提供 `GET /api/trains` 端点，返回 `List<Train>`。
- 确保 `Train` 实体包含必要字段（如ID、路线、出发/到达时间等）。如果需要扩展字段，可修改 `Train.java` 并更新数据库迁移脚本。

#### 2. 前端修改
- **修改首页组件**：假设首页是 `src/views/Home.vue` 或 `src/App.vue`（需确认实际文件）。
  - 添加数据状态：`const trains = ref([])`
  - 在 `onMounted` 中调用API：`fetchTrains()`
  - 定义 `fetchTrains` 函数：使用 `api/index.ts` 中的请求方法调用 `/api/trains`，更新 `trains` 状态。
- **添加列表显示**：
  - 使用 `<ul>` 或 `<table>` 渲染火车列表。
  - 每个列表项显示火车关键信息，如：`<li>火车 {{ train.id }}: {{ train.route }} - {{ train.departureTime }}</li>`
  - 添加加载状态和错误处理。
- **样式优化**：使用 Tailwind CSS 确保列表响应式和美观。

#### 3. 示例代码片段
```vue
<template>
  <div>
    <h1>火车信息列表</h1>
    <ul v-if="trains.length">
      <li v-for="train in trains" :key="train.id">
        火车 {{ train.id }}: {{ train.route }} - {{ train.departureTime }}
      </li>
    </ul>
    <p v-else>加载中...</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { api } from '@/api'

const trains = ref([])

const fetchTrains = async () => {
  try {
    const response = await api.get('/trains')
    trains.value = response.data
  } catch (error) {
    console.error('获取火车列表失败:', error)
  }
}

onMounted(fetchTrains)
</script>
```

#### 4. 测试和验证
- 前端：运行 `pnpm dev`，检查首页是否正确显示列表。
- 后端：确保API返回正确数据，可使用Postman测试。
- 集成测试：添加单元测试验证组件渲染和API调用。

#### 5. 潜在扩展
- 添加搜索/过滤功能。
- 实现分页（使用现有 `Pagination.vue` 组件）。
- 添加点击跳转到详情页。

这个实现基于现有架构，无需大幅改动。加油干！如果需要具体代码修改或进一步细节，请提供更多信息。

这个文档基于当前代码结构编写。如需更新或添加更多细节，请提供具体要求。