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
          viewType: "ListViewCellInputText",
          inputType: "text",
          hint: "",
          type: "captcha",
          inputText: "",
          name: "input"
        }, {
          viewType: "ListViewCellButton",
          btnTitle: "确 定",
          btnType: "ok",
          name: "ok"
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
      if ((root._platform != null) && root._platform === "ios") {
        $A().page().widget(this._page_name + "_ListViewBase_0").refreshData(JSON.stringify(this._listview_data));
      }
      return root.setActionBartitle();
    };

    ECpageClass.prototype.onItemClick = function(data) {};

    ECpageClass.prototype.onItemInnerClick = function(data) {
      var input, item;
      item = root._listview_data.data[data.position];
      input = data._form.input != null ? data._form.input : "";
      switch ("" + item.name) {
        case "ok":
          if (input !== "") {
            $A().page("" + root._item_info.page_name).param({
              key: "" + root._item_info.key,
              value: "" + input
            });
            $A().app().makeToast("正在设置");
            return $A().page().setTimeout("2000").then(function() {
              return $A().app().closePage();
            });
          }
      }
    };

    ECpageClass.prototype.onResume = function() {
      return $A().page().param("_setting_changed").then(function(data) {
        if (data !== "") {
          $A().page().param({
            key: "_setting_changed",
            value: ""
          });
          return initPage();
        }
      });
    };

    ECpageClass.prototype.onResult = function() {};

    ECpageClass.prototype.prepareForInitView = function() {
      $A().app().platform().then(function(platform) {
        return root._platform = platform;
      });
      return $A().page().param("info").then(function(data) {
        data = JSON.parse(data);
        root._item_info = data;
        root._listview_data.data[0].hint = data.hint;
        return root._listview_data.data[0].inputText = data.inputText;
      });
    };

    ECpageClass.prototype.setActionBartitle = function() {
      return $A().page().setTimeout("100").then(function() {
        return $A().page("page_input").widget("ActionBar").title({
          title: "" + this._item_info.hint
        });
      });
    };

    return ECpageClass;

  })();

  new ECpageClass("page_input");

}).call(this);
