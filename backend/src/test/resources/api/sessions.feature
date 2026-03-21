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

  场景: 登录失败返回 401
    假如存在"用户":
      | username              | password | fullName |
      | zhangsan@example.com  | 123456   | 张三     |
    当POST "/sessions":
    """
    {
      "username": "zhangsan@example.com",
      "password": "wrong"
    }
    """
    那么response should be:
    """
    : {
      code=401
      body.json.message="用户名或密码错误"
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

  场景: 无效 token 不能获取当前用户
    假如Authorization头是"Bearer nonexistent@example.com"
    当GET "/sessions"
    那么response should be:
    """
    : {
      code=401
      body.json.message="请先登录"
    }
    """
