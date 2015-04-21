// Generated by CoffeeScript 1.9.0
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
          hint: "请输入您的反馈意见",
          lines: 3,
          name: "content"
        }, {
          viewType: "ListViewCellInputText",
          inputType: "number",
          hint: "联系方式:邮箱/QQ/手机号",
          name: "contact",
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

    ECpageClass.prototype._constructor = function(_at__page_name) {
      this._page_name = _at__page_name;
      root = this;
      this.prepareForInitView();
      $A().lrucache().get("phone").then(function(phone) {
        return root._listview_data.data[1].inputText = (phone != null) && phone !== "" ? phone : "";
      });
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
      var contact, content;
      content = data._form.content != null ? data._form.content : "";
      contact = data._form.contact != null ? data._form.contact : "";
      if (content === "") {
        return $A().app().makeToast("请输入您的反馈意见！");
      } else if (contact === "") {
        return $A().app().makeToast("请输入您的联系方式");
      } else {
        $A().app().makeToast("正在提交");
        return $A().app().callApi({
          method: "project/feedbaks/create",
          content: content,
          contact_info: contact,
          cacheTime: 0
        }).then(function(data) {
          if (data.success === true) {
            $A().app().makeToast("提交成功，谢谢您的反馈。");
            return $A().page().setTimeout("2000").then(function() {
              return $A().app().closePage();
            });
          } else {
            return $A().app().makeToast("提交失败，请重试或者检查您的网络是否打开。");
          }
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

  Page = new ECpageClass("page_feedback");

}).call(this);