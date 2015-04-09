# page类
class ECpageClass
    root = {}
    _page_name : ""
    _item_info : {}
    _platform:""
    _listview_data:
        pullable: false
        hasFooterDivider: true
        hasHeaderDivider: true
        dividerHeight: 0
        dividerColor: "#EBEBEB"
        data: [
            {
                viewType: "ListViewCellLine"
                _rightLayoutSize: 0
                _leftLayoutSize: 40
                centerTitle: "正在加载......"
                hasFooterDivider:"false"

            } 
        ]
    _constructor: (@_page_name) ->
        root = this
        @prepareForInitView()
        $A().page().widget("#{@_page_name}_ListViewBase_0").data JSON.stringify @_listview_data
        $A().page().widget("#{@_page_name}_ListViewBase_0").onItemInnerClick (data)-> root.onItemInnerClick(data)
        $A().page().widget("#{@_page_name}_ListViewBase_0").onItemClick (data)-> root.onItemClick(data)
        # $A().page().onResume ()-> root.onResume()
        # $A().page().onResult (data)-> root.onResult(data)
        $A().page().onCreated -> root.onCreated()
        #处理带来的参数
        @getParams()
    constructor: (_page_name) ->
        @_constructor(_page_name)

    onCreated: () ->
        $A().page().widget("#{@_page_name}_ListViewBase_0").refreshData JSON.stringify @_listview_data if root._platform? and root._platform == "ios"
        #自定义函数调用
        @getArticle()
    
    onItemClick: (data) ->    
        item = @_listview_data.data[data.position]
        if item.viewType == "ListViewCellImage"
            $A().app().fullImage
                imageurl: item.image.imageSrc
                params: {}

    onItemInnerClick: (data) ->
        item = @_listview_data.data[data.position]
        if item._type? and  item._type == "back"
            $A().app().closePage()
    onResume: () ->

    #---------------------------------------具体业务代码---------------------------------------------

    prepareForInitView: () ->
        $A().app().platform().then (platform) ->
            root._platform = platform

    getParams: () ->
        $A().page().param("info").then (data) ->
            reg = new RegExp("\r\n","g");
            reg1 = new RegExp("\t","g");
            root._item_info = JSON.parse(data.replace(reg, "\\r\\n").replace(reg1, "\\t"))

    getArticle: () ->
        $A().page().widget("ActionBar").title
            title: "#{@_item_info.item_colum}"
        $A().app().callApi
            method:"courses/indices/knowledges/detail"
            item_id: @_item_info.id
            cacheTime: 0
        .then (data) -> 
            if data.errors? 
                if data.errors[0].error_num?
                    $A().app().makeToast "网络状态不好，请重新加载"
                else
                    $A().app().makeToast "没有网络"
            else
                root.showArticle(data)        
    
    # 获取具体业务的内容
    showArticle: (content_info) ->
        # reg = new RegExp("\r\n{1}","g")
        @_listview_data.data = []
        contents = []
        @data = []
        #装载文章主标题
        @_listview_data.data.push
            viewType: "ListViewCellArticleTitle"
            headTitle:"#{@_item_info.title}"
            subheadTitle:if typeof @_item_info.subtitle != "undefined" and "#{@_item_info.subtitle}"? then "#{@_item_info.subtitle}" else ""
            hasFooterDivider:"false"

        content_info.content = content_info.content.trim()
        contents = content_info.content.split("[img]")
        if content_info.image_cover.url? and  content_info.image_cover.url != ""
            @_listview_data.data.push
                viewType: "ListViewCellImage"
                image: 
                    imageType: "imageServer"
                    imageSize: "xlarge"
                    imageSrc:content_info.image_cover.url
                hasFooterDivider:"false"

        if contents.length == 1
            @_listview_data.data.push                
                viewType: "ListViewCellLine"
                centerBottomdes2: content_info.content.replace(/(\n\r){1,}/g,"").replace(/(\r\n)/g,"\r\n\r\n")
                hasFooterDivider:"false"
                #上句正则表达式将多个回车换行都替换为1个，然后在将1个替换为2个
        else
            for item,i in contents
                if item? and  item.trim() != ""
                    @_listview_data.data.push
                        viewType: "ListViewCellLine"
                        centerBottomdes2: item.replace(/(\n\r){1,}/g,"").replace(/(\r\n)/g,"\r\n\r\n")
                        hasFooterDivider:"false"
                        #上句正则表达式将多个回车换行都替换为1个，然后在将1个替换为2个
                if (i+1  < contents.length) && content_info.attachments and content_info.attachments.length>0 and content_info.attachments[i]? and  content_info.attachments[i].attachment.url?  and content_info.attachments[i].attachment.url!= ""
                    @_listview_data.data.push
                        viewType: "ListViewCellImage"
                        image: 
                            imageType: "imageServer"
                            imageSize: "xlarge"
                            imageSrc: content_info.attachments[i].attachment.url
                        hasFooterDivider:"false"
        @_listview_data.data.push
            viewType: "ListViewCellButton"
            btnTitle: "返 回"
            btnType : "ok"
            _type : "back"
        $A().page().widget("#{@_page_name}_ListViewBase_0").refreshData JSON.stringify @_listview_data
        

#启动程序
Page = new ECpageClass("page_course_article_detail")
