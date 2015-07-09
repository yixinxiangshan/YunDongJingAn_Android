// Generated by CoffeeScript 1.9.1
(function() {
  var ECpageClass;

  ECpageClass = (function() {
    var root;

    root = {};

    ECpageClass.prototype._page_name = "";

    ECpageClass.prototype._item_info = {};

    ECpageClass.prototype._platform = "";

    ECpageClass.prototype._listview_data = {
      pullable: false,
      hasFooterDivider: true,
      hasHeaderDivider: false,
      dividerHeight: 1,
      dividerColor: "#EBEBEB",
      data: [
        {
          viewType: "ListViewCellLine",
          _rightLayoutSize: 0,
          _leftLayoutSize: 0,
          centerTitle: "未登录",
          rightImage: {
            imageType: "assets",
            imageSize: "fitSize",
            imageSrc: "webview/images/icon/default/listview_right.png"
          },
          name: "mycenter",
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
      $A().page().onResume(function() {
        return root.onResume();
      });
      return $A().page().onCreated(function() {
        return root.onCreated();
      });
    };

    function ECpageClass(_page_name) {
      this._constructor(_page_name);
    }

    ECpageClass.prototype.onCreated = function() {
      return $A().lrucache().get("phone").then(function(phone) {
        if ((phone != null) && phone !== "") {
          root._listview_data.data[0].centerTitle = "" + phone;
        } else {
          root._listview_data.data[0].centerTitle = "未登录";
        }
        return $A().page().widget(root._page_name + "_ListViewBase_0").refreshData(JSON.stringify(root._listview_data));
      });
    };

    ECpageClass.prototype.onItemClick = function(data) {
      var item;
      item = this._listview_data.data[data.position];
      switch ("" + item.name) {
        case "mycenter":
          return $A().lrucache().get("phone").then(function(phone) {
            if ((phone != null) && phone !== "") {
              return $A().app().openPage({
                page_name: "page_mycenter",
                params: {},
                close_option: ""
              });
            } else {
              return $A().app().openPage({
                page_name: "page_login",
                params: {},
                close_option: ""
              });
            }
          });
      }
    };

    ECpageClass.prototype.onItemInnerClick = function(data) {};

    ECpageClass.prototype.onResult = function(data) {};

    ECpageClass.prototype.onResume = function() {
      return $A().page("page_my").param("_setting_changed").then(function(data) {
        if ((data != null) && data !== "") {
          $A().page("page_my").param({
            key: "_setting_changed",
            value: ""
          });
          return root.onCreated();
        }
      });
    };

    ECpageClass.prototype.prepareForInitView = function() {
      return $A().app().platform().then(function(platform) {
        return root._platform = platform;
      });
    };

    return ECpageClass;

  })();

  new ECpageClass("page_my");

}).call(this);
