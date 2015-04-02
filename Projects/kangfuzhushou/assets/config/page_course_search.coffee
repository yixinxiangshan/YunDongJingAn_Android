# page类
class ECpageClass
    root = {}  # 这是ECpageClass的一个实例的全局变量
    _page_name : "" # 属性
    _item_info : {}
    _platform:""
    _listview_data:
        pullable: false
        hasFooterDivider: false
        hasHeaderDivider: true
        dividerHeight: 1
        dividerColor: "#EBEBEB"
        data: [
            {
                viewType: "ListViewCellInputText"
                inputType:"number"
                inputText:"560359093" #
                hint:"课程码"
                # btnName:"扫一扫"
                name:"number"
            }
            {
                viewType: "ListViewCellButton"
                btnTitle: "添  加"
                btnType : "ok"
                _type:"search"
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
        #root.    
    onItemInnerClick: (data) ->
        if root._listview_data.data[data.position]._type == "search"
            if  data._form.number? and data._form.number!= ""
                if data._form.number.length == 9
                    $A().app().showLoadingDialog
                        content:"正在验证课程码"
                    $A().app().callApi
                        method:"doctor_groups/detail" 
                        doctor_group_id: data._form.number
                        cacheTime: 0
                    .then (data) ->
                        $A().app().closeLoadingDialog()
                        # $A().app().log JSON.stringify data
                        if data.courses? and data.courses.length>0
                            $A().app().openPage 
                                page_name:"page_course_choose",
                                params: 
                                    courses: JSON.stringify data.courses
                                close_option: "close"
                        else
                            $A().app().makeToast "请输入正确的机构码"
                else
                    $A().app().makeToast "请输入正确的机构码"

            else
                $A().app().makeToast "请输入机构码/课程码"
    
    onResume: () ->
    
    onResult: () ->

    #---------------------------------------具体业务代码---------------------------------------------
    
    prepareForInitView: () ->
        $A().app().platform().then (platform) ->
            root._platform = platform

#启动程序
new ECpageClass("page_course_search")