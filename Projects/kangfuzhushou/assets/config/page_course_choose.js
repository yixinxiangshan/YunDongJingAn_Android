// Generated by CoffeeScript 1.9.1
(function() {
  var ECpageClass,
    indexOf = [].indexOf || function(item) { for (var i = 0, l = this.length; i < l; i++) { if (i in this && this[i] === item) return i; } return -1; };

  ECpageClass = (function() {
    var _theme, root;

    root = {};

    _theme = "default";

    ECpageClass.prototype._platform = "";

    ECpageClass.prototype._page_name = "";

    ECpageClass.prototype._cfg = {
      itemCheckedId: 0,
      src_prefix: "webview/images/icon/" + _theme + "/",
      bottomDivider: {
        imageType: "assets",
        imageSize: "fitSize",
        imageSrc: "webview/images/icon/default/divider.png"
      },
      checked_image_url: "webview/images/icon/default/checkbox_check.png",
      unchecked_image_url: "webview/images/icon/default/checkbox_uncheck.png",
      _centerBottomdes2Color: {
        _clickable: false,
        pressed: "#999999",
        selected: "#999999",
        normal: "#999999"
      }
    };

    ECpageClass.prototype._listview_data = {
      pullable: false,
      hasFooterDivider: true,
      hasHeaderDivider: true,
      bottomButton: true,
      dividerHeight: 0,
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

    ECpageClass.prototype._listview_data_bottom = {
      pullable: false,
      hasFooterDivider: false,
      hasHeaderDivider: false,
      dividerHeight: 0,
      dividerColor: "#EBEBEB",
      data: [
        {
          viewType: "ListViewCellButton",
          btnTitle: "确 定",
          btnType: "disable",
          type: "_submit"
        }
      ]
    };

    ECpageClass.prototype._constructor = function(_page_name1) {
      this._page_name = _page_name1;
      root = this;
      this.prepareForInitView();
      $A().page().widget(this._page_name + "_ListViewBase_bottom").data(JSON.stringify(this._listview_data_bottom));
      $A().page().widget(this._page_name + "_ListViewBase_bottom").onItemInnerClick(function(data) {
        return root.onItemInnerClick(data);
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
        $A().page().widget(this._page_name + "_ListViewBase_0").refreshData(JSON.stringify(this._listview_data));
      }
      return root.getParams();
    };

    ECpageClass.prototype.onItemClick = function(data) {
      if (root._listview_data.data[data.position].type === "_course") {
        return root.itemChecked(parseInt(data.position));
      }
    };

    ECpageClass.prototype.onItemInnerClick = function(data) {
      return $A().app().showConfirm({
        ok: "确定添加",
        title: "声明",
        cancel: "取消",
        message: "1.本软件及所提供的内容不能代替诊断过程；\n2.本软件及所提供的内容不代表临床医治过程；\n3.本软件及所提供的建议不是医嘱；\n\n本软件及所提供的内容会与患者的具体情况有所不同，只能在疾病的诊治过程中提供一些信息支持和帮助，并非医嘱。您有任何不适与异状，请及时联系护士或医生。"
      }).then(function(data) {
        if (data.state === "ok") {
          root.favCourse();
        }
        if (data.state === "cancel") {
          return false;
        }
      });
    };

    ECpageClass.prototype.onResume = function() {};

    ECpageClass.prototype.onResult = function(data) {};

    ECpageClass.prototype.prepareForInitView = function() {
      return $A().app().platform().then(function(platform) {
        return root._platform = platform;
      });
    };

    ECpageClass.prototype.getParams = function() {
      return $A().page().param("courses").then(function(res) {
        var reg, reg1;
        root._listview_data.data = [];
        if (res !== "" && res !== "{}" && res !== "(null)") {
          reg = new RegExp("\r\n", "g");
          reg1 = new RegExp("\t", "g");
          res = JSON.parse(res.replace(reg, "\\r\\n").replace(reg1, "\\t"));
          return $A().page().setTimeout("500").then(function() {
            return root.getCourse(res);
          });
        } else {
          return $A().app().callApi({
            method: "courses/hot",
            cacheTime: 0
          }).then(function(res) {
            var item, k, len, ref;
            ref = res.items;
            for (k = 0, len = ref.length; k < len; k++) {
              item = ref[k];
              item.nocheck = true;
            }
            $A().page().widget("ActionBar").title({
              title: "热门课程选择"
            });
            return root.getCourse(res.items);
          });
        }
      });
    };

    ECpageClass.prototype.favCourse = function() {
      var course_code, item;
      item = root._listview_data.data[root._cfg.itemCheckedId];
      course_code = "" + item.item_info.code;
      $A().app().showLoadingDialog({
        content: "正在添加课程,请稍候"
      });
      return $A().app().callApi({
        method: "users/courses/create",
        course_id: item.itemId,
        cacheTime: 0
      }).then(function(res) {
        if (res.errors != null) {
          $A().app().closeLoadingDialog();
          if (res.errors === "没有网络") {
            $A().app().makeToast("没有网络");
            return;
          }
          if ((res.errors[0].error_num != null) && ("" + res.errors[0].error_num) === "100000") {
            $A().app().makeToast("网络状态不好，请重试");
            return;
          }
          if ((res.errors[0].error_num != null) && ("" + res.errors[0].error_num) === "301101") {
            $A().app().makeToast("你已添加过该课程，不能重复添加。");
            return;
          }
        }
        $A().app().showLoadingDialog({
          content: "课程添加成功"
        });
        return $A().page().setTimeout("2000").then(function() {
          $A().page("page_home").param({
            key: "_setting_changed",
            value: "true"
          });
          $A().app().showLoadingDialog({
            content: "正在导入课程数据,请稍候"
          });
          return root.addCourseBycourseCode(course_code).then(function(data) {
            $A().app().closeLoadingDialog();
            if (data === "_false") {
              $A().app().makeToast("网络状态不好，请重试");
            }
            if (data === "_true") {
              return $A().page().setTimeout("1000").then(function() {
                $A().lrucache().set({
                  key: "download_all_courses",
                  value: "true"
                });
                return $A().app().showConfirm({
                  title: "请自行修改时间设置",
                  ok: "我 知 道 了",
                  message: "我们已经为课程设置了默认时间。为了更好地查看课程内容，请根据你自己的具体情况修改时间设置。"
                }).then(function(data) {
                  $A().app().makeToast("正在返回首页请稍后");
                  return $A().page().setTimeout("1000").then(function() {
                    return $A().app().openPage({
                      page_name: "page_home",
                      params: {},
                      close_option: "close_others"
                    });
                  });
                });
              });
            }
          });
        });
      });
    };

    ECpageClass.prototype.getCourse = function(res) {
      var check_num, item, k, len;
      check_num = 0;
      for (k = 0, len = res.length; k < len; k++) {
        item = res[k];
        root._listview_data.data.push({
          item_info: item,
          viewType: "ListViewCellLine",
          _rightLayoutSize: 0,
          _leftLayoutSize: 40,
          leftImage: {
            imageType: "assets",
            imageSize: "fitSize",
            imageSrc: root._cfg.unchecked_image_url
          },
          centerTitle: "" + item.title,
          _centerBottomdes2Color: root._cfg._centerBottomdes2Color,
          _bottomDivider: root._cfg.bottomDivider,
          itemId: item.id,
          isChecked: false,
          type: "_course"
        });
      }
      root._listview_data.data.push({
        viewType: "ListViewCellGroupTitle",
        textTitle: "已选 " + check_num + " 个课程",
        color: "",
        type: "_count"
      });
      if (check_num === 0) {
        root._listview_data_bottom.data[0]["btnType"] = "disable";
      } else {
        root._listview_data_bottom.data[0]["btnType"] = "ok";
      }
      $A().page().widget("page_course_choose_ListViewBase_bottom").refreshData(JSON.stringify(root._listview_data_bottom));
      return $A().page().widget("page_course_choose_ListViewBase_0").refreshData(JSON.stringify(root._listview_data));
    };

    ECpageClass.prototype.itemChecked = function(item_position) {
      var checked_count, i, item, k, len, ref;
      checked_count = 1;
      ref = root._listview_data.data;
      for (i = k = 0, len = ref.length; k < len; i = ++k) {
        item = ref[i];
        if (item.type === "_course") {
          if (i !== item_position) {
            item.isChecked = false;
            item.leftImage.imageSrc = root._cfg.unchecked_image_url;
          } else {
            root._cfg.itemCheckedId = i;
            item.isChecked = true;
            item.leftImage.imageSrc = root._cfg.checked_image_url;
          }
        }
        if (item.type === "_count") {
          item.textTitle = "已选 " + checked_count + " 个课程";
        }
        if (root._listview_data_bottom.data[0].type === "_submit") {
          root._listview_data_bottom.data[0]["btnType"] = (checked_count === 0 ? "disable" : "ok");
        }
      }
      $A().page().widget("page_course_choose_ListViewBase_bottom").refreshData(JSON.stringify(root._listview_data_bottom));
      return $A().page().widget("page_course_choose_ListViewBase_0").refreshData(JSON.stringify(root._listview_data));
    };

    ECpageClass.prototype.addCourseBycourseCode = function(course_code) {
      var column_data, column_types;
      column_data = {};
      column_types = [];
      return $A().app().callApi({
        method: "courses/package",
        course_code: course_code,
        cacheTime: 0
      }).then(function(res) {
        var column_types_with_id, course_id, course_id_index_arr, fav_id, i, item, item_column_types, j, k, k_day_time, key_day_time, l, len, len1, ref, ref1, ref2, ref3, v_day_time, value_day_time;
        if ((res.errors != null) && (res.errors.error_num != null) && res.errors.error_num === "100000") {
          $A().app().makeToast("网络状态不好，请重试");
          return "_false";
        }
        if ((res.errors != null) && res.errors === "没有网络") {
          $A().app().makeToast("请开启网络");
          return "_false";
        }
        fav_id = res.detail.fav_id;
        course_id = res.detail.id;
        course_id_index_arr = [];
        ref = res.contents;
        for (key_day_time in ref) {
          value_day_time = ref[key_day_time];
          course_id_index_arr.push("" + key_day_time);
        }
        $A().lrucache().get("fav_id_arr").then(function(data) {
          var fav_id_arr;
          fav_id_arr = {};
          fav_id_arr = (data != null) && data !== "" ? JSON.parse(data) : {};
          fav_id_arr[fav_id] = {
            info: res.detail,
            index: course_id_index_arr
          };
          return $A().lrucache().set({
            key: "fav_id_arr",
            value: JSON.stringify(fav_id_arr)
          });
        });
        ref1 = res.contents;
        for (k_day_time in ref1) {
          v_day_time = ref1[k_day_time];
          $A().lrucache().set({
            key: "" + k_day_time,
            value: JSON.stringify(v_day_time)
          });
        }
        ref2 = res.contents;
        for (k_day_time in ref2) {
          v_day_time = ref2[k_day_time];
          for (i = k = 0, len = v_day_time.length; k < len; i = ++k) {
            item = v_day_time[i];
            if ((item.item_colum != null) && item.item_colum !== "") {
              item["day_offset"] = k_day_time.substr(k_day_time.lastIndexOf("_") + 1);
              item["day_name"] = res.detail[k_day_time.substring(k_day_time.indexOf("_") + 1, k_day_time.lastIndexOf("_"))];
              if (!(ref3 = item.item_colum, indexOf.call(column_types, ref3) >= 0)) {
                column_types.push(item.item_colum);
                column_data["" + item.item_colum] = [];
              }
              column_data["" + item.item_colum].push(item);
            }
          }
        }
        column_types_with_id = [];
        for (j = l = 0, len1 = column_types.length; l < len1; j = ++l) {
          item_column_types = column_types[j];
          column_types_with_id.push({
            key: "" + j,
            value: "" + item_column_types
          });
          $A().lrucache().set({
            key: "column_" + j + "_" + fav_id,
            value: JSON.stringify(column_data["" + item_column_types])
          });
        }
        $A().lrucache().get("column_arr").then(function(data) {
          var column_arr;
          column_arr = {};
          column_arr = (data != null) && data !== "" ? JSON.parse(data) : {};
          column_arr[fav_id] = column_types_with_id;
          return $A().lrucache().set({
            key: "column_arr",
            value: JSON.stringify(column_arr)
          });
        });
        $A().lrucache().set({
          key: "download_all_courses",
          value: "true"
        });
        return "_true";
      });
    };

    return ECpageClass;

  })();

  new ECpageClass("page_course_choose");

}).call(this);
