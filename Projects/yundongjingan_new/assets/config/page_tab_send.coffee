class ECpageClass
    root = {}  # 这是ECpageClass的一个实例的全局变量
    _page_name : "" # 属性
    _item_info : {}
    _platform:""
    _listview_data:
        pullable: false
        hasFooterDivider: false
        hasHeaderDivider: false
        dividerHeight: 0
        dividerColor: "#cccccc"
        data: [
            {
                viewType: "ListViewCellLine"
                _rightLayoutSize: 0
                _leftLayoutSize: 60
                centerTitle: "正在加载......"
            }
        ]
#    _theme = "default"

    _constructor: (@_page_name) ->
        root = this
        #获取其他界面传来的数据
        @prepareForInitView()
#        $A().lrucache().get("phone").then (phone) ->
#            root._listview_data.data[1].inputText =  if phone? and phone != "" then phone else ""

        $A().page().widget("#{@_page_name}_ListViewBase_0").data JSON.stringify @_listview_data
        $A().page().widget("#{@_page_name}_ListViewBase_0").onItemInnerClick (data)-> root.onItemInnerClick(data)
        $A().page().widget("#{@_page_name}_ListViewBase_0").onItemClick (data)-> root.onItemClick(data)
        $A().page().onResume (data)-> root.onResume()
        $A().page().onResult (data)-> root.onResult(data)
        $A().page().onCreated -> root.onCreated()

    constructor: (_page_name) ->
        @_constructor(_page_name)

    onCreated: () ->
        $A().app().callApi
            method:"content/news/index"
            sort_id: 525
            cacheTime: 0
        .then (data) ->
            if data.errors?
                if data.errors[0].error_num?
                    $A().app().makeToast "网络状态不好，请重新加载"
                else
                    $A().app().makeToast "没有网络"
            else
                root._listview_data.data = []

                for content in data.content_list
                    root._listview_data.data.push
                        viewType : "ListViewCellLine"
                        centerTitle : "#{content.title}"
                        leftImage : {
                            imageType : "imageServer"
                            imageSize : "middle"
                            imageSrc : "#{content.image_cover.url}"
                        }
                        centerBottomdes : "#{content.abstract}"
                $A().page().widget("#{root._page_name}_ListViewBase_0").refreshData JSON.stringify root._listview_data

#        @_listview_data.data[0].centerTitle = "Richard Center Title"
#        @_listview_data.data[0].leftImage = {
#            imageType : "imageServer"
#            imageSize : "middle"
#            imageSrc : "3012659.jpg"
#        }
#        @_listview_data.data[0].centerBottomdes = "Richard Center Bottomes"

#        $A().page().widget("#{@_page_name}_ListViewBase_0").refreshData JSON.stringify @_listview_data if root._platform? and root._platform == "ios"

#        $A().page().widget("#{@_page_name}_ListViewBase_0").refreshData JSON.stringify @_listview_data if root._platform? and root._platform == "ios"
        #自定义函数
    
    onItemClick: (data) ->

    
    onItemInnerClick: (data) ->
        # data._form = JSON.parse data._form
#        content = if data._form.content? then data._form.content else ""
#        contact = if data._form.contact? then data._form.contact else ""
#        if content == ""
#            $A().app().makeToast "请输入您的反馈意见！"
#        else if contact == ""
#            $A().app().makeToast "请输入您的联系方式"
#        else
#            $A().app().makeToast "正在提交"
#            $A().app().callApi
#                method:"project/feedbaks/create"
#                content:content
#                contact_info:contact
#                cacheTime: 0
#            .then (data) ->
                # $A().app().log JSON.stringify data
#                if data.success == true
#                    $A().app().makeToast "提交成功，谢谢您的反馈。"
#                    $A().page().setTimeout("2000").then () ->
#                        $A().app().closePage()
#                else
#                    $A().app().makeToast "提交失败，请重试或者检查您的网络是否打开。"
    
    onResume: () ->
        
    onResult: (data) ->
    
    #---------------------------------------具体业务代码---------------------------------------------
    prepareForInitView: () ->
        $A().app().platform().then (platform) ->
            root._platform = platform

#启动程序
Page = new ECpageClass("page_tab_send")
