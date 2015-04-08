// Generated by CoffeeScript 1.9.1
(function() {
  var ECpageClass;

  ECpageClass = (function() {
    var checked_count, root;

    root = {};

    checked_count = 0;

    ECpageClass.prototype._platform = "";

    ECpageClass.prototype._cfg = {
      checked_image_url: "webview/images/icon/default/checkbox_check.png",
      unchecked_image_url: "webview/images/icon/default/checkbox_uncheck.png"
    };

    ECpageClass.prototype._page_name = "";

    ECpageClass.prototype._item_info = {};

    ECpageClass.prototype._listview_data = {
      pullable: false,
      hasFooterDivider: true,
      hasHeaderDivider: true,
      dividerHeight: 0,
      dividerColor: "#EBEBEB",
      data: [
        {
          viewType: "ListViewCellLine",
          _rightLayoutSize: 0,
          _leftLayoutSize: 0,
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
      return root.getNetResource();
    };

    ECpageClass.prototype.onItemClick = function(data) {
      var i, item, j, len, position, ref, updateItemsData;
      updateItemsData = [];
      position = data.position;
      item = root._listview_data.data[data.position];
      if (item.type === "_todo") {
        if (root._listview_data.data[position].checked === false) {
          root._listview_data.data[position].checked = true;
          root.itemCheck_saveFav_id_id(root._listview_data);
          $A().lrucache().set({
            key: "" + root._item_info.fav_id_id,
            value: "" + root._item_info.fav_id_id_value
          });
          root._listview_data.data[position].leftImage.imageSrc = root._cfg.checked_image_url;
          updateItemsData.push({
            position: position,
            data: root._listview_data.data[position]
          });
          $A().page("page_home").param({
            key: "_setting_changed",
            value: "true"
          });
          checked_count = 0;
          ref = root._listview_data.data;
          for (i = j = 0, len = ref.length; j < len; i = ++j) {
            item = ref[i];
            if (item.checked && item.type === "_todo") {
              if (root._listview_data.data[i].checked === true) {
                checked_count = checked_count + 1;
              }
            }
          }
          root._listview_data.data[0].centerRightdes = " " + checked_count + " / " + root._item_info.item_lists.length;
          updateItemsData.push({
            position: 0,
            data: root._listview_data.data[0]
          });
          $A().page().widget(root._page_name + "_ListViewBase_0").updateItems({
            data: updateItemsData
          });
          if (("" + checked_count) === ("" + root._item_info.item_lists.length)) {
            $A().app().makeToast(root._item_info.title + " 已完成！");
            root.updateState("done", "" + root._item_info.fav_id_id_value, "" + root._item_info.fav_id, "" + root._item_info.id);
            return $A().page().setTimeout("500").then(function() {
              return $A().page("page_home").param({
                key: "_setting_changed",
                value: "true"
              });
            });
          }
        }
      }
    };

    ECpageClass.prototype.onItemInnerClick = function(data) {
      var item;
      item = root._listview_data.data[data.position];
      if ((item._type != null) && item._type === "back") {
        return $A().app().closePage();
      }
    };

    ECpageClass.prototype.onResume = function() {};

    ECpageClass.prototype.onResult = function() {};

    ECpageClass.prototype.prepareForInitView = function() {
      $A().app().platform().then(function(platform) {
        return root._platform = platform;
      });
      return $A().page().param("info").then(function(data) {
        data = JSON.parse(data);
        return root._item_info = data;
      });
    };

    ECpageClass.prototype.getNetResource = function() {
      return $A().lrucache().get(root._item_info.fav_id_id).then(function(data) {
        var j, key, keys, len, ref, v, val;
        keys = data.split(",");
        ref = root._item_info.item_lists;
        for (key in ref) {
          val = ref[key];
          val.checked = false;
          for (j = 0, len = keys.length; j < len; j++) {
            v = keys[j];
            if ((parseInt(key) + 1) === parseInt(v)) {
              val.checked = true;
              checked_count = checked_count + 1;
            }
          }
        }
        return $A().page().setTimeout("300").then(function() {
          $A().page().widget("ActionBar").title({
            title: "" + root._item_info.title
          });
          root.getTodo_Contents(root._item_info.item_lists);
          return root._listview_data.data.push({
            viewType: "ListViewCellButton",
            btnTitle: "返 回",
            btnType: "ok",
            _type: "back"
          });
        });
      });
    };

    ECpageClass.prototype.getTodo_Contents = function(data) {
      var item, j, len;
      root._listview_data.data.pop();
      for (j = 0, len = data.length; j < len; j++) {
        item = data[j];
        root._listview_data.data.push(root.getTodo_Contents_getItem(item.checked, item.title, item.id));
      }
      root._listview_data.data.splice(0, 0, {
        viewType: "ListViewCellLine",
        _rightLayoutSize: 0,
        _leftLayoutSize: 20,
        _bottomDivider: {
          imageType: "assets",
          imageSize: "fitSize",
          imageSrc: "webview/images/icon/default/divider.png"
        },
        _backgroundColor: {
          _clickable: true,
          pressed: "#ffffff",
          selected: "#ffffff",
          normal: "#ffffff",
          alpha: 38
        },
        centerRightdes: checked_count + " / " + root._listview_data.data.length + " " + (checked_count === root._listview_data.data.length ? '( 已完成 )' : ''),
        centerTitle: " ",
        type: "_count",
        hasFooterDivider: "true"
      });
      return $A().page().setTimeout("100").then(function() {
        return $A().page().widget(root._page_name + "_ListViewBase_0").refreshData(JSON.stringify(root._listview_data));
      });
    };

    ECpageClass.prototype.getTodo_Contents_getItem = function(checked, title, id) {
      var item;
      item = {
        id: id,
        viewType: "ListViewCellLine",
        _rightLayoutSize: 10,
        _leftLayoutSize: 40,
        _bottomDivider: {
          imageType: "assets",
          imageSize: "fitSize",
          imageSrc: "webview/images/icon/default/divider.png"
        },
        leftImage: {
          imageType: "assets",
          imageSize: "fitSize",
          imageSrc: checked ? root._cfg.checked_image_url : root._cfg.unchecked_image_url
        },
        centerTitle: title,
        checked: checked,
        type: "_todo"
      };
      if (checked) {
        item._backgroundColor = {
          _clickable: true,
          pressed: "#ffffff",
          selected: "#ffffff",
          normal: "#ffffff",
          alpha: 38
        };
      }
      return item;
    };

    ECpageClass.prototype.itemCheck_saveFav_id_id = function(_listview_data) {
      var fav_id_ids, i, item, j, len, ref;
      fav_id_ids = [];
      ref = _listview_data.data;
      for (i = j = 0, len = ref.length; j < len; i = ++j) {
        item = ref[i];
        if (item.checked === true) {
          fav_id_ids.push(parseInt(item.id) - parseInt(root._item_info.item_lists[0].id) + 1);
        }
      }
      return root._item_info.fav_id_id_value = fav_id_ids.join(",");
    };

    ECpageClass.prototype.updateState = function(action_type, action, kf_course_user_fav_id, course_index_id) {
      var update_all, update_item;
      $A().lrucache().set({
        key: kf_course_user_fav_id + "_" + course_index_id,
        value: "" + action
      });
      update_all = [];
      update_item = {
        action: action,
        action_type: action_type,
        kf_course_user_fav_id: kf_course_user_fav_id,
        course_index_id: course_index_id
      };
      return $A().app().netState().then(function(net_state) {
        return $A().lrucache().get("update_all").then(function(data) {
          if ((data != null) && data !== "") {
            update_all = JSON.parse(data);
          }
          update_all.push(update_item);
          if (net_state === "offline") {
            return $A().lrucache().set({
              key: "update_all",
              value: update_all
            });
          } else {
            return $A().app().callApi({
              method: "users/courses/course_indices/massaction",
              actions: update_all,
              cacheTime: 0
            }).then(function(res) {
              if ((res.success != null) && res.success === true) {
                return $A().lrucache().remove("update_all");
              } else {
                return $A().lrucache().set({
                  key: "update_all",
                  value: update_all
                });
              }
            });
          }
        });
      });
    };

    return ECpageClass;

  })();

  new ECpageClass("page_course_todo");

}).call(this);
