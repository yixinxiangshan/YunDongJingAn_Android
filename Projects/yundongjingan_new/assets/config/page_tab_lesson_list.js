// Generated by CoffeeScript 1.9.3
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
      hasFooterDivider: true,
      hasHeaderDivider: true,
      dividerHeight: 1,
      dividerColor: "#EBEBEB",
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
      $A().page().widget("ActionBar").onItemClick(function(data) {
        return root.onActionBarItemClick(data);
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

    ECpageClass.prototype.onActionBarItemClick = function(data) {
      if (data === "5") {
        return $A().app().openPage({
          page_name: "page_my",
          params: {},
          close_option: ""
        });
      }
    };

    ECpageClass.prototype.onCreated = function() {
      return $A().app().callApi({
        method: "content/coupons/index",
        sort_id: 528,
        shop_content_id: 2543,
        cacheTime: 0,
        simple_result: true
      }).then(function(data) {
        var content, i, len, ref;
        if (data.errors != null) {
          if (data.errors[0].error_num != null) {
            return $A().app().makeToast("网络状态不好，请重新加载");
          } else {
            return $A().app().makeToast("没有网络");
          }
        } else {
          root._listview_data.data = [];
          if (data.count === 0) {
            root._listview_data.data = [];
            root._listview_data.data.push({
              viewType: "ListViewCellLine",
              centerTitle: "暂无优惠"
            });
            return $A().page().widget(root._page_name + "_ListViewBase_0").refreshData(JSON.stringify(root._listview_data));
          } else {
            ref = data.content_list;
            for (i = 0, len = ref.length; i < len; i++) {
              content = ref[i];
              root._listview_data.data.push({
                viewType: "ListViewCellLine",
                centerTitle: "" + content.title,
                leftImage: {
                  imageType: "imageServer",
                  imageSize: "middle",
                  imageSrc: "" + content.image
                },
                _leftLayoutSize: 75,
                centerBottomdes: "" + content.abstract,
                content_id: "" + content.id,
                hasFooterDivider: "true"
              });
            }
            return $A().page().widget(root._page_name + "_ListViewBase_0").refreshData(JSON.stringify(root._listview_data));
          }
        }
      });
    };

    ECpageClass.prototype.onItemClick = function(data) {
      var item;
      item = this._listview_data.data[data.position];
      if (item.content_id != null) {
        return $A().app().openPage({
          page_name: "page_lesson_info",
          params: {
            info: item.content_id
          },
          close_option: ""
        });
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

  Page = new ECpageClass("page_tab_lesson_list");

}).call(this);
