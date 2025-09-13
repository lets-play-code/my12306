# language: zh-CN
@ui
功能: 买火车票

  场景: 查看火车票
    假如存在"停靠站":
      | train.name | order | name |
      | G102       | 2     | 上海虹桥 |
      | G102       | 1     | 北京南  |
      | G103       | 1     | 上海虹桥 |
      | G103       | 2     | 北京南  |
    当查询火车票
    那么页面包含如下内容:
      | G102 北京南-上海虹桥 | G103 上海虹桥-北京南 |

  场景: 买火车票
    假如存在"停靠站":
      | train.name | order | name |
      | G102       | 1     | 北京南  |
      | G102       | 2     | 上海虹桥 |
      | G103       | 1     | 上海虹桥 |
      | G103       | 2     | 北京南  |
    假如存在"座位":
      | name | train.name |
      | 2D4  | G102       |
      | 2D5  | G102       |
      | 2D4  | G103       |
    当买火车票"G103"
    那么所有"车票"应为:
    """
    : |  seat.name | seat.train.name | from.name | to.name |
      |  2D4       | G103            | 上海虹桥   | 北京南   |
    """

  场景: 票卖完时显示错误信息
    假如存在"停靠站":
      | train.name | order | name |
      | G104       | 1     | 北京南  |
      | G104       | 2     | 上海虹桥 |
    假如存在"座位":
      | name | train.name |
      | 2D4  | G104       |
    假如存在"车票":
      | seat.name | seat.train.name | from.name | to.name |
      | 2D4       | G104            | 北京南       | 上海虹桥    |
    当买火车票"G104"
    那么显示错误信息"票已卖完"
