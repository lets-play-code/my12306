# 已购票临近发车提醒功能实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现用户登录认证和已购票临近发车提醒功能

**Architecture:** 
- 后端：Spring Boot + JPA，新增 User 实体，使用 JWT Token 认证
- 前端：Vue 3 + TypeScript + Element Plus，el-alert 组件显示警告横幅
- 数据库：Flyway 迁移脚本添加字段

**Tech Stack:** Spring Boot 2.4.4, MySQL, JWT (jjwt), Vue 3, Element Plus, Playwright

---

## 文件结构

```
backend/
├── src/main/java/com/agiletour/
│   ├── entity/
│   │   ├── User.java          # 新增
│   │   ├── Train.java         # 修改: 添加 departureTime
│   │   └── Ticket.java        # 修改: 添加 user, travelDate
│   ├── repo/
│   │   ├── UserRepo.java      # 新增
│   │   └── TicketRepo.java    # 修改: 添加查询方法
│   ├── controller/
│   │   ├── AuthController.java    # 新增: 登录
│   │   ├── SessionController.java # 新增: 当前用户
│   │   └── TicketController.java  # 新增: 已购票 API
│   ├── dto/
│   │   ├── LoginRequest.java      # 新增
│   │   ├── TokenResponse.java     # 新增
│   │   ├── TicketResponse.java     # 新增
│   │   └── UpcomingTicketResponse.java # 新增
│   ├── security/
│   │   ├── JwtUtil.java           # 新增: JWT 工具类
│   │   └── JwtInterceptor.java    # 新增: Token 拦截器
│   └── config/
│       └── WebConfig.java         # 修改: 注册拦截器
├── src/main/resources/
│   ├── db/migration/
│   │   ├── V2__add_user_table.sql         # 新增
│   │   ├── V3__add_train_departure_time.sql  # 新增
│   │   └── V4__add_ticket_user_and_date.sql  # 新增
│   └── application.yaml           # 修改: 添加 jwt 配置
└── build.gradle                   # 修改: 添加 jjwt 依赖

frontend/
├── src/
│   ├── api/
│   │   └── services/
│   │       └── tickets.ts        # 新增: 已购票 API
│   ├── services/
│   │   └── authenticationService.ts  # 修改: JWT Token
│   ├── views/train/
│   │   └── Index.vue             # 修改: 警告横幅 + 日期选择
│   └── model/
│       └── LoginUser.ts          # 新增: 登录用户类型
└── src/test/resources/
    └── api/ticket.feature        # 新增: API 测试
```

---

## Phase 1: 基础 - 用户认证 + 数据模型

### Task 1: 数据库迁移 - 新增用户表

**Files:**
- Create: `backend/src/main/resources/db/migration/V2__add_user_table.sql`
- Modify: `backend/src/main/resources/db/migration/R__1_seed_demo_data.sql`

- [ ] **Step 1: 创建用户表迁移脚本**

```sql
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `full_name` VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入测试用户: alice/password123, bob/password456
INSERT INTO `user` (username, password, full_name) VALUES 
    ('alice', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Alice Wang'),
    ('bob', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Bob Chen');
```
> 注：密码 BCrypt 加密后的值对应 "password123"

- [ ] **Step 2: 在种子数据中添加测试用户**

在 `R__1_seed_demo_data.sql` 末尾添加：
```sql
-- 清理并插入测试用户（如果不存在）
DELETE FROM `user` WHERE username IN ('alice', 'bob');
INSERT INTO `user` (username, password, full_name) VALUES 
    ('alice', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Alice Wang'),
    ('bob', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Bob Chen');
```

- [ ] **Step 3: 提交**

```bash
cd backend
git add src/main/resources/db/migration/V2__add_user_table.sql src/main/resources/db/migration/R__1_seed_demo_data.sql
git commit -m "feat: add user table migration"
```

---

### Task 2: 数据库迁移 - 新增火车发车时间

**Files:**
- Create: `backend/src/main/resources/db/migration/V3__add_train_departure_time.sql`

- [ ] **Step 1: 创建火车发车时间迁移脚本**

```sql
ALTER TABLE `train` ADD COLUMN `departure_time` TIME NOT NULL DEFAULT '08:00:00';

-- 更新现有火车的发车时间
UPDATE `train` SET `departure_time` = '08:00:00' WHERE `name` = 'G102';
UPDATE `train` SET `departure_time` = '14:30:00' WHERE `name` = 'G103';
```

- [ ] **Step 2: 提交**

```bash
cd backend
git add src/main/resources/db/migration/V3__add_train_departure_time.sql
git commit -m "feat: add train departure_time field"
```

---

### Task 3: 数据库迁移 - Ticket 关联用户和日期

**Files:**
- Create: `backend/src/main/resources/db/migration/V4__add_ticket_user_and_date.sql`

- [ ] **Step 1: 创建 Ticket 关联字段迁移脚本**

```sql
ALTER TABLE `ticket` ADD COLUMN `user_id` BIGINT NOT NULL;
ALTER TABLE `ticket` ADD COLUMN `travel_date` DATE NOT NULL;

-- 添加外键约束
ALTER TABLE `ticket` ADD CONSTRAINT `fk_ticket_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`);
```

- [ ] **Step 2: 提交**

```bash
cd backend
git add src/main/resources/db/migration/V4__add_ticket_user_and_date.sql
git commit -m "feat: add user_id and travel_date to ticket"
```

---

### Task 4: 后端 - User 实体和 UserRepo

**Files:**
- Create: `backend/src/main/java/com/agiletour/entity/User.java`
- Create: `backend/src/main/java/com/agiletour/repo/UserRepo.java`

- [ ] **Step 1: 创建 User 实体**

```java
package com.agiletour.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "user")
@Accessors(chain = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @OneToMany(mappedBy = "user")
    private List<Ticket> tickets = new ArrayList<>();
}
```

- [ ] **Step 2: 创建 UserRepo**

```java
package com.agiletour.repo;

import com.agiletour.entity.User;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface UserRepo extends Repository<User, Long> {
    Optional<User> findByUsername(String username);
}
```

- [ ] **Step 3: 提交**

```bash
cd backend
git add src/main/java/com/agiletour/entity/User.java src/main/java/com/agiletour/repo/UserRepo.java
git commit -m "feat: add User entity and UserRepo"
```

---

### Task 5: 后端 - JWT 认证工具类

**Files:**
- Create: `backend/src/main/java/com/agiletour/security/JwtUtil.java`

- [ ] **Step 1: 创建 JWT 工具类**

```java
package com.agiletour.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret:mySecretKey123456789012345678901234567890}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 24 hours in milliseconds
    private long expiration;

    public String generateToken(long userId, String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("username", username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
```

- [ ] **Step 2: 添加 jjwt 依赖到 build.gradle**

```groovy
// 在 dependencies 中添加
implementation 'io.jsonwebtoken:jjwt-api:0.11.2'
runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.11.2'
runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.11.2'
```

- [ ] **Step 3: 在 application.yaml 中添加 JWT 配置**

```yaml
jwt:
  secret: ${JWT_SECRET:mySecretKey123456789012345678901234567890}
  expiration: 86400000
```

- [ ] **Step 4: 提交**

```bash
cd backend
git add src/main/java/com/agiletour/security/JwtUtil.java build.gradle src/main/resources/application.yaml
git commit -m "feat: add JWT utility class"
```

---

### Task 6: 后端 - DTO 类

**Files:**
- Create: `backend/src/main/java/com/agiletour/dto/LoginRequest.java`
- Create: `backend/src/main/java/com/agiletour/dto/TokenResponse.java`
- Create: `backend/src/main/java/com/agiletour/dto/CurrentUserResponse.java`
- Create: `backend/src/main/java/com/agiletour/dto/TicketResponse.java`
- Create: `backend/src/main/java/com/agiletour/dto/UpcomingTicketResponse.java`

- [ ] **Step 1: 创建 LoginRequest**

```java
package com.agiletour.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRequest {
    private String username;
    private String password;
}
```

- [ ] **Step 2: 创建 TokenResponse**

```java
package com.agiletour.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResponse {
    private String token;
    private String fullName;
}
```

- [ ] **Step 3: 创建 CurrentUserResponse**

```java
package com.agiletour.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CurrentUserResponse {
    private String fullName;
}
```

- [ ] **Step 4: 创建 TicketResponse**

```java
package com.agiletour.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TicketResponse {
    private long id;
    private String trainName;
    private String departureTime;
    private String travelDate;
    private String fromStation;
    private String toStation;
}
```

- [ ] **Step 5: 创建 UpcomingTicketResponse**

```java
package com.agiletour.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpcomingTicketResponse {
    private long id;
    private String trainName;
    private String departureTime;
    private String travelDate;
    private String fromStation;
    private String toStation;
    private int remainingMinutes;
}
```

- [ ] **Step 6: 提交**

```bash
cd backend
git add src/main/java/com/agiletour/dto/*.java
git commit -m "feat: add DTO classes"
```

---

### Task 7: 后端 - AuthController 登录接口

**Files:**
- Create: `backend/src/main/java/com/agiletour/controller/AuthController.java`

- [ ] **Step 1: 创建 AuthController**

```java
package com.agiletour.controller;

import com.agiletour.dto.LoginRequest;
import com.agiletour.dto.TokenResponse;
import com.agiletour.entity.User;
import com.agiletour.repo.UserRepo;
import com.agiletour.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
public class AuthController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/sessions")
    public TokenResponse login(@RequestBody LoginRequest request) {
        User user = userRepo.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadRequestException("用户名或密码错误"));

        // 简单的密码比较（生产环境应使用 BCrypt）
        if (!user.getPassword().equals(request.getPassword())) {
            throw new BadRequestException("用户名或密码错误");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        return new TokenResponse(token, user.getFullName());
    }

    @GetMapping("/sessions")
    public CurrentUserResponse getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        long userId = jwtUtil.getUserIdFromToken(token);
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new BadRequestException("用户不存在"));
        return new CurrentUserResponse(user.getFullName());
    }
}
```

> 注意：需要在 BadRequestException 中添加构造函数或使用现有构造函数

- [ ] **Step 2: 提交**

```bash
cd backend
git add src/main/java/com/agiletour/controller/AuthController.java
git commit -m "feat: add AuthController for login"
```

---

### Task 8: 后端 - 修改 Ticket 和 Train 实体

**Files:**
- Modify: `backend/src/main/java/com/agiletour/entity/Ticket.java`
- Modify: `backend/src/main/java/com/agiletour/entity/Train.java`

- [ ] **Step 1: 修改 Ticket 实体**

在 Ticket.java 中添加 user 和 travelDate 字段：

```java
// 添加导入
import java.time.LocalDate;

// 在现有字段后添加
@ManyToOne
private User user;

@Column(name = "travel_date", nullable = false)
private LocalDate travelDate;
```

- [ ] **Step 2: 修改 Train 实体**

在 Train.java 中添加 departureTime 字段：

```java
// 添加导入
import java.time.LocalTime;

// 在 name 字段后添加
@Column(name = "departure_time", nullable = false)
private LocalTime departureTime = LocalTime.of(8, 0);
```

- [ ] **Step 3: 提交**

```bash
cd backend
git add src/main/java/com/agiletour/entity/Ticket.java src/main/java/com/agiletour/entity/Train.java
git commit -m "feat: add user, travelDate to Ticket and departureTime to Train"
```

---

### Task 9: 后端 - JWT 拦截器

**Files:**
- Create: `backend/src/main/java/com/agiletour/security/JwtInterceptor.java`
- Modify: `backend/src/main/java/com/agiletour/config/WebConfig.java`

- [ ] **Step 1: 创建 JwtInterceptor**

```java
package com.agiletour.security;

import com.agiletour.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepo userRepo;

    // 不需要认证的路径
    private static final String[] EXCLUDE_PATHS = {
        "/api/sessions",      // 登录接口
        "/api/trains"         // 查询火车（不需要登录）
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();

        // 检查是否是需要排除的路径
        for (String excludePath : EXCLUDE_PATHS) {
            if (path.equals(excludePath) || (excludePath.endsWith("/") && path.startsWith(excludePath))) {
                return true;
            }
        }

        // POST /api/trains/{id}/tickets 需要认证
        if (request.getMethod().equals("POST") && path.matches("/api/trains/\\d+/tickets")) {
            // 继续检查 Token
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"未授权，请先登录\"}");
            return false;
        }

        String token = authHeader.replace("Bearer ", "");
        if (!jwtUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Token无效或已过期\"}");
            return false;
        }

        // 将用户 ID 存入请求属性，供后续使用
        long userId = jwtUtil.getUserIdFromToken(token);
        request.setAttribute("userId", userId);

        return true;
    }
}
```

- [ ] **Step 2: 创建/修改 WebConfig**

```java
package com.agiletour.config;

import com.agiletour.security.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**");
    }
}
```

- [ ] **Step 3: 提交**

```bash
cd backend
git add src/main/java/com/agiletour/security/JwtInterceptor.java src/main/java/com/agiletour/config/WebConfig.java
git commit -m "feat: add JWT interceptor for authentication"
```

---

### Task 10: 后端 - 修改 TrainsController.buyTicket

**Files:**
- Modify: `backend/src/main/java/com/agiletour/controller/TrainsController.java`

- [ ] **Step 1: 修改 buyTicket 方法**

修改 FromAndTo 类，添加 travelDate：

```java
public static class FromAndTo {
    public int from;
    public int to;
    public String travelDate;  // 新增
}
```

修改 buyTicket 方法：

```java
@PostMapping("/trains/{trainId}/tickets")
public void buyTicket(
    @PathVariable long trainId,
    @RequestBody FromAndTo fromAndTo,
    HttpServletRequest request
) {
    // 获取当前用户 ID
    long userId = (Long) request.getAttribute("userId");
    
    var train = trainRepo.findById(trainId);
    train.getSeats().stream().filter(seat -> seat.isAvailable(fromAndTo.from, fromAndTo.to))
            .findFirst().ifPresentOrElse(seat -> {
                var from = train.findStop(fromAndTo.from);
                var to = train.findStop(fromAndTo.to);
                
                // 查找用户
                User user = userRepo.findById(userId).orElseThrow();
                
                // 解析日期
                LocalDate travelDate = LocalDate.parse(fromAndTo.travelDate);
                
                Ticket ticket = new Ticket()
                    .setSeat(seat)
                    .setFrom(from)
                    .setTo(to)
                    .setUser(user)
                    .setTravelDate(travelDate);
                
                ticketRepo.save(ticket);
            }, () -> {
                throw new BadRequestException("票已卖完");
            });
}
```

> 需要添加 HttpServletRequest 和 LocalDate 导入

- [ ] **Step 2: 提交**

```bash
cd backend
git add src/main/java/com/agiletour/controller/TrainsController.java
git commit -m "feat: update buyTicket to accept travelDate and userId"
```

---

### Task 11: 后端 - TicketController 已购票 API

**Files:**
- Create: `backend/src/main/java/com/agiletour/controller/TicketController.java`
- Modify: `backend/src/main/java/com/agiletour/repo/TicketRepo.java`

- [ ] **Step 1: 修改 TicketRepo 添加查询方法**

```java
package com.agiletour.repo;

import com.agiletour.entity.Ticket;
import org.springframework.data.repository.Repository;

import java.time.LocalDateTime;
import java.util.List;

public interface TicketRepo extends Repository<Ticket, Long> {

    List<Ticket> findAll();

    void save(Ticket ticket);

    // 新增方法：按用户查询所有票
    List<Ticket> findByUserId(long userId);

    // 新增方法：查询用户的所有票（按出发日期排序）
    List<Ticket> findByUserIdOrderByTravelDateAsc(long userId);
}
```

- [ ] **Step 2: 创建 TicketController**

```java
package com.agiletour.controller;

import com.agiletour.dto.TicketResponse;
import com.agiletour.dto.UpcomingTicketResponse;
import com.agiletour.entity.Ticket;
import com.agiletour.repo.TicketRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/tickets")
public class TicketController {

    @Autowired
    private TicketRepo ticketRepo;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    @GetMapping("/my")
    public List<TicketResponse> getMyTickets(HttpServletRequest request) {
        long userId = (Long) request.getAttribute("userId");
        List<Ticket> tickets = ticketRepo.findByUserIdOrderByTravelDateAsc(userId);

        return tickets.stream().map(ticket -> {
            String departureTime = ticket.getSeat().getTrain().getDepartureTime().format(TIME_FORMATTER);
            return new TicketResponse(
                ticket.getId(),
                ticket.getSeat().getTrain().getName(),
                departureTime,
                ticket.getTravelDate().toString(),
                ticket.getFrom().getName(),
                ticket.getTo().getName()
            );
        }).collect(Collectors.toList());
    }

    @GetMapping("/upcoming")
    public List<UpcomingTicketResponse> getUpcomingTickets(HttpServletRequest request) {
        long userId = (Long) request.getAttribute("userId");
        List<Ticket> tickets = ticketRepo.findByUserIdOrderByTravelDateAsc(userId);

        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalTime currentTime = now.toLocalTime();

        return tickets.stream()
            .filter(ticket -> {
                LocalDate travelDate = ticket.getTravelDate();
                LocalTime departureTime = ticket.getSeat().getTrain().getDepartureTime();
                
                // 计算发车时间点
                LocalDateTime departureDateTime = LocalDateTime.of(travelDate, departureTime);
                
                // 只返回未来的车次
                if (departureDateTime.isBefore(now)) {
                    return false;
                }
                
                // 计算剩余分钟数
                long minutesUntilDeparture = java.time.Duration.between(now, departureDateTime).toMinutes();
                
                // 只返回 3 小时（180 分钟）内的车次
                return minutesUntilDeparture <= 180;
            })
            .map(ticket -> {
                LocalDate travelDate = ticket.getTravelDate();
                LocalTime departureTime = ticket.getSeat().getTrain().getDepartureTime();
                LocalDateTime departureDateTime = LocalDateTime.of(travelDate, departureTime);
                int remainingMinutes = (int) java.time.Duration.between(now, departureDateTime).toMinutes();
                
                String depTime = departureTime.format(TIME_FORMATTER);
                return new UpcomingTicketResponse(
                    ticket.getId(),
                    ticket.getSeat().getTrain().getName(),
                    depTime,
                    travelDate.toString(),
                    ticket.getFrom().getName(),
                    ticket.getTo().getName(),
                    remainingMinutes
                );
            })
            .collect(Collectors.toList());
    }
}
```

- [ ] **Step 3: 提交**

```bash
cd backend
git add src/main/java/com/agiletour/repo/TicketRepo.java src/main/java/com/agiletour/controller/TicketController.java
git commit -m "feat: add TicketController for user tickets API"
```

---

## Phase 2: 前端 - 认证服务 + 警告横幅

### Task 12: 前端 - 更新认证服务使用 JWT

**Files:**
- Modify: `frontend/src/services/authenticationService.ts`
- Create: `frontend/src/model/LoginUser.ts`

- [ ] **Step 1: 创建 LoginUser 类型**

```typescript
export interface LoginUser {
    username: string;
    password: string;
}
```

- [ ] **Step 2: 更新 authenticationService**

```typescript
import session, { CurrentUser, TokenResponse } from '@/api/services/session'
import { LoginUser } from '@/model/LoginUser'

const TOKEN = 'token'
const USER_NAME = 'userName'

class AuthenticationService {
    currentUser: CurrentUser | undefined

    login = async (user: LoginUser): Promise<void> => {
        const response = await session.login(user) as TokenResponse
        localStorage.setItem(TOKEN, response.token)
        localStorage.setItem(USER_NAME, response.fullName)
    }

    getCurrentUser = async (): Promise<CurrentUser | undefined> => {
        if (!this.currentUser) {
            const token = this.getToken()
            if (token) {
                // 有 token 但无 currentUser 时，从本地获取姓名
                const fullName = localStorage.getItem(USER_NAME)
                if (fullName) {
                    this.currentUser = { fullName }
                }
            }
        }
        return this.currentUser
    }

    getToken = (): string | null => {
        return localStorage.getItem(TOKEN)
    }
    
    clearToken = () => {
        localStorage.removeItem(TOKEN)
        localStorage.removeItem(USER_NAME)
        this.currentUser = undefined
    }
    
    isLoggedIn = (): boolean => {
        return !!this.getToken()
    }
}

export default new AuthenticationService()
```

- [ ] **Step 3: 更新 session.ts API**

```typescript
import Request from '../request'

const modelName = 'sessions'
export interface TokenResponse {
    token: string
    fullName: string
}

export interface CurrentUser {
    fullName: string
}

class Session {
    login = (user: any): Promise<TokenResponse> => Request.post(`${modelName}`, user)

    currentUser = (): Promise<CurrentUser> => Request.get(`${modelName}`)
}

export default new Session()
```

- [ ] **Step 4: 提交**

```bash
cd frontend
git add src/services/authenticationService.ts src/api/services/session.ts src/model/LoginUser.ts
git commit -m "feat: update auth service for JWT token"
```

---

### Task 13: 前端 - 新增 tickets API

**Files:**
- Create: `frontend/src/api/services/tickets.ts`

- [ ] **Step 1: 创建 tickets API 服务**

```typescript
import Request from '../request'

export interface TicketResponse {
    id: number
    trainName: string
    departureTime: string
    travelDate: string
    fromStation: string
    toStation: string
}

export interface UpcomingTicketResponse extends TicketResponse {
    remainingMinutes: number
}

class Tickets {
    getMyTickets = (): Promise<TicketResponse[]> => {
        return Request.get('/tickets/my')
    }

    getUpcomingTickets = (): Promise<UpcomingTicketResponse[]> => {
        return Request.get('/tickets/upcoming')
    }
}

export default new Tickets()
```

- [ ] **Step 2: 提交**

```bash
cd frontend
git add src/api/services/tickets.ts
git commit -m "feat: add tickets API service"
```

---

### Task 14: 前端 - Index.vue 添加警告横幅和日期选择

**Files:**
- Modify: `frontend/src/views/train/Index.vue`

- [ ] **Step 1: 添加警告横幅和日期选择器**

在 `<template>` 部分，添加警告横幅（在 `<div class="bg-white...">` 内部最前面）：

```html
<template>
    <div class="bg-white shadow-md rounded-lg p-6">
        <!-- 临近车次警告横幅 -->
        <el-alert
            v-if="showUpcomingAlert && upcomingTickets.length > 0"
            type="error"
            title="⚠️ 温馨提示"
            :closable="true"
            @close="handleCloseAlert"
            class="mb-4"
        >
            <template #default>
                <div class="text-sm">
                    <p class="mb-2 font-medium">您购买的车次即将发车，请注意出行时间：</p>
                    <ul class="list-disc list-inside space-y-1">
                        <li v-for="ticket in upcomingTickets" :key="ticket.id" class="text-red-600">
                            <span class="font-bold">{{ ticket.trainName }}</span> |
                            {{ ticket.travelDate }} |
                            {{ formatRemainingTime(ticket.remainingMinutes) }}后发车 |
                            {{ ticket.fromStation }} → {{ ticket.toStation }}
                        </li>
                    </ul>
                </div>
            </template>
        </el-alert>

        <h1 class="text-2xl font-bold mb-4">火车信息列表</h1>
        
        <!-- 查询表单 - 添加日期选择 -->
        <div class="mb-4 flex gap-4 items-end">
            <!-- 现有表单内容保持不变 -->
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">出发日期</label>
                <el-date-picker
                    v-model="travelDate"
                    type="date"
                    placeholder="选择日期"
                    :disabled-date="disabledDate"
                    format="YYYY-MM-DD"
                    value-format="YYYY-MM-DD"
                    data-testid="travel-date"
                    class="w-36"
                />
            </div>
            <!-- ... 其余表单内容保持不变 ... -->
        </div>
        <!-- ... 其余内容保持不变 ... -->
    </div>
</template>
```

在 `<script setup>` 部分：

```typescript
import { ref, onMounted } from 'vue';
import axios from "@/api/index";
import { showMessage } from "@/main";
import ticketsService, { UpcomingTicketResponse } from "@/api/services/tickets";
import authentication from "@/services/authenticationService";

// 新增状态
const showUpcomingAlert = ref(true);
const upcomingTickets = ref<UpcomingTicketResponse[]>([]);
const travelDate = ref<string>(new Date().toISOString().split('T')[0]);

// 新增方法
const handleCloseAlert = () => {
    showUpcomingAlert.value = false;
};

const formatRemainingTime = (minutes: number): string => {
    if (minutes >= 60) {
        const hours = Math.floor(minutes / 60);
        const mins = minutes % 60;
        return `${hours}小时${mins > 0 ? mins + '分钟' : ''}`;
    }
    return `${minutes}分钟`;
};

const disabledDate = (time: Date) => {
    return time.getTime() < Date.now() - 8.64e7; // 禁用今天之前的日期
};

// 在 onMounted 中获取临近提醒
onMounted(async () => {
    if (authentication.isLoggedIn()) {
        try {
            const tickets = await ticketsService.getUpcomingTickets();
            upcomingTickets.value = tickets || [];
        } catch (e) {
            console.log('获取临近车次失败:', e);
        }
    }
});
```

修改 handleClick 方法，将 travelDate 添加到请求体：

```typescript
const handleClick = async (row: any) => {
    try {
        let fromStopId, toStopId;
        
        if (fromStation.value && toStation.value) {
            const fromStop = row.stops.find((stop: any) => stop.name === fromStation.value);
            const toStop = row.stops.find((stop: any) => stop.name === toStation.value);
            
            if (fromStop && toStop) {
                fromStopId = fromStop.id;
                toStopId = toStop.id;
            } else {
                fromStopId = row.stops.at(0).id;
                toStopId = row.stops.at(-1).id;
            }
        } else {
            fromStopId = row.stops.at(0).id;
            toStopId = row.stops.at(-1).id;
        }
        
        // 添加 travelDate 参数
        const res = await axios.post('/trains/'+row.id+'/tickets', {
            from: fromStopId,
            to: toStopId,
            travelDate: travelDate.value  // 新增
        });
        showMessage("购票成功");
        await handleQuery();
        
        // 购票成功后刷新临近提醒
        if (authentication.isLoggedIn()) {
            const tickets = await ticketsService.getUpcomingTickets();
            upcomingTickets.value = tickets || [];
            showUpcomingAlert.value = true;
        }
    } catch (e: any) {
        console.log(e);
        if (e.response && e.response.data && e.response.data.message) {
            showMessage(e.response.data.message);
        } else {
            showMessage("购票失败，请稍后重试");
        }
    }
};
```

- [ ] **Step 2: 提交**

```bash
cd frontend
git add src/views/train/Index.vue
git commit -m "feat: add upcoming ticket alert banner"
```

---

### Task 15: 前端 - 更新 401 响应处理

**Files:**
- Modify: `frontend/src/api/request.ts`

- [ ] **Step 1: 更新 request.ts 统一处理 401**

```typescript
import axios, { InternalAxiosRequestConfig } from 'axios'
import authentication from '@/services/authenticationService'
import { showMessage } from '@/main';

const instance = axios.create({
    baseURL: '/api',
    headers: {
        'Content-Type': 'application/json',
    },
    timeout: 1000 * 30
})

instance.interceptors.response.use(
    res => {
        return Promise.resolve(res.data);
    },
    error => {
        if (error.response?.status === 401) {
            authentication.clearToken();
            // 如果不是登录页面，跳转到登录
            if (!window.location.pathname.includes('/login')) {
                showMessage('登录已过期，请重新登录');
                window.location.href = '/login';
            }
        }
        return Promise.reject(error);
    }
)

instance.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
        const token = authentication.getToken();
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
)

export default instance
```

- [ ] **Step 2: 提交**

```bash
cd frontend
git add src/api/request.ts
git commit -m "feat: handle 401 response with redirect to login"
```

---

## 验证步骤

完成所有 Task 后，运行以下验证：

### 1. 启动 Docker MySQL
```bash
cd env
docker compose up -d mysql
```

### 2. 运行 Flyway 迁移
```bash
cd backend
./gradlew flywayMigrate
```

### 3. 启动后端
```bash
./gradlew bootRun
```

### 4. 测试登录 API
```bash
curl -X POST http://localhost:8080/api/sessions \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"password123"}'
```

### 5. 启动前端
```bash
cd frontend
pnpm run dev
```

### 6. 运行 UI 测试
```bash
cd backend
./gradlew cucumber -Pfile=src/test/resources/api/ticket.feature
```

---

## 总结

| Phase | Tasks | 说明 |
|-------|-------|------|
| 1 | 1-11 | 后端：数据库迁移、User 实体、JWT 认证、API |
| 2 | 12-15 | 前端：JWT 认证、tickets API、警告横幅 |
