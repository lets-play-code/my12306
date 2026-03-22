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
