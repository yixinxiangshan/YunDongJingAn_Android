class ECpageClass
  root = {} # 这是ECpageClass的一个实例的全局变量
  _page_name: "" # 属性
  _item_info: {}
  _platform: ""
  _id: 0
  _listview_data:
    pullable: false
    hasFooterDivider: false
    hasHeaderDivider: false
    dividerHeight: 0
    dividerColor: "#cccccc"
    data: [
      {
        viewType: "ListViewCellInputText"
        inputType: "number"
        hint: "请输入优惠码"
        name: "content"
        inputText: ""
      }
      {
        viewType: "ListViewCellButton"
        btnTitle: "提 交"
        btnType: "ok"
        name: "submit"
      }
      {
        viewType: "ListViewCellLine"
        _rightLayoutSize: 0
        _leftLayoutSize: 0
        centerTitle: ""
        name: "info"
      }
    ]

  _constructor: (@_page_name) ->
    root = this
    #获取其他界面传来的数据
    @prepareForInitView()

    $A().page().widget("#{@_page_name}_ListViewBase_0").data JSON.stringify @_listview_data
    $A().page().widget("#{@_page_name}_ListViewBase_0").onItemInnerClick (data)-> root.onItemInnerClick(data)
    $A().page().widget("#{@_page_name}_ListViewBase_0").onItemClick (data)-> root.onItemClick(data)
    $A().page().onCreated -> root.onCreated()

  constructor: (_page_name) ->
    @_constructor(_page_name)

  onCreated: () ->
    $A().page().widget("#{@_page_name}_ListViewBase_0").refreshData JSON.stringify @_listview_data if root._platform? and root._platform == "ios"
#自定义函数

  onItemClick: (data) ->

  onItemInnerClick: (data) ->
    content = if data._form.content? then data._form.content else ""
    if content == ""
      $A().app().makeToast "请输入优惠码！"
    else
      $A().app().makeToast "正在提交"
      $A().app().callApi
        method: "admin/user/orders/checkcouponcode"
        coupon_code: content
      .then (data) ->
        message = ""
        if data.order_status == 4
          message = "优惠券使用成功，谢谢。"
        else if data.order_status == 2
          message = "无效优惠券，该优惠券已被使用。"
        else
          message = "优惠券不存在。"
        $A().app().makeToast message
        root._listview_data.data[2].centerTitle = message
        $A().page().widget("#{root._page_name}_ListViewBase_0").refreshData JSON.stringify root._listview_data

  onResume: () ->

  onResult: (data) ->

#---------------------------------------具体业务代码---------------------------------------------
  prepareForInitView: () ->
    $A().app().platform().then (platform) ->
      root._platform = platform

#启动程序
Page = new ECpageClass("page_index")
