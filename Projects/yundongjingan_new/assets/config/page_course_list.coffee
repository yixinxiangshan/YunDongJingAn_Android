# page类
class ECpageClass
    root = {}  # 这是ECpageClass的一个实例的全局变量
    _page_name : "" # 属性
    _item_info : {}
    _platform:""
    _option_course_states: { 1:"当前课程", 2:"已结束",3:"已暂停" , 4:"已删除" ,5:"已取消课程"}
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
        # $A().page().onResume -> root.onResume()
        $A().page().onCreated -> root.onCreated()

    constructor: (_page_name) ->
        @_constructor(_page_name)

    onCreated: () ->
        #自定义函数
        $A().page().widget("#{@_page_name}_ListViewBase_0").refreshData JSON.stringify @_listview_data if root._platform? and root._platform == "ios"
        # $A().page().setTimeout("100").then ()->
        root.getNetResource()
    
    onItemClick: (data) ->
        # $A().app().log JSON.stringify data
        item = @_listview_data.data[data.position]
        if item._type == "_item"
            if parseInt(item.course_state) == 1
                $A().app().openPage
                    page_name:"page_course_info"
                    params: 
                        info : JSON.stringify @_listview_data.data[data.position].item
                    close_option: ""
            if parseInt(item.course_state) == 5
                $A().app().showConfirm
                    title:"是否再次添加此课程"
                    ok: "添加"
                    cancel:"取消"
                    message:item.item.course_info.title
                .then (data) ->
                    if data.state == "ok"
                        root.favCourse(item.item.course_info.code,item.item.course_info.id)
            return "_false"
    
    onItemInnerClick: (data) ->
        item = @_listview_data.data[data.position]
        if item._type? and  item._type == "back"
            $A().app().closePage()
    
    onResume: () ->
    #---------------------------------------具体业务代码---------------------------------------------
    
    prepareForInitView: () ->
        $A().app().platform().then (platform) ->
            root._platform = platform

    getNetResource: () ->
        # $A().app().log "getNetResource"
        root._listview_data.data = []
        $A().app().callApi
            method:"users/courses"
            cacheTime: 0
        .then (res)->
            # 整理课程
            groups = { 1:[] , 2:[] , 3:[] , 4:[] , 5:[]}
            # $A().app().log JSON.stringify groups
            for item in res.items
                groups[item.state].push item
            # $A().app().log JSON.stringify groups

            for key,group of groups
                if group.length > 0
                    root._listview_data.data.push
                        viewType: "ListViewCellGroupTitle"
                        textTitle: root._option_course_states[key]
                        color: "#5793ff"
                        _type: "_title"
                    for item,i in group
                        listdata = 
                            viewType: "ListViewCellLine"
                            _rightLayoutSize: 0
                            _leftLayoutSize: 0
                            _bottomDivider:
                                imageType: "assets"
                                imageSize: "fitSize"
                                imageSrc: "webview/images/icon/default/divider.png"
                            rightImage:
                                imageType: "assets"
                                imageSize: "fitSize"
                                imageSrc: "webview/images/icon/default/listview_right.png"
                            centerTitle: "#{item.course_info.title}"
                            centerBottomdes2: "#{item.course_info.content}"
                            _centerBottomdes2Color: {_clickable: false,pressed: "#999999",selected: "#999999",normal: "#999999"}
                            item: item
                            _type: "_item"
                            course_state:key
                            hasFooterDivider:"true"
                        root._listview_data.data.push listdata 
            root._listview_data.data.push
                viewType: "ListViewCellButton"
                btnTitle: "返 回"
                btnType : "cancel"
                _type : "back"
            $A().page().widget("#{root._page_name}_ListViewBase_0").refreshData JSON.stringify root._listview_data

    #reset class start ------------------------------------------------------------------------------------------------------------------
    
    favCourse : (course_code,itemId) ->
        $A().app().showLoadingDialog
            # title:"添加课程"
            content:"正在添加课程,请稍候"
        $A().app().callApi
            method:"users/courses/create" 
            course_id: itemId
            cacheTime: 0
        .then (res) ->
            if res.errors? 
                $A().app().closeLoadingDialog()
                if res.errors == "没有网络"
                    $A().app().makeToast "没有网络" 
                    return

                if res.errors[0].error_num? and "#{res.errors[0].error_num}" == "100000"  #超时
                    $A().app().makeToast "网络状态不好，请重试" 
                    return 
                if res.errors[0].error_num? and "#{res.errors[0].error_num}" == "301101"  #课程不能重复添加
                    $A().app().makeToast "你已添加过该课程，不能重复添加。" 
                    return
            #jpush 添加别名
            # $A().notification().setAlias
            #     alias:"#{item.centerTitle}"
            # $A().app().log "favCourse:" + JSON.stringify res
            $A().app().showLoadingDialog
                # title:"添加课程"
                content:"课程添加成功"
        
            $A().page().setTimeout("2000").then () ->
                $A().page("page_home").param
                    key: "_setting_changed"
                    value: "true"
                #加载课程数据
                $A().app().showLoadingDialog
                    # title:"添加课程"
                    content:"正在导入课程数据,请稍候"
                #导入课程数据
                root.addCourseBycourseCode(course_code).then (data) ->
                    # $A().app().log "addCourseBycourseCode:" + data
                    $A().app().closeLoadingDialog()
                    if data == "_false"
                        $A().app().makeToast "网络状态不好，请重试" 
                    if data == "_true"
                        $A().page().setTimeout("1000").then () -> 
                            # $A().app().closeLoadingDialog()
                            $A().lrucache().set
                                key:"download_all_courses"
                                value:"true"
                            # 跳转到 设置时间界面   主界面
                            #弹出对话框 点击回到主界面
                            $A().app().showConfirm
                                title:"请自行修改时间设置"
                                ok: "我 知 道 了",
                                message:"我们已经为课程设置了默认时间。为了更好地查看课程内容，请根据你自己的具体情况修改时间设置。"
                            .then (data) ->
                                $A().app().makeToast "正在返回首页请稍后"
                                $A().page().setTimeout("1000").then () -> 
                                    $A().app().openPage
                                        page_name:"page_home"
                                        params:  {}
                                        close_option: "close"

    addCourseBycourseCode: (course_code) ->
        #更新栏目数量 和名称
        column_data = {}

        column_types = []
        #新增课程对应的课程数据 栏目
        # column_{column_id}_fav_id

        #导入课程数据
        $A().app().callApi
            method:"courses/package" 
            course_code: course_code
            cacheTime: 0
        .then (res)->
            if res.errors? and res.errors.error_num? and res.errors.error_num ==  "100000"  #超时
                $A().app().makeToast "网络状态不好，请重试"
                return "_false"

            if res.errors? and res.errors == "没有网络"
                $A().app().makeToast "请开启网络"
                return "_false"

            fav_id = res.detail.fav_id
            course_id  = res.detail.id

            course_id_index_arr = []
            for key_day_time,value_day_time of res.contents
                course_id_index_arr.push "#{key_day_time}"
            # 课程索引和课程简介
            $A().lrucache().get("fav_id_arr").then (data) ->
                fav_id_arr = {}            
                fav_id_arr =  if data? and data != ""  then JSON.parse data else {}
                fav_id_arr[fav_id] = 
                    info: res.detail
                    index: course_id_index_arr 
                $A().lrucache().set
                    key:"fav_id_arr"
                    value: JSON.stringify fav_id_arr
            #本地化每天的数据
            for k_day_time,v_day_time of res.contents
                $A().lrucache().set
                    key:"#{k_day_time}"
                    value: JSON.stringify v_day_time

            #本地化栏目 数据
            for k_day_time,v_day_time of res.contents
                for item,i in v_day_time
                    if item.item_colum? and item.item_colum != ""
                        item["day_offset"] = k_day_time.substr(k_day_time.lastIndexOf("_")+1)
                        item["day_name"] = res.detail[k_day_time.substring(k_day_time.indexOf("_")+1,k_day_time.lastIndexOf("_"))]
                        if  !(item.item_colum in column_types)
                            column_types.push item.item_colum
                            column_data["#{item.item_colum}"] = []
                        column_data["#{item.item_colum}"].push item
            
            column_types_with_id = []
            for item_column_types,j in column_types
                #导入数据到本地
                column_types_with_id.push
                    key:"#{j}"
                    value:"#{item_column_types}"
                $A().lrucache().set
                    key:"column_#{j}_#{fav_id}"
                    value: JSON.stringify column_data["#{item_column_types}"]
                    
            $A().lrucache().get("column_arr").then (data) ->
                column_arr = {}            
                column_arr =  if data? and data != ""  then JSON.parse data else {}
                column_arr[fav_id] = column_types_with_id
                $A().lrucache().set
                    key:"column_arr"
                    value: JSON.stringify column_arr

            #标记已经添加过课程了
            $A().lrucache().set
                key:"download_all_courses"
                value:"true"

            return "_true"

    #reset class end   ------------------------------------------------------------------------------------------------------------------

#启动程序
new ECpageClass("page_course_list")
