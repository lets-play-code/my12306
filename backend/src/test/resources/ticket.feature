# language: zh-CN

功能: 买火车票
  场景: 买全程票
    假如存在"车次":
      | name |
      | G102 |
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
    : | train.name |
      | G102       |
    """