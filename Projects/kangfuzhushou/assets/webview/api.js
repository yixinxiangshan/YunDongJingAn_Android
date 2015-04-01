// Generated by CoffeeScript 1.7.1
(function() {
    var ECAPI;

    this.$A = function() {
        return new ECAPI();
    };

    ECAPI = function() {
        this.action = {
            //            pid: _env.parent_id
        };
        return this;
    };

    ECAPI.prototype = {};

    ECAPI.prototype.page = function(add_action) {};

    ECAPI.prototype.lrucache = function(add_action) {};

    ECAPI.prototype.widget = function(add_action) {};

    ECAPI.prototype.app = function(add_action) {};

    ECAPI.prototype.db = function(add_action) {};

    ECAPI.prototype.image = function(add_action) {};

    ECAPI.prototype.fs = function(add_action) {};

    ECAPI.prototype.map = function(add_action) {};

    ECAPI.prototype.phone = function(add_action) {};

    ECAPI.prototype.network = function(add_action) {};

    ECAPI.prototype.share = function(add_action) {};

    ECAPI.prototype.media = function(add_action) {};

    ECAPI.prototype.sms = function(add_action) {};

    ECAPI.prototype.service = function(add_action) {};

    ECAPI.prototype.utiliy = function(add_action) {};

    ECAPI.prototype.view = function(add_action) {};
    
    ECAPI.prototype.notification = function(add_action) {};

    ECAPI.prototype.onCreate = function(call_with_params) {};

    ECAPI.prototype.onResult = function(call_with_params) {};

    ECAPI.prototype.onCreated = function(call_with_params) {};

    ECAPI.prototype.onResume = function(call_with_params) {};

    ECAPI.prototype.onKeyDown = function(call_with_params) {};

    ECAPI.prototype.onItemClick = function(call_with_params) {};

    ECAPI.prototype.onClick = function(call_with_params) {};

    ECAPI.prototype.onBackClick = function(call_with_params) {};

    ECAPI.prototype.onSubmit = function(call_with_params) {};

    ECAPI.prototype.onSubmitSuccess = function(call_with_params) {};

    ECAPI.prototype.onChange = function(call_with_params) {};

    ECAPI.prototype.onMoreData = function(call_with_params) {};

    ECAPI.prototype.onLoadData = function(call_with_params) {};

    ECAPI.prototype.onMapPopupClicked = function(call_with_params) {};

    ECAPI.prototype.makeToast = function(call_with_params) {};

    ECAPI.prototype.data = function(call_with_params) {};

    ECAPI.prototype.refresh = function(call_with_params) {};
    ECAPI.prototype.updateItem = function(call_with_params) {};
    ECAPI.prototype.updateItems = function(call_with_params) {};    

    ECAPI.prototype.showLoadingDialog = function(call_with_params) {};
    ECAPI.prototype.closeLoadingDialog = function(call_with_params) {};

    ECAPI.prototype.param = function(call_with_params) {};

    ECAPI.prototype.wait = function(call_with_params) {};

    ECAPI.prototype.resumeWait = function(call_with_params) {};

    ECAPI.prototype.log = function(call_with_params) {};

    ECAPI.prototype.callApi = function(call_with_params) {};

    ECAPI.prototype.location = function(call_with_params) {};

    ECAPI.prototype.title = function(call_with_params) {};

    ECAPI.prototype.change = function(call_with_params) {};

    ECAPI.prototype.confirm = function(call_with_params) {};

    ECAPI.prototype.openPage = function(call_with_params) {};

    ECAPI.prototype.closePage = function(call_with_params) {};

    ECAPI.prototype.itemClick = function(call_with_params) {};

    ECAPI.prototype.click = function(call_with_params) {};

    ECAPI.prototype.back = function(call_with_params) {};

    ECAPI.prototype.move = function(call_with_params) {};

    ECAPI.prototype.zoom = function(call_with_params) {};

    ECAPI.prototype.addPoint = function(call_with_params) {};

    ECAPI.prototype.submit = function(call_with_params) {};

    ECAPI.prototype.callFun = function(call_with_params) {};

    ECAPI.prototype.preference = function(call_with_params) {};

    ECAPI.prototype.refreshData = function(call_with_params) {};

    ECAPI.prototype.append = function(call_with_params) {};

    ECAPI.prototype.openUrl = function(call_with_params) {};
    ECAPI.prototype.playVideo = function(call_with_params) {};

    ECAPI.prototype.showConfirm = function(call_with_params) {};
    ECAPI.prototype.showRadioConfirm = function(call_with_params) {};
    ECAPI.prototype.showDatepickerConfirm = function(call_with_params) {};
    ECAPI.prototype.showInputConfirm = function(call_with_params) {};

    ECAPI.prototype.append = function(call_with_params) {};

    ECAPI.prototype.prepend = function(call_with_params) {};

    ECAPI.prototype.src = function(call_with_params) {};

    ECAPI.prototype.text = function(call_with_params) {};

    ECAPI.prototype.hide = function(call_with_params) {};

    ECAPI.prototype.show = function(call_with_params) {};

    ECAPI.prototype.remove = function(call_with_params) {};

    ECAPI.prototype.option = function(call_with_params) {};

    ECAPI.prototype.openPopupWindow = function(call_with_params) {};

    ECAPI.prototype.closePopupWindow = function(call_with_params) {};

    ECAPI.prototype.pop = function(call_with_params) {};

    ECAPI.prototype.getCache = function(call_with_params) {};

    ECAPI.prototype.getCaches = function(call_with_params) {};

    ECAPI.prototype.putCache = function(call_with_params) {};

    ECAPI.prototype.openQRCapture = function(call_with_params) {};
    //add by 高帅朋 
    ECAPI.prototype.phone = function(call_with_params) {};
    ECAPI.prototype.cacheSize = function(call_with_params) {};
    ECAPI.prototype.deleteCache = function(call_with_params) {};
    ECAPI.prototype.onItemInnerClick = function(call_with_params) {};
    // app method
    ECAPI.prototype.getAppVersion = function(call_with_params) {};
    ECAPI.prototype.getRemoteVersion = function(call_with_params) {};
    ECAPI.prototype.checkRemoteVersion = function(call_with_params) {};
    ECAPI.prototype.confirmDownloadNewVersion = function(call_with_params) {};
    
    //语音
    ECAPI.prototype.ttsPlay = function(call_with_params) {};
    ECAPI.prototype.ttsStop = function(call_with_params) {};
    //add by 李立波
    ECAPI.prototype.fullImage = function(call_with_params) {};
    ECAPI.prototype.showCalendarConfirm = function(call_with_params) {};
    
    //add by 程巍巍
    //map method
    ECAPI.prototype.distance = function(call_with_params) {};
    //app method
    ECAPI.prototype.callNativeMethod = function(call_with_params) {}; //ios
    ECAPI.prototype.readFile = function(call_with_params) {};
    ECAPI.prototype.login = function(call_with_params) {};
    ECAPI.prototype.isLogin = function(call_with_params) {};
    ECAPI.prototype.logout = function(call_with_params) {};
    ECAPI.prototype.registe = function(call_with_params) {};
    ECAPI.prototype.platform = function(call_with_params) {};
    //service
    ECAPI.prototype.onRecieved = function(call_with_params) {};
    ECAPI.prototype.registeNotice = function(call_with_params) {};
    ECAPI.prototype.unRegisteNotice = function(call_with_params) {};
    ECAPI.prototype.dispatchNotice = function(call_with_params) {};
    //page method
    ECAPI.prototype.open = function(call_with_params) {};
    ECAPI.prototype.onPause = function(call_with_params) {};
    ECAPI.prototype.onPageSelected = function(call_with_params) {};
    ECAPI.prototype.showMenu = function(call_with_params) {};
    ECAPI.prototype.setTimeout = function(call_with_params) {};
    
    

    //widget method
    ECAPI.prototype.selector = function(call_with_params) {};

    //view method
    ECAPI.prototype.badge = function(call_with_params) {};
    ECAPI.prototype.insertSubview = function(call_with_params) {};
    ECAPI.prototype.removeSubviews = function(call_with_params) {}; //清空subviews
    ECAPI.prototype.endEdit = function(call_with_params) {}; //textview textField
    ECAPI.prototype.setProperty = function(call_with_params) {}; //ios only

    //utiliy method
    ECAPI.prototype.compileTpl = function(call_with_params) {};
    ECAPI.prototype.shareWords = function(call_with_params) {};
    ECAPI.prototype.shareImage = function(call_with_params) {};
    ///net
    ECAPI.prototype.netState = function(call_with_params) {};
    ECAPI.prototype.openIntent = function(call_with_params) {};
    //lrucache
    ECAPI.prototype.get = function(call_with_params) {};
    ECAPI.prototype.clear = function(call_with_params) {};
    ECAPI.prototype.remove = function(call_with_params) {};
    ECAPI.prototype.set = function(call_with_params) {};
    ECAPI.prototype.massGet = function(call_with_params) {};
    ECAPI.prototype.massRemove = function(call_with_params) {};
    //notification
    ECAPI.prototype.add = function(call_with_params) {};
    ECAPI.prototype.clear = function(call_with_params) {};
    ECAPI.prototype.remove = function(call_with_params) {};
    ECAPI.prototype.setAlias = function(call_with_params) {};
    ECAPI.prototype.clearAlias = function(call_with_params) {};

    (function() {
        var addParams, deviceCallPromise, makeAPI;
        deviceCallPromise = function(self, action, query) {
            return new Promise(function(resolve, reject) {
                self.action.pid = _env.parent_id;
                return callCoreApi(action, query, resolve, self.action);
            });
        };
        addParams = function(self, action, query) {
            if (self.action.pid) {
                self.action = {};
            }
            self.action[action] = query || '_';
            return self;
        };
        makeAPI = function(orig_api) {
            var add_action_fn, call_with_params_fn, fn_str, key;
            add_action_fn = (function(add_action) {}).toString();
            call_with_params_fn = (function(call_with_params) {}).toString();
            orig_api = orig_api.prototype;
            for (key in orig_api) {
                fn_str = orig_api[key].toString();
                if (fn_str === add_action_fn) {
                    orig_api[key] = (function(key) {
                        return function(query) {
                            return addParams(this, key, query);
                        };
                    })(key);
                } else if (fn_str === call_with_params_fn) {
                    orig_api[key] = (function(key) {
                        return function(query) {
                            return deviceCallPromise(this, key, query);
                        };
                    })(key);
                }
            }
            return null;
        };
        return makeAPI(ECAPI);
    })(null);

    this.$v = function(x) {
        return $A().view(x);
    };
    this.$p = function(x) {
        return $A().page(x);
    };

}).call(this);

//# sourceMappingURL=api4promise.map



