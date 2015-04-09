# page类
class ECpageClass
    root = {}  # 这是ECpageClass的一个实例的全局变量
    _page_name : "" # 属性
    _item_info : {}
    _pagerwidget_data:
        pagerCount: 200
        offset:0
        itemPageName:"page_home_pager"

    _constructor: (@_page_name) ->
        root = this
        #获取其他界面传来的数据
        @prepareForInitView()
        $A().app().log "-----------------------------------"
        # $A().page().widget("#{@_page_name}_ListViewBase_0").data JSON.stringify @_listview_data
        # $A().page().widget("#{@_page_name}_ListViewBase_0").onItemInnerClick (data)-> root.onItemInnerClick(data)
        $A().page().widget("ActionBar").onItemClick (data)-> root.onItemClick(data)
        
        $A().page().onResume ()-> root.onResume()
        $A().page().onResult (data)-> root.onResult(data)
        $A().page().onPageSelected (data) -> root.onPageSelected(data)
        $A().page().onCreated -> root.onCreated()

    constructor: (_page_name) ->
        @_constructor(_page_name)

    onCreated: () ->
        #自定义函数
        #root.
    onItemClick: (itemId) ->    
        switch "#{itemId}"
            when "扫一扫"
                $A().page().openQRCapture({});
            when "管理课程"
            
                $A().app().openPage 
                    page_name:"page_course_list",
                    params: {}
                    close_option: ""
            when "设置时间"
                $A().app().openPage
                    page_name:"page_course_time_choose"
                    params:  {}
                    close_option: ""
            when "意见反馈"
                # $A().app().makeToast  "意见反馈"
                $A().app().openPage 
                    page_name:"page_feedback",
                    params: {}
                    close_option: ""
            when "Lee"
                # $A().app().makeToast  "意见反馈"
                $A().app().openPage 
                    page_name:"page_lee",
                    params: {}
                    close_option: ""
            when "登录"
                # $A().app().makeToast  "意见反馈"
                $A().app().openPage 
                    page_name:"page_login",
                    params: {}
                    close_option: ""
            when "设置"
                # $A().app().makeToast  "意见反馈"
                $A().app().openPage 
                    page_name:"page_setting",
                    params: {}
                    close_option: ""
            when "我的"
                
                $A().app().openPage 
                    page_name:"page_my",
                    params: {}
                    close_option: ""
            when "个人中心"
                # $A().app().makeToast  "意见反馈"
                $A().app().openPage 
                    page_name:"page_mycenter",
                    params: {}
                    close_option: ""
            when "搜索课程"
                # $A().app().makeToast  "意见反馈"
                $A().app().openPage 
                    page_name:"page_course_search",
                    params: {}
                    close_option: ""
        if itemId.title? and itemId.title != "undefined"
            barTitle = itemId.title
            $A().app().showCalendarConfirm
                title:"Test"
                date: barTitle
            .then (data) ->
                root._pagerwidget_data.offset = parseInt(parseInt(root._pagerwidget_data.offset) + parseInt(getDayDiff(data.dateStr,barTitle)))
                
                $A().page("#{root._page_home}").widget("page_home_PagerWidget_0").refreshData JSON.stringify root._pagerwidget_data
                
                # $A().notification().add
                #   title:"提醒"
                #   content:"内容"
                #   notificationId:"1000"
                #   broadcastTime:"#{new Date().getTime() + 1000 * 15}"
                # $A().app().makeToast  "添加通知 成功"
            # when "关闭通知"
            #   # $A().app().showLoadingDialog
      #             # title:""
      #             # content:"正在导入课程数据,请稍后"
      #             # cancelable:"true"
            #   $A().notification().clear()
            #   # $A().app().makeToast  "关闭通知"
            
            # when "_title" 
                
            #     $A().app().showDatepickerConfirm
            #         ok: "确定",
            #         cancel:"取消"
            #         title:"选择日期"
            #     .then (data) ->
            #         $A().page().param("today_date").then (today_date) ->
            #             time_begin =  today_date.split "-"
            #             time_end = data.value.split "-"
            #             reg = new RegExp("-","g")
            #             tmpBeginTime = new Date(time_begin[0] , parseInt(time_begin[1])-1 , time_begin[2],0,0,0,0).getTime()
            #             tmpEndTime = new Date(time_end[0] , parseInt(time_end[1])-1 , time_end[2],0,0,0,0).getTime()
            #             time_diff = ((tmpEndTime - tmpBeginTime) / (1000 * 60 * 60 * 24));                  
            #             root._pagerwidget_data.offset = parseInt time_diff 
            #             # $A().app().log   "offset:" + _pagerwidget_data.offset
            #             $A().page("#{root._page_home}").widget("page_home_PagerWidget_0").refreshData JSON.stringify root._pagerwidget_data
   
    onItemInnerClick: (data) ->

    onPageSelected: (data) ->
        # $A().page().param({key:"is_first_open" , value: "false"})
        # $A().app().log "page_home onPageSelected: " + data.position
        $A().page().param({key:"_now_offset" , value: data.position})
        root._pagerwidget_data.offset = data.position
        $A().page().setTimeout("300").then () ->
            $A().page("page_home_pager?offset=#{parseInt(data.position)}").callFun({"pageName":"onPageSelected", "input":{} })
        # $A().app().log "page_home onPageSelected: end"
        
            $A().page().param({key:"is_first_open" , value: "false"}) if data.position == 0
    onResume: () ->
    
    onResult: (data) ->
        # $A().app().log "result data: #{data}"
        doctor_group_id = data.codeString.split("=")[1]
        @showDoctorCourses doctor_group_id
        return "_false" 

    #---------------------------------------具体业务代码---------------------------------------------
    
    prepareForInitView: () ->
        $A().page().param({key:"is_first_open" , value: "true"})

    getDayDiff = (day1_str,day2_str) ->
        day1_arr = day1_str.split('-');
        day1 = new Date(parseInt(day1_arr[0]),parseInt(Number(day1_arr[1])-1),Number(day1_arr[2]));
        day2_arr = day2_str.split('-');
        day2 = new Date(parseInt(day2_arr[0]),parseInt(Number(day2_arr[1])-1),Number(day2_arr[2]));
        # $A().app().log "days:" +"#{parseInt(day1.getTime() - day2.getTime()) / (86400000)}"
        return "#{parseInt(day1.getTime() - day2.getTime()) / (86400000)}"

    # 处理二维码扫描结果
    showDoctorCourses: (doctor_group_id)->
        $A().app().callApi
            method:"doctor_groups/detail" 
            doctor_group_id: doctor_group_id
            cacheTime: 0
        .then (data) ->
            $A().app().openPage 
                page_name:"page_course_choose",
                params: 
                    courses: JSON.stringify data.courses
                close_option: ""
#启动程序
new ECpageClass("page_home")