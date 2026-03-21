# language: zh-CN
功能: 车次发车提醒数据

  场景: 查询车次时返回发车时间
    假如存在"停靠站":
      | train.name | train.departureTime   | order | name   |
      | G102       | 2026-03-21T10:00:00 | 1     | 北京南   |
      | G102       | 2026-03-21T10:00:00 | 2     | 上海虹桥 |
    当GET "/trains"
    那么response should be:
    """
    : {
      code=200
      body.json= | id | name | departureTime | stops.name[]      | remainingTickets |
                 | *  | G102 | 2026-03-21T10:00:00 | [北京南 上海虹桥] | 0 |
    }
    """
