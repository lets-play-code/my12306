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
