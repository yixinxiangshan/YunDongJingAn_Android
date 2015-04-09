# page类
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
        #自定义函数
        $A().page().widget("#{@_page_name}_ListViewBase_0").refreshData JSON.stringify @_listview_data if root._platform? and root._platform == "ios"
        root.getNetResourse()

    onItemClick: (data) ->    
        item = root._listview_data.data[data.position]
        # $A().app().log  "onItemClick" + JSON.stringify _listview_data.data[data.position]
        position = data.position
        if root._listview_data.data[data.position]._type == "_item"
            $A().app().showDatepickerConfirm
                ok: "确定",
                cancel:"取消"
                defaultDay: if  item.item_setting.time_content? then  item.rightDes else item.item_setting.suggest_date
                title:root._listview_data.data[data.position].centerTitle
            .then (data) ->
                if  data.state == "ok"
                    $A().page("page_home").param
                        key: "_setting_changed"
                        value: "true"
                    $A().lrucache().set
                        key:"has_add_notifications"
                        value:"false"
                    $A().app().showLoadingDialog
                        # title:"添加课程"
                        content:"正在设置#{item.centerTitle}"
                    date_time = data.value
                    $A().app().callApi
                        method:"users/courses/set_day"
                        fav_id: item.item_setting.fav_id
                        day_type: item.item_setting.day_type
                        date_time: date_time
                        cacheTime: 0
                    .then (data)->
                        if data? and data.success? and data.success == true
                            day_type = "#{item.item_setting.day_type}"
                            #存到本地
                            $A().lrucache().get("fav_id_arr").then (data) ->
                                fav_id_arr = {}            
                                fav_id_arr =  if data? and data != ""  then JSON.parse data else {}
                                fav_id_arr["#{item.item_setting.fav_id}"].info["#{day_type}"] = date_time
                                $A().lrucache().set
                                    key:"fav_id_arr"
                                    value: JSON.stringify fav_id_arr
                            root._listview_data.data[position].rightDes = date_time
                            $A().page().widget("#{root._page_name}_ListViewBase_0").refreshData JSON.stringify root._listview_data
                            $A().page().setTimeout("400").then () ->
                                $A().app().closeLoadingDialog()
                        else if data? and data.errors?
                            if data.errors == "没有网络"
                                $A().app().makeToast "请打开网络,然后再设置或更改时间。"
                            else
                                $A().app().makeToast "网络环境不好，请重试！"
    
    onItemInnerClick: (data) ->
        $A().page().setTimeout("100").then () ->
            $A().app().closePage()

        
    onResume: () ->
    
    onResult: () ->

    #---------------------------------------具体业务代码---------------------------------------------
    
    prepareForInitView: () ->
        $A().app().platform().then (platform) ->
            root._platform = platform


    getNetResourse: () ->
        root._listview_data.data = []
        $A().app().callApi
            method:"users/courses/times" 
            cacheTime: 0
        .then (res)->
            # $A().app().log JSON.stringify res
            for item in res.items
                root._listview_data.data.push
                    viewType: "ListViewCellGroupTitle"
                    textTitle: item.course_title
                    # color: "#90B4E3"
                    _type: "_title"
                for time,i in item.set_times
                    listdata = []
                    listdata = 
                        viewType: "ListViewCellLine"
                        _rightLayoutSize: 0
                        _leftLayoutSize: 20
                        _bottomDivider:
                            imageType: "assets"
                            imageSize: "fitSize"
                            imageSrc: "webview/images/icon/default/divider.png"
                        rightImage:
                            imageType: "assets"
                            imageSize: "fitSize"
                            imageSrc: "webview/images/icon/default/listview_right.png"
                        centerTitle: "#{time.time_name}时间"
                        rightDes: if time.content? then time.content else "未设置"
                        item_setting:
                            suggest_date:time.suggest_date
                            fav_id: item.fav_id
                            course_title: item.course_title
                            day_type: time.time_type
                            name: time.time_name
                            time_content: time.content
                        _type: "_item"
                        hasFooterDivider:"true" 
                    if i == 0
                        listdata._topDivider =
                                imageType: "assets"
                                imageSize: "fitSize"
                                imageSrc: "webview/images/icon/default/divider.png"
                    root._listview_data.data.push listdata
            
            root._listview_data.data.push
                viewType: "ListViewCellLine"
            
            root._listview_data.data.push
                viewType: "ListViewCellButton"
                btnTitle: "确 定"
                btnType : "ok"
            $A().page().setTimeout("300").then () ->
                # $A().app().log "here :"+ (JSON.stringify _listview_data)
                $A().page().widget("#{root._page_name}_ListViewBase_0").refreshData JSON.stringify root._listview_data

#启动程序
new ECpageClass("page_course_time_choose")