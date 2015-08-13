// Generated by CoffeeScript 1.9.1
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
      dividerColor: "#EBEBEB"
    };

    ECpageClass.prototype._data = {
      latitude: 0,
      longitude: 0,
      title: ""
    };

    ECpageClass.prototype._constructor = function(_page_name1) {
      this._page_name = _page_name1;
      root = this;
      this.prepareForInitView();
      $A().page().widget(this._page_name + "_MapWidget_0").data(JSON.stringify(this._data));
      $A().page().widget(this._page_name + "_MapWidget_0").onItemInnerClick(function(data) {
        return root.onItemInnerClick(data);
      });
      $A().page().widget(this._page_name + "_MapWidget_0").onItemClick(function(data) {
        return root.onItemClick(data);
      });
      $A().page().onResume(function() {
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
      $A().page().param("latitude").then(function(data) {
        return root._data.latitude = data;
      });
      $A().page().param("longitude").then(function(data) {
        return root._data.longitude = data;
      });
      $A().page().param("title").then(function(data) {
        return root._data.title = data;
      });
      return $A().page().widget(root._page_name + "_MapWidget_0").refreshData(JSON.stringify(root._data));
    };

    ECpageClass.prototype.onItemClick = function(data) {};

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

  new ECpageClass("page_simple_map");

}).call(this);