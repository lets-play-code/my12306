# language: zh-CN
@ui
功能: 买火车票

  场景: 查看火车票
    假如存在"车次":
      | name |
      | G102 |
      | G103 |
    当查询火车票
    那么页面包含如下内容:
      | G102 | G103 |

  场景: 买火车票
    假如存在"座位":
      | name | train.name |
      | 2D4  | G102       |
      | 2D4  | G103       |
    当买火车票"G103"
    那么所有"车票"应为:
    """
    : |  seat.name | seat.train.name |
      |  2D4       | G103            |
    """
