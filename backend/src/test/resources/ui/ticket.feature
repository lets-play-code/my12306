# language: zh-CN
@ui
功能: 买火车票

  场景: 查看火车票
    假如存在"车票":
      | train.name | status    |
      | G102       | AVAILABLE |
      | G103       | AVAILABLE |
    当查询火车票
    那么应该看到车票信息:
      | train.name |
      | G102       |
      | G103       |

