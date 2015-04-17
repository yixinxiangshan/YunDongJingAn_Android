# page类
class ECpageClass
    root = {}  # 这是ECpageClass的一个实例的全局变量
    _page_name : "" # 属性
    _content_info : {}
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
        $A().app().log "-----------------------------------" + JSON.stringify root._content_info
#        root.showArticle(root._content_info)
    onItemClick: (data) ->    

    onItemInnerClick: (data) ->

    onResume: () ->
    
    onResult: (data) ->

    #---------------------------------------具体业务代码---------------------------------------------
    
    prepareForInitView: () ->
        $A().app().platform().then (platform) ->
            root._platform = platform

    showArticle: (info) ->
        root._listview_data.data = [
            {
                viewType: "ListViewCellGroupTitle"
                textTitle: ""
                _type : "_split"
            }
            {
                viewType: "ListViewCellLine"
                _rightLayoutSize: 0
                _leftLayoutSize: 80
                _bottomDivider:
                    imageType: "assets"
                    imageSize: "fitSize"
                    imageSrc: "webview/images/icon/default/divider.png"
                leftImage:
                    imageType: "imageServer"
                    imageSize: "middle"
                    imageSrc: info.course_info.image_cover.url
                centerTitle: info.course_info.title
                hasFooterDivider:"true"
                # centerBottomdes: info.course_info.content
                # centerRighttopdes: info.start_date
            }
        ]
        for time,i in info.set_times
            root._listview_data.data.push
                viewType: "ListViewCellLine"
                _rightLayoutSize: 0
                _leftLayoutSize: 20
                _bottomDivider:
                    imageType: "assets"
                    imageSize: "fitSize"
                    imageSrc: "webview/images/icon/default/divider.png"
                rightImage:
                    imageType: "assets"
                    imageSize: "micro"
                    imageSrc: "webview/images/icon/default/listview_right.png"
                centerTitle: "#{time.time_name}时间"
                rightDes: if time.content? then time.content else "未设置"
                item_setting: 
                    fav_id: info.id
                    course_title: info.course_info.title
                    day_type: time.time_type
                    name: time.time_name
                    time_content: time.content
                _type: "_item"
                hasFooterDivider:"true"
        root._listview_data.data.push
            viewType: "ListViewCellGroupTitle"
            textTitle: ""
            _type : "_split"
        root._listview_data.data.push
            viewType: "ListViewCellLine"
            _rightLayoutSize: 0
            _leftLayoutSize: 0
            _bottomDivider:
                imageType: "assets"
                imageSize: "fitSize"
                imageSrc: "webview/images/icon/default/divider.png"
            centerBottomdes2: info.course_info.content
            _centerBottomdes2Color: {_clickable: false,pressed: "#999999",selected: "#999999",normal: "#999999"}
            # centerRighttopdes: info.start_date
            hasFooterDivider:"false"
        root._listview_data.data.push
            viewType: "ListViewCellButton"
            btnTitle: "取 消 课 程"
            btnType : "cancel"
            _type : "_cancel"
        # $A().app().log JSON.stringify _listview_data
        $A().page().widget("#{root._page_name}_ListViewBase_0").refreshData JSON.stringify root._listview_data            

#启动程序
new ECpageClass("page_send_info")