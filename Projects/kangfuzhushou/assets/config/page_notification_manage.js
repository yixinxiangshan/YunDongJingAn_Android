// Generated by CoffeeScript 1.9.0
(function() {
  var ECpageClass, Page;

  ECpageClass = (function() {
    var root;

    root = {};

    ECpageClass.prototype._page_name = "";

    ECpageClass.prototype._platform = "";

    ECpageClass.prototype._local_notifications = [];

    ECpageClass.prototype._listview_data = {
      pullable: false,
      hasFooterDivider: false,
      hasHeaderDivider: true,
      dividerHeight: 1,
      dividerColor: "#EBEBEB",
      data: [
        {
          viewType: "ListViewCellLine",
          _rightLayoutSize: 0,
          _leftLayoutSize: 40,
          centerTitle: "正在加载......"
        }
      ]
    };

    ECpageClass.prototype._constructor = function(_at__page_name) {
      this._page_name = _at__page_name;
      root = this;
      this.prepareForInitView();
      $A().page().widget(this._page_name + "_ListViewBase_0").data(JSON.stringify(this._listview_data));
      $A().page().widget(this._page_name + "_ListViewBase_0").onItemInnerClick(function(data) {
        return root.onItemInnerClick(data);
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
      return root.refreshView();
    };

    ECpageClass.prototype.onItemClick = function(data) {};

    ECpageClass.prototype.onItemInnerClick = function(data) {
      var item;
      item = this._listview_data.data[data.position];
      if (item._type === "back") {
        $A().page().setTimeout("100").then(function() {
          return $A().app().closePage();
        });
        return;
      }
      if (item._type === "notification" && data.isChecked === "false") {
        this._local_notifications[data.position].isOpen = false;
        $A().app().makeToast("关闭“" + this._local_notifications[data.position].title + "”提醒");
        $A().notification().remove({
          notificationId: item.item_info.notificationId
        });
      } else {
        $A().app().makeToast("开启“" + this._local_notifications[data.position].title + "”提醒");
        this._local_notifications[data.position].isOpen = true;
        $A().notification().add(item.item_info);
      }
      return $A().lrucache().set({
        key: "local_notifications",
        value: this._local_notifications
      });
    };

    ECpageClass.prototype.onResume = function() {};

    ECpageClass.prototype.prepareForInitView = function() {
      return $A().app().platform().then(function(platform) {
        return root._platform = platform;
      });
    };

    ECpageClass.prototype.refreshView = function() {
      return $A().lrucache().get("local_notifications").then(function(data) {
        var k_notification, listview_data_temp, v_notification, _ref;
        listview_data_temp = [];
        if ((data != null) && data.length !== 0) {
          root._local_notifications = JSON.parse(data);
          _ref = JSON.parse(data);
          for (k_notification in _ref) {
            v_notification = _ref[k_notification];
            listview_data_temp.push({
              item_info: v_notification,
              viewType: "ListViewCellSetting",
              name: v_notification.title,
              value: "",
              description: root.dateFormat(new Date(parseInt(v_notification.broadcastTime))),
              isOpen: v_notification.isOpen === "true" ? true : false,
              _type: "notification"
            });
          }
        }
        listview_data_temp.push({
          viewType: "ListViewCellButton",
          btnTitle: "返 回",
          btnType: "ok",
          _type: "back"
        });
        root._listview_data.data = listview_data_temp;
        return $A().page().widget(root._page_name + "_ListViewBase_0").refreshData(JSON.stringify(root._listview_data));
      });
    };

    ECpageClass.prototype.dateFormat = function(day) {
      var MM, dd, hh, mm, ss, yyyy;
      yyyy = day.getFullYear();
      MM = day.getMonth() + 1;
      dd = day.getDate();
      hh = day.getHours();
      mm = day.getMinutes();
      ss = day.getSeconds();
      return MM + "月" + dd + "日" + hh + ":" + mm + ":" + ss;
    };

    return ECpageClass;

  })();

  Page = new ECpageClass("page_notification_manage");

}).call(this);
