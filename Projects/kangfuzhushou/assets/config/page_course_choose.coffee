# page类
class ECpageClass
    root = {}  # 这是ECpageClass的一个实例的全局变量
    _theme = "default"
    _platform:""
    _page_name : "" # 属性
    _cfg: 
        itemCheckedId:0 #选择的课程所在的位置
        src_prefix : "webview/images/icon/#{_theme}/"
        bottomDivider:
            imageType: "assets"
            imageSize: "fitSize"
            imageSrc: "webview/images/icon/default/divider.png"
        checked_image_url: "webview/images/icon/default/checkbox_check.png"
        unchecked_image_url: "webview/images/icon/default/checkbox_uncheck.png"
        _centerBottomdes2Color: {_clickable: false,pressed: "#999999",selected: "#999999",normal: "#999999"}
    # _item_info : {}
    _listview_data:
        pullable: false
        hasFooterDivider: true
        hasHeaderDivider: true
        bottomButton: true
        dividerHeight: 0
        dividerColor: "#EBEBEB"
        data: [
            {
                viewType: "ListViewCellLine"
                _rightLayoutSize: 0
                _leftLayoutSize: 40
                centerTitle: "正在加载......"
            } 
        ]

    _listview_data_bottom:
        pullable: false
        hasFooterDivider: false
        hasHeaderDivider: false
        dividerHeight: 0
        dividerColor: "#EBEBEB"
        data: [
            {
                viewType: "ListViewCellButton"
                btnTitle: "确 定"
                btnType : "disable"
                type : "_submit"
            } 
        ]


    _constructor: (@_page_name) ->
        root = this
        #获取其他界面传来的数据
        @prepareForInitView()
        $A().page().widget("#{@_page_name}_ListViewBase_bottom").data JSON.stringify @_listview_data_bottom
        $A().page().widget("#{@_page_name}_ListViewBase_bottom").onItemInnerClick (data)-> root.onItemInnerClick(data)

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
        root.getParams()

    onItemClick: (data) ->
        root.itemChecked parseInt data.position if root._listview_data.data[data.position].type == "_course"
    
    onItemInnerClick: (data) ->
        # $A().app().log JSON.stringify data

        $A().app().showConfirm
            ok: "确定添加",
            title:"声明"
            cancel: "取消", 
            message: "1.本软件及所提供的内容不能代替诊断过程；\n2.本软件及所提供的内容不代表临床医治过程；\n3.本软件及所提供的建议不是医嘱；\n\n本软件及所提供的内容会与患者的具体情况有所不同，只能在疾病的诊治过程中提供一些信息支持和帮助，并非医嘱。您有任何不适与异状，请及时联系护士或医生。"
        .then (data) ->
            root.favCourse() if data.state == "ok"
            return false if data.state == "cancel"
    
    onResume: () ->
    
    onResult: (data) ->

    #---------------------------------------具体业务代码---------------------------------------------
    
    prepareForInitView: () ->
        $A().app().platform().then (platform) ->
            root._platform = platform

    getParams: () ->
        $A().page().param("courses").then (res) ->
            # $A().app().log "courses #{res}"
            root._listview_data.data = []
            if res != "" && res != "{}" && res != "(null)"
                reg = new RegExp("\r\n","g");
                reg1 = new RegExp("\t","g");
                res = JSON.parse res.replace(reg, "\\r\\n").replace(reg1, "\\t")
                $A().page().setTimeout("500").then () ->
                    root.getCourse res
            else
                $A().app().callApi
                    method:"courses/hot" 
                    cacheTime: 0
                .then (res)->
                    item.nocheck = true for item in res.items
                    $A().page().widget("ActionBar").title
                        title : "热门课程选择"
                    root.getCourse res.items
    favCourse : () ->
        item = root._listview_data.data[root._cfg.itemCheckedId]
        course_code = "#{item.item_info.code}"
        
        $A().app().showLoadingDialog
            # title:"添加课程"
            content:"正在添加课程,请稍候"
        $A().app().callApi
            method:"users/courses/create" 
            course_id: item.itemId
            cacheTime: 0
        .then (res) ->
            if res.errors? 
                $A().app().closeLoadingDialog()
                if res.errors == "没有网络"
                    $A().app().makeToast "没有网络" 
                    return

                if res.errors[0].error_num? and "#{res.errors[0].error_num}" ==  "100000"  #超时
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
                                        close_option: "close_others"
    getCourse: (res)->
        # $A().app().log "getCourse" + JSON.stringify res
        check_num = 0
        for item in res
            root._listview_data.data.push
                item_info:item
                viewType: "ListViewCellLine"
                _rightLayoutSize: 0
                _leftLayoutSize: 40
                # _backgroundColor:
                #     _clickable : true
                #     pressed: "#ffffff"
                #     selected: "#ffffff"
                #     normal: "#ffffff"
                #     alpha: 38
                leftImage:
                    imageType: "assets"
                    imageSize: "fitSize"
                    imageSrc: root._cfg.unchecked_image_url #if item.nocheck? then unchecked_image_url else checked_image_url
                centerTitle: "#{item.title}"
                # centerBottomdes2: "#{item.content}"
                _centerBottomdes2Color:root._cfg._centerBottomdes2Color
                _bottomDivider: root._cfg.bottomDivider
                itemId: item.id
                isChecked: false #!item.nocheck?
                type : "_course"
            # check_num = check_num + 1 if !item.nocheck?
        root._listview_data.data.push 
            viewType: "ListViewCellGroupTitle"
            textTitle: "已选 #{check_num} 个课程"
            color: ""
            type : "_count"
        if check_num == 0
            root._listview_data_bottom.data[0]["btnType"] = "disable" 
        else 
            root._listview_data_bottom.data[0]["btnType"] = "ok"
        $A().page().widget("page_course_choose_ListViewBase_bottom").refreshData JSON.stringify root._listview_data_bottom
        $A().page().widget("page_course_choose_ListViewBase_0").refreshData JSON.stringify root._listview_data
    
    itemChecked: (item_position)->
        checked_count = 1
        for item,i in root._listview_data.data
            if item.type  == "_course"
                if i != item_position
                    item.isChecked = false
                    item.leftImage.imageSrc = root._cfg.unchecked_image_url
                else
                    root._cfg.itemCheckedId = i
                    item.isChecked = true
                    item.leftImage.imageSrc = root._cfg.checked_image_url
            # checked_count = 1 #checked_count + 1 if item.type == "_course" && item.isChecked
            item.textTitle = "已选 #{checked_count} 个课程" if item.type == "_count"
            root._listview_data_bottom.data[0]["btnType"] = (if checked_count == 0 then "disable" else "ok") if root._listview_data_bottom.data[0].type == "_submit" 

        $A().page().widget("page_course_choose_ListViewBase_bottom").refreshData JSON.stringify root._listview_data_bottom
        $A().page().widget("page_course_choose_ListViewBase_0").refreshData JSON.stringify root._listview_data
    
    #添加课程内容到本地
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
#启动程序
new ECpageClass("page_course_choose")