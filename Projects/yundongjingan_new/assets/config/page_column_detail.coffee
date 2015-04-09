# page类
class ECpageClass
    root = {}  # 这是ECpageClass的一个实例的全局变量
    _page_name : "" # 属性
    _global_data = {}
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
    _theme = "default"

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

    _constructor: (@_page_name) ->
        root = this
        #获取其他界面传来的数据
        root.prepareForInitView()
        
        $A().page().widget("#{@_page_name}_ListViewBase_0").data JSON.stringify @_listview_data
        $A().page().widget("#{@_page_name}_ListViewBase_0").onItemInnerClick (data)-> root.onItemInnerClick(data)
        $A().page().widget("#{@_page_name}_ListViewBase_0").onItemClick (data)-> root.onItemClick(data)
        # $A().page().onResume ()-> root.onResume()
        # $A().page().setTimeout("100").then ()->
        $A().page().onCreated -> root.onCreated()

        $A().page().setTimeout("1000").then ()->
            root._listview_data.data = []
            root.viewControl()

    constructor: (_page_name) ->
        @_constructor(_page_name)

    onCreated: () ->
        #自定义函数
        #root.
        $A().page().widget("#{@_page_name}_ListViewBase_0").refreshData JSON.stringify @_listview_data if root._platform? and root._platform == "ios"
        $A().page(root._page_name).widget("ActionBar").title
            title : _global_data["title"]
    
    onItemInnerClick: (data) ->
    
    onResume: () ->
    
    onResult: () ->

    #---------------------------------------具体业务代码---------------------------------------------
    
    prepareForInitView: () ->
        $A().app().platform().then (platform) ->
            root._platform = platform

        $A().page().param("info").then (data) ->
            data = JSON.parse data
            _global_data["title"] = data.columnName
            _global_data["datas"] = data.pageParams
            uniqueFid = []
            for tempFid in data.pageParams
                if !(tempFid.fId in uniqueFid)
                    uniqueFid.push tempFid.fId
            _global_data["uniqueFid"] = uniqueFid
        
        $A().lrucache().get("fav_id_arr").then (data) ->
            data = JSON.parse data
            _global_data["FavName"] = data

        root.initContent()

    initContent:->
        cacheKeys = []
        for id in _global_data["uniqueFid"]
            for colArr in _global_data["datas"]
                if id == colArr.fId
                    cacheKeys.push "column_"+colArr.cId+"_"+id

        $A().lrucache().massGet(JSON.stringify(cacheKeys)).then (res)->
            _global_data["content"] = {}
            for resk,resv of res
                _global_data["content"][resk] = JSON.parse resv

    viewControl:->
        for id in _global_data["uniqueFid"]
            if root.getTitle(id)
                for colArr in _global_data["datas"]
                    if id == colArr.fId
                        root.getContent("column_"+colArr.cId+"_"+id)
        
        if root._listview_data.data.length == 0
            root._listview_data.data.push
                viewType: "ListViewCellLine"
                _rightLayoutSize: 0
                _leftLayoutSize: 60
                centerTitle: "此栏目没有内容…"

        $A().page().widget("#{root._page_name}_ListViewBase_0").refreshData JSON.stringify root._listview_data

    getTitle:(paramFavId)->
        tempData = []
        havaTitleSign = true
        tempTitle = null

        try#当fid_arr遍历后没有对应的fid时会出错
            tempTitle = _global_data["FavName"][paramFavId].info.title
        catch
            tempTitle = null

        if tempTitle? and tempTitle.length > 0
            tempData.push
                eventType:"title"
                _backgroundColor:{_clickable: false,pressed: "#f2f2f2",selected: "#f2f2f2",normal: "#f2f2f2"}
                viewType: "ListViewCellTextWithButton"
                title:tempTitle
            root._listview_data.data = root._listview_data.data.concat(tempData)
        else
            havaTitleSign = false

        return havaTitleSign

    getContent:(colId)->
        data = _global_data["content"][colId]
        root.showView(data)

    showView:(resv)->
        listview_data_temp_group = []
        unique_title_list = [];
        for item,i in resv
            # $A().app().log "prepare:" + JSON.stringify item
            #-------- unique title process ------------
            #instruction:使用此模块只需去掉注释，将下面的操作
            #并入到else分支。
            #-------------- start ---------------------
            # if unique_title_list.length == 0
            #     unique_title_list.push item.title
            # else
            #     sign = false
            #     for uniqueT,i in unique_title_list
            #         if uniqueT == item.title
            #             sign = true;
            #             break;
            #     if sign
            #         continue
            #     else
            #         unique_title_list.push item.title
            #---------------- end ----------------------
            if item.parent_id == 0
                if parseInt(item.day_offset) == 0
                    centerRighttopdes = "#{item.day_name}当天"
                else if parseInt(item.day_offset) < 0
                    centerRighttopdes = "#{item.day_name}前#{-parseInt(item.day_offset)}天" 
                else if parseInt(item.day_offset) > 0
                    centerRighttopdes = "#{item.day_name}后#{parseInt(item.day_offset)}天"

                listview_data_temp_group.push
                    item_info:item
                    is_expanded: false
                    eventType: _cfg.list_config[item.kf_course_item_type_id].name
                    viewType: "ListViewCellLine"
                    _rightLayoutSize: 25
                    _leftLayoutSize: 40
                    _bottomDivider:_cfg._bottomDivider if !(item.kf_course_item_type_id == 2 and item.is_expanded? and  item.is_expanded == true)
                    
                    centerTitle: item.title
                    centerRighttopdes:"#{item.item_type_name}" if item.item_type_name? and item.item_type_name != "undefined"   #_cfg.list_config[item.kf_course_item_type_id].cnName
                    _centerBottomdesColor:_cfg._centerBottomdesColor
                    _centerTitleColor: _cfg._centerTitleColor 
                    centerRighttopdes:centerRighttopdes
                    centerBottomdes: if item.content?  then "#{item.content}" else ""
                    _centerBottomdes2Color:_cfg._centerBottomdes2Color
                    rightImage:
                        imageType: "assets"
                        imageSize: "fitSize"
                        imageSrc: "#{_cfg.listview_right_img}"
                    leftImage:
                        imageType: "assets"
                        imageSize: "fitSize"
                        imageSrc: "#{_cfg.src_prefix}#{_cfg.list_config[item.kf_course_item_type_id].name}.png"
                    hasFooterDivider:"true"

                # 加入文章列表 默认不显示
                if item.item_lists? and item.item_count > 0
                    if item.kf_course_item_type_id == 2
                        for inner_item,j in item.item_lists
                            inner_item["subtitle"] = item.title;
                            inner_item["item_colum"] = item.item_colum;
                            listview_data_temp_group.push
                                item_info:inner_item
                                kf_course_item_type_id:item.kf_course_item_type_id
                                # fav_id_id: item.fav_id_id  
                                eventType: "article_list"
                                viewType: if item.is_expanded? and item.is_expanded == true then  "ListViewCellLine" else "ListViewCellNull"
                                _rightLayoutSize: 25
                                _leftLayoutSize: 40
                                centerBottomdes2: "#{inner_item.title}" if inner_item.title and inner_item.title != "undefined"
                                _centerBottomdes2Color:_cfg._centerBottomdesColor_article_list
                                _bottomDivider:_cfg._bottomDivider if item.item_count == j+1
                                hasFooterDivider:"true" if item.item_count == j+1
            else
                continue

        root._listview_data.data = root._listview_data.data.concat(listview_data_temp_group)
    
    onItemClick : (data) ->
        updateItemsData = []  
        item = root._listview_data.data[data.position]
        info = item.item_info
        position = data.position
        if item.eventType in ["set_time","todo","survey","article_list"]
            $A().app().openPage
                page_name: _cfg.list_page_name[item.eventType].page_name
                params: { info  :  JSON.stringify info }
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
                    item._bottomDivider = _cfg._bottomDivider
                    #文章列表 隐藏
                    for inner_item,i in item.item_info.item_lists
                        root._listview_data.data[parseInt(position) + i + 1].viewType = "ListViewCellNull"
            else
                item.centerBottomdes2 = item.centerBottomdes
                item.centerBottomdes = ""
                item.rightImage.imageSrc = "#{_cfg.listview_right_down_img}" if item.eventType != "remind"

                #文章的展开处理
                if item.eventType == "article"
                    item._bottomDivider = {}
                    #文章列表显示
                    for inner_item,i in item.item_info.item_lists
                        root._listview_data.data[parseInt(position) + i + 1].viewType = "ListViewCellLine"
            
        item.is_expanded = !item.is_expanded
        root._listview_data.data[position] = item    
        #更新被点击的item
        updateItemsData.push {position:position,data:item}
        #更新文章列表
        if item.eventType == "article"
            for inner_item,i in item.item_info.item_lists
                updateItemsData.push
                    position:parseInt(position) + i + 1
                    data:root._listview_data.data[parseInt(position) + i + 1]
        $A().page().widget("#{root._page_name}_ListViewBase_0").updateItems 
            data:updateItemsData                

#启动程序
new ECpageClass("page_column_detail")