# README.md

本文件为 Claude Code (claude.ai/code) 在此代码库中工作时提供指导。

## 项目概述

这是一个火车票订票系统 (my12306)，采用 Spring Boot 后端和 Vue 3 前端架构。应用程序允许用户查询火车并购买特定路线的车票。

## 架构

### 后端 (Spring Boot)
- **框架**: Spring Boot 2.4.4 with Spring Data JPA
- **数据库**: MySQL with Flyway 迁移
- **测试**: JUnit 5, Cucumber with JFactory 测试数据生成
- **端口**: 8080

### 前端 (Vue 3)
- **框架**: Vue 3 with TypeScript and Vite
- **UI**: Tailwind CSS and Element Plus
- **测试**: Vitest with Testing Library
- **开发服务器**: 端口 9090 (代理 API 调用到后端 8080)

## 🚀 Agent 快速执行策略

### 测试执行原则

**⚡️ 重要：Agent 应该默认运行单个测试，而不是所有测试，以提升效率**

1. **默认行为**：优先运行单个相关测试文件，而非整个测试套件
2. **测试文件定位**：
   - UI 测试：`src/test/resources/ui/ticket.feature`
   - API 测试：`src/test/resources/api/ticket.feature`
3. **调试需要**：使用 `-Dheadless=false` 显示浏览器窗口
4. **完整测试**：仅在明确要求或验证整体功能时运行全部测试

## 开发命令

### 后端
```bash
cd backend
./gradlew build          # 构建应用程序

# ✅ 推荐：运行单个 UI 测试文件（默认 headless）
./gradlew cucumber -Pfile=src/test/resources/ui/ticket.feature

# ✅ 推荐：运行单个 API 测试文件（默认 headless）
./gradlew cucumber -Pfile=src/test/resources/api/ticket.feature

# 🔍 调试：显示浏览器窗口进行可视化调试
./gradlew cucumber -Pfile=src/test/resources/ui/ticket.feature -Dheadless=false

# 🎯 精确：运行单个 feature 文件中的特定行场景
# 例如：运行 ticket.feature 文件第 10 行开始的场景
./gradlew cucumber -Pfile=src/test/resources/ui/ticket.feature:10

# ⚠️ 不推荐：运行所有测试（仅在全面验证时使用）
./gradlew cucumber

./gradlew bootRun        # 运行应用程序
```
**💡 提示：UI测试共用相同的前端实例，而同一个前端连接的backend同时只能是bootRun 和测试中的一个。因此无法在启动bootRun的同时执行测试**


### 前端
```bash
cd frontend
pnpm install             # 安装依赖
pnpm run dev             # 启动开发服务器
pnpm run build           # 生产环境构建
pnpm run test            # 运行测试
pnpm run lint            # 运行 ESLint
pnpm run format          # 使用 Prettier 格式化代码
```

## 用 Docker 启动依赖环境（MySQL/MockServer/PhpMyAdmin）

Docker 编排文件位于 `env/docker-compose.yml`，用于本地快速启动项目依赖（数据库、Mock、浏览器自动化等）。

### 1) 先决条件
- 已安装 Docker Desktop（macOS/Windows）或 Docker Engine（Linux）

### 2) 准备环境变量
- 在 `env/` 目录创建 `.env`（可从 `.env.example` 复制）：

可用变量说明（默认与后端配置一致）：
- `MYSQL_PORT_OUT=23306`：宿主机映射的 MySQL 端口（后端使用该端口）
- `MYSQL_DATABASE=db`、`MYSQL_USER=admin`、`MYSQL_PASSWORD=123456`、`MYSQL_ROOT_PASSWORD=root`
- `PHPMYADMIN_PORT_OUT=18080`：PhpMyAdmin 访问端口
- `MOCK_SERVER_PORT_OUT=1080`：MockServer 访问端口
- `HOST_NAME=host.docker.internal`：容器访问宿主机用（用于 UI 自动化）

### 3) 启动服务
- 在项目根目录运行（推荐，显式指定 env 文件）：

```bash
docker compose --env-file env/.env -f env/docker-compose.yml up -d mysql mock-server phpmyadmin
```

或进入 `env/` 目录后直接运行（此时会自动读取 `env/.env`）：
```bash
cd env
docker compose up -d mysql mock-server phpmyadmin
```

查看状态与日志：
```bash
docker compose --env-file env/.env -f env/docker-compose.yml ps
docker compose --env-file env/.env -f env/docker-compose.yml logs -f mysql
```

### 4) 主机名映射（重要）
后端的数据库连接在 `backend/src/main/resources/application.yaml` 中为：

- `jdbc:mysql://mysql.tool.net:23306/db`

为使后端在本机通过该主机名访问到 Docker 中的 MySQL，请在宿主机添加 hosts 记录：

```bash
# 需要管理员权限，内容示例（请确认未重复）：
127.0.0.1  mysql.tool.net
```

macOS/Linux：编辑 `/etc/hosts`；Windows：编辑 `C:\Windows\System32\drivers\etc\hosts`。

如不想修改 hosts，可将后端配置改为 `localhost:23306`。

### 5) 服务校验与访问
- MySQL：`localhost:${MYSQL_PORT_OUT}`（默认 23306）
- PhpMyAdmin：浏览器访问 `http://localhost:${PHPMYADMIN_PORT_OUT}`（默认 18080）
	- Server/Host：`db`（容器内别名）或 `127.0.0.1:23306`
	- 用户名/密码：`admin / 123456`
- MockServer：`http://localhost:${MOCK_SERVER_PORT_OUT}`（默认 1080）

### 6) 与后端/前端联动
- 保证 Docker 中 MySQL 已启动且健康（`docker compose ps` HEALTHY）
- 后端直接运行：
	```bash
	cd backend
	./gradlew bootRun
	```
- 前端开发代理到后端 `http://localhost:8080`（详见 `frontend/vite.config.ts`），启动：
	```bash
	cd frontend
	pnpm install
	pnpm run dev
	```

> 注意：docker-compose.yml 中包含 `web-driver` 和 `web-driver-arm` 服务，这些是历史遗留配置。当前 UI 测试使用 Playwright 直接在本机启动浏览器，不需要启动这些服务。

## 核心组件

### 后端实体
- `Train`: 表示包含多个站点和座位的火车
- `Seat`: 个别座位，具有可用性跟踪
- `Ticket`: 座位在两个站点之间的预订记录
- `Stop`: 火车站点，包含顺序信息

### API 端点
- `GET /api/trains` - 查询所有可用火车
- `POST /api/trains/{trainId}/tickets` - 购买特定火车和路线的车票

### 前端结构
- `src/views/` - 页面组件 (登录, 火车订票)
- `src/components/` - 可重用 UI 组件
- `src/api/` - axios API 服务层
- `src/router/` - Vue Router 配置

## 数据库配置

应用程序使用 MySQL 和 Flyway 迁移。数据库配置在 `backend/src/main/resources/application.yaml` 中：
- URL: jdbc:mysql://mysql.tool.net:23306/db
- 架构迁移位于 `backend/src/main/resources/db/migration/`

## 测试策略

### 后端测试
- JUnit 5 单元测试
- Cucumber 和 JFactory 验收测试
- 测试数据工厂在 `src/test/java/com/agiletour/spec/`
- 使用 Playwright 进行 UI 自动化测试

### 前端测试
- Vitest 单元测试
- Testing Library Vue 组件测试
- 测试工具在 `tests/components/test-utils.ts`

## UI 测试 Headless 模式配置

**默认行为**：UI 测试默认使用 headless 模式运行，避免弹出浏览器窗口干扰工作。

### 快速使用

#### ✅ 运行单个 UI 测试（默认 headless）
```bash
cd backend
./gradlew cucumber -Pfile=src/test/resources/ui/ticket.feature
```

#### 🔍 调试模式（显示浏览器窗口）
```bash
# 运行特定测试并显示浏览器窗口
./gradlew cucumber -Pfile=src/test/resources/ui/ticket.feature -Dheadless=false

# 运行特定行开始的场景
./gradlew cucumber -Pfile=src/test/resources/ui/ticket.feature:10 -Dheadless=false
```

### 完整配置说明

#### 方式 1：通过 Gradle 属性设置（推荐）
```bash
# 明确指定 headless 模式
./gradlew cucumber -Pfile=src/test/resources/ui/ticket.feature -Dheadless=true

# 或设置为环境变量
export HEADLESS=true
./gradlew cucumber -Pfile=src/test/resources/ui/ticket.feature
```

#### 方式 2：设置环境变量
```bash
export HEADLESS=true
./gradlew cucumber -Pfile=src/test/resources/ui/ticket.feature
```

#### 方式 3：在测试代码中设置
测试代码默认读取系统属性：
```java
String headless = System.getProperty("headless", "true"); // 默认 true
```

### 浏览器显示控制

- **Headless 模式** (`true`): 无头运行，不弹出浏览器窗口，适合 CI/CD 和后台测试
- **有头模式** (`false`): 显示浏览器窗口，便于调试和可视化测试过程

### 💡 调试建议

如需在有头模式下调试特定测试，建议：

1. **运行单个测试文件**：使用 `-Pfile` 指定特定文件
2. **显示浏览器窗口**：添加 `-Dheadless=false`
3. **定位特定场景**：使用 `:行号` 运行特定行开始的场景

示例：
```bash
# 调试 UI 测试
./gradlew cucumber -Pfile=src/test/resources/ui/ticket.feature -Dheadless=false

# 调试 API 测试
./gradlew cucumber -Pfile=src/test/resources/api/ticket.feature -Dheadless=false

# 精确调试特定场景
./gradlew cucumber -Pfile=src/test/resources/ui/ticket.feature:15 -Dheadless=false
```

## 开发工作流

1. 前端开发服务器代理 API 调用到后端
2. Pre-commit hooks 强制执行 ESLint 检查
3. 使用 Prettier 进行代码格式化
4. 使用 TypeScript 和 vue-tsc 进行类型检查
5. 使用 Flyway 管理数据库迁移

## 构建输出

前端构建到 `backend/build/dist/public/` 以便与 Spring Boot 应用程序一起部署。
