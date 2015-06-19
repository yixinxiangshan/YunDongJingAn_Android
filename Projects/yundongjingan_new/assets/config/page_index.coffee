# page类
class ECpageClass
  root = {} # 这是ECpageClass的一个实例的全局变量
  _page_name: "" # 属性
  _content_info: {}
  _platform: ""
  _listview_data:
    pullable: false
    hasFooterDivider: true
    hasHeaderDivider: true
    dividerHeight: 0
    dividerColor: "#EBEBEB"

  _constructor: (@_page_name) ->
    root = this
    #获取其他界面传来的数据
    @prepareForInitView()

    $A().page().widget("#{@_page_name}_SatelliteWidget_0").data JSON.stringify @_listview_data
    $A().page().widget("#{@_page_name}_SatelliteWidget_0").onItemInnerClick (data)-> root.onItemInnerClick(data)
    $A().page().widget("#{@_page_name}_SatelliteWidget_0").onItemClick (data)-> root.onItemClick(data)
    # $A().page().onResume ()-> root.onResume()
    # $A().page().onResult (data)-> root.onResult(data)
    $A().page().onCreated -> root.onCreated()

  constructor: (_page_name) ->
    @_constructor(_page_name)

  onCreated: () ->
    $A().app().netState().then (net_state) ->
      if net_state == "offline"
        $A().app().makeToast "没有网络"
      else
        $A().page().setTimeout("3000").then () ->
          $A().app().callApi
            method: "project/projects/detail"
            cacheTime: 0
          .then (data) ->
            if data.errors?
              if data.errors[0].error_num?
                $A().app().makeToast "网络状态不好，请重新加载"
              else
                $A().app().makeToast "没有网络"
            else
              $A().app().preference {key: "net_version_num", value: data.version_num}
              $A().app().preference {key: "net_version_url", value: data.download_url}
              $A().app().getAppVersion().then (version)->
                if parseFloat(data.version_num) > parseFloat(version)
                  data.description = "" if !data.description?
                  $A().app().confirmDownloadNewVersion
                    ok: "下载"
                    data: "最新版本:#{data.version_num}\n\n【更新内容】\n" + data.description if data.description?

    $A().page().widget("#{@_page_name}_SatelliteWidget_0").refreshData JSON.stringify @_listview_data if root._platform? and root._platform == "ios"
#自定义函数
  onItemClick: (data) ->

  onItemInnerClick: (data) ->
#    item = @_listview_data.data[data.position]
#    if item._type? and item._type == 'coupon'
#      $A().app().openPage
#        page_name: "page_tab_coupon"
#        params: []
#        close_option: ""
#    else if item._type? and item._type == 'send'
#      $A().app().openPage
#        page_name: "page_tab_send"
#        params: []
#        close_option: ""
#    else if item._type? and item._type == 'signup'
#      $A().app().openPage
#        page_name: "page_tab_signup"
#        params: []
#        close_option: ""
#    else if item._type? and item._type == 'news'
#      $A().app().openPage
#        page_name: "page_tab_news"
#        params: []
#        close_option: ""
#    else
#      $A().app().openPage
#        page_name: "page_empty"
#        params: []
#        close_option: ""

  onResume: () ->

  onResult: (data) ->

#---------------------------------------具体业务代码---------------------------------------------

  prepareForInitView: () ->
    $A().app().platform().then (platform) ->
      root._platform = platform

#启动程序
new ECpageClass("page_index")