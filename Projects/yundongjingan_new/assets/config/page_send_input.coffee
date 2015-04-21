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
                viewType: "ListViewCellInputText"
                inputType:"text"
                hint:"姓名（必填）"
                name:"name"
            }
            {
                viewType: "ListViewCellInputText"
                inputType:"text"
                hint:"地址（必填）"
                lines:2
                name:"address"
            }
            {
                viewType: "ListViewCellInputText"
                inputType:"number"
                hint:"电话（必填）"
                name:"phone"
                inputText:""
            }
            {
                viewType: "ListViewCellInputText"
                inputType:"number"
                hint:"邮编（可选）"
                name:"zip"
                inputText:""
            }
            {
                viewType: "ListViewCellButton"
                inputType:"number"
                btnTitle: "提 交"
                btnType : "ok"
                _type : "ok"
            }
        ]

    _constructor: (@_page_name) ->
        root = this
        #获取其他界面传来的数据
        @prepareForInitView()
#        $A().lrucache().get("phone").then (phone) ->
#            root._listview_data.data[1].inputText =  if phone? and phone != "" then phone else ""

        $A().page().widget("#{@_page_name}_ListViewBase_0").data JSON.stringify @_listview_data
        $A().page().widget("#{@_page_name}_ListViewBase_0").onItemInnerClick (data)-> root.onItemInnerClick(data)
        $A().page().widget("#{@_page_name}_ListViewBase_0").onItemClick (data)-> root.onItemClick(data)
        # $A().page().onResume (data)-> root.onResume()
        # $A().page().onResult (data)-> root.onResult(data)
        $A().page().onCreated -> root.onCreated()

    constructor: (_page_name) ->
        @_constructor(_page_name)

    onCreated: () ->
        $A().page().widget("#{@_page_name}_ListViewBase_0").refreshData JSON.stringify @_listview_data if root._platform? and root._platform == "ios"
        #自定义函数
    
    onItemClick: (data) ->
    
    onItemInnerClick: (data) ->
        $A().app().log "---------------------data" + data
        $A().app().log "---------------------JSON.stringify data" + JSON.stringify data
        # data._form = JSON.parse data._form
        name = if data._form.name? then data._form.name else ""
        address = if data._form.address? then data._form.address else ""
        phone = if data._form.phone? then data._form.phone else ""
        zip = if data._form.zip? then data._form.zip else ""

        if name == ""
            $A().app().makeToast "请输入您的姓名！"
        else if address == ""
            $A().app().makeToast "请输入您的地址"
        else if phone == ""
            $A().app().makeToast "请输入您的电话"
        else
            $A().page().param("info").then (info) ->
                $A().app().makeToast "正在提交"
                info_ = JSON.parse info
                $A().app().callApi
                    method: "trade/ships/create"
                    cms_content_id: info_.content_id
                    title: info_.content_title
                    consignee_name: name
                    consignee_address: address
                    phone: phone
                    consignee_zip: zip
                    is_default: 0
                    cacheTime: 0
                .then (data) ->
                    if data.success == true
                        $A().app().makeToast "提交成功，谢谢您的申请。"
                        $A().page().setTimeout("2000").then () ->
                            $A().app().closePage()
                    else
                        $A().app().makeToast "提交失败，请重试或者检查您的网络是否打开。"
    
    onResume: () ->
        
    onResult: (data) ->
    
    #---------------------------------------具体业务代码---------------------------------------------
    prepareForInitView: () ->
        $A().app().platform().then (platform) ->
            root._platform = platform

#启动程序
Page = new ECpageClass("page_send_input")
