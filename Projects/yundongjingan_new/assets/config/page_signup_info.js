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
      dividerColor: "#EBEBEB",
      data: [
        {
          viewType: "ListViewCellLine",
          _rightLayoutSize: 0,
          _leftLayoutSize: 60,
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
        return $A().page().widget(this._page_name + "_ListViewBase_0").refreshData(JSON.stringify(this._listview_data));
      }
    };

    ECpageClass.prototype.onItemClick = function(data) {};

    ECpageClass.prototype.onItemInnerClick = function(data) {
      var item;
      item = this._listview_data.data[data.position];
      if ((item._type != null) && item._type === 'ok') {
        $A().lrucache().get("phone").then(function(phone) {
          var content;
          if ((phone != null) && phone !== "") {
            content = {
              content_id: item.content_id,
              content_title: item.content_title
            };
            return $A().app().openPage({
              page_name: "page_signup_input",
              params: {
                info: JSON.stringify(content)
              },
              close_option: ""
            });
          } else {
            return $A().app().showConfirm({
              ok: "登陆",
              cancel: "取消",
              title: "警告",
              message: "您尚未登陆，请先登陆"
            }).then(function(data) {
              if (data.state === "ok") {
                $A().app().openPage({
                  page_name: "page_login",
                  params: {},
                  close_option: ""
                });
              }
              if (data.state === "cancel") {
                return false;
              }
            });
          }
        });
      }
      if ((item._type != null) && item._type === 'cancel') {
        return $A().lrucache().get("phone").then(function(phone) {
          var content;
          if ((phone != null) && phone !== "") {
            content = {
              content_id: item.content_id
            };
            return $A().app().openPage({
              page_name: "page_signup_list",
              params: {
                info: JSON.stringify(content)
              },
              close_option: ""
            });
          } else {
            return $A().app().showConfirm({
              ok: "登陆",
              cancel: "取消",
              title: "警告",
              message: "您尚未登陆，请先登陆"
            }).then(function(data) {
              if (data.state === "ok") {
                $A().app().openPage({
                  page_name: "page_login",
                  params: {},
                  close_option: ""
                });
              }
              if (data.state === "cancel") {
                return false;
              }
            });
          }
        });
      }
    };

    ECpageClass.prototype.onResume = function() {};

    ECpageClass.prototype.onResult = function(data) {};

    ECpageClass.prototype.prepareForInitView = function() {
      return $A().app().platform().then(function(platform) {
        root._platform = platform;
        return $A().page().param("info").then(function(info) {
          return $A().app().callApi({
            method: "content/news/detail",
            content_id: info,
            cacheTime: 0
          }).then(function(data) {
            if (data.errors != null) {
              if (data.errors[0].error_num != null) {
                return $A().app().makeToast("网络状态不好，请重新加载");
              } else {
                return $A().app().makeToast("没有网络");
              }
            } else {
              root._listview_data.data = [];
              root._listview_data.data.push({
                viewType: "ListViewCellArticleTitle",
                headTitle: "" + data.content_info.title
              });
              root._listview_data.data.push({
                viewType: "ListViewCellImage",
                image: {
                  imageType: "imageServer",
                  imageSize: "xlarge",
                  imageSrc: "" + data.content_info.image_cover.url
                }
              });
              root._listview_data.data.push({
                viewType: "ListViewCellArticle",
                content: "" + data.content_info.content
              });
              root._listview_data.data.push({
                viewType: "ListViewCellButton",
                btnTitle: "我要申请",
                btnType: "ok",
                _type: "ok",
                content_id: "" + data.content_info.id,
                content_title: "" + data.content_info.title
              });
              root._listview_data.data.push({
                viewType: "ListViewCellButton",
                btnTitle: "已申请列表",
                btnType: "cancel",
                _type: "cancel",
                content_id: "" + data.content_info.id,
                content_title: "" + data.content_info.title
              });
              return $A().page().widget(root._page_name + "_ListViewBase_0").refreshData(JSON.stringify(root._listview_data));
            }
          });
        });
      });
    };

    return ECpageClass;

  })();

  new ECpageClass("page_signup_info");

}).call(this);
