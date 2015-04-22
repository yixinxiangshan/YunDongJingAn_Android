// Generated by CoffeeScript 1.9.1
(function() {
  var ECpageClass, Page;

  ECpageClass = (function() {
    var root;

    root = {};

    ECpageClass.prototype._page_name = "";

    ECpageClass.prototype._item_info = {};

    ECpageClass.prototype._platform = "";

    ECpageClass.prototype._listview_data = {
      pullable: false,
      hasFooterDivider: false,
      hasHeaderDivider: false,
      dividerHeight: 0,
      dividerColor: "#cccccc",
      data: [
        {
          viewType: "ListViewCellLine",
          _rightLayoutSize: 0,
          _leftLayoutSize: 60,
          centerTitle: "正在加载......"
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
      $A().page().onResume(function(data) {
        return root.onResume();
      });
      $A().page().onResult(function(data) {
        return root.onResult(data);
      });
      return $A().page().onCreated(function() {
        return root.onCreated();
      });
    };

    function ECpageClass(_page_name) {
      this._constructor(_page_name);
    }

    ECpageClass.prototype.onCreated = function() {
      return $A().page().param("info").then(function(info) {
        var info_;
        info_ = JSON.parse(info);
        return $A().app().callApi({
          method: "trade/ships/detail",
          cms_content_id: info_.content_id,
          cacheTime: 0
        }).then(function(data) {
          var content, i, len, ref;
          $A().app().log("---------------------JSON.stringify data" + JSON.stringify(data));
          if (data.errors != null) {
            if (data.errors[0].error_num != null) {
              if (data.errors[0].error_msg != null) {
                root._listview_data.data[0].centerTitle = data.errors[0].error_msg;
                return $A().page().widget(root._page_name + "_ListViewBase_0").refreshData(JSON.stringify(root._listview_data));
              } else {
                return $A().app().makeToast("网络状态不好，请重新加载");
              }
            } else {
              return $A().app().makeToast("没有网络");
            }
          } else {
            root._listview_data.data = [];
            ref = data.order;
            for (i = 0, len = ref.length; i < len; i++) {
              content = ref[i];
              root._listview_data.data.push({
                viewType: "ListViewCellLine",
                centerTitle: "" + content.consignee_name,
                centerBottomdes: "" + content.updated_at,
                content_id: "" + content.cms_content_id,
                content_title: "" + content.title,
                order_id: "" + content.id,
                consignee_id: "" + content.user_consignee_id
              });
            }
            return $A().page().widget(root._page_name + "_ListViewBase_0").refreshData(JSON.stringify(root._listview_data));
          }
        });
      });
    };

    ECpageClass.prototype.onItemClick = function(data) {
      var content, item;
      item = this._listview_data.data[data.position];
      content = {
        content_id: item.content_id,
        content_title: item.content_title,
        order_id: item.order_id,
        consignee_id: item.consignee_id
      };
      return $A().app().openPage({
        page_name: "page_send_input",
        params: {
          info: content
        },
        close_option: ""
      });
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

  Page = new ECpageClass("page_send_list");

}).call(this);
