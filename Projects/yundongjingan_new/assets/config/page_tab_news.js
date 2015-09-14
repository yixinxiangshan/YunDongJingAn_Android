// Generated by CoffeeScript 1.10.0
(function() {
  var ECpageClass;

  ECpageClass = (function() {
    var root;

    root = {};

    ECpageClass.prototype._page_name = "";

    ECpageClass.prototype._content_info = {};

    ECpageClass.prototype._platform = "";

    ECpageClass.prototype._listview_data = {
      pullable: false,
      hasFooterDivider: true,
      hasHeaderDivider: true,
      dividerHeight: 1,
      dividerColor: "#EBEBEB",
      data: [
        {
          viewType: "ListViewCellButton",
          btnTitle: "赛事报名",
          btnType: "ok",
          _type: "0"
        }, {
          viewType: "ListViewCellButton",
          btnTitle: "最新消息",
          btnType: "ok",
          _type: "519"
        }, {
          viewType: "ListViewCellButton",
          btnTitle: "主题活动",
          btnType: "ok",
          _type: "520"
        }, {
          viewType: "ListViewCellButton",
          btnTitle: "楼宇特色",
          btnType: "ok",
          _type: "521"
        }, {
          viewType: "ListViewCellButton",
          btnTitle: "静安体彩",
          btnType: "ok",
          _type: "522"
        }, {
          viewType: "ListViewCellButton",
          btnTitle: "竞技体育",
          btnType: "ok",
          _type: "523"
        }, {
          viewType: "ListViewCellButton",
          btnTitle: "公共体育",
          btnType: "ok",
          _type: "524"
        }, {
          viewType: "ListViewCellButton",
          btnTitle: "其他",
          btnType: "ok",
          _type: "1970"
        }
      ]
    };

    ECpageClass.prototype._constructor = function(_page_name1) {
      this._page_name = _page_name1;
      root = this;
      this.prepareForInitView();
      $A().page().widget(this._page_name + "_ListViewBase_0").data(JSON.stringify(root._listview_data));
      $A().page().widget(this._page_name + "_ListViewBase_0").onItemInnerClick(function(data) {
        return root.onItemInnerClick(data);
      });
      $A().page().widget(this._page_name + "_ListViewBase_0").onItemClick(function(data) {
        return root.onItemClick(data);
      });
      $A().page().widget("ActionBar").onItemClick(function(data) {
        return root.onActionBarItemClick(data);
      });
      return $A().page().onCreated(function() {
        return root.onCreated();
      });
    };

    function ECpageClass(_page_name) {
      this._constructor(_page_name);
    }

    ECpageClass.prototype.onActionBarItemClick = function(data) {
      if (data === "3") {
        return $A().app().openPage({
          page_name: "page_my",
          params: {},
          close_option: ""
        });
      }
    };

    ECpageClass.prototype.onCreated = function() {
      if ((root._platform != null) && root._platform === "ios") {
        return $A().page().widget(this._page_name + "_ListViewBase_0").refreshData(JSON.stringify(this._listview_data));
      }
    };

    ECpageClass.prototype.onItemClick = function(data) {
      var item;
      item = root._listview_data.data[data.position];
      if (item._type) {
        if (item._type === "0") {
          return $A().app().openPage({
            page_name: "page_tab_signup",
            params: [],
            close_option: ""
          });
        } else {
          return $A().app().openPage({
            page_name: "page_tab_news_list",
            params: {
              sort_id: item._type
            },
            close_option: ""
          });
        }
      }
    };

    ECpageClass.prototype.onItemInnerClick = function(data) {};

    ECpageClass.prototype.onResume = function() {};

    ECpageClass.prototype.onResult = function(data) {};

    ECpageClass.prototype.prepareForInitView = function() {
      return $A().app().platform().then(function(platform) {
        return root._platform = platform;
      });
    };

    return ECpageClass;

  })();

  new ECpageClass("page_tab_news");

}).call(this);
