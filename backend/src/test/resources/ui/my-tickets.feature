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
