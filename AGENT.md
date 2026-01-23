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

## 开发命令

### 后端
```bash
cd backend
./gradlew build          # 构建应用程序
./gradlew test           # 运行单元测试
./gradlew cucumber       # 运行 Cucumber 验收测试
./gradlew bootRun        # 运行应用程序
```

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

## 用 Docker 启动依赖环境（MySQL/MockServer/PhpMyAdmin/WebDriver）

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
docker compose --env-file env/.env -f env/docker-compose.yml up -d mysql mock-server phpmyadmin web-driver
# 如为 Apple Silicon（M1/M2/M3），使用 ARM 版本 WebDriver：
docker compose --env-file env/.env -f env/docker-compose.yml up -d mysql mock-server phpmyadmin web-driver-arm
```

或进入 `env/` 目录后直接运行（此时会自动读取 `env/.env`）：
```bash
cd env
docker compose up -d mysql mock-server phpmyadmin web-driver
```

可选：需要录屏时再启动视频容器：
```bash
docker compose --env-file env/.env -f env/docker-compose.yml up -d web-driver-video
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
- Selenium VNC：`http://localhost:7900/?autoconnect=1&resize=scale`（无密码）

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

> 注意：`env/docker-compose.yml` 同时包含 `web-driver`（x86_64）与 `web-driver-arm`（ARM）两个服务，按你的机器架构二选一启动即可。

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

## 开发工作流

1. 前端开发服务器代理 API 调用到后端
2. Pre-commit hooks 强制执行 ESLint 检查
3. 使用 Prettier 进行代码格式化
4. 使用 TypeScript 和 vue-tsc 进行类型检查
5. 使用 Flyway 管理数据库迁移

## 构建输出

前端构建到 `backend/build/dist/public/` 以便与 Spring Boot 应用程序一起部署。