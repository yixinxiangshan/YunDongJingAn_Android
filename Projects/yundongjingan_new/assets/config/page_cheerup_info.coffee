# page类
class ECpageClass
  root = {} # 这是ECpageClass的一个实例的全局变量
  _page_name: "" # 属性
  _content: {}
  _platform: ""
  _listview_data:
    pullable: false
    hasFooterDivider: true
    hasHeaderDivider: true
    dividerHeight: 1
    dividerColor: "#EBEBEB"
    data: [
      {
        viewType: "ListViewCellLine"
        _rightLayoutSize: 0
        _leftLayoutSize: 60
        centerTitle: "正在加载......"
      }
    ]

  _constructor: (@_page_name) ->
    root = this
    #获取其他界面传来的数据
    @prepareForInitView()

    $A().page().widget("#{@_page_name}_ListViewBase_0").data JSON.stringify @_listview_data
    $A().page().widget("#{@_page_name}_ListViewBase_0").onItemInnerClick (data)-> root.onItemInnerClick(data)
    $A().page().widget("#{@_page_name}_ListViewBase_0").onItemClick (data)-> root.onItemClick(data)
    # $A().page().onResume ()-> root.onResume()
    # $A().page().onResult (data)-> root.onResult(data)
    $A().page().onCreated -> root.onCreated()

  constructor: (_page_name) ->
    @_constructor(_page_name)

  onCreated: () ->
    $A().page().widget("#{@_page_name}_ListViewBase_0").refreshData JSON.stringify @_listview_data if root._platform? and root._platform == "ios"
#自定义函数
  onItemClick: (data) ->

  onItemInnerClick: (data) ->

  onResume: () ->

  onResult: (data) ->

#---------------------------------------具体业务代码---------------------------------------------

  prepareForInitView: () ->
    $A().app().platform().then (platform) ->
      root._platform = platform
    $A().page().param("info").then (info) ->
      $A().app().callApi
        method: "ContentsCouponsByShops/get"
        shopid: info
        cacheTime: 0
      .then (data) ->
        if data.errors?
          if data.errors[0].error_num?
            $A().app().makeToast "网络状态不好，请重新加载"
          else
            $A().app().makeToast "没有网络"
        else
          root._content = data
          root._listview_data.data = []

          root._listview_data.data.push
            viewType: "ListViewCellArticleTitle"
            headTitle: "#{root._content.shop.title}"

          root._listview_data.data.push
            viewType: "ListViewCellImage"
            image: {
              imageType: "imageServer"
              imageSize: "xlarge"
              imageSrc: "#{root._content.shop.image_cover}"
            }

          root._listview_data.data.push
            viewType: "ListViewCellGroupTitle"
            textTitle: "活动优惠"

          for lesson in root._content.info
            root._listview_data.data.push
              viewType: "ListViewCellLine"
              centerTitle: "#{lesson.title}"
              content_id: "#{lesson.id}"
              hasFooterDivider: "true"

          root._listview_data.data.push
            viewType: "ListViewCellGroupTitle"
            textTitle: "联系方式"

          root._listview_data.data.push
            viewType: "ListViewCellLine"
            centerTitle: "地址：" + "#{root._content.shop.address}"
            hasFooterDivider: "true"

          root._listview_data.data.push
            viewType: "ListViewCellLine"
            centerTitle: "电话：" + "#{root._content.shop.phone_num}"
            hasFooterDivider: "true"

          root._listview_data.data.push
            viewType: "ListViewCellLine"
            centerTitle: "查看所有评论"
            hasFooterDivider: "true"

          root._listview_data.data.push
            viewType: "ListViewCellArticle"
            content: "#{root._content.shop.content}"

          $A().page().widget("#{root._page_name}_ListViewBase_0").refreshData JSON.stringify root._listview_data

#启动程序
new ECpageClass("page_cheerup_info")
