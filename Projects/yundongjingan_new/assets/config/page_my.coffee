# page类
class ECpageClass
  root = {} # 这是ECpageClass的一个实例的全局变量
  _page_name: "" # 属性
  _item_info: {}
  _platform: ""
  _listview_data:
    pullable: true
    hasFooterDivider: true
    hasHeaderDivider: true
    dividerHeight: 1
    dividerColor: "#EBEBEB"
    data: [
      {
        viewType: "ListViewCellLine"
        _rightLayoutSize: 0
        _leftLayoutSize: 0 #60
# leftImage:
#     imageType: "assets"
#     imageSize: "mini"
#     imageSrc: "webview/images/icon/default/article.png"
        centerTitle: "未登录"
        rightImage:
          imageType: "assets"
          imageSize: "fitSize"
          imageSrc: "webview/images/icon/default/listview_right.png"
        name: "mycenter"
        hasFooterDivider: "true"
      }
      {
        viewType: "ListViewCellLine"
        _rightLayoutSize: 0
        _leftLayoutSize: 0
        centerTitle: "我的赛事报名"
        name: "signup"
        hasFooterDivider: "true"
      }
      {
        viewType: "ListViewCellLine"
        _rightLayoutSize: 0
        _leftLayoutSize: 0
        centerTitle: "我的你点我送"
        name: "send"
        hasFooterDivider: "true"
      }
#            {
#                viewType: "ListViewCellLine"
#                _rightLayoutSize: 0
#                _leftLayoutSize: 0
#                centerTitle: "提醒管理"
#                name:"notification"
#                hasFooterDivider:"true"
#            }
#            {
#                viewType: "ListViewCellLine"
#                _rightLayoutSize: 0
#                _leftLayoutSize: 0
#                centerTitle: "意见反馈"
#                name:"feedback"
#                hasFooterDivider:"true"
#            }
      {
        viewType: "ListViewCellLine"
        _rightLayoutSize: 0
        _leftLayoutSize: 0
        centerTitle: "设置"
        name: "setting"
        hasFooterDivider: "true"
      }
    ]

  _constructor: (@_page_name) ->
    root = this
    #获取其他界面传来的数据
    @prepareForInitView()

    $A().page().widget("#{@_page_name}_ListViewBase_0").data JSON.stringify @_listview_data
    $A().page().widget("#{@_page_name}_ListViewBase_0").onItemInnerClick (data)-> root.onItemInnerClick(data)
    $A().page().widget("#{@_page_name}_ListViewBase_0").onItemClick (data)-> root.onItemClick(data)
    $A().page().onResume ()-> root.onResume()
    # $A().page().onResult (data)-> root.onResult(data)
    $A().page().onCreated -> root.onCreated()

  constructor: (_page_name) ->
    @_constructor(_page_name)

  onCreated: () ->
    $A().lrucache().get("phone").then (phone) ->
# $A().app().makeToast "phone" + "#{phone}"
      if phone? and phone != ""
        root._listview_data.data[0].centerTitle = "#{phone}"
      else
        root._listview_data.data[0].centerTitle = "未登录"
      $A().page().widget("#{root._page_name}_ListViewBase_0").refreshData JSON.stringify root._listview_data
#自定义函数
#root.

  onItemClick: (data) ->
# $A().app().makeToast JSON.stringify data.position
    item = @_listview_data.data[data.position]
    switch "#{item.name}"
      when "mycenter"
# $A().lrucache().set
#     key:"name"
#     value:"pengpeng"
        $A().lrucache().get("phone").then (phone) ->
# $A().app().makeToast JSON.stringify data
# $A().lrucache().set
#     key:"name"
#     value:""
          if phone? and phone != ""
            $A().app().openPage
              page_name: "page_mycenter",
              params: {}
              close_option: ""
          else
            $A().app().openPage
              page_name: "page_login",
              params: {}
              close_option: ""
#            when "course_list"
#                $A().app().openPage
#                    page_name:"page_course_list",
#                    params: {}
#                    close_option: ""
#            when "feedback"
#                $A().app().openPage
#                    page_name:"page_feedback",
#                    params: {}
#                    close_option: ""
      when "setting"
#                $A().app().makeToast  "意见反馈"
#                $A().app().ttsPlay  "意见反馈"
        $A().app().openPage
          page_name: "page_setting",
          params: {}
          close_option: ""
#            when "notification"
#                $A().app().openPage
#                    page_name:"page_notification_manage",
#                    params: {}
#                    close_option: ""
      when "signup"
        content =
        {
          content_id: item.content_id
        }
        $A().app().openPage
          page_name: "page_signup_list"
          params:
            info: JSON.stringify content
          close_option: ""
      when "send"
        content =
        {
          content_id: ""
        }
        $A().app().openPage
          page_name: "page_send_list"
          params:
            info: JSON.stringify content
          close_option: ""

  onItemInnerClick: (data) ->

  onResult: (data) ->

  onResume: () ->
    $A().page("page_my").param("_setting_changed").then (data) ->
      if data? and data != ""
        $A().page("page_my").param {key: "_setting_changed", value: ""}
        # 本地刷新
        root.onCreated()

#---------------------------------------具体业务代码---------------------------------------------
  prepareForInitView: () ->
    $A().app().platform().then (platform) ->
      root._platform = platform

#启动程序
new ECpageClass("page_my")