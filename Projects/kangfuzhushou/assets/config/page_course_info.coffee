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
        root.showArticle(root._content_info)
    onItemClick: (data) ->    
        # $A().app().log  "onItemClick" + JSON.stringify _listview_data.data[data.position]
        item = root._listview_data.data[data.position]
        # $A().app().log  "onItemClick" + JSON.stringify _listview_data.data[data.position]
        position = data.position
        if root._listview_data.data[data.position]._type? and root._listview_data.data[data.position]._type == "_item"
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
                    # $A().app().log JSON.stringify data
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
                            $A().page().widget("page_course_time_choose_ListViewBase_0").refreshData JSON.stringify root._listview_data
                        else if data? and data.errors? and data.errors == "没有网络"
                            $A().app().makeToast "请打开网络,然后再设置或更改时间。"
                        else
                            $A().app().makeToast "网络环境不好，请重试！"
    
    onItemInnerClick: (data) ->
        if root._listview_data.data[data.position]._type == "_cancel"
            $A().app().showConfirm
                ok: "确定",
                title:"取消课程"
                cancel: "我再想想", 
                message: "取消课程后,课程内容将无法显示。如需查看，请再次添加。"
            .then (data) ->
                if data.state == "ok"
                    $A().app().showLoadingDialog
                        # title:"添加课程"
                        content:"正在取消课程..."
                    $A().app().callApi
                        method:"users/courses/cancel"
                        fav_id: root._content_info.id
                        cacheTime: 0
                    .then (res)->
                        # $A().lrucache().clear()
                        $A().app().showLoadingDialog
                            # title:"添加课程"
                            content:"取消成功"
                        # $A().app().makeToast "取消成功"
                        $A().page("page_home").param
                            key: "_setting_changed"
                            value: "true"
                        root.deleteCourseByFavId(root._content_info.id)
                        
                        $A().app().closeLoadingDialog()

                        $A().page().setTimeout("2000").then () ->
                            $A().app().closePage()
                    return "_false"
    
    onResume: () ->
    
    onResult: (data) ->

    #---------------------------------------具体业务代码---------------------------------------------
    
    prepareForInitView: () ->
        $A().app().platform().then (platform) ->
            root._platform = platform
        $A().page().param("info").then (data) ->
            reg = new RegExp("\r\n","g");
            root._content_info = JSON.parse(data.replace(reg, "\\r\\n"))
            $A().page().widget("ActionBar").title
                title : root._content_info.course_info.title
            # showArticle content_info

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


    #删除课程
    deleteCourseByFavId:  (fav_id) ->
        #删除通知  删除部分通知
        $A().notification().clear()

        $A().lrucache().set
            key:"has_add_notifications"
            value:"false"
        $A().lrucache().remove("local_notifications")

        # 更新 update_all
        $A().lrucache().get("update_all").then (data)->
            update_all = []
            update_all_temp = []
            update_all = JSON.parse data if  data? and data != ""
            for update_item in update_all
                update_all_temp.push update_item if update_item.fav_id? and  "#{update_item.fav_id}" != "#{fav_id}"
            $A().lrucache().set
                key:"update_all"
                value:update_all_temp
        # $A().lrucache().massRemove(JSON.stringify ["has_add_notifications","local_notifications"])
        #删除每天的课程内容
        $A().lrucache().get("fav_id_arr").then (data) ->
            fav_id_arr = {}
            all_fav_id_ids = []
            fav_id_arr =  if data? and data != ""  then JSON.parse data else {}
            for k_fav_id,v_fav_id of fav_id_arr
                # $A().app().log "k_fav_id" + typeof k_fav_id + "#{k_fav_id}"
                if "#{k_fav_id}" == "#{fav_id}"
                    #删除已经保存的完成状态
                    # for item_index in  v_fav_id.index
                    $A().lrucache().massGet(JSON.stringify v_fav_id["index"]).then (res) ->
                        for resk,resv of res
                            resv = JSON.parse resv
                            for item,i in resv
                                all_fav_id_ids.push item.fav_id_id if item.fav_id_id? and item.fav_id_id != ""
                        $A().lrucache().massRemove(JSON.stringify all_fav_id_ids) #清除本地记录的完成状态
                        $A().lrucache().massRemove(JSON.stringify v_fav_id["index"]) #清除每天的数据
                        delete fav_id_arr["#{fav_id}"]
                        
                        #更新 fav_id_arr
                        $A().lrucache().set
                            key:"fav_id_arr"
                            value: JSON.stringify fav_id_arr
                        
                        #删除栏目数据
                        $A().lrucache().get("column_arr").then (data) ->
                            column_arr = {}            
                            column_arr =  if data? and data != ""  then JSON.parse data else {}
                            # column_arr[fav_id]
                            column_delete_keys = []
                            column_delete_keys.push "column_#{item.key}_#{fav_id}" for item in column_arr[fav_id]
                            $A().lrucache().massRemove(JSON.stringify column_delete_keys) if column_delete_keys.length > 0
                            
                            delete column_arr["#{fav_id}"]
                            # 更新 column_arr
                            $A().lrucache().set
                                key:"column_arr"
                                value: JSON.stringify column_arr
                    return
                    
#启动程序
new ECpageClass("page_course_info")