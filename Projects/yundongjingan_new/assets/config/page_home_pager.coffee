# $A().app().log "page_home_pager................startjs"
_pagerwidget_data = 
    pagerCount: 200
    offset:1
    itemPageName:"page_home_pager"
# 全局变量初始化
page_name = "page_home_pager"
_platform = ""
is_open = false
offset = 0
date = ""  # 用的是本地时间
_progressBarInnerNum = "0"
_theme = "default"
_local_data =
    index:[]
    data:{}
_fav_id_arr = {}
_listview_data =
    todo_count : 0
    done_count : 0
    pullable: false
    hasFooterDivider: true
    hasHeaderDivider: false
    hasFloatButton: false
    hasFixedTitle:false
    dividerHeight: 0
    dividerColor: "#cccccc"
    data: []
_cfg = 
    src_prefix : "webview/images/icon/#{_theme}/"
    list_sort : ["process","set_course","remind","todo","survey","article"]
    list_page_name : 
        "set_course":
            page_name: "page_course_choose"
        "set_time":
            page_name: "page_course_time_choose"
        "todo":
            page_name: "page_course_todo"
        "title":
            page_name: "page_course_time_choose"
        "survey":
            page_name: "page_course_survey"
        "article_list":
            page_name: "page_course_article_detail"
    list_config :
        7:
            name:"remind"
        6:
            name: "tips"
        5:
            name: "survey"
        4: 
            name: "todo"
        2:
            name: "article"
    _centerTitleColor: {_clickable: false,pressed: "#1a1a1a",selected: "#1a1a1a",normal: "#1a1a1a"}
    _centerTitleColorDone: {_clickable: false,pressed: "#999999",selected: "#999999",normal: "#999999"}
    _centerBottomdesColor: {_clickable: false,pressed: "#999999",selected: "#999999",normal: "#999999"}
    _centerBottomdes2Color: {_clickable: false,pressed: "#4D4D4D",selected: "#4D4D4D",normal: "#4D4D4D"}
    _centerBottomdesColor_article_list: {_clickable: false,pressed: "#5793ff",selected: "#5793ff",normal: "#5793ff"}
    _bottomDivider: {imageType: "assets",imageSize: "fitSize",imageSrc: "webview/images/icon/default/divider.png"}
    listview_right_img: "webview/images/icon/#{_theme}/listview_right.png"
    listview_right_down_img: "webview/images/icon/#{_theme}/listview_right_down.png"
    
circleProgressBar =
    _backgroundColor: {_clickable: true,pressed: "#ffffff",selected: "#ffffff",normal: "#ffffff",alpha: 38}
    eventType: "process"
    viewType: "ListViewCellICircleProgressBar"
    progressBarInnerNumPre:"#{_progressBarInnerNum}"
    progressBarInnerNum:"#{_progressBarInnerNum}"
    progressBarInnerText:"读取中..."
    rightText:""
    imageLeftBtn:
        imageType: "assets"
        imageSize: "fitSize"
        imageSrc: "#{_cfg.src_prefix}arrows_left.png"
    imageRightBtn:
        imageType: "assets"
        imageSize: "fitSize"
        imageSrc: "#{_cfg.src_prefix}arrows_right.png"
    rightText_notice_show: false
    rightText_notice:
        imageType: "assets"
        imageSize: "fitSize"
        imageSrc: "#{_cfg.src_prefix}warning.png"
    _bottomDivider:_cfg._bottomDivider
#column plugin datas
_global_data = {}
# 初始化基础view
initBasePage = ()->
    $A().app().platform().then (platform) ->
        _platform = platform
    # 事件触发接口初始化
    $A().page().onPageSelected onPageSelected
    $A().page().onResume onResume
    $A().page().widget("#{page_name}_ListViewBase_0").onItemInnerClick onItemInnerClick
    $A().page().widget("#{page_name}_ListViewBase_0").onItemClick onItemClick
    #初始化栏目所需要的数据
    
    initBaseView()

# 从接口获取数据，初始化数据内容
initPage = ()->
    # $A().app().log "page_home_pager................initPage"
    if is_open == false
        _global_data = {}
        _local_data =
            index:[]
            data:{}
        _fav_id_arr = {}
        _listview_data = 
            todo_count : 0
            done_count : 0
            pullable: false
            hasFooterDivider: true
            hasHeaderDivider: false
            hasFloatButton: false
            hasFixedTitle:false
            dividerHeight: 0
            dividerColor: "#cccccc"
            data: [] 
        initColumnData() #if _platform? and _platform == "android"
        getNetResource()

onPageSelected = (res) ->
    # $A().app().log "zztest ------------------------- onPageSelected: #{offset}"
    initPage()
    # 标记当前选择页面  把标记放在page_home里面
    # $A().page("page_home").param {key: "_now_offset" , value: offset}
    # 设置title
    $A().page("page_home").param("_now_offset").then (data) ->
        # 如果是第一次进入 或者 _now_offset == offset
        # $A().app().log "_now_offset1:" + typeof data + " offset:" + offset
        # if (data== "") or (parseInt(data) == parseInt(offset) and date != "")
        if parseInt(data) == parseInt(offset) or (parseInt(offset) == 0 && data == "")
            $A().page("page_home").widget("ActionBar").title 
                title: getCurrentDay("#{offset}")
                icon:"justClick"

onResume = ()->
    $A().page("page_home").param("_now_offset").then (data) ->
        # 如果不是当前页或者第一次进入
        if parseInt(data) == parseInt(offset) or (is_open == true && parseInt(offset) == 0 && data == "")
            $A().page("page_home").param("_setting_changed").then (data) ->
                if data != ""
                    $A().page("page_home").param {key: "_setting_changed" , value: ""}
                    # 强制刷新数据
                    # $A().app().makeToast "请稍等,正在更新"
                    #添加提醒
                    addNotifications()
                    is_open = false
                    initPage()
            #没用了
            $A().page("page_home").param("_data_changed").then (data) ->
                if data != ""
                    $A().page("page_home").param {key: "_data_changed" , value: ""}
                    # 本地刷新
                    # $A().app().makeToast "请稍等,正在更新"
                    is_open = false
                    initPage()

onItemInnerClick = (data) ->
    # $A().app().log JSON.stringify data
    #-------column more button change---------start-----------------------------------------------
    if data.target == "cloumnItem"
        updateItemsData = []
        if data.columnName == "更多"
            plug = {}
            plug.viewType = "ListViewCellColumn"
            plug.hasFooterDivider = "true"
            plug.viewConfig =
                colNum:5
                hSpace:5
                vSpace:8
            items = []
            iterator(items,_global_data["uniqueItemName"],0,_global_data["uniqueItemName"].length)
            listViewMaker(items,"收起","webview/images/icon/default/column_more.png")
            plug.items = items
            updateItemsData.push {position:data.position,data:plug}
            $A().page().widget("#{page_name}_ListViewBase_0").updateItems 
                data:updateItemsData
        else if data.columnName == "收起"
            plug = {}
            plug.viewType = "ListViewCellColumn"
            plug.viewConfig =
                colNum:5
                hSpace:5
                vSpace:8
            items = []
            iterator(items,_global_data["uniqueItemName"],0,4)
            listViewMaker(items,"更多","webview/images/icon/default/column_more.png")
            plug.items = items
            
            updateItemsData.push {position:data.position,data:plug}
            $A().page().widget("#{page_name}_ListViewBase_0").updateItems 
                data:updateItemsData
        else
            pageParams = []
            for temp in _global_data["newData"]
                if data.columnName == temp.cName
                    pageParams.push
                        cId:temp.cId
                        fId:temp.fId
            info = 
                columnName:"#{data.columnName}"
                pageParams:pageParams
            
            $A().app().openPage
                page_name:"page_column_detail"
                params:
                    info: JSON.stringify info
                close_option: ""
    #-------column more button change---------end-----------------------------------------------
    else if data.target == "dateSelect"
            _pagerwidget_data.offset = parseInt(parseInt(offset) + parseInt(data.offset))
            $A().page("page_home").widget("page_home_PagerWidget_0").refreshData JSON.stringify _pagerwidget_data
    else
        #不是栏目控件类型的
        item = _listview_data.data[data.position]
        position = data.position
        #左右箭头翻页
        if data.target == "rightBtn"
            _pagerwidget_data.offset = parseInt(parseInt(offset)+1)
            $A().page("page_home").widget("page_home_PagerWidget_0").refreshData JSON.stringify _pagerwidget_data
        
        if data.target == "leftBtn"
            _pagerwidget_data.offset = parseInt(parseInt(offset)-1)
            $A().page("page_home").widget("page_home_PagerWidget_0").refreshData JSON.stringify _pagerwidget_data
        
        #设置时间
        if data.target == "title_button"
            # if data.viewName? and data.viewName == "button"
            $A().page().setTimeout("100").then ()->
                $A().app().openPage
                    page_name:"page_course_time_choose"
                    params:  {}
                    close_option: ""
        if data.target == "title_button1"
            # if data.viewName? and data.viewName == "button1"
            if item.eventType == "title"
                $A().app().showDatepickerConfirm
                    ok: "确 定",
                    cancel:"取 消"
                    defaultDay: item.item_info.date
                    title:item._title
                .then (data) ->
                    if  data.state == "ok"
                        $A().lrucache().set
                            key:"has_add_notifications"
                            value:"false"
                        $A().app().showLoadingDialog
                            # title:"添加课程"
                            content:item.btn1Title
                        date_time = data.value
                        $A().app().callApi
                            method:"users/courses/set_day"
                            fav_id: item.item_info.fav_id
                            day_type: item.item_info.day_type
                            date_time: date_time
                            cacheTime: 0
                        .then (data)->
                            # $A().app().log "fav_id:" + "#{item.item_setting.fav_id}"
                            $A().page().setTimeout("400").then () ->
                                    $A().app().closeLoadingDialog()
                            if data? and data.success? and data.success == true
                                day_type = "#{item.item_info.day_type}"
                                #存到本地
                                $A().lrucache().get("fav_id_arr").then (data) ->
                                    fav_id_arr = {}            
                                    fav_id_arr =  if data? and data != ""  then JSON.parse data else {}
                                    fav_id_arr["#{item.item_info.fav_id}"].info["#{day_type}"] = date_time
                                    $A().lrucache().set
                                        key:"fav_id_arr"
                                        value: JSON.stringify fav_id_arr
                                
                                $A().page().setTimeout("400").then () ->
                                    #页面刷新
                                    $A().app().makeToast "#{item.btn1Title}成功,正在刷新页面..."
                                    addNotifications()
                                    is_open = false
                                    initPage()
                            else if data? and data.errors?
                                if data.errors == "没有网络"
                                    $A().app().makeToast "请打开网络,然后再#{item.btn1Title}。"
                                else
                                    $A().app().makeToast "网络环境不好，请重试！"

onItemClick = (data) ->
    updateItemsData = []  
    item = _listview_data.data[data.position]
    info = item.item_info
    position = data.position
    if item.eventType in ["set_time","todo","survey","article_list"]
        # $A().app().log JSON.stringify item
        $A().app().openPage
            page_name: _cfg.list_page_name[item.eventType].page_name
            params: { info  :  JSON.stringify info }
            close_option: ""
    
    else if item.eventType == "set_course"
        #添加课程
        $A().app().showConfirm
            ok: "扫一扫",
            title:"添加课程"
            cancel: "课程码", 
            message: "通过“扫一扫”或输入“课程码”添加课程。"
        .then (data) ->
            if data.state == "ok"
                $A().page().setTimeout("500").then () ->
                    $A().page("page_home").openQRCapture({});
            else
                $A().app().openPage
                    page_name: "page_course_search"
                    params:{}
                    close_option: ""
    
    # 指导，提醒，贴士 点击处理
    else if item.eventType in ["article","remind","tips"]
        if item.is_expanded  #点击时是展开的， 需要关闭
            item._centerBottomdesColor = _cfg._centerBottomdesColor
            item.centerBottomdes = item.centerBottomdes2
            item.centerBottomdes2 = ""
            item.rightImage.imageSrc = "#{_cfg.listview_right_img}" if item.eventType != "remind"
            #文章的关闭处理
            if item.eventType == "article"
                item.hasFooterDivider = "true"
                item._bottomDivider = _cfg._bottomDivider
                #文章列表 隐藏
                for inner_item,i in item.item_info.item_lists
                    _listview_data.data[parseInt(position) + i + 1].viewType = "ListViewCellNull"
        else
            item.centerBottomdes2 = item.centerBottomdes
            item.centerBottomdes = ""
            item.rightImage.imageSrc = "#{_cfg.listview_right_down_img}" if item.eventType != "remind"

            #文章的展开处理
            if item.eventType == "article"
                item._bottomDivider = {}
                item.hasFooterDivider = "false"
                #文章列表显示
                for inner_item,i in item.item_info.item_lists
                    _listview_data.data[parseInt(position) + i + 1].viewType = "ListViewCellLine"
        
        # 是否完成
        if item.action_type != "done"
            item.leftImage.imageSrc = "#{_cfg.src_prefix}done.png"
            item._centerTitleColor = _cfg._centerTitleColorDone
            updateState("done","","#{item.item_info.fav_id}","#{item.item_info.id}")
            item.action_type = "done"
            #完成度更新
            _listview_data.done_count = _listview_data.done_count + 1
            _listview_data.todo_count = _listview_data.todo_count
            percent = Math.ceil( (_listview_data.done_count / _listview_data.todo_count) * 100 )
            percent = 100 if percent > 100
            circleProgressBar.progressBarInnerNumPre = if percent  == 100  then percent else _progressBarInnerNum
            circleProgressBar.progressBarInnerNum  = percent
            _progressBarInnerNum = circleProgressBar.progressBarInnerNum
            # 此版本暂停使用,更新完成度数字
            # updateItemsData.push {position:0,data:circleProgressBar}
        item.is_expanded = !item.is_expanded
        _listview_data.data[position] = item 
        
        #更新被点击的item
        updateItemsData.push {position:position,data:item}
        #更新文章列表
        if item.eventType == "article"
            for inner_item,i in item.item_info.item_lists
                updateItemsData.push
                    position:parseInt(position) + i + 1
                    data:_listview_data.data[parseInt(position) + i + 1]
        $A().page().widget("#{page_name}_ListViewBase_0").updateItems 
            data:updateItemsData     

#-----------Column Plugin Start---------------------------------------------------------------------------------

initColumnData = ->
    # $A().app().log "initColumnData"
    $A().lrucache().get("column_arr").then (data) ->
        dataTemp = JSON.parse data
        newData = [];
        uniqueItemName = []; 
        if data? and data.length != 0
            for favId,colItems of dataTemp
                for items in colItems
                    newData.push
                        fId:favId
                        cId:items.key
                        cName:items.value
            for i in newData
                if !(i.cName in uniqueItemName)
                    uniqueItemName.push i.cName

            _global_data["newData"] = newData;
            _global_data["uniqueItemName"] = uniqueItemName;
        # $A().app().log JSON.stringify _global_data
        return "_false"

#viewControl 返回栏目控件内容
viewControl = ->
    # $A().app().log "viewControl"
    list_data = []
    # $A().app().log "viewControl 1" 
    if _global_data["uniqueItemName"]? and _global_data["uniqueItemName"].length > 0
        plugin = {}
        plugin.viewType = "ListViewCellColumn"
        plugin.viewConfig =
            colNum:5
            hSpace:5
            vSpace:8
        items = []
        
        if _global_data["uniqueItemName"].length > 5
            iterator(items,_global_data["uniqueItemName"],0,4)
            listViewMaker(items,"更多","webview/images/icon/default/column_more.png")
        else
            iterator(items,_global_data["uniqueItemName"],0,_global_data["uniqueItemName"].length)

        plugin.items = items
        list_data.push plugin
        # $A().app().log  JSON.stringify list_data
    list_data

listViewMaker = (items,columnText,imgSrc)->
    items.push
        imageModel:
            imageType: "assets"
            imageSize: "custom"
            imageSrc: "#{imgSrc}"#"webview/images/icon/default/column_item.png"
        text:columnText
    return "_false"

iterator = (items,data,from,end)->
    tempImgSrc = "webview/images/icon/default/column_item.png"
    while from < end
        #以后在此处获取数据里的图片地址
        listViewMaker(items,data[from],tempImgSrc)
        from = from + 1

#-----------Column Plugin end--------------------------------------------------------------------------------

#-----------Date Select Start--------------------------------------------------------------------------------
dateSelectWidget = ()->
    TempArray = []
    Temp = {}
    Temp.viewType = "ListViewCellDateSelect"
    Temp.hasFooterDivider = "true"
    Temp.viewConfig =
            colNum:7
            hSpace:15
            vSpace:0
            bgColor:0xffecebdf
    Items = [];
    for num in [-3..3]
        datestr = getCurrentDay(parseInt(offset)+parseInt(num));
        Items.push
            date:
                week:getWeek(datestr)
                dateTime:datestr.substr(datestr.indexOf("-")+1)
            content:""
            offset:getDayDiff(datestr,getCurrentDay(offset))

    Temp.items = Items
    TempArray.push Temp
    return TempArray

#-----------Date Select End----------------------------------------------------------------------------------
#获取网络数据
getNetResource = () ->
    date = getCurrentDay offset
    $A().page("page_home").param("_now_offset").then (data) ->
        # 如果不是当前页或者第一次进入
        if parseInt(data) == parseInt(offset) and date != ""
            $A().page("page_home").widget("ActionBar").title 
                title: getCurrentDay("#{offset}")
                icon:"justClick"
    #查找今天的课程数据
    $A().lrucache().get("fav_id_arr").then (fav_id_arr) ->
        if fav_id_arr? and fav_id_arr != ""
            fav_id_arr = JSON.parse fav_id_arr
            _fav_id_arr = fav_id_arr
            for k_fav_id,v_fav_id of _fav_id_arr
                # $A().app().log "k_fav_id" + k_fav_id
                fav_id_info  = v_fav_id.info
                fav_id_index = v_fav_id.index
                course_id = fav_id_info.id
                day_names = {"day_1_time":{"name":"day_1_name"},"day_2_time":{"name":"day_2_name"},"day_3_time":{"name":"day_3_name"},"day_4_time":{"name":"day_4_name"},"day_5_time":{"name":"day_5_name"}}
                day_names_valid = []  #有效时间点的数据 指的是 有时间点
                for key_day_name,value_day_name of day_names
                    if (fav_id_info[value_day_name.name]? and fav_id_info[value_day_name.name]!= "") or (v_fav_id.info.suggest_times["#{key_day_name}"]? and v_fav_id.info.suggest_times["#{key_day_name}"] != "")
                        is_suggest_time = if fav_id_info["#{key_day_name}"]? and  fav_id_info["#{key_day_name}"] != ""  then false else true
                        now_day = if fav_id_info["#{key_day_name}"]? and  fav_id_info["#{key_day_name}"] != "" then fav_id_info["#{key_day_name}"] else v_fav_id.info.suggest_times[key_day_name]                   
                        # now_day = fav_id_info["#{key_day_name}"]
                        temp = {};
                        temp =
                            is_suggest_time:is_suggest_time
                            course_title:fav_id_info["title"]
                            fav_id:k_fav_id
                            day_x_name:value_day_name.name
                            day_type:"#{key_day_name}"
                            name:fav_id_info["#{value_day_name.name}"] #入院 出院
                            date: now_day#if fav_id_info["#{key_day_name}"]? then fav_id_info["#{key_day_name}"] else "" 
                            offset:if now_day?  then  "#{getDayDiff(date,now_day)}" else ""
                        day_names_valid.push temp
                        # $A().app().log "day_names_valid==" + JSON.stringify temp
                _local_data.data["#{k_fav_id}"] = day_names_valid

                # 取数据的索引
                for item in day_names_valid
                    if "#{course_id}_#{item.day_x_name}_#{item.offset}" in fav_id_index
                        item.index = "#{course_id}_#{item.day_x_name}_#{item.offset}"
                        _local_data.index.push item
        
        $A().app().log  "getNetResource 4" if _platform? and _platform == "ios"
        formatNetResource()

formatNetResource = () ->
    $A().app().log  "formatNetResource.....................start _local_data:#{JSON.stringify _local_data}" if _platform? and _platform == "ios"
    listview_data_top = []
    listview_data_temp = []
    keys = []
    keys.push item.index for item,i in _local_data.index
    $A().lrucache().massGet(JSON.stringify(keys)).then (res)->
        # $A().app().log  res
        for resk,resv of res
            resv = JSON.parse resv
            listview_data_temp_title = []
            listview_data_temp_group = []
            value = {}
            for item,i in resv
                if i == 0
                    for local_data_inner_item in _local_data.index
                        if local_data_inner_item.index == resk
                            value = local_data_inner_item
                            break;
                    if parseInt(value.offset) == 0
                        rightText = "#{value.name}当天"
                    else if parseInt(value.offset) < 0
                        rightText = "#{value.name}前#{-parseInt(value.offset)}天" 
                    else if parseInt(value.offset) > 0
                        rightText = "#{value.name}后#{parseInt(value.offset)}天"

                    listview_data_temp_title.push
                        eventType:"title"
                        item_info:value
                        _backgroundColor:{_clickable: false,pressed: "#f2f2f2",selected: "#f2f2f2",normal: "#f2f2f2"}
                        viewType: "ListViewCellTextWithButton"
                        title:"#{item.title}"
                        btnTitle:"#{rightText}"if !value.is_suggest_time
                        btn1Title:if value.is_suggest_time then  "#{rightText}[设置#{value.name}时间]" else ""
                        _title:"设置#{value.name}时间"
                        hasFooterDivider:"true"
                else
                    listview_data_temp_group.push
                        item_info: item
                        kf_course_item_type_id : item.kf_course_item_type_id
                        fav_id_id: item.fav_id_id
                        action_type: ""
                        is_expanded: false
                        fav_id: item.fav_id
                        eventType: _cfg.list_config[item.kf_course_item_type_id].name
                        viewType: "ListViewCellLine"
                        _rightLayoutSize: 25
                        _leftLayoutSize: 40
                        _bottomDivider:_cfg._bottomDivider if !(item.kf_course_item_type_id == 2 and item.is_expanded? and  item.is_expanded == true)
                        leftImage:
                            imageType: "assets"
                            imageSize: "fitSize"
                            imageSrc: "#{_cfg.src_prefix}#{_cfg.list_config[item.kf_course_item_type_id].name}.png"
                        centerTitle: item.title
                        centerRighttopdes:"#{item.item_type_name}" if item.item_type_name? and item.item_type_name != "undefined"   #_cfg.list_config[item.kf_course_item_type_id].cnName
                        _centerBottomdesColor:_cfg._centerBottomdesColor
                        _centerTitleColor: _cfg._centerTitleColor 
                        centerRighttopdes: if item.item_colum? and item.item_colum != "" then item.item_colum  else item.item_type_name 
                        centerBottomdes: if item.content?  then "#{item.content}" else ""
                        _centerBottomdes2Color:_cfg._centerBottomdes2Color
                        rightImage:
                            imageType: "assets"
                            imageSize: "fitSize"
                            imageSrc: "#{_cfg.listview_right_img}"
                        hasFooterDivider:"true"

                    # 加入文章列表 默认不显示
                    if item.item_lists? and item.item_count > 0
                        if item.kf_course_item_type_id == 2
                            for inner_item,j in item.item_lists
                                # $A().app().log "item_lists:"+ typeof item.item_lists
                                inner_item.subtitle = item.title
                                inner_item.item_colum = if item.item_colum? and item.item_colum != "" then item.item_colum  else item.item_type_name 
                                listview_data_temp_group.push
                                    item_info:inner_item
                                    kf_course_item_type_id:item.kf_course_item_type_id
                                    # fav_id_id: item.fav_id_id  
                                    eventType: "article_list"
                                    action_type:""
                                    viewType: if item.is_expanded? and item.is_expanded == true then  "ListViewCellLine" else "ListViewCellNull"
                                    _rightLayoutSize: 25
                                    _leftLayoutSize: 40
                                    centerBottomdes2: "#{inner_item.title}" if inner_item.title and inner_item.title != "undefined"
                                    _centerBottomdes2Color:_cfg._centerBottomdesColor_article_list
                                    _bottomDivider:_cfg._bottomDivider if item.item_count == j+1
                                    hasFooterDivider: "true" if item.item_count == j+1
            listview_data_temp = listview_data_temp.concat(listview_data_temp_title).concat(listview_data_temp_group)
        _listview_data.data = []
        # ----------本地原始数据 本地再加工--------
        # # 全局通知
        listview_data_top = listview_data_top.concat( getCourseContent_getGlobalNotices() )
        #  算完成度  添加完成状态 显示
        getCourseContent_showData(listview_data_top , listview_data_temp)
        
# 显示数据流程：先更新显示相关数据，然后从缓存中读取必要的完成度信息整合进数据，然后需要排序，最后后刷新view
getCourseContent_showData = (data1,data2) ->
    count_cache_keys = {}
    keys = []
    for item,i in data2
        # 添加一个准备读取缓存的记录
        if item.fav_id_id? and item.fav_id_id!= "" #取出来的时每条数据 不包括 分组头，全局通知 和 完成度控件
            count_cache_keys[item.fav_id_id] = i
            keys.push item.fav_id_id
    # 设置完成度
    circleProgressBar.progressBarInnerText = "完成度"
    if keys.length != 0
        $A().lrucache().massGet(JSON.stringify(keys)).then (res)->
            # $A().app().log res
            #添加完成状态
            for resk,resv of res
                count = 0
                item = data2[count_cache_keys[resk]]
                if typeof resv == "string"  and resv != "done"
                    count = resv.split(",").length if resv != ""
                else if typeof resv == "string"  and resv == "done"
                    item.action_type = "done"
                else
                    count = count + 1 for k,v of resv
                item.action_type = "done" if count!= 0 and count == item.item_info.item_count
            # 计算完成度
            todo_count = 0
            done_count = 0
            for item,i in data2
                # done的数据中，修改view相关的变化
                todo_count = todo_count + 1 if item.fav_id_id? and item.fav_id_id != ""
                if item.action_type? and  item.action_type != "undefined" and  item.action_type == "done" #改变图标和标题颜色
                    done_count = done_count + 1
                    item.leftImage.imageSrc = "#{_cfg.src_prefix}done.png"
                    item._centerTitleColor = _cfg._centerTitleColorDone
                    # item._backgroundColor = 
                    #     _clickable: true
                    #     pressed: "#f2f2f2"
                    #     selected: "#f2f2f2"
                    #     normal: "#f2f2f2"
                if item.action_type? and item.action_type != "undefined" and item.eventType in ["remind","article","tips"] and item.is_expanded? and item.is_expanded == true #改变图标和标题颜色        
                    item.centerBottomdes2 = item.centerBottomdes
                    item.centerBottomdes = ""
                    item.rightImage.imageSrc = "#{_cfg.listview_right_down_img}"
            if todo_count > 0
                percent = Math.ceil( (done_count / todo_count) * 100 )
                percent = 100 if percent > 100
                _listview_data.done_count = done_count
                _listview_data.todo_count = todo_count
            circleProgressBar.progressBarInnerNumPre = if percent  == 100  then percent else _progressBarInnerNum
            circleProgressBar.progressBarInnerNum  = percent
            _progressBarInnerNum = circleProgressBar.progressBarInnerNum
    #拼接栏目控件数据     
    # _listview_data.data = [circleProgressBar].concat(viewControl()).concat(data1).concat(data2)
    if _platform? and _platform == "android"#concat([circleProgressBar])
        _listview_data.data = dateSelectWidget().concat(viewControl()).concat(data1).concat(data2)
    else
        _listview_data.data = dateSelectWidget().concat(viewControl()).concat(data1).concat(data2)
    $A().page().setTimeout("100").then () ->
        $A().page().widget("#{page_name}_ListViewBase_0").refreshData JSON.stringify _listview_data
    is_open = true

# 设置全局通知，包括 未添加课程、未设置时间的情况
getCourseContent_getGlobalNotices = () ->
    # $A().app().log  "getCourseContent_getGlobalNotices.....................start" 
    data_items = 0
    data_all_items = 0
    for k,v of _local_data.data
        for item in v
            data_all_items = data_all_items + 1
            data_items = data_items + 1 if item["date"]? and  item["date"] != ""
    items = []
    notices = []
    count = 0
    count = count + 1 for k,v of _fav_id_arr
    if count == 0 #isObjEmpty _fav_id_arr 
        notices.push {eventType: "set_course" , centerTitle: "添加新课程" ,itemInfo: {}}
    else if _local_data.index.length == 0  and  (data_items == 0 || data_items != data_all_items)
        notices.push {eventType: "set_time" , centerTitle: "设置课程时间" ,itemInfo: {}}
    else if _local_data.index.length == 0  and  data_items != 0 
        notices.push {eventType: "" , centerTitle: "今天没有要做的事情，请查看其他时间。或点击右上角“加号”，扫一扫添加新课程。" ,itemInfo: {}}
    for notice in notices
        items.push
            item_info: notice.itemInfo
            centerTitle: notice.centerTitle
            eventType: notice.eventType
            action_type:""
            viewType: "ListViewCellLine"
            _rightLayoutSize: 0
            _leftLayoutSize: if notice.eventType? and notice.eventType != "" then 40 else 0
            _bottomDivider:_cfg._bottomDivider
            hasFooterDivider:"true"

            leftImage: {imageType: "assets",imageSize: "fitSize",imageSrc: "#{_cfg.src_prefix}remind.png"} if notice.eventType? and notice.eventType != ""
            rightImage: {imageType: "assets" , imageSize: "fitSize" ,imageSrc: "#{_cfg.src_prefix}listview_right.png"} if notice.eventType? and notice.eventType != ""
    return items

# 初始化时显示的空页面
initBaseView = ->
    $A().page().widget("#{page_name}_ListViewBase_0").refreshData JSON.stringify 
        pullable: false
        hasFooterDivider: false
        hasHeaderDivider: false
        hasFloatButton: false
        hasFixedTitle:false
        dividerHeight: 0
        dividerColor: "#cccccc"
        #此版本去掉了circleProgressBar
        # data:[circleProgressBar]
        data:[]
    $A().page().param("offset").then (res) ->
        $A().page("page_home").param("is_first_open").then (is_first_open) ->
            offset = res
            if parseInt(offset) == 0 && is_first_open == "true"
                # -- zzm
                onPageSelected()
                
                $A().page().setTimeout("3000").then () ->
                    # 检查版本
                    $A().app().callApi
                        method:"projects/detail" 
                        cacheTime: 0
                    .then (res) ->
                        $A().app().log JSON.stringify res
                        $A().app().preference {key: "net_version_num" ,value: res.version_num}
                        $A().app().preference {key: "net_version_url" ,value: res.download_url}
                        $A().app().getAppVersion().then (version)->
                            if parseFloat(res.version_num) > parseFloat(version)
                                res.update_des = "" if !res.update_des? 
                                $A().app().confirmDownloadNewVersion
                                    ok:"下载"
                                    data: "最新版本:#{res.version_num}\n\n【更新内容】\n" + res.update_des if res.update_des? #and res.update_des != ""
                                return
                    #更新课程
                    updateAllCourses()
                    #更新提醒
                    addNotifications()

                    downLoadAllCourses()

#更新所有需要更新的课程，
updateAllCourses = () ->
    $A().lrucache().get("fav_id_arr").then (data) ->
        fav_id_arr = {}
        fav_ids = []            
        fav_id_arr =  if data? and data != ""  then JSON.parse data else {}
        for k_fav_id,v_fav_id of fav_id_arr
            fav_ids.push parseInt k_fav_id
        if fav_ids.length != 0
            $A().app().callApi
                method:"courses/checkupdate"
                fav_ids: fav_ids
                cacheTime: 0
            .then (res1) ->
                # $A().app().log "courses/checkupdate" + JSON.stringify res1
                #比较 时间
                update_fav_ids = []
                for k_fav_id, v_updated_at of res1
                    update_fav_ids.push k_fav_id if fav_id_arr["#{k_fav_id}"].info.updated_at != v_updated_at
                #更新课程的机制     1、根据索引删除每天的内容  2、添加课程  课程设置的时间 要留着
                for  update_fav_id in update_fav_ids
                    # $A().lrucache().massRemove(JSON.stringify fav_id_arr["#{update_fav_id}"].index)
                    updateCourseByCourseCode fav_id_arr["#{update_fav_id}"]

#更新 单个课程
updateCourseByCourseCode = (fav_id_item) ->
    # fav_id_item = JSON.parse fav_id_item
    #导入课程数据
    column_data = {}
    column_types = []
    $A().app().callApi
        method:"courses/package" 
        course_code: fav_id_item.info.code
        cacheTime: 0
    .then (res)->
        if res.errors? and res.errors.error_num? and res.errors.error_num ==  "100000"  #超时
            $A().app().makeToast "网络状态不好，正在拼命加载中"
            updateCourseByCourseCode fav_id_item
        if res.errors? and res.errors == "没有网络"
            return
        #更新提醒
        $A().lrucache().set
            key:"has_add_notifications"
            value:"false"
        addNotifications()

        fav_id = res.detail.fav_id
        course_id  = res.detail.id
        course_id_index_arr = []
        for key_day_time,value_day_time of res.contents
            course_id_index_arr.push "#{key_day_time}"
        
        # 保留已经设置的时间点
        for k, v of fav_id_item.info    
            res.detail[k] = v if k in ["day_1_time","day_2_time","day_3_time","day_4_time","day_5_time"]
 
        #更新课程info和课程索引
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

        
        #删除栏目信息
        
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


#1.53 升级到1.54后 没有课程信息。
downLoadAllCourses = () ->
    $A().lrucache().get("download_all_courses").then (data)->
        if data? and data == "true"
            return
        groups = []
        $A().app().callApi
            method:"users/courses"
            cacheTime: 0
        .then (res)->   
            if res? and res != "" and res.items? and res.items != ""
                for item in res.items
                    groups.push item if parseInt(item.state) == 1
                if groups.length != 0
                    $A().app().showLoadingDialog
                        content:"正在导入课程数据,请稍候！"
                    for item1 in groups
                        addCourseBycourseCode(item1.course_info.code)
                    $A().page().setTimeout("5000").then ()->
                        $A().app().closeLoadingDialog()
                        $A().app().makeToast "课程数据导入成功，即将重启康复助手！"
                        $A().lrucache().set
                            key:"download_all_courses"
                            value:"true"
                        $A().page().setTimeout("1000").then ()->
                            $A().app().openIntent("restart") #重启启动app

#添加课程内容到本地
addCourseBycourseCode = (course_code) ->
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

#完成状态提交
updateState = (action_type,action,kf_course_user_fav_id,course_index_id) ->
    $A().lrucache().set
        key:"#{kf_course_user_fav_id}_#{course_index_id}"
        value:"#{action_type}"
    update_all = []
    update_item =
        action: action
        action_type: action_type
        kf_course_user_fav_id: kf_course_user_fav_id
        course_index_id: course_index_id

    $A().app().netState().then (net_state) ->    
        $A().lrucache().get("update_all").then (data)->
            update_all = JSON.parse data if  data? and data != "" #and data != "[]"
            update_all.push  update_item
            if net_state == "offline"
                $A().lrucache().set
                    key:"update_all"
                    value:update_all
            else
                $A().app().callApi
                    method:"users/courses/course_indices/massaction"
                    actions: update_all
                    cacheTime: 0
                .then (res) ->
                    if res.success? and res.success == true
                        $A().lrucache().remove("update_all")
                    else
                        $A().lrucache().set
                            key:"update_all"
                            value:update_all

#添加提醒
addNotifications = () ->
    $A().lrucache().get("has_add_notifications").then (data)->
        if data? and data == "true"
            return
        $A().notification().clear()
        local_notifications = []
        $A().lrucache().get("fav_id_arr").then (data) ->
            fav_id_arr = {}
            all_fav_id_ids = []
            fav_id_arr =  if data? and data != ""  then JSON.parse data else {}
            for k_fav_id,v_fav_id of fav_id_arr
                # $A().app().log "k_fav_id" + typeof k_fav_id + "#{k_fav_id}"
                $A().lrucache().massGet(JSON.stringify v_fav_id["index"]).then (res) ->
                    for resk,resv of res
                        resv = JSON.parse resv
                        for item,i in resv
                            #添加提醒
                            if item.notice_info? and item.notice_info != ""
                                #日期 时间
                                date = ""
                                for day_time_item in _local_data.data["#{item.fav_id}"]
                                    if day_time_item.day_x_name == item.notice_info.notice_day_type
                                        date = day_time_item.date 
                                        break
                                day_arr = date.split('-')
                                time_arr = item.notice_info.notice_time.split(':')
                                day = new Date(parseInt(day_arr[0]),parseInt(Number(day_arr[1])-1),Number(day_arr[2]),Number(time_arr[0]),Number(time_arr[1]),Number(time_arr[2]))
                                day_temp = new Date(day.getTime() + 1 * 24 * 60 * 60 * 1000 * parseInt(item.notice_info.notice_day_offset))

                                if day_temp.getTime() - new Date().getTime() > 0
                                    notification =
                                        isOpen:"true"
                                        title: "#{item.notice_info.notice_title}"
                                        content:  if item.notice_info.notice_content? and item.notice_info.notice_content != "" then "#{item.notice_info.notice_content}" else ""
                                        notificationId: "#{item.id}"
                                        broadcastTime: "#{day_temp.getTime()}" #  "#{new Date().getTime() + 1000 * 60}" # 
                                    local_notifications.push  notification
                                    $A().notification().add notification
          
            $A().page().setTimeout("1000").then ()->
                if local_notifications.length != 0
                    $A().lrucache().set
                        key:"has_add_notifications"
                        value:"true"
                    $A().lrucache().set
                        key:"local_notifications"
                        value:local_notifications

getCurrentDay = (offset) ->
    # $A().app().log "getCurrentDay:" + offset
    today = new Date();
    current_day_temp = new Date(today.getTime() + 1 * 24 * 60 * 60 * 1000 * offset);
    dd = current_day_temp.getDate();
    mm = current_day_temp.getMonth() + 1; #January is 0!
    yyyy = current_day_temp.getFullYear();
    dd = '0' + dd if dd < 10 
    mm = '0' + mm if mm < 10 
    return "#{yyyy}-#{mm}-#{dd}"

getDayDiff = (day1_str,day2_str) ->
    day1_arr = day1_str.split('-');
    day1 = new Date(parseInt(day1_arr[0]),parseInt(Number(day1_arr[1])-1),Number(day1_arr[2]));
    day2_arr = day2_str.split('-');
    day2 = new Date(parseInt(day2_arr[0]),parseInt(Number(day2_arr[1])-1),Number(day2_arr[2]));
    # $A().app().log "days:" +"#{parseInt(day1.getTime() - day2.getTime()) / (86400000)}"
    return "#{parseInt(day1.getTime() - day2.getTime()) / (86400000)}"

getWeek = (dayTime)->
    backStr = ""
    dayTimeArr = dayTime.split('-');
    day = new Date(Number(dayTimeArr[0]),Number(dayTimeArr[1])-1,Number(dayTimeArr[2]))
    # $A().app().log "daytime = #{day.getDay()}------#{dayTime}----#{Number(dayTimeArr[0])}-#{Number(dayTimeArr[1])-1}-#{Number(dayTimeArr[2])}"
    switch day.getDay()
        when 0 then backStr = "日"
        when 1 then backStr = "一"
        when 2 then backStr = "二"
        when 3 then backStr = "三"
        when 4 then backStr = "四"
        when 5 then backStr = "五"
        when 6 then backStr = "六"
    return backStr

# 初始化程序
$A().page().onCreated ()->
    initBasePage()