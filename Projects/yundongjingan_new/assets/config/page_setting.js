// Generated by CoffeeScript 1.9.1
(function() {
  var ECpageClass, Page;

  ECpageClass = (function() {
    var root;

    root = {};

    ECpageClass.prototype._page_name = "";

    ECpageClass.prototype._platform = "";

    ECpageClass.prototype._item_info = {};

    ECpageClass.prototype._listview_data = {
      pullable: false,
      hasFooterDivider: true,
      hasHeaderDivider: true,
      dividerHeight: 1,
      dividerColor: "#EBEBEB",
      data: [
        {
          viewType: "ListViewCellImage",
          image: {
            imageType: "resource",
            imageSize: "middle",
            imageSrc: "proj_icon"
          },
          bottomTitle: "运动静安",
          titlePosition: "center",
          _type: "logo",
          hasFooterDivider: "true"
        }, {
          viewType: "ListViewCellLine",
          _rightLayoutSize: 0,
          _leftLayoutSize: 0,
          centerTitle: "检测新版本",
          _type: "update",
          hasFooterDivider: "true"
        }
      ]
    };

    ECpageClass.prototype._constructor = function(_page_name1) {
      this._page_name = _page_name1;
      root = this;
      this.prepareForInitView();
      $A().page().widget(this._page_name + "_ListViewBase_0").data(JSON.stringify(this._listview_data));
      $A().page().widget(this._page_name + "_ListViewBase_0").onItemInnerClick(function(data) {
        return root.onItemInnerClick(data);
      });
      $A().page().widget(this._page_name + "_ListViewBase_0").onItemClick(function(data) {
        return root.onItemClick(data);
      });
      return $A().page().onCreated(function() {
        return root.onCreated();
      });
    };

    function ECpageClass(_page_name) {
      this._constructor(_page_name);
    }

    ECpageClass.prototype.onCreated = function() {
      if ((root._platform != null) && root._platform === "ios") {
        return $A().page().widget(this._page_name + "_ListViewBase_0").refreshData(JSON.stringify(this._listview_data));
      }
    };

    ECpageClass.prototype.onResult = function(data) {};

    ECpageClass.prototype.onItemClick = function(data) {
      if (this._listview_data.data[data.position]._type === "update") {
        $A().app().showLoadingDialog({
          content: "正在检查新版本信息"
        });
        return $A().app().callApi({
          method: "projects/detail",
          cacheTime: 0
        }).then(function(res) {
          $A().app().preference({
            key: "net_version_num",
            value: data.version_num
          });
          $A().app().preference({
            key: "net_version_url",
            value: data.download_url
          });
          return $A().app().getAppVersion().then(function(version) {
            $A().app().closeLoadingDialog();
            if (parseFloat(data.version_num) > parseFloat(version)) {
              if (data.description == null) {
                data.description = "";
              }
              return $A().app().confirmDownloadNewVersion({
                ok: "下载",
                data: data.description != null ? ("最新版本:" + data.version_num + "\n\n【更新内容】\n") + data.description : void 0
              });
            } else {
              return $A().app().showConfirm({
                ok: "确认",
                message: "已是最新版本",
                title: "版本确认"
              });
            }
          });
        });
      }
    };

    ECpageClass.prototype.onItemInnerClick = function(data) {
      if (this._listview_data.data[data.position]._type === "logout") {
        return $A().app().showConfirm({
          title: "退出当前账号",
          ok: "确认退出",
          cancel: "取消",
          message: "退出当前帐号。"
        }).then(function(data) {
          if (data.state === "ok") {
            $A().lrucache().set({
              key: "phone",
              value: ""
            });
            $A().page("page_my").param({
              key: "_setting_changed",
              value: "true"
            });
            $A().page("page_home").param({
              key: "_setting_changed",
              value: "true"
            });
            $A().app().makeToast("正在退出");
            return $A().page().setTimeout("2000").then(function() {
              return $A().app().closePage();
            });
          }
        });
      }
    };

    ECpageClass.prototype.onResume = function() {};

    ECpageClass.prototype.prepareForInitView = function() {
      $A().app().platform().then(function(platform) {
        return root._platform = platform;
      });
      $A().app().getAppVersion().then(function(version) {
        return root._listview_data.data[0].bottomTitle = "运动静安 " + ("" + version);
      });
      return $A().lrucache().get("phone").then(function(phone) {
        if ((phone != null) && phone !== "") {
          root._listview_data.hasFooterDivider = false;
          return root._listview_data.data.push({
            viewType: "ListViewCellButton",
            btnTitle: "退出当前账号",
            btnType: "ok",
            _type: "logout"
          });
        }
      });
    };

    return ECpageClass;

  })();

  Page = new ECpageClass("page_setting");

}).call(this);
