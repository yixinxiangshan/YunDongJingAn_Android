# page类
class ECpageClass
    root = {}  # 这是ECpageClass的一个实例的全局变量
    checked_count = 0
    _platform:""
    _cfg:
        checked_image_url: "webview/images/icon/default/checkbox_check.png"
        unchecked_image_url: "webview/images/icon/default/checkbox_uncheck.png"
    _page_name : "" # 属性
    _item_info : {}
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
                _leftLayoutSize: 0
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
        root.getNetResource()

    onItemClick: (data) ->    
        updateItemsData = []
        position = data.position
        item = root._listview_data.data[data.position]
        if item.type == "_todo"
            if root._listview_data.data[position].checked  ==  false
                root._listview_data.data[position].checked  = true
                root.itemCheck_saveFav_id_id root._listview_data
                $A().lrucache().set
                    key:"#{root._item_info.fav_id_id}"
                    value:"#{root._item_info.fav_id_id_value}"
                root._listview_data.data[position].leftImage.imageSrc = root._cfg.checked_image_url             
                updateItemsData.push
                    position:position
                    data:root._listview_data.data[position]
                $A().page("page_home").param
                    key: "_setting_changed"
                    value: "true"
                checked_count = 0
                for item,i in root._listview_data.data
                    if item.checked && item.type == "_todo"
                        if root._listview_data.data[i].checked  ==  true
                            checked_count = checked_count + 1
                
                root._listview_data.data[0].centerRightdes = " #{checked_count} / #{root._item_info.item_lists.length}" 
                updateItemsData.push
                    position:0
                    data:root._listview_data.data[0]
                
                $A().page().widget("#{root._page_name}_ListViewBase_0").updateItems 
                    data:updateItemsData 
                # 如果是最后一项，则确认完成待办
                if "#{checked_count}" == "#{root._item_info.item_lists.length}"
                    $A().app().makeToast "#{root._item_info.title} 已完成！"
                    root.updateState("done","#{root._item_info.fav_id_id_value}","#{root._item_info.fav_id}","#{root._item_info.id}")
                    $A().page().setTimeout("500").then () ->
                        $A().page("page_home").param
                            key: "_setting_changed"
                            value: "true"
    
    onItemInnerClick: (data) ->
        item = root._listview_data.data[data.position]
        if item._type? and  item._type == "back"
            $A().app().closePage()

    
    onResume: () ->
    
    onResult: () ->

    #---------------------------------------具体业务代码---------------------------------------------

    prepareForInitView: () ->
        $A().app().platform().then (platform) ->
            root._platform = platform

        $A().page().param("info").then (data) ->
            data = JSON.parse data
            root._item_info = data
    
    # 从接口获取数据，初始化数据内容
    getNetResource: ->
        #更新数据
        $A().lrucache().get(root._item_info.fav_id_id).then (data)->
            keys = data.split ","
            for key ,val of  root._item_info.item_lists
                val.checked = false
                for v in keys
                    if (parseInt(key) + 1) == parseInt(v)
                        val.checked = true
                        checked_count = checked_count + 1
            $A().page().setTimeout("300").then () ->                    
                $A().page().widget("ActionBar").title
                    title : "#{root._item_info.title}"
                root.getTodo_Contents root._item_info.item_lists
                root._listview_data.data.push
                    viewType: "ListViewCellButton"
                    btnTitle: "返 回"
                    btnType : "ok"
                    _type : "back"
    
    # 获取具体业务的内容
    getTodo_Contents: (data) ->
        root._listview_data.data.pop()
        for item in data
            root._listview_data.data.push root.getTodo_Contents_getItem(item.checked , item.title,item.id)
        
        root._listview_data.data.splice 0 , 0 ,
            viewType: "ListViewCellLine"
            _rightLayoutSize: 0
            _leftLayoutSize: 20
            _bottomDivider:
                imageType: "assets"
                imageSize: "fitSize"
                imageSrc: "webview/images/icon/default/divider.png"
            _backgroundColor:
                _clickable: true
                pressed: "#ffffff"
                selected: "#ffffff"
                normal: "#ffffff"
                alpha: 38
            centerRightdes: "#{checked_count} / #{root._listview_data.data.length} #{if checked_count == root._listview_data.data.length then '( 已完成 )' else ''}"
            centerTitle: " "
            type : "_count"
            hasFooterDivider:"true"
        $A().page().setTimeout("100").then () ->
            $A().page().widget("#{root._page_name}_ListViewBase_0").refreshData JSON.stringify root._listview_data
    
    getTodo_Contents_getItem: (checked ,title,id) ->
        item = 
            id: id
            viewType: "ListViewCellLine"
            _rightLayoutSize: 10
            _leftLayoutSize: 40
            _bottomDivider:
                imageType: "assets"
                imageSize: "fitSize"
                imageSrc: "webview/images/icon/default/divider.png"
            leftImage:
                imageType: "assets"
                imageSize: "fitSize"
                imageSrc: if checked then root._cfg.checked_image_url else root._cfg.unchecked_image_url
            centerTitle: title
            checked: checked
            type : "_todo"
        if checked
            item._backgroundColor = 
                _clickable: true
                pressed: "#ffffff"
                selected: "#ffffff"
                normal: "#ffffff"
                alpha: 38
        return item

    itemCheck_saveFav_id_id: (_listview_data) ->
        fav_id_ids = []
        for item,i in _listview_data.data
            fav_id_ids.push (parseInt(item.id) - parseInt(root._item_info.item_lists[0].id) + 1 ) if item.checked == true
        root._item_info.fav_id_id_value = fav_id_ids.join ","
    
    #完成状态提交
    updateState: (action_type,action,kf_course_user_fav_id,course_index_id) ->
        $A().lrucache().set
            key:"#{kf_course_user_fav_id}_#{course_index_id}"
            value:"#{action}"
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
#启动程序
new ECpageClass("page_course_todo")