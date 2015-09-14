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
          viewType: "ListViewCellLine",
          _rightLayoutSize: 0,
          _leftLayoutSize: 75,
          centerTitle: "运动加油站",
          btnType: "ok",
          _type: "cheerup",
          hasFooterDivider: "true",
          leftImage: {
            imageType: "imageServer",
            imageSize: "middle",
            imageSrc: "3006088.jpg"
          }
        }, {
          viewType: "ListViewCellLine",
          _rightLayoutSize: 0,
          _leftLayoutSize: 75,
          centerTitle: "场馆优惠",
          btnType: "ok",
          _type: "coupon",
          hasFooterDivider: "true",
          leftImage: {
            imageType: "imageServer",
            imageSize: "middle",
            imageSrc: "3013652.jpg"
          }
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
      return $A().page().onCreated(function() {
        return root.onCreated();
      });
    };

    function ECpageClass(_page_name) {
      this._constructor(_page_name);
    }

    ECpageClass.prototype.onActionBarItemClick = function(data) {
      if (data === "2") {
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
      item = this._listview_data.data[data.position];
      if ((item._type != null) && item._type === 'cheerup') {
        return $A().app().openPage({
          page_name: "page_cheerup_list",
          params: [],
          close_option: ""
        });
      } else {
        return $A().app().openPage({
          page_name: "page_coupon_main",
          params: [],
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

  new ECpageClass("page_tab_cheerup");

}).call(this);
