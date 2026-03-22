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
      body.json= | id | trainName | fromStation | toStation | departureTime         | status        | statusText |
                 | *  | G303      | 天津南      | 北京南    | 2026-03-21T09:00:00   | DEPARTED      | 已发车     |
                 | *  | G301      | 北京南      | 上海虹桥  | 2026-03-21T13:30:00   | UPCOMING_SOON | 即将发车   |
                 | *  | G302      | 南京南      | 杭州东    | 2026-03-21T16:30:00   | UPCOMING      | 未发车     |
    }
    """
