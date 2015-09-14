// Generated by CoffeeScript 1.10.0
(function() {
  var ECpageClass;

  ECpageClass = (function() {
    var root;

    root = {};

    ECpageClass.prototype._page_name = "";

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
          _leftLayoutSize: 0,
          centerTitle: "姓名",
          centerRightdes: "未设置",
          type: "nickname",
          hasFooterDivider: "true"
        }, {
          viewType: "ListViewCellLine",
          _rightLayoutSize: 0,
          _leftLayoutSize: 0,
          centerTitle: "性别",
          centerRightdes: "未填写",
          type: "sex",
          hasFooterDivider: "true"
        }, {
          viewType: "ListViewCellLine",
          _rightLayoutSize: 0,
          _leftLayoutSize: 0,
          centerTitle: "生日",
          centerRightdes: "未填写",
          type: "birthday",
          hasFooterDivider: "true"
        }, {
          viewType: "ListViewCellLine",
          _rightLayoutSize: 0,
          _leftLayoutSize: 0,
          centerTitle: "账号",
          centerRightdes: "未填写",
          type: "phone",
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
      $A().page().widget("ActionBar").onItemClick(function(data) {
        return root.onActionBarItemClick(data);
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

    ECpageClass.prototype.onActionBarItemClick = function(data) {
      return $A().app().openPage({
        page_name: "page_my",
        params: {},
        close_option: ""
      });
    };

    ECpageClass.prototype.onCreated = function() {
      if ((root._platform != null) && root._platform === "ios") {
        $A().page().widget(this._page_name + "_ListViewBase_0").refreshData(JSON.stringify(this._listview_data));
      }
      return root.getNetResource();
    };

    ECpageClass.prototype.onItemClick = function(data) {
      var info, item, position;
      item = root._listview_data.data[data.position];
      position = data.position;
      switch ("" + item.type) {
        case "nickname":
          info = {
            key: "nickname",
            page_name: root._page_name,
            hint: "设置姓名",
            inputText: (item.centerRightdes != null) && item.centerRightdes !== "未设置" ? item.centerRightdes : ""
          };
          return $A().app().openPage({
            page_name: "page_input",
            params: {
              info: JSON.stringify(info)
            },
            close_option: ""
          });
        case "sex":
          return $A().app().showRadioConfirm({
            ok: "确定",
            title: "" + item.centerTitle,
            target: (item.centerRightdes != null) && item.centerRightdes !== "未填写" ? "" + item.centerRightdes : "男",
            items: "男-女"
          }).then(function(data) {
            var sex;
            if (data.state === "ok") {
              sex = {
                "男": 0,
                "女": 1
              };
              root._listview_data.data[position].centerRightdes = data.target;
              $A().page().widget(root._page_name + "_ListViewBase_0").refreshData(JSON.stringify(root._listview_data));
              return $A().app().callApi({
                method: "users/modify",
                sex: "" + sex[data.target],
                cacheTime: 0
              });
            }
          });
        case "birthday":
          return $A().app().showDatepickerConfirm({
            ok: "确定",
            cancel: "取消",
            defaultDay: (item.centerRightdes != null) && item.centerRightdes !== "未填写" ? "" + item.centerRightdes : "",
            title: "" + item.centerTitle
          }).then(function(data) {
            if (data.state === "ok") {
              root._listview_data.data[position].centerRightdes = data.value;
              $A().page().widget(root._page_name + "_ListViewBase_0").refreshData(JSON.stringify(root._listview_data));
              return $A().app().callApi({
                method: "users/modify",
                birthday: "" + data.value,
                cacheTime: 0
              });
            }
          });
        case "phone":
          return $A().app().makeToast("账号不可修改");
      }
    };

    ECpageClass.prototype.onItemInnerClick = function(data) {
      var item;
      item = root._listview_data.data[data.position];
      if ((item._type != null) && item._type === "back") {
        return $A().app().closePage();
      }
    };

    ECpageClass.prototype.onResume = function() {
      return $A().page().param("nickname").then(function(data) {
        if (data !== "") {
          $A().page().param({
            key: "nickname",
            value: ""
          });
          root._listview_data.data[0].centerRightdes = data;
          $A().page().widget(root._page_name + "_ListViewBase_0").refreshData(JSON.stringify(root._listview_data));
          return $A().app().callApi({
            method: "users/modify",
            nickname: "" + data,
            cacheTime: 0
          });
        }
      });
    };

    ECpageClass.prototype.prepareForInitView = function() {
      $A().app().platform().then(function(platform) {
        return root._platform = platform;
      });
      return $A().lrucache().get("phone").then(function(phone) {
        if ((phone != null) && phone !== "") {
          return root._listview_data.data[3].centerRightdes = phone;
        }
      });
    };

    ECpageClass.prototype.getNetResource = function() {
      return $A().app().callApi({
        method: "users/detail",
        cacheTime: 0
      }).then(function(data) {
        var sex;
        $A().app().log("users/detail" + JSON.stringify(data));
        sex = {
          0: "男",
          1: "女"
        };
        if ((data != null) && data !== "") {
          if ((data.nickname != null) && data.nickname !== "") {
            root._listview_data.data[0].centerRightdes = data.nickname;
          }
          if ((data.sex != null) && data.sex !== "") {
            root._listview_data.data[1].centerRightdes = sex[data.sex];
          }
          if ((data.birthday != null) && data.birthday !== "") {
            root._listview_data.data[2].centerRightdes = root.date2str(data.birthday);
          }
          return $A().page().widget(root._page_name + "_ListViewBase_0").refreshData(JSON.stringify(root._listview_data));
        }
      });
    };

    ECpageClass.prototype.date2str = function(date) {
      var substr;
      substr = date.substring(0, 10);
      return substr;
    };

    return ECpageClass;

  })();

  new ECpageClass("page_mycenter");

}).call(this);
