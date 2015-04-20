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
          viewType: "ListViewCellInputText",
          inputType: "text",
          hint: "姓名",
          name: "name"
        }, {
          viewType: "ListViewCellInputText",
          inputType: "text",
          hint: "地址",
          lines: 3,
          name: "address"
        }, {
          viewType: "ListViewCellInputText",
          inputType: "number",
          hint: "电话",
          name: "phone",
          inputText: ""
        }, {
          viewType: "ListViewCellInputText",
          inputType: "number",
          hint: "邮编",
          name: "zip",
          inputText: ""
        }, {
          viewType: "ListViewCellButton",
          inputType: "number",
          btnTitle: "提 交",
          btnType: "ok",
          _type: "ok"
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

    ECpageClass.prototype.onItemClick = function(data) {};

    ECpageClass.prototype.onItemInnerClick = function(data) {
      var address, name, phone, zip;
      name = data._form.name != null ? data._form.name : "";
      address = data._form.address != null ? data._form.address : "";
      phone = data._form.phone != null ? data._form.phone : "";
      zip = data._form.zip != null ? data._form.zip : "";
      if (name === "") {
        return $A().app().makeToast("请输入您的姓名！");
      } else if (address === "") {
        return $A().app().makeToast("请输入您的地址");
      } else if (phone === "") {
        return $A().app().makeToast("请输入您的电话");
      } else {
        return $A().page().param("info").then(function(info) {
          $A().app().makeToast("正在提交");
          return $A().app().callApi({
            method: "project/feedbaks/create",
            content: "aaa",
            contact_info: "bbb",
            cacheTime: 0
          }).then(function(data) {
            $A().app().log("---------------------------------" + JSON.stringify(data));
            if (data.success === true) {
              $A().app().makeToast("提交成功，谢谢您的反馈。");
              return $A().page().setTimeout("2000").then(function() {
                return $A().app().closePage();
              });
            } else {
              return $A().app().makeToast("提交失败，请重试或者检查您的网络是否打开。");
            }
          });
        });
      }
    };

    ECpageClass.prototype.onResume = function() {};

    ECpageClass.prototype.onResult = function(data) {};

    ECpageClass.prototype.prepareForInitView = function() {
      return $A().app().platform().then(function(platform) {
        return root._platform = platform;
      });
    };

    return ECpageClass;

  })();

  Page = new ECpageClass("page_send_input");

}).call(this);
