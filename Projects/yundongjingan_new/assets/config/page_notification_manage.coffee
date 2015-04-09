# page类
class ECpageClass
    root = {}  # 这是ECpageClass的一个实例的全局变量
    _page_name: "" # 属性
    # _item_info : {}
    _platform:""
    _local_notifications:[]
    _listview_data:
        pullable: false
        hasFooterDivider: false
        hasHeaderDivider: true
        dividerHeight: 1
        dividerColor: "#EBEBEB"
        data: [
            {
                viewType: "ListViewCellLine"
                _rightLayoutSize: 0
                _leftLayoutSize: 40
                centerTitle: "正在加载......"
            } 
        ]

    _constructor: (@_page_name) ->
        root = this
        #获取其他界面传来的数据
        @prepareForInitView()

        $A().page().widget("#{@_page_name}_ListViewBase_0").data JSON.stringify @_listview_data
        $A().page().widget("#{@_page_name}_ListViewBase_0").onItemInnerClick (data)-> root.onItemInnerClick(data)
        # $A().page().widget("#{@_page_name}_ListViewBase_0").onItemClick (data)-> root.onItemClick(data)
        # $A().page().widget("#{@_page_name}_ListViewBase_0").onResume (data)-> root.onResume(data)
        # $A().page().widget("#{@_page_name}_ListViewBase_0").onResult (data)-> root.onResult(data)
        $A().page().onCreated -> root.onCreated()

    constructor: (_page_name) ->
        @_constructor(_page_name)

    onCreated: () ->
        $A().page().widget("#{@_page_name}_ListViewBase_0").refreshData JSON.stringify @_listview_data if root._platform? and root._platform == "ios"
        #自定义函数
        root.refreshView()
    
    onItemClick: (data) ->    

    
    onItemInnerClick: (data) ->
        # $A().app().log JSON.stringify data
        item  = @_listview_data.data[data.position]
        if item._type == "back"
            $A().page().setTimeout("100").then () ->
                $A().app().closePage()
            return
        if item._type == "notification" and data.isChecked == "false"
            @_local_notifications[data.position].isOpen = false
            $A().app().makeToast "关闭“" + @_local_notifications[data.position].title + "”提醒"
            $A().notification().remove
                notificationId:item.item_info.notificationId
        else
            $A().app().makeToast "开启“" + @_local_notifications[data.position].title + "”提醒"
            @_local_notifications[data.position].isOpen = true
            $A().notification().add(item.item_info)
        $A().lrucache().set
            key:"local_notifications"
            value:@_local_notifications
    
    onResume: () ->
    
    #---------------------------------------具体业务代码---------------------------------------------
    prepareForInitView: () ->
        $A().app().platform().then (platform) ->
            root._platform = platform
            
    refreshView: () ->
        # $A().app().log "refreshView.........."
        $A().lrucache().get("local_notifications").then (data) ->
            # $A().app().log "local_notifications.........." + JSON.stringify data
            listview_data_temp = []
            if data? and data.length != 0
                root._local_notifications = JSON.parse(data)
                for k_notification,v_notification of JSON.parse(data)
                    # $A().app().log "v_notification" + typeof v_notification
                    listview_data_temp.push
                        item_info:v_notification
                        viewType: "ListViewCellSetting"
                        name:v_notification.title
                        value:""
                        description:root.dateFormat(new Date(parseInt(v_notification.broadcastTime)))
                        isOpen:if v_notification.isOpen == "true" then true else false
                        _type:"notification"
            listview_data_temp.push
                viewType: "ListViewCellButton"
                btnTitle: "返 回"
                btnType : "ok"
                _type:"back"
            root._listview_data.data = listview_data_temp
            # $A().app().log "listview_data_temp root._page_name.........." + root._page_name
            # $A().app().log "listview_data_temp root._listview_data.........." + JSON.stringify root._listview_data
            $A().page().widget("#{root._page_name}_ListViewBase_0").refreshData JSON.stringify root._listview_data

    dateFormat: (day) ->
        yyyy = day.getFullYear()
        MM = day.getMonth() + 1
        dd = day.getDate() #//日 
        hh =  day.getHours() #'0' + day.getHours() if day.getHours() < 10   #//小时 
        mm = day.getMinutes() #'0' + day.getMinutes() if day.getMinutes() < 10 # //分 
        ss = day.getSeconds() #'0' + day.getSeconds() if day.getSeconds() < 10  # //秒 
        return "#{MM}月#{dd}日#{hh}:#{mm}:#{ss}"##{yyyy}年
#启动程序
Page = new ECpageClass("page_notification_manage")