# language: zh-CN
功能: 买火车票

  场景: 买全程票
    假如存在"座位":
      | name | train.name |
      | 2D4  | G102 |
    当POST "/trains/1/tickets":
    """
    {}
    """
    那么response should be:
    """
    code=200
    """
    那么所有"车票"应为:
    """
    : |  seat.name |
      |  2D4       |
    """

  场景: 买全程票 - 找未卖出的座位
    假如存在"座位":
      | name | train.name |
      | 2D4  | G102 |
        | 2D5  | G102 |
    假如存在"车票":
      | seat.name |
      | 2D4       |
    当POST "/trains/1/tickets":
    """
    {}
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

#  场景: 显示所有车票
#    假如存在"车票":
#      | train.name | status    |
#      | G102       | AVAILABLE |
#      | G103       | AVAILABLE |
#    当GET "/tickets"
#    那么response should be:
#    """
#    : {
#      code=200
#      body.json= | trainName  | status    | id |
#                 | G102       | AVAILABLE | *  |
#                 | G103       | AVAILABLE | *  |
#    }
#    """