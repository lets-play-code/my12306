# language: zh-CN
功能: 买火车票

  场景: 显示所有车次
    假如存在"停靠站":
      | train.name | order | name |
      | G102       | 2     | 上海虹桥 |
      | G102       | 1     | 北京南  |
      | G103       | 1     | 上海虹桥 |
      | G103       | 2     | 北京南  |
    当GET "/trains"
    那么response should be:
    """
    : {
      code=200
      body.json= | id | name | stops.name[]  |
                 | *  | G102 | [北京南 上海虹桥] |
                 | *  | G103 | [上海虹桥 北京南] |
    }
    """

  Rule: 买票
    场景: 买全程票
      假如存在"停靠站":
        | train.name | order | name |
        | G102       | 1     | 北京南  |
        | G102       | 2     | 上海虹桥 |
      假如存在"座位":
        | name | train.name |
        | 2D4  | G102       |
      当POST "/trains/1/tickets":
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
    : |  seat.name | from.name | to.name |
      |  2D4       | 北京南     | 上海虹桥 |
    """

    场景: 买全程票 - 找未卖出的座位
      假如存在"停靠站":
        | train.name | order | name |
        | G102       | 1     | 北京南  |
        | G102       | 2     | 上海虹桥 |
      假如存在"座位":
        | name | train.name |
        | 2D4  | G102       |
        | 2D5  | G102       |
      假如存在"车票":
        | seat.name | from.train.name | from.name | to.train.name | to.name |
        | 2D4       | G102            | 北京南       | G102          | 上海虹桥    |
      当POST "/trains/1/tickets":
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
      : | seat.name |
        | 2D4       |
        | 2D5       |
      """

    场景: 买全程票 - 座位已卖完
      假如存在"停靠站":
        | train.name | order | name |
        | G102       | 1     | 北京南  |
        | G102       | 2     | 上海虹桥 |
      假如存在"座位":
        | name | train.name |
        | 2D4  | G102       |
      假如存在"车票":
        | seat.name | from.train.name | from.name | to.train.name | to.name |
        | 2D4       | G102            | 北京南       | G102          | 上海虹桥    |
      当POST "/trains/1/tickets":
      """
      {
        "from": ${停靠站.name[北京南].id},
        "to": ${停靠站.name[上海虹桥].id}
      }
      """
      那么response should be:
      """
      : {
        code= 400
        body.json: {
          message: "票已卖完"
        }
      }
      """
      那么所有"车票"应为:
      """
      : | seat.name |
        | 2D4       |
      """

    场景: 买非全程票
      假如存在"停靠站":
        | train.name | order | name |
        | G102       | 1     | 北京南  |
        | G102       | 2     | 南京南  |
        | G102       | 3     | 上海虹桥 |
      假如存在"座位":
        | name | train.name |
        | 2D4  | G102       |
      当POST "/trains/1/tickets":
      """
      {
        "from": ${停靠站.name[南京南].id},
        "to": ${停靠站.name[上海虹桥].id}
      }
      """
      那么response should be:
      """
      code=200
      """
      那么所有"车票"应为:
      """
      : |  seat.name | from.name | to.name |
        |  2D4       | 南京南     | 上海虹桥 |
      """

    场景: 买非全程票 - 找未卖出的区间from=ticket.to
      假如存在"停靠站":
        | train.name | order | name |
        | G102       | 1     | 北京南  |
        | G102       | 2     | 南京南  |
        | G102       | 3     | 上海虹桥 |
      假如存在"座位":
        | name | train.name |
        | 2D4  | G102       |
      假如存在"车票":
        | seat.name | from.train.name | from.name | to.train.name | to.name |
        | 2D4       | G102            | 北京南       | G102          | 南京南     |
      当POST "/trains/1/tickets":
      """
      {
        "from": ${停靠站.name[南京南].id},
        "to": ${停靠站.name[上海虹桥].id}
      }
      """
      那么response should be:
      """
      code=200
      """
      那么所有"车票"应为:
      """
      : |  seat.name | from.name | to.name |
        |  2D4       | 北京南     | 南京南  |
        |  2D4       | 南京南     | 上海虹桥 |
      """

    场景: 买非全程票 - 找未卖出的区间from>ticket.to
      假如存在"停靠站":
        | train.name | order | name |
        | G102       | 1     | 北京南  |
        | G102       | 2     | 南京南  |
        | G102       | 3     | 镇江   |
        | G102       | 4     | 上海虹桥 |
      假如存在"座位":
        | name | train.name |
        | 2D4  | G102       |
      假如存在"车票":
        | seat.name | from.train.name | from.name | to.train.name | to.name |
        | 2D4       | G102            | 北京南       | G102          | 南京南     |
      当POST "/trains/1/tickets":
      """
      {
        "from": ${停靠站.name[镇江].id},
        "to": ${停靠站.name[上海虹桥].id}
      }
      """
      那么response should be:
      """
      code=200
      """
      那么所有"车票"应为:
      """
      : |  seat.name | from.name | to.name |
        |  2D4       | 北京南     | 南京南  |
        |  2D4       | 镇江       | 上海虹桥 |
      """

    场景: 买非全程票 - 无可卖区间from<ticket.to
      假如存在"停靠站":
        | train.name | order | name |
        | G102       | 1     | 北京南  |
        | G102       | 2     | 南京南  |
        | G102       | 3     | 上海虹桥 |
      假如存在"座位":
        | name | train.name |
        | 2D4  | G102       |
      假如存在"车票":
        | seat.name | from.train.name | from.name | to.train.name | to.name |
        | 2D4       | G102            | 北京南       | G102          | 上海虹桥    |
      当POST "/trains/1/tickets":
      """
      {
        "from": ${停靠站.name[南京南].id},
        "to": ${停靠站.name[上海虹桥].id}
      }
      """
      那么response should be:
      """
      code=400
      """
      那么所有"车票"应为:
      """
      : |  seat.name | from.name | to.name |
        |  2D4       | 北京南     | 上海虹桥  |
      """

    场景: 买非全程票 - 找未卖出的区间to=ticket.from
      假如存在"停靠站":
        | train.name | order | name |
        | G102       | 1     | 北京南  |
        | G102       | 2     | 南京南  |
        | G102       | 3     | 上海虹桥 |
      假如存在"座位":
        | name | train.name |
        | 2D4  | G102       |
      假如存在"车票":
        | seat.name | from.train.name | from.name | to.train.name | to.name |
        | 2D4       | G102            | 南京南       | G102          | 上海虹桥    |
      当POST "/trains/1/tickets":
      """
      {
        "from": ${停靠站.name[北京南].id},
        "to": ${停靠站.name[南京南].id}
      }
      """
      那么response should be:
      """
      code=200
      """
      那么所有"车票"应为:
      """
      : |  seat.name | from.name | to.name |
        |  2D4       | 南京南     | 上海虹桥 |
        |  2D4       | 北京南     | 南京南  |
      """

    场景: 买非全程票 - 无可卖区间to>ticket.from
      假如存在"停靠站":
        | train.name | order | name |
        | G102       | 1     | 北京南  |
        | G102       | 2     | 南京南  |
        | G102       | 3     | 上海虹桥 |
      假如存在"座位":
        | name | train.name |
        | 2D4  | G102       |
      假如存在"车票":
        | seat.name | from.train.name | from.name | to.train.name | to.name |
        | 2D4       | G102            | 南京南       | G102          | 上海虹桥    |
      当POST "/trains/1/tickets":
      """
      {
        "from": ${停靠站.name[北京南].id},
        "to": ${停靠站.name[上海虹桥].id}
      }
      """
      那么response should be:
      """
      code=400
      """
      那么所有"车票"应为:
      """
      : |  seat.name | from.name | to.name |
        |  2D4       | 南京南     | 上海虹桥 |
      """

    场景: 买非全程票 - 找未卖出的区间to<ticket.from
      假如存在"停靠站":
        | train.name | order | name |
        | G102       | 1     | 北京南  |
        | G102       | 2     | 南京南  |
        | G102       | 3     | 镇江   |
        | G102       | 4     | 上海虹桥 |
      假如存在"座位":
        | name | train.name |
        | 2D4  | G102       |
      假如存在"车票":
        | seat.name | from.train.name | from.name | to.train.name | to.name |
        | 2D4       | G102            | 镇江        | G102          | 上海虹桥    |
      当POST "/trains/1/tickets":
        """
        {
          "from": ${停靠站.name[北京南].id},
          "to": ${停靠站.name[南京南].id}
        }
        """
      那么response should be:
        """
        code=200
        """
      那么所有"车票"应为:
        """
        : |  seat.name | from.name | to.name |
          |  2D4       | 镇江       | 上海虹桥 |
          |  2D4       | 北京南     | 南京南  |
        """
