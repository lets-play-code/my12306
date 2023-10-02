# language: zh-CN
功能: 买火车票

  场景: 买全程票
    假如存在"车票":
      | train.name | status    |
      | G102       | AVAILABLE |
    当POST "/trains/1/tickets":
    """
    {}
    """
    那么response should be:
    """
    code=200
    """
    那么All data "车票" should be:
    """
    : | status |
      | SOLD   |
    """

  场景: 显示所有车票
    假如存在"车票":
      | train.name | status    |
      | G102       | AVAILABLE |
      | G103       | AVAILABLE |
    当GET "/tickets"
    那么response should be:
    """
    : {
      code=200
      body.json= | trainName  | status    | id |
                 | G102       | AVAILABLE | *  |
                 | G103       | AVAILABLE | *  |
    }
    """