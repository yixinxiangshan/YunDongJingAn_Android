$A().app().log "page_course_survey"
class ECpageClass
    root = {}
    _page_name : "" # 属性
    _platform:""
    _listview_data :
        pullable: false
        hasFooterDivider: true
        hasHeaderDivider: false
        dividerHeight: 0
        dividerColor: "#cccccc"
        data: [
            {
                viewType: "ListViewCellLine"
                _rightLayoutSize: 0
                _leftLayoutSize: 20
                centerTitle: "正在加载......"
                hasFooterDivider:"true"
            }
        ]
    
    _info = {}
    _res = {}  #w网络数据
    _server_answers = null
    _local_answers = null
    _now_index = 0

    _have_press_position = null  #记录点击的多选项 
    _mViewMethod = null  #类型: 单选 多选 文本 
    _mFirstSign = true  #多选页面初始化控制，第一次打开页面加载缓存内容
    _have_cache = true #如果有缓存页面加载后不允许修改内容，默认为有缓存

    _cfg = 
        page_name: "page_course_survey"
        divider_config: 
            imageType: "assets"
            imageSize: "fitSize"
            imageSrc: "webview/images/icon/default/divider.png"
        checked_image_url: "webview/images/icon/default/checkbox_check.png"
        unchecked_image_url: "webview/images/icon/default/checkbox_uncheck_pressed.png"
        radio:"radio"
        checkbox:"checkbox"
        string:"string"
        text:"text"

    _constructor:(@_page_name)->
        root = this

        @prepareForInitView()
        $A().page().widget("#{@_page_name}_ListViewBase_0").data JSON.stringify @_listview_data
        $A().page().widget("#{@_page_name}_ListViewBase_0").onItemInnerClick (data)-> root.onItemInnerClick(data)
        $A().page().widget("#{@_page_name}_ListViewBase_0").onItemClick (data)-> root.onItemClick(data) if !_have_cache
        # $A().page().onResume ()-> root.onResume()
        # $A().page().onResult (data)-> root.onResult(data)、
        $A().page().onCreated -> root.onCreated()
        
    constructor:(_page_name)->
        @_constructor(_page_name)

    onCreated: () ->
        $A().page().widget("#{@_page_name}_ListViewBase_0").refreshData JSON.stringify @_listview_data #if root._platform? and root._platform == "ios"
        root.showSurvey()

    onItemClick: (data) ->
        item = root._listview_data.data[data.position]
        if _mViewMethod == _cfg.checkbox
            if _have_press_position[data.position-1] == 0
                _have_press_position[data.position-1] = data.position
            else
                _have_press_position[data.position-1] = 0
        root.showSurvey_showForm _res
                
        if item.type == "_option" and _mViewMethod == _cfg.radio
            root.saveToCache(item,item.value)
            root.showSurvey_showForm _res
        #有问题，最后一个点了以后未提交的话，仍然显示已完成。
        $A().page("page_home").param
            key: "_data_changed"
            value: "true"

    onItemInnerClick: (data) ->
        canGoNext = true
        if _mViewMethod != _cfg.radio and _mViewMethod != _cfg.checkbox
            # data._form = JSON.parse data._form
            value = data._form.value + ""
            if !value? or value.length == 0
                canGoNext = false
                $A().app().makeToast "请输入内容"
            #_listview_data.data由_now_index负责从总的数据中装载
            #此处写成 1 ，是因为当前页面只有一个EditText
            item = root._listview_data.data[1]
            root.saveToCache(item,value)
            
        else if _mViewMethod == _cfg.checkbox
            item = root._listview_data.data[_now_index]
            res = ""
            #变量拼接控制符，控制逗号的位置
            sign = true
            for value,i in _have_press_position
                if value != 0
                    if sign
                        res = res + value
                        sign = false
                    else
                        res = res + "," + value
            #如果下个页面还是多选，将_have_press_postion设置为null，
            #这样下次viewMaker会新建数组来存放多选项        
            _have_press_position = null
            root.saveToCache(item,res)
        #当在单选和多选界面时，用户点击了button说明要进入下个页面或者结束，
        #将_mFirstSign=true便于下个页面还是单选或多选时，页面还是会初始化缓存内容
        _mFirstSign = true            

        if canGoNext 
            _now_index = _now_index + 1
            if _now_index > _res.items.length - 1
                if _info.action_type? && _info.action_type == "done"
                    return $A().app().closePage()
                root.updateState("done","#{JSON.stringify _local_answers}","#{_info.fav_id}","#{_info.id}")
                $A().page().setTimeout("500").then () ->
                    $A().page("page_home").param
                        key: "_setting_changed"
                        value: "true"
                    $A().app().closePage()
            else
                root.showSurvey_showForm _res



    onResume: () ->
    
    onResult: () ->
    
    #---------------------------------------具体业务代码---------------------------------------------

    prepareForInitView: () ->

        $A().app().platform().then (platform) ->
            root._platform = platform
        $A().page().param("info").then (data) ->
            # data.action = JSON.parse data.action
            reg = new RegExp("\r\n","g");
            reg1 = new RegExp("\t","g");
            _info = JSON.parse(data.replace(reg, "\\r\\n").replace(reg1, "\\t"))

    showSurvey: ->
        $A().page().widget("ActionBar").title
            title : _info.title

        $A().app().callApi
            method:"courses/indices/forms" 
            item_id:_info.id
            cacheTime: 0
        .then (res)->
            _res = res
            root.showSurvey_setAnswerToRes()
            $A().page().setTimeout("500").then () ->
                #判断用户是否完成的全部的操作，从而确定cache里面的数据是否是完整的
                root.isDone()
                root.showSurvey_showForm _res

    showSurvey_setAnswerToRes: ->
        _server_answers = _info.action if _info.action?
        $A().lrucache().get(_info.fav_id_id).then (data)->
            _local_answers = JSON.parse data if data != ""
            if _server_answers?
                for item,i in _res.items
                    if _server_answers["#{item.id}"]?
                        item.action = _server_answers["#{item.id}"]
            else if _local_answers?
                for item,i in _res.items
                    if _local_answers["#{item.id}"]?
                        item.action = _local_answers["#{item.id}"]
                        # $A().app().log "local:" + item.action

    showSurvey_showForm: (data)->
        # $A().app().log  "showForm:" + JSON.stringify data
        list_data = []
        done_count = 0
        form = {}
        answers = {}
        mViewType = null
        mProcessFun = null

        form = data.items[_now_index]
        form.number = _now_index
        
        for item in data.items
            done_count = done_count + 1 if item.action?
        # count & title
        list_data.push 
            viewType: "ListViewCellLine"
            _rightLayoutSize: 0
            _leftLayoutSize: 0
            _bottomDivider: _cfg.divider_config
            centerTitle: form.title
            centerRighttopdes: "#{_now_index + 1} / #{data.items.length}"
            type : "_title"
            hasFooterDivider:"true"

        root.mViewMaker(list_data,form)
        root.mShowAnswer(list_data,form,false);

        root._listview_data.data = list_data
        
        $A().page().widget("#{_cfg.page_name}_ListViewBase_0").refreshData JSON.stringify root._listview_data

    mButton: (list_data)->
        list_data.push 
            viewType: "ListViewCellButton"
            btnTitle: if _now_index == (_res.items.length - 1) then "完 成" else "进 入 下 一 题" 
            btnType : "cancel"
            type : "_submit"

    mViewMaker: (list_data,form)->
        _mViewMethod = form.form_type
        switch "#{_mViewMethod}"
            when "radio"
                if _mFirstSign
                    #如果数据不完整，清除原有cache数据
                    if !_have_cache then form.action = null
                    _mFirstSign = false    
                mViewType = "ListViewCellLine"
                mProcessFun = (list_data,form,mViewType)->
                    for option,i in form.content.split "\r\n" 
                        list_data.push
                            viewType: mViewType
                            _rightLayoutSize: 0
                            _leftLayoutSize: 40
                            _bottomDivider: _cfg.divider_config
                            leftImage:
                                imageType: "assets"
                                imageSize: "fitSize"
                                imageSrc: if form.action? && parseInt(form.action) == i then _cfg.checked_image_url else _cfg.unchecked_image_url
                            centerTitle: option#item data
                            form: form
                            value: i
                            type : "_option"
                            hasFooterDivider:"true"
                    #如果点击了才显示button
                    if form.action? 
                        root.mButton(list_data)
                        return "_false"

            when "checkbox"
                mViewType = "ListViewCellLine"
                mProcessFun = (list_data,form,mViewType)->
                    # $A().app().log "form.action:" + JSON form.action 
                    if _have_press_position == null 
                        _have_press_position = new Array(form.content.split("\r\n").length)
                        for item,i in _have_press_position 
                            _have_press_position[i] = 0
                        if _mFirstSign
                            if _have_cache and form.action != null and typeof form.action != "undefined"
                                for value ,i in form.action.split ","
                                    if parseInt(value) - 1 < _have_press_position.length
                                        _have_press_position[parseInt(value)-1] = parseInt(value)
                            _mFirstSign = false

                    for option,i in form.content.split "\r\n" 
                        list_data.push
                            viewType: mViewType
                            _rightLayoutSize: 0
                            _leftLayoutSize: 40
                            _bottomDivider: _cfg.divider_config
                            leftImage:
                                imageType: "assets"
                                imageSize: "fitSize"
                                imageSrc: 
                                    if _have_press_position[i] == 0 then _cfg.unchecked_image_url else _cfg.checked_image_url
                            centerTitle: option#item data
                            form: form
                            value: i
                            type : "_option"
                            hasFooterDivider:"true"
                    #如果点击了才显示button
                    for i in _have_press_position
                        if i != 0
                            root.mButton(list_data)
                            return "_false"

            when "string"
                mViewType = "ListViewCellInputText"
                mProcessFun = (list_data,form,mViewType)->
                    list_data.push
                        viewType: mViewType
                        inputType:"text"
                        hint:"输入内容"
                        lines:1
                        name:"value"
                        inputText: if _have_cache then form.action
                        form: form
                        enable:if _have_cache then false else true
                        type : "_option"
                        hasFooterDivider:"true"
                    root.mButton(list_data)

            when "text"
                mViewType = "ListViewCellInputText"
                mProcessFun = (list_data,form,mViewType)->
                    list_data.push
                        viewType: mViewType
                        inputType:"text"
                        hint:"输入内容"
                        lines:5
                        name:"value"
                        inputText: if _have_cache then form.action
                        form: form
                        enable:if _have_cache then false else true
                        type : "_option"
                        hasFooterDivider:"true"
                    root.mButton(list_data)

            else 
                mViewType = "ListViewCellLine"
                mProcessFun = (list_data,form,mViewType)->
                    for option,i in form.content.split "\r\n" 
                        list_data.push
                            viewType: mViewType
                            _rightLayoutSize: 0
                            _leftLayoutSize: 40
                            _bottomDivider: _cfg.divider_config
                            leftImage:
                                imageType: "assets"
                                imageSize: "fitSize"
                                imageSrc: if form.action? && parseInt(form.action) == i then _cfg.checked_image_url else _cfg.unchecked_image_url
                            centerTitle: option#item data
                            form: form
                            value: i
                            type : "_option"
                            hasFooterDivider:"true"
        mProcessFun(list_data,form,mViewType) 

        return "_false"

    isDone: ->
        #只要有未定义的数据，说明用户未完成操作，所以不再装载缓存里的数据
        for i in _res.items
            $A().app().log typeof i.action
            if typeof i.action == "undefined"
                _have_cache = false
                break
    
    saveToCache: (item,value)->
        _local_answers = {} if !_local_answers?
        _local_answers[item.form.id] = value
        $A().lrucache().set
            key: _info.fav_id_id
            value: JSON.stringify _local_answers
        root.showSurvey_setAnswerToRes()

    #完成状态提交
    updateState: (action_type,action,kf_course_user_fav_id,course_index_id) ->
        $A().lrucache().set
            key:"#{kf_course_user_fav_id}_#{course_index_id}"
            value:action
            # value:"#{action_type}"
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

    mShowAnswer: (list_data,form,sign)->
        if sign == true
            if form.action? && _now_index == form.number
                # $A().app().log "form:" + JSON.stringify form
                list_data.push
                    viewType: "ListViewCellArticle"
                    content: form.answer
                    type : "_answer"
                    _backgroundColor:
                        _clickable: false
                        pressed: "#f2f2f2"
                        selected: "#f2f2f2"
                        normal: "#f2f2f2"
        return "_false"
    
new ECpageClass("page_course_survey")
