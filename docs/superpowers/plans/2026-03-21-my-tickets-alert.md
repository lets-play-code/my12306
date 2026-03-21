# My Tickets Alert Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a minimal login-backed “我的车票” page that shows only the current user's purchased tickets, warns in red when departure is within 3 hours, and keeps departed tickets visible with an “已发车” status.

**Architecture:** Keep auth and ticket state calculation on the backend. `POST /api/sessions` returns a minimal bearer token (`Authorization: Bearer <username>`), a shared backend auth helper resolves the current user from that header, `POST /api/trains/{trainId}/tickets` stores `ticket.user`, and `GET /api/tickets/me` returns tickets sorted by departure time with backend-computed `status` / `statusText`. The frontend reuses the existing login shell, adds a dedicated `/my-tickets` route, and only renders the backend-provided state.

**Tech Stack:** Spring Boot 2.4, Spring Data JPA, Flyway, Cucumber + RESTful-cucumber + Playwright, Vue 3 + TypeScript + Axios + Tailwind + Element Plus.

---

## File Structure / Responsibility Map

### Backend production files

- Create: `backend/src/main/java/com/agiletour/entity/User.java`
  - Minimal persisted user (`id`, `username`, `password`, `fullName`)
- Create: `backend/src/main/java/com/agiletour/repo/UserRepo.java`
  - User lookup by username for login and bearer-token resolution
- Create: `backend/src/main/java/com/agiletour/controller/SessionsController.java`
  - `POST /api/sessions` and `GET /api/sessions`
- Create: `backend/src/main/java/com/agiletour/controller/TicketsController.java`
  - `GET /api/tickets/me`
- Create: `backend/src/main/java/com/agiletour/controller/AuthenticatedUserSupport.java`
  - Parse `Authorization` header, require `Bearer <username>`, load current user, throw 401 on failure
- Create: `backend/src/main/java/com/agiletour/controller/UnauthorizedException.java`
  - Explicit 401 exception type
- Create: `backend/src/main/java/com/agiletour/dto/MyTicketResponse.java`
  - API response DTO with `trainName`, `fromStation`, `toStation`, `departureTime`, `status`, `statusText`
- Create: `backend/src/main/java/com/agiletour/support/CurrentTimeProvider.java`
  - Single place that returns `LocalDateTime.now()` for status calculations
- Modify: `backend/src/main/java/com/agiletour/entity/Stop.java`
  - Add `departureTime`
- Modify: `backend/src/main/java/com/agiletour/entity/Ticket.java`
  - Add `user`
- Modify: `backend/src/main/java/com/agiletour/controller/TrainsController.java`
  - Require authenticated user for purchase and store ticket ownership
- Modify: `backend/src/main/java/com/agiletour/controller/GlobalExceptionHandler.java`
  - Map `UnauthorizedException` to HTTP 401 with `{ "message": ... }`
- Modify: `backend/src/main/java/com/agiletour/repo/TicketRepo.java`
  - Add query for current-user tickets ordered by departure time
- Modify: `backend/src/main/resources/db/migration/R__0_init_db.sql`
  - Add `user` table, `stop.departure_time`, `ticket.user_id`
- Modify: `backend/src/main/resources/db/migration/R__1_seed_demo_data.sql`
  - Seed demo users and stop times that make manual login/purchase possible

### Backend test files

- Create: `backend/src/test/resources/api/sessions.feature`
  - Focused API acceptance coverage for login and current-user lookup
- Create: `backend/src/test/resources/api/my-tickets.feature`
  - Focused API acceptance coverage for auth-required purchase, `/tickets/me`, ownership isolation, sorting, statuses
- Create: `backend/src/test/resources/ui/my-tickets.feature`
  - Focused UI acceptance coverage for login, navigation, empty state, warning banner, red row styling, departed label
- Create: `backend/src/test/java/com/agiletour/cucumber/ApiAuthSteps.java`
  - Cucumber steps to set/reset Authorization header for API features
- Create: `backend/src/test/java/com/agiletour/cucumber/TimeSteps.java`
  - Cucumber steps to freeze “current time” for deterministic status tests
- Create: `backend/src/test/java/com/agiletour/cucumber/TestTimeConfiguration.java`
  - Test-only `@Primary` fake time provider bean
- Modify: `backend/src/test/java/com/agiletour/cucumber/CucumberConfiguration.java`
  - Import test time configuration
- Modify: `backend/src/test/java/com/agiletour/cucumber/ApplicationSteps.java`
  - Clear `user` table and reset API headers between scenarios
- Modify: `backend/src/test/java/com/agiletour/cucumber/UiSteps.java`
  - Add login / open-my-tickets / class-assertion steps
- Modify: `backend/src/test/java/com/agiletour/cucumber/Browser.java`
  - Add helpers for clicking links / clicking by test id / asserting CSS classes
- Modify: `backend/src/test/java/com/agiletour/spec/JFactorySpecs.java`
  - Add `用户` spec and support new `departureTime` / `user.username` fields

### Frontend production files

- Create: `frontend/src/api/services/ticket.ts`
  - Fetch `/tickets/me`
- Create: `frontend/src/views/my-tickets/Index.vue`
  - Dedicated my-tickets page, empty state, warning banner, ticket rows, status styling
- Modify: `frontend/src/router/index.ts`
  - Add `/my-tickets`
- Modify: `frontend/src/components/layout/Header.vue`
  - Show “我的车票” link only when logged in
- Modify: `frontend/src/views/login/Index.vue`
  - Add stable `data-testid` attributes for UI tests
- Modify: `frontend/src/views/train/Index.vue`
  - Require login before purchase so the new backend auth rule has a usable frontend flow
- Modify: `frontend/src/api/index.ts`
  - Show backend 401 message (or a Chinese fallback) instead of a hard-coded English logout message
- Modify: `frontend/src/api/request.ts`
  - Remove duplicated `/api` prefix so wrapper works with axios `baseURL`
- Modify: `frontend/src/api/services/session.ts`
  - Continue using request wrapper after prefix fix; no ad-hoc URLs
- Modify: `frontend/src/services/authenticationService.ts`
  - Clear cached user when token changes so login/logout state refreshes correctly

## Shared implementation decisions to lock in before coding

- Token contract: backend accepts `Authorization: Bearer <username>` and returns the same `<username>` string as `token` from `POST /api/sessions`
- “Current user” response stays minimal: `{ "fullName": "张三" }`
- Ticket status is backend-owned:
  - `DEPARTED` → `已发车`
  - `UPCOMING_SOON` → `即将发车`
  - `UPCOMING` → `未发车`
- `/api/tickets/me` default ordering: ascending by `from.departureTime`
- Time-dependent tests use a fake `CurrentTimeProvider`; do **not** rely on wall-clock time in acceptance tests
- Frontend renders backend text and styles from `status`; it must not recalculate “3 hours” itself

---

### Task 1: Establish minimal session authentication

**Files:**
- Create: `backend/src/test/resources/api/sessions.feature`
- Create: `backend/src/test/java/com/agiletour/cucumber/ApiAuthSteps.java`
- Create: `backend/src/main/java/com/agiletour/entity/User.java`
- Create: `backend/src/main/java/com/agiletour/repo/UserRepo.java`
- Create: `backend/src/main/java/com/agiletour/controller/SessionsController.java`
- Create: `backend/src/main/java/com/agiletour/controller/AuthenticatedUserSupport.java`
- Create: `backend/src/main/java/com/agiletour/controller/UnauthorizedException.java`
- Modify: `backend/src/main/java/com/agiletour/controller/GlobalExceptionHandler.java`
- Modify: `backend/src/main/resources/db/migration/R__0_init_db.sql`
- Modify: `backend/src/main/resources/db/migration/R__1_seed_demo_data.sql`
- Modify: `backend/src/test/java/com/agiletour/cucumber/ApplicationSteps.java`
- Modify: `backend/src/test/java/com/agiletour/spec/JFactorySpecs.java`

- [ ] **Step 1: Write the failing API acceptance test for login and current-user lookup**

Create `backend/src/test/resources/api/sessions.feature` with scenarios like:

```gherkin
# language: zh-CN
功能: 登录与当前用户

  场景: 登录成功返回 token
    假如存在"用户":
      | username              | password | fullName |
      | zhangsan@example.com  | 123456   | 张三     |
    当POST "/sessions":
    """
    {
      "username": "zhangsan@example.com",
      "password": "123456"
    }
    """
    那么response should be:
    """
    : {
      code=200
      body.json.token="zhangsan@example.com"
    }
    """

  场景: 带 token 获取当前用户
    假如存在"用户":
      | username              | password | fullName |
      | zhangsan@example.com  | 123456   | 张三     |
    假如Authorization头是"Bearer zhangsan@example.com"
    当GET "/sessions"
    那么response should be:
    """
    : {
      code=200
      body.json.fullName="张三"
    }
    """

  场景: 未登录不能获取当前用户
    当GET "/sessions"
    那么response should be:
    """
    : {
      code=401
      body.json.message="请先登录"
    }
    """
    ```

Add `ApiAuthSteps.java` with a tiny step that calls `restfulStep.header("Authorization", value)` and a `@Before` hook that resets headers every scenario.

- [ ] **Step 2: Run the feature to verify it fails for the expected reason**

Run:

```bash
cd backend
./gradlew cucumber -Pfile=src/test/resources/api/sessions.feature
```

Expected:
- FAIL because `用户` spec / auth header step / `/api/sessions` endpoints do not exist yet
- No production code changed before seeing this red state

- [ ] **Step 3: Implement the smallest backend auth layer that makes the feature pass**

Implement exactly this shape:

```java
@Entity
@Getter
@Setter
public class User {
    @Id @GeneratedValue(strategy = IDENTITY)
    private long id;
    private String username;
    private String password;
    private String fullName;
}
```

```java
public interface UserRepo extends Repository<User, Long> {
    Optional<User> findByUsername(String username);
    void save(User user);
}
```

```java
@RestController
@RequestMapping("api/sessions")
public class SessionsController {
    @Autowired private UserRepo userRepo;
    @Autowired private AuthenticatedUserSupport authenticatedUserSupport;

    @PostMapping
    public Map<String, String> login(@RequestBody LoginRequest request) {
        User user = userRepo.findByUsername(request.username)
            .filter(found -> found.getPassword().equals(request.password))
            .orElseThrow(() -> new UnauthorizedException("用户名或密码错误"));
        return Map.of("token", user.getUsername());
    }

    @GetMapping
    public Map<String, String> currentUser(@RequestHeader(value = "Authorization", required = false) String authorization) {
        User user = authenticatedUserSupport.requiredUser(authorization);
        return Map.of("fullName", user.getFullName());
    }
}
```

```java
@Component
public class AuthenticatedUserSupport {
    @Autowired private UserRepo userRepo;

    public User requiredUser(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new UnauthorizedException("请先登录");
        }
        String username = authorization.substring("Bearer ".length()).trim();
        return userRepo.findByUsername(username).orElseThrow(() -> new UnauthorizedException("请先登录"));
    }
}
```

Also update:
- `GlobalExceptionHandler` to return 401 for `UnauthorizedException`
- `R__0_init_db.sql` to create `user`
- `R__1_seed_demo_data.sql` to seed at least `zhangsan@example.com / 123456 / 张三` and `lisi@example.com / 123456 / 李四`
- `ApplicationSteps.cleanDb()` to clear `user`
- `JFactorySpecs` to add `用户`

- [ ] **Step 4: Run the feature again to verify green**

Run:

```bash
cd backend
./gradlew cucumber -Pfile=src/test/resources/api/sessions.feature
```

Expected:
- PASS for all three scenarios
- `POST /api/sessions` returns `token = username`
- `GET /api/sessions` requires a bearer token and returns the matching `fullName`

- [ ] **Step 5: Commit**

```bash
git add \
  backend/src/test/resources/api/sessions.feature \
  backend/src/test/java/com/agiletour/cucumber/ApiAuthSteps.java \
  backend/src/main/java/com/agiletour/entity/User.java \
  backend/src/main/java/com/agiletour/repo/UserRepo.java \
  backend/src/main/java/com/agiletour/controller/SessionsController.java \
  backend/src/main/java/com/agiletour/controller/AuthenticatedUserSupport.java \
  backend/src/main/java/com/agiletour/controller/UnauthorizedException.java \
  backend/src/main/java/com/agiletour/controller/GlobalExceptionHandler.java \
  backend/src/main/resources/db/migration/R__0_init_db.sql \
  backend/src/main/resources/db/migration/R__1_seed_demo_data.sql \
  backend/src/test/java/com/agiletour/cucumber/ApplicationSteps.java \
  backend/src/test/java/com/agiletour/spec/JFactorySpecs.java

git commit -m "feat: add minimal session authentication"
```

### Task 2: Bind purchases to the authenticated user

**Files:**
- Create: `backend/src/test/resources/api/my-tickets.feature`
- Modify: `backend/src/main/java/com/agiletour/entity/Ticket.java`
- Modify: `backend/src/main/java/com/agiletour/controller/TrainsController.java`
- Modify: `backend/src/main/resources/db/migration/R__0_init_db.sql`
- Modify: `backend/src/test/java/com/agiletour/spec/JFactorySpecs.java`

- [ ] **Step 1: Write the failing purchase-ownership scenarios**

Start `backend/src/test/resources/api/my-tickets.feature` with these first scenarios:

```gherkin
# language: zh-CN
功能: 我的车票

  场景: 未登录不能买票
    假如存在"停靠站":
      | train.name | order | name   |
      | G201       | 1     | 北京南 |
      | G201       | 2     | 上海虹桥 |
    假如存在"座位":
      | name | train.name |
      | 2D4  | G201       |
    当POST "/trains/${车次.name[G201].id}/tickets":
    """
    {
      "from": ${停靠站.name[北京南].id},
      "to": ${停靠站.name[上海虹桥].id}
    }
    """
    那么response should be:
    """
    : {
      code=401
      body.json.message="请先登录"
    }
    """

  场景: 已登录购票后车票归属当前用户
    假如存在"用户":
      | username              | password | fullName |
      | zhangsan@example.com  | 123456   | 张三     |
    假如存在"停靠站":
      | train.name | order | name   |
      | G202       | 1     | 北京南 |
      | G202       | 2     | 上海虹桥 |
    假如存在"座位":
      | name | train.name |
      | 2D4  | G202       |
    假如Authorization头是"Bearer zhangsan@example.com"
    当POST "/trains/${车次.name[G202].id}/tickets":
    """
    {
      "from": ${停靠站.name[北京南].id},
      "to": ${停靠站.name[上海虹桥].id}
    }
    """
    那么response should be:
    """
    code=200
    """
    那么所有"车票"应为:
    """
    : | seat.name | user.username         | from.name | to.name   |
      | 2D4       | zhangsan@example.com  | 北京南    | 上海虹桥  |
    """
```

- [ ] **Step 2: Run the feature to verify the red state**

Run:

```bash
cd backend
./gradlew cucumber -Pfile=src/test/resources/api/my-tickets.feature
```

Expected:
- FAIL because `Ticket.user` is missing
- FAIL because purchase currently succeeds without auth

- [ ] **Step 3: Implement the smallest ownership change**

Update `Ticket.java`:

```java
@ManyToOne
private User user;
```

Update `TrainsController` to require auth and persist ownership:

```java
@Autowired
private AuthenticatedUserSupport authenticatedUserSupport;

@PostMapping("/trains/{trainId}/tickets")
public void buyTicket(
    @PathVariable long trainId,
    @RequestBody FromAndTo fromAndTo,
    @RequestHeader(value = "Authorization", required = false) String authorization
) {
    User currentUser = authenticatedUserSupport.requiredUser(authorization);
    var train = trainRepo.findById(trainId);
    train.getSeats().stream()
        .filter(seat -> seat.isAvailable(fromAndTo.from, fromAndTo.to))
        .findFirst()
        .ifPresentOrElse(seat -> {
            var from = train.findStop(fromAndTo.from);
            var to = train.findStop(fromAndTo.to);
            ticketRepo.save(new Ticket().setSeat(seat).setFrom(from).setTo(to).setUser(currentUser));
        }, () -> { throw new BadRequestException("票已卖完"); });
}
```

Also update `R__0_init_db.sql` so `ticket` has `user_id int NOT NULL`.

- [ ] **Step 4: Re-run the feature to verify green**

Run:

```bash
cd backend
./gradlew cucumber -Pfile=src/test/resources/api/my-tickets.feature
```

Expected:
- PASS for unauthenticated purchase rejection
- PASS for authenticated purchase ownership persistence

- [ ] **Step 5: Commit**

```bash
git add \
  backend/src/test/resources/api/my-tickets.feature \
  backend/src/main/java/com/agiletour/entity/Ticket.java \
  backend/src/main/java/com/agiletour/controller/TrainsController.java \
  backend/src/main/resources/db/migration/R__0_init_db.sql \
  backend/src/test/java/com/agiletour/spec/JFactorySpecs.java

git commit -m "feat: bind purchased tickets to current user"
```

### Task 3: Add deterministic departure-time status and `/tickets/me`

**Files:**
- Create: `backend/src/main/java/com/agiletour/controller/TicketsController.java`
- Create: `backend/src/main/java/com/agiletour/dto/MyTicketResponse.java`
- Create: `backend/src/main/java/com/agiletour/support/CurrentTimeProvider.java`
- Create: `backend/src/test/java/com/agiletour/cucumber/TimeSteps.java`
- Create: `backend/src/test/java/com/agiletour/cucumber/TestTimeConfiguration.java`
- Modify: `backend/src/main/java/com/agiletour/entity/Stop.java`
- Modify: `backend/src/main/java/com/agiletour/repo/TicketRepo.java`
- Modify: `backend/src/main/resources/db/migration/R__0_init_db.sql`
- Modify: `backend/src/main/resources/db/migration/R__1_seed_demo_data.sql`
- Modify: `backend/src/test/java/com/agiletour/cucumber/CucumberConfiguration.java`
- Modify: `backend/src/test/java/com/agiletour/cucumber/ApplicationSteps.java`
- Modify: `backend/src/test/resources/api/my-tickets.feature`

- [ ] **Step 1: Extend the API feature with failing `/tickets/me` scenarios**

Append to `backend/src/test/resources/api/my-tickets.feature`:

```gherkin
  场景: 未登录不能查看我的车票
    当GET "/tickets/me"
    那么response should be:
    """
    : {
      code=401
      body.json.message="请先登录"
    }
    """

  场景: 只返回当前用户车票并按发车时间升序返回状态
    假如当前时间是"2026-03-21T12:00:00"
    假如存在"用户":
      | username              | password | fullName |
      | zhangsan@example.com  | 123456   | 张三     |
      | lisi@example.com      | 123456   | 李四     |
    假如存在"停靠站":
      | train.name | order | name   | departureTime         |
      | G301       | 1     | 北京南 | 2026-03-21T13:30:00   |
      | G301       | 2     | 上海虹桥 | 2026-03-21T18:00:00 |
      | G302       | 1     | 南京南 | 2026-03-21T16:30:00   |
      | G302       | 2     | 杭州东 | 2026-03-21T18:00:00   |
      | G303       | 1     | 天津南 | 2026-03-21T09:00:00   |
      | G303       | 2     | 北京南 | 2026-03-21T11:00:00   |
      | G304       | 1     | 苏州北 | 2026-03-21T13:00:00   |
      | G304       | 2     | 上海虹桥 | 2026-03-21T14:00:00 |
    假如存在"座位":
      | name | train.name |
      | 2D4  | G301       |
      | 2D5  | G302       |
      | 2D6  | G303       |
      | 2D7  | G304       |
    假如存在"车票":
      | seat.name | user.username         | from.name | to.name   |
      | 2D4       | zhangsan@example.com  | 北京南    | 上海虹桥  |
      | 2D5       | zhangsan@example.com  | 南京南    | 杭州东    |
      | 2D6       | zhangsan@example.com  | 天津南    | 北京南    |
      | 2D7       | lisi@example.com      | 苏州北    | 上海虹桥  |
    假如Authorization头是"Bearer zhangsan@example.com"
    当GET "/tickets/me"
    那么response should be:
    """
    : {
      code=200
      body.json= | trainName | fromStation | toStation | departureTime       | status         | statusText |
                 | G303      | 天津南      | 北京南    | 2026-03-21T09:00:00 | DEPARTED       | 已发车     |
                 | G301      | 北京南      | 上海虹桥  | 2026-03-21T13:30:00 | UPCOMING_SOON  | 即将发车   |
                 | G302      | 南京南      | 杭州东    | 2026-03-21T16:30:00 | UPCOMING       | 未发车     |
    }
    """
```

- [ ] **Step 2: Run the feature and confirm it fails because time/status support is missing**

Run:

```bash
cd backend
./gradlew cucumber -Pfile=src/test/resources/api/my-tickets.feature
```

Expected:
- FAIL because `Stop.departureTime`, `/tickets/me`, and frozen-time steps do not exist yet

- [ ] **Step 3: Implement deterministic status support and the query endpoint**

Implement `CurrentTimeProvider`:

```java
@Component
public class CurrentTimeProvider {
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
```

Implement a test override in `TestTimeConfiguration.java`:

```java
@TestConfiguration
public class TestTimeConfiguration {
    @Bean
    @Primary
    public FakeCurrentTimeProvider currentTimeProvider() {
        return new FakeCurrentTimeProvider();
    }
}
```

Expose a mutable fake provider plus `TimeSteps`:

```java
@假如("当前时间是{string}")
public void currentTimeIs(String value) {
    fakeCurrentTimeProvider.freezeAt(LocalDateTime.parse(value));
}
```

Update `Stop.java`:

```java
private LocalDateTime departureTime;
```

Update `TicketRepo.java` with ordered lookup:

```java
List<Ticket> findByUser_IdOrderByFrom_DepartureTimeAsc(long userId);
```

Implement `MyTicketResponse` with a `from(Ticket ticket, LocalDateTime now)` factory and status mapping:

```java
public enum Status { DEPARTED, UPCOMING_SOON, UPCOMING }
```

Implement `TicketsController`:

```java
@RestController
@RequestMapping("api/tickets")
public class TicketsController {
    @Autowired private TicketRepo ticketRepo;
    @Autowired private AuthenticatedUserSupport authenticatedUserSupport;
    @Autowired private CurrentTimeProvider currentTimeProvider;

    @GetMapping("/me")
    public List<MyTicketResponse> mine(@RequestHeader(value = "Authorization", required = false) String authorization) {
        User currentUser = authenticatedUserSupport.requiredUser(authorization);
        LocalDateTime now = currentTimeProvider.now();
        return ticketRepo.findByUser_IdOrderByFrom_DepartureTimeAsc(currentUser.getId()).stream()
            .map(ticket -> MyTicketResponse.from(ticket, now))
            .collect(Collectors.toList());
    }
}
```

Import `TestTimeConfiguration` from `CucumberConfiguration`, reset fake time in a `@Before`, and update Flyway SQL to add `stop.departure_time` and populate demo stops with explicit datetimes.

- [ ] **Step 4: Run the API feature again to verify all my-ticket scenarios are green**

Run:

```bash
cd backend
./gradlew cucumber -Pfile=src/test/resources/api/my-tickets.feature
```

Expected:
- PASS for unauthenticated `/tickets/me`
- PASS for status mapping (`DEPARTED`, `UPCOMING_SOON`, `UPCOMING`)
- PASS for current-user isolation and ascending departure-time ordering

- [ ] **Step 5: Commit**

```bash
git add \
  backend/src/main/java/com/agiletour/controller/TicketsController.java \
  backend/src/main/java/com/agiletour/dto/MyTicketResponse.java \
  backend/src/main/java/com/agiletour/support/CurrentTimeProvider.java \
  backend/src/test/java/com/agiletour/cucumber/TimeSteps.java \
  backend/src/test/java/com/agiletour/cucumber/TestTimeConfiguration.java \
  backend/src/main/java/com/agiletour/entity/Stop.java \
  backend/src/main/java/com/agiletour/repo/TicketRepo.java \
  backend/src/main/resources/db/migration/R__0_init_db.sql \
  backend/src/main/resources/db/migration/R__1_seed_demo_data.sql \
  backend/src/test/java/com/agiletour/cucumber/CucumberConfiguration.java \
  backend/src/test/java/com/agiletour/cucumber/ApplicationSteps.java \
  backend/src/test/resources/api/my-tickets.feature

git commit -m "feat: add my tickets status query"
```

### Task 4: Add frontend route, navigation, login test hooks, and empty-state page

**Files:**
- Create: `frontend/src/api/services/ticket.ts`
- Create: `frontend/src/views/my-tickets/Index.vue`
- Create: `backend/src/test/resources/ui/my-tickets.feature`
- Modify: `frontend/src/router/index.ts`
- Modify: `frontend/src/components/layout/Header.vue`
- Modify: `frontend/src/views/login/Index.vue`
- Modify: `frontend/src/views/train/Index.vue`
- Modify: `frontend/src/api/index.ts`
- Modify: `frontend/src/api/request.ts`
- Modify: `frontend/src/api/services/session.ts`
- Modify: `frontend/src/services/authenticationService.ts`
- Modify: `backend/src/test/java/com/agiletour/cucumber/Browser.java`
- Modify: `backend/src/test/java/com/agiletour/cucumber/UiSteps.java`

- [ ] **Step 1: Write the failing UI scenarios for login, navigation, and empty state**

Create `backend/src/test/resources/ui/my-tickets.feature` with this minimal first slice:

```gherkin
# language: zh-CN
@ui
功能: 我的车票页面

  场景: 登录后可以进入我的车票页面并看到空状态
    假如存在"用户":
      | username              | password | fullName |
      | zhangsan@example.com  | 123456   | 张三     |
    当打开登录页面
    当用户输入用户名"zhangsan@example.com"和密码"123456"登录
    当点击链接"我的车票"
    那么页面包含如下内容:
      | 我的车票 |
      | 暂无已购车票 |
```

Add failing UI step support first:
- `打开登录页面`
- `用户输入用户名"..."和密码"..."登录`
- `点击链接"..."`

Update `Browser.java` with helpers like `clickLinkByText`, `clickByTestId`, and reuse existing `input`.

- [ ] **Step 2: Run the UI feature to verify red**

Run:

```bash
cd backend
./gradlew cucumber -Pfile=src/test/resources/ui/my-tickets.feature
```

Expected:
- FAIL because the login page lacks stable selectors
- FAIL because `/my-tickets` route and header link do not exist yet

- [ ] **Step 3: Implement the smallest frontend slice to satisfy the feature**

Fix `frontend/src/api/request.ts` first so it stops doubling `/api`:

```ts
const prefix = ''
```

Create `frontend/src/api/services/ticket.ts`:

```ts
import Request from '../request'

export interface MyTicket {
  id: number
  trainName: string
  fromStation: string
  toStation: string
  departureTime: string
  status: 'DEPARTED' | 'UPCOMING_SOON' | 'UPCOMING'
  statusText: string
}

class TicketService {
  myTickets = (): Promise<MyTicket[]> => Request.get('tickets/me')
}

export default new TicketService()
```

Create a minimal `frontend/src/views/my-tickets/Index.vue` that:
- renders title `我的车票`
- loads `ticketService.myTickets()` on mount
- shows `暂无已购车票` when the list is empty

Modify:
- `router/index.ts` to register `/my-tickets`
- `Header.vue` to show a logged-in nav link for `我的车票`
- `login/Index.vue` to add `data-testid="username"`, `data-testid="password"`, `data-testid="login-button"`
- `train/Index.vue` to short-circuit unauthenticated purchases with `showMessage("请先登录")` and route users to `/login`
- `api/index.ts` to prefer `error.response?.data?.message` for 401 handling, with `请先登录` as fallback
- `authenticationService.ts` to clear `currentUser` when token is set/cleared so the header updates after login

- [ ] **Step 4: Re-run the UI feature and verify green**

Run:

```bash
cd backend
./gradlew cucumber -Pfile=src/test/resources/ui/my-tickets.feature
```

Expected:
- PASS for login flow
- PASS for header navigation to `/my-tickets`
- PASS for empty-state rendering

- [ ] **Step 5: Commit**

```bash
git add \
  frontend/src/api/services/ticket.ts \
  frontend/src/views/my-tickets/Index.vue \
  backend/src/test/resources/ui/my-tickets.feature \
  frontend/src/router/index.ts \
  frontend/src/components/layout/Header.vue \
  frontend/src/views/login/Index.vue \
  frontend/src/views/train/Index.vue \
  frontend/src/api/index.ts \
  frontend/src/api/request.ts \
  frontend/src/api/services/session.ts \
  frontend/src/services/authenticationService.ts \
  backend/src/test/java/com/agiletour/cucumber/Browser.java \
  backend/src/test/java/com/agiletour/cucumber/UiSteps.java

git commit -m "feat: add my tickets page navigation"
```

### Task 5: Render warning banner, red highlighting, departed labels, and final regressions

**Files:**
- Modify: `frontend/src/views/my-tickets/Index.vue`
- Modify: `backend/src/test/resources/ui/my-tickets.feature`
- Modify: `backend/src/test/resources/api/ticket.feature`
- Modify: `backend/src/test/resources/ui/ticket.feature`
- Modify: `backend/src/test/java/com/agiletour/cucumber/Browser.java`
- Modify: `backend/src/test/java/com/agiletour/cucumber/UiSteps.java`
- Verify: `backend/src/test/resources/api/sessions.feature`
- Verify: `backend/src/test/resources/api/my-tickets.feature`
- Verify: `backend/src/test/resources/api/ticket.feature`
- Verify: `backend/src/test/resources/ui/my-tickets.feature`
- Verify: `backend/src/test/resources/ui/ticket.feature`

- [ ] **Step 1: Extend the UI feature with failing status/highlight scenarios**

Append to `backend/src/test/resources/ui/my-tickets.feature`:

```gherkin
  场景: 进入我的车票页面时显示提醒并区分状态
    假如当前时间是"2026-03-21T12:00:00"
    假如存在"用户":
      | username              | password | fullName |
      | zhangsan@example.com  | 123456   | 张三     |
      | lisi@example.com      | 123456   | 李四     |
    假如存在"停靠站":
      | train.name | order | name   | departureTime         |
      | G401       | 1     | 北京南 | 2026-03-21T13:00:00   |
      | G401       | 2     | 上海虹桥 | 2026-03-21T15:00:00 |
      | G402       | 1     | 南京南 | 2026-03-21T17:30:00   |
      | G402       | 2     | 杭州东 | 2026-03-21T19:00:00   |
      | G403       | 1     | 天津南 | 2026-03-21T09:00:00   |
      | G403       | 2     | 北京南 | 2026-03-21T10:30:00   |
      | G404       | 1     | 苏州北 | 2026-03-21T13:30:00   |
      | G404       | 2     | 上海虹桥 | 2026-03-21T14:30:00 |
    假如存在"座位":
      | name | train.name |
      | 2D4  | G401       |
      | 2D5  | G402       |
      | 2D6  | G403       |
      | 2D7  | G404       |
    假如存在"车票":
      | seat.name | user.username         | from.name | to.name   |
      | 2D4       | zhangsan@example.com  | 北京南    | 上海虹桥  |
      | 2D5       | zhangsan@example.com  | 南京南    | 杭州东    |
      | 2D6       | zhangsan@example.com  | 天津南    | 北京南    |
      | 2D7       | lisi@example.com      | 苏州北    | 上海虹桥  |
    当打开登录页面
    当用户输入用户名"zhangsan@example.com"和密码"123456"登录
    当点击链接"我的车票"
    那么页面包含如下内容:
      | 你有 1 张车票将在 3 小时内发车，请留意出行时间 |
      | G401 北京南-上海虹桥 |
      | G402 南京南-杭州东 |
      | G403 天津南-北京南 |
      | 即将发车 |
      | 未发车 |
      | 已发车 |
    那么页面不包含如下内容:
      | G404 苏州北-上海虹桥 |
    那么元素"upcoming-soon-alert"包含class"bg-red-"
    那么元素"ticket-row-G401"包含class"border-red-"
    那么元素"ticket-row-G403"包含class"text-gray-"
```

Use predictable `data-testid` values in the frontend:
- `upcoming-soon-alert`
- `ticket-row-<trainName>`

- [ ] **Step 2: Run the UI feature and verify the red state**

Run:

```bash
cd backend
./gradlew cucumber -Pfile=src/test/resources/ui/my-tickets.feature
```

Expected:
- FAIL because the page currently has no banner, no status rows, and no CSS-class assertions

- [ ] **Step 3: Implement the final UI rendering with backend-driven status**

Update `frontend/src/views/my-tickets/Index.vue` to:
- fetch `myTickets`
- derive `upcomingSoonTickets = tickets.filter(ticket => ticket.status === 'UPCOMING_SOON')`
- show banner only when `upcomingSoonTickets.length > 0`
- render rows sorted exactly as returned by backend
- map styles with small helpers:

```ts
const rowClass = (ticket: MyTicket) => {
  if (ticket.status === 'UPCOMING_SOON') return 'border-red-200 bg-red-50 text-red-700'
  if (ticket.status === 'DEPARTED') return 'border-gray-200 bg-gray-50 text-gray-500'
  return 'border-gray-200 bg-white text-gray-900'
}
```

Render readable content like:

```vue
<div :data-testid="`ticket-row-${ticket.trainName}`" :class="rowClass(ticket)">
  <div>{{ ticket.trainName }} {{ ticket.fromStation }}-{{ ticket.toStation }}</div>
  <div>{{ formatDeparture(ticket.departureTime) }}</div>
  <div>{{ ticket.statusText }}</div>
</div>
```

Add browser helpers to assert a `data-testid` contains a class fragment.

- [ ] **Step 4: Update the pre-existing purchase acceptance tests, then run focused regressions until all are green**

Before running regressions:
- update `backend/src/test/resources/api/ticket.feature` so every purchase scenario seeds a user and sets `Authorization` before `POST /trains/.../tickets`
- update `backend/src/test/resources/ui/ticket.feature` so every scenario that clicks `购票` logs in first (or establishes the same authenticated browser state through the UI)

Then run, in this order:

```bash
cd backend
./gradlew cucumber -Pfile=src/test/resources/ui/my-tickets.feature
./gradlew cucumber -Pfile=src/test/resources/api/sessions.feature
./gradlew cucumber -Pfile=src/test/resources/api/my-tickets.feature
./gradlew cucumber -Pfile=src/test/resources/api/ticket.feature
./gradlew cucumber -Pfile=src/test/resources/ui/ticket.feature
```

Expected:
- New UI feature passes
- Session API feature still passes
- My-tickets API feature still passes
- Existing ticket API and UI regressions still pass after adding auth ownership and new header/nav code

- [ ] **Step 5: Commit**

```bash
git add \
  frontend/src/views/my-tickets/Index.vue \
  backend/src/test/resources/ui/my-tickets.feature \
  backend/src/test/resources/api/ticket.feature \
  backend/src/test/resources/ui/ticket.feature \
  backend/src/test/java/com/agiletour/cucumber/Browser.java \
  backend/src/test/java/com/agiletour/cucumber/UiSteps.java

git commit -m "feat: highlight imminent departures in my tickets"
```

## Notes for the implementing agent

- Keep controller methods under 50 lines by pushing parsing/mapping into support classes or DTO factories
- Do **not** introduce Spring Security for this task; the explicit auth helper is enough for the accepted scope
- Do **not** let the frontend compute the 3-hour window; use backend status only
- If existing `api/ticket.feature` starts failing because purchase now requires auth, update that feature intentionally rather than weakening auth checks. Prefer seeding a user and setting `Authorization` headers in existing purchase scenarios.
- If the UI feature becomes brittle because of locale-formatted time strings, assert on route names / statuses / banner text / train labels first, and keep the formatted departure text assertion minimal
