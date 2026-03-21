# language: zh-CN
@ui
功能: 登录

  场景: 登录成功
    假如打开登录页面
    当输入用户名"testuser"和密码"password123"
    当点击"Sign in"按钮
    那么等待2秒后当前页面URL包含"/"
