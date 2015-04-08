// Generated by CoffeeScript 1.9.0
(function() {
  var ECpageClass,
    __indexOf = [].indexOf || function(item) { for (var i = 0, l = this.length; i < l; i++) { if (i in this && this[i] === item) return i; } return -1; };

  ECpageClass = (function() {
    var root, _cfg, _global_data, _theme;

    root = {};

    ECpageClass.prototype._page_name = "";

    _global_data = {};

    ECpageClass.prototype._platform = "";

    ECpageClass.prototype._listview_data = {
      pullable: false,
      hasFooterDivider: false,
      hasHeaderDivider: false,
      dividerHeight: 0,
      dividerColor: "#cccccc",
      data: [
        {
          viewType: "ListViewCellLine",
          _rightLayoutSize: 0,
          _leftLayoutSize: 60,
          centerTitle: "正在加载......"
        }
      ]
    };

    _theme = "default";

    _cfg = {
      src_prefix: "webview/images/icon/" + _theme + "/",
      list_sort: ["process", "set_course", "remind", "todo", "survey", "article"],
      list_page_name: {
        "set_course": {
          page_name: "page_course_choose"
        },
        "set_time": {
          page_name: "page_course_time_choose"
        },
        "todo": {
          page_name: "page_course_todo"
        },
        "title": {
          page_name: "page_course_time_choose"
        },
        "survey": {
          page_name: "page_course_survey"
        },
        "article_list": {
          page_name: "page_course_article_detail"
        }
      },
      list_config: {
        7: {
          name: "remind"
        },
        6: {
          name: "tips"
        },
        5: {
          name: "survey"
        },
        4: {
          name: "todo"
        },
        2: {
          name: "article"
        }
      },
      _centerTitleColor: {
        _clickable: false,
        pressed: "#1a1a1a",
        selected: "#1a1a1a",
        normal: "#1a1a1a"
      },
      _centerTitleColorDone: {
        _clickable: false,
        pressed: "#999999",
        selected: "#999999",
        normal: "#999999"
      },
      _centerBottomdesColor: {
        _clickable: false,
        pressed: "#999999",
        selected: "#999999",
        normal: "#999999"
      },
      _centerBottomdes2Color: {
        _clickable: false,
        pressed: "#4D4D4D",
        selected: "#4D4D4D",
        normal: "#4D4D4D"
      },
      _centerBottomdesColor_article_list: {
        _clickable: false,
        pressed: "#5793ff",
        selected: "#5793ff",
        normal: "#5793ff"
      },
      _bottomDivider: {
        imageType: "assets",
        imageSize: "fitSize",
        imageSrc: "webview/images/icon/default/divider.png"
      },
      listview_right_img: "webview/images/icon/" + _theme + "/listview_right.png",
      listview_right_down_img: "webview/images/icon/" + _theme + "/listview_right_down.png"
    };

    ECpageClass.prototype._constructor = function(_at__page_name) {
      this._page_name = _at__page_name;
      root = this;
      root.prepareForInitView();
      $A().page().widget(this._page_name + "_ListViewBase_0").data(JSON.stringify(this._listview_data));
      $A().page().widget(this._page_name + "_ListViewBase_0").onItemInnerClick(function(data) {
        return root.onItemInnerClick(data);
      });
      $A().page().widget(this._page_name + "_ListViewBase_0").onItemClick(function(data) {
        return root.onItemClick(data);
      });
      $A().page().onCreated(function() {
        return root.onCreated();
      });
      return $A().page().setTimeout("1000").then(function() {
        root._listview_data.data = [];
        return root.viewControl();
      });
    };

    function ECpageClass(_page_name) {
      this._constructor(_page_name);
    }

    ECpageClass.prototype.onCreated = function() {
      if ((root._platform != null) && root._platform === "ios") {
        $A().page().widget(this._page_name + "_ListViewBase_0").refreshData(JSON.stringify(this._listview_data));
      }
      return $A().page(root._page_name).widget("ActionBar").title({
        title: _global_data["title"]
      });
    };

    ECpageClass.prototype.onItemInnerClick = function(data) {};

    ECpageClass.prototype.onResume = function() {};

    ECpageClass.prototype.onResult = function() {};

    ECpageClass.prototype.prepareForInitView = function() {
      $A().app().platform().then(function(platform) {
        return root._platform = platform;
      });
      $A().page().param("info").then(function(data) {
        var tempFid, uniqueFid, _i, _len, _ref, _ref1;
        data = JSON.parse(data);
        _global_data["title"] = data.columnName;
        _global_data["datas"] = data.pageParams;
        uniqueFid = [];
        _ref = data.pageParams;
        for (_i = 0, _len = _ref.length; _i < _len; _i++) {
          tempFid = _ref[_i];
          if (!(_ref1 = tempFid.fId, __indexOf.call(uniqueFid, _ref1) >= 0)) {
            uniqueFid.push(tempFid.fId);
          }
        }
        return _global_data["uniqueFid"] = uniqueFid;
      });
      $A().lrucache().get("fav_id_arr").then(function(data) {
        data = JSON.parse(data);
        return _global_data["FavName"] = data;
      });
      return root.initContent();
    };

    ECpageClass.prototype.initContent = function() {
      var cacheKeys, colArr, id, _i, _j, _len, _len1, _ref, _ref1;
      cacheKeys = [];
      _ref = _global_data["uniqueFid"];
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        id = _ref[_i];
        _ref1 = _global_data["datas"];
        for (_j = 0, _len1 = _ref1.length; _j < _len1; _j++) {
          colArr = _ref1[_j];
          if (id === colArr.fId) {
            cacheKeys.push("column_" + colArr.cId + "_" + id);
          }
        }
      }
      return $A().lrucache().massGet(JSON.stringify(cacheKeys)).then(function(res) {
        var resk, resv, _results;
        _global_data["content"] = {};
        _results = [];
        for (resk in res) {
          resv = res[resk];
          _results.push(_global_data["content"][resk] = JSON.parse(resv));
        }
        return _results;
      });
    };

    ECpageClass.prototype.viewControl = function() {
      var colArr, id, _i, _j, _len, _len1, _ref, _ref1;
      _ref = _global_data["uniqueFid"];
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        id = _ref[_i];
        if (root.getTitle(id)) {
          _ref1 = _global_data["datas"];
          for (_j = 0, _len1 = _ref1.length; _j < _len1; _j++) {
            colArr = _ref1[_j];
            if (id === colArr.fId) {
              root.getContent("column_" + colArr.cId + "_" + id);
            }
          }
        }
      }
      if (root._listview_data.data.length === 0) {
        root._listview_data.data.push({
          viewType: "ListViewCellLine",
          _rightLayoutSize: 0,
          _leftLayoutSize: 60,
          centerTitle: "此栏目没有内容…"
        });
      }
      return $A().page().widget(root._page_name + "_ListViewBase_0").refreshData(JSON.stringify(root._listview_data));
    };

    ECpageClass.prototype.getTitle = function(paramFavId) {
      var havaTitleSign, tempData, tempTitle;
      tempData = [];
      havaTitleSign = true;
      tempTitle = null;
      try {
        tempTitle = _global_data["FavName"][paramFavId].info.title;
      } catch (_error) {
        tempTitle = null;
      }
      if ((tempTitle != null) && tempTitle.length > 0) {
        tempData.push({
          eventType: "title",
          _backgroundColor: {
            _clickable: false,
            pressed: "#f2f2f2",
            selected: "#f2f2f2",
            normal: "#f2f2f2"
          },
          viewType: "ListViewCellTextWithButton",
          title: tempTitle
        });
        root._listview_data.data = root._listview_data.data.concat(tempData);
      } else {
        havaTitleSign = false;
      }
      return havaTitleSign;
    };

    ECpageClass.prototype.getContent = function(colId) {
      var data;
      data = _global_data["content"][colId];
      return root.showView(data);
    };

    ECpageClass.prototype.showView = function(resv) {
      var centerRighttopdes, i, inner_item, item, j, listview_data_temp_group, unique_title_list, _i, _j, _len, _len1, _ref;
      listview_data_temp_group = [];
      unique_title_list = [];
      for (i = _i = 0, _len = resv.length; _i < _len; i = ++_i) {
        item = resv[i];
        if (item.parent_id === 0) {
          if (parseInt(item.day_offset) === 0) {
            centerRighttopdes = item.day_name + "当天";
          } else if (parseInt(item.day_offset) < 0) {
            centerRighttopdes = item.day_name + "前" + (-parseInt(item.day_offset)) + "天";
          } else if (parseInt(item.day_offset) > 0) {
            centerRighttopdes = item.day_name + "后" + (parseInt(item.day_offset)) + "天";
          }
          listview_data_temp_group.push({
            item_info: item,
            is_expanded: false,
            eventType: _cfg.list_config[item.kf_course_item_type_id].name,
            viewType: "ListViewCellLine",
            _rightLayoutSize: 25,
            _leftLayoutSize: 40,
            _bottomDivider: !(item.kf_course_item_type_id === 2 && (item.is_expanded != null) && item.is_expanded === true) ? _cfg._bottomDivider : void 0,
            centerTitle: item.title,
            centerRighttopdes: (item.item_type_name != null) && item.item_type_name !== "undefined" ? "" + item.item_type_name : void 0,
            _centerBottomdesColor: _cfg._centerBottomdesColor,
            _centerTitleColor: _cfg._centerTitleColor,
            centerRighttopdes: centerRighttopdes,
            centerBottomdes: item.content != null ? "" + item.content : "",
            _centerBottomdes2Color: _cfg._centerBottomdes2Color,
            rightImage: {
              imageType: "assets",
              imageSize: "fitSize",
              imageSrc: "" + _cfg.listview_right_img
            },
            leftImage: {
              imageType: "assets",
              imageSize: "fitSize",
              imageSrc: "" + _cfg.src_prefix + _cfg.list_config[item.kf_course_item_type_id].name + ".png"
            },
            hasFooterDivider: "true"
          });
          if ((item.item_lists != null) && item.item_count > 0) {
            if (item.kf_course_item_type_id === 2) {
              _ref = item.item_lists;
              for (j = _j = 0, _len1 = _ref.length; _j < _len1; j = ++_j) {
                inner_item = _ref[j];
                inner_item["subtitle"] = item.title;
                inner_item["item_colum"] = item.item_colum;
                listview_data_temp_group.push({
                  item_info: inner_item,
                  kf_course_item_type_id: item.kf_course_item_type_id,
                  eventType: "article_list",
                  viewType: (item.is_expanded != null) && item.is_expanded === true ? "ListViewCellLine" : "ListViewCellNull",
                  _rightLayoutSize: 25,
                  _leftLayoutSize: 40,
                  centerBottomdes2: inner_item.title && inner_item.title !== "undefined" ? "" + inner_item.title : void 0,
                  _centerBottomdes2Color: _cfg._centerBottomdesColor_article_list,
                  _bottomDivider: item.item_count === j + 1 ? _cfg._bottomDivider : void 0,
                  hasFooterDivider: item.item_count === j + 1 ? "true" : void 0
                });
              }
            }
          }
        } else {
          continue;
        }
      }
      return root._listview_data.data = root._listview_data.data.concat(listview_data_temp_group);
    };

    ECpageClass.prototype.onItemClick = function(data) {
      var i, info, inner_item, item, position, updateItemsData, _i, _j, _k, _len, _len1, _len2, _ref, _ref1, _ref2, _ref3, _ref4;
      updateItemsData = [];
      item = root._listview_data.data[data.position];
      info = item.item_info;
      position = data.position;
      if ((_ref = item.eventType) === "set_time" || _ref === "todo" || _ref === "survey" || _ref === "article_list") {
        $A().app().openPage({
          page_name: _cfg.list_page_name[item.eventType].page_name,
          params: {
            info: JSON.stringify(info)
          },
          close_option: ""
        });
      } else if ((_ref1 = item.eventType) === "article" || _ref1 === "remind" || _ref1 === "tips") {
        if (item.is_expanded) {
          item._centerBottomdesColor = _cfg._centerBottomdesColor;
          item.centerBottomdes = item.centerBottomdes2;
          item.centerBottomdes2 = "";
          if (item.eventType !== "remind") {
            item.rightImage.imageSrc = "" + _cfg.listview_right_img;
          }
          if (item.eventType === "article") {
            item._bottomDivider = _cfg._bottomDivider;
            _ref2 = item.item_info.item_lists;
            for (i = _i = 0, _len = _ref2.length; _i < _len; i = ++_i) {
              inner_item = _ref2[i];
              root._listview_data.data[parseInt(position) + i + 1].viewType = "ListViewCellNull";
            }
          }
        } else {
          item.centerBottomdes2 = item.centerBottomdes;
          item.centerBottomdes = "";
          if (item.eventType !== "remind") {
            item.rightImage.imageSrc = "" + _cfg.listview_right_down_img;
          }
          if (item.eventType === "article") {
            item._bottomDivider = {};
            _ref3 = item.item_info.item_lists;
            for (i = _j = 0, _len1 = _ref3.length; _j < _len1; i = ++_j) {
              inner_item = _ref3[i];
              root._listview_data.data[parseInt(position) + i + 1].viewType = "ListViewCellLine";
            }
          }
        }
      }
      item.is_expanded = !item.is_expanded;
      root._listview_data.data[position] = item;
      updateItemsData.push({
        position: position,
        data: item
      });
      if (item.eventType === "article") {
        _ref4 = item.item_info.item_lists;
        for (i = _k = 0, _len2 = _ref4.length; _k < _len2; i = ++_k) {
          inner_item = _ref4[i];
          updateItemsData.push({
            position: parseInt(position) + i + 1,
            data: root._listview_data.data[parseInt(position) + i + 1]
          });
        }
      }
      return $A().page().widget(root._page_name + "_ListViewBase_0").updateItems({
        data: updateItemsData
      });
    };

    return ECpageClass;

  })();

  new ECpageClass("page_column_detail");

}).call(this);
