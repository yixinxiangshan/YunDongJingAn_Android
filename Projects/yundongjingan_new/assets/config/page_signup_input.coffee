class ECpageClass
  root = {} # 这是ECpageClass的一个实例的全局变量
  _page_name: "" # 属性
  _item_info: {}
  _platform: ""
  _listview_data:
    pullable: false
    hasFooterDivider: false
    hasHeaderDivider: false
    dividerHeight: 0
    dividerColor: "#cccccc"
    data: [
      {
        viewType: "ListViewCellInputText"
        inputType: "text"
        hint: "姓名（必填）"
        name: "username"
        inputText: ""
      }
      {
        viewType: "ListViewCellInputText"
        inputType: "number"
        hint: "年龄（必填）"
        name: "age"
        inputText: ""
      }
#      {
#        viewType: "ListViewCellInputText"
#        inputType: "text"
#        hint: "性别（必填）"
#        name: "gender"
#        inputText: ""
#      }
      {
        viewType: "ListViewCellInputText"
        inputType: "number"
        hint: "电话（必填）"
        name: "phone"
        inputText: ""
      }
      {
        viewType: "ListViewCellInputText"
        inputType: "text"
        hint: "单位（可选）"
        name: "address"
        inputText: ""
      }
      {
        viewType: "ListViewCellButton"
        inputType: "number"
        btnTitle: "提 交"
        btnType: "ok"
        _type: "ok"
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
    $A().page().param("info").then (info) ->
      root._item_info = JSON.parse info
      if root._item_info.signup_id?
        $A().app().callApi
          method: "trade/signups/detail"
          signup_id: root._item_info.signup_id
          cacheTime: 0
        .then (data) ->
          if data.errors?
            if data.errors[0].error_num?
              $A().app().makeToast "网络状态不好，请重新加载"
            else
              $A().app().makeToast "没有网络"
          else
            root._listview_data.data[0].inputText = data.order.username
            root._listview_data.data[1].inputText = data.order.age
#            root._listview_data.data[2].inputText = data.order.gender
            root._listview_data.data[2].inputText = "#{data.order.phone}"
            root._listview_data.data[3].inputText = "#{data.order.address}"
            root._listview_data.data.push
              viewType: "ListViewCellButton"
              inputType: "number"
              btnTitle: "删 除"
              btnType: "cancel"
              _type: "cancel"
            $A().page().widget("#{root._page_name}_ListViewBase_0").refreshData JSON.stringify root._listview_data

      $A().page().widget("#{root._page_name}_ListViewBase_0").refreshData JSON.stringify root._listview_data if root._platform? and root._platform == "ios"
  #自定义函数

  onItemClick: (data) ->

  onItemInnerClick: (data) ->
    item = @_listview_data.data[data.position]
    if item._type? and item._type == 'cancel'
      $A().app().showConfirm
        ok: "确定",
        title:"警告"
        cancel: "取消",
        message: "删除之后将无法恢复，您需要重新申请。确定需要删除吗？"
      .then (result) ->
        if result.state == "ok"
          $A().app().makeToast "正在删除"
          $A().app().callApi
            method: "trade/signups/destroy"
            id: root._item_info.signup_id
            cacheTime: 0
          .then (data) ->
            if data.success == true
              $A().app().makeToast "删除成功。"
              $A().page().setTimeout("2000").then () ->
                $A().app().closePage()
            else
              $A().app().makeToast "删除失败，请重试或者检查您的网络是否打开。"
        else
          return false
    else
      username = if data._form.username? then data._form.username else ""
      age = if data._form.age? then data._form.age else ""
#      gender = if data._form.gender? then data._form.gender else ""
      phone = if data._form.phone? then data._form.phone else ""
      address = if data._form.address? then data._form.address else ""

      if username == ""
        $A().app().makeToast "请输入您的姓名"
      else if age == ""
        $A().app().makeToast "请输入您的年龄"
#      else if gender == ""
#        $A().app().makeToast "请输入您的性别"
      else if phone == ""
        $A().app().makeToast "请输入您的电话"
      else
        if item._type? and item._type == 'ok'
          $A().app().makeToast "正在提交"
          if root._item_info.signup_id? #已存在，修改
            $A().app().callApi
              method: "trade/signups/modify"
              id: root._item_info.signup_id
              cms_content_id: root._item_info.content_id
              title: root._item_info.content_title
              username: username
              age: age
#              gender: gender
              phone: phone
              address: address
              cacheTime: 0
            .then (data) ->
              if data.success == true
                $A().app().makeToast "提交成功，谢谢您的申请。"
                $A().page().setTimeout("2000").then () ->
                  $A().app().closePage()
              else
                $A().app().makeToast "提交失败，请重试或者检查您的网络是否打开。"
          else #未存在，创建
            $A().app().callApi
              method: "trade/signups/create"
              cms_content_id: root._item_info.content_id
              title: root._item_info.content_title
              username: username
              age: age
#              gender: gender
              phone: phone
              address: address
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
Page = new ECpageClass("page_signup_input")