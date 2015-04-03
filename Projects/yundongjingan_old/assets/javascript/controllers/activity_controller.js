// 控制器逻辑部分
ActivityController = Class.create({
  initialize: function() {
    this.name = "order"
  },
  TAG: "JS-ActivityController",
  //获取活动详细信息
  getActivityInfoById: function(params) {
		$L.D(this.TAG, "params.id = " + params.id);
		var res = Ajax.callAPI("GetContentsShopsByActivity/get", {
			key: params.id 
		}, "1.0", true).evalJSON();
		var err = Ajax.checkError(res);
		if (err) return alert(err.errordes, "pecct://app/null");

		// 数据容错处理
		if (!res.data || res.data == null || typeof res.data.error != "undefined") res = {
			ListByType: []
		};
		else res = res.data;
		$L.D(this.TAG, "response string  = " + Object.toJSON(res));
    
    var startTime = /\d{4}-\d{1,2}-\d{1,2}/g.exec(res.info.start_time);
		var endTime = /\d{4}-\d{1,2}-\d{1,2}/g.exec(res.info.end_time);
		var itemString = {
    	"title":res.info.title,
      "abstracts":res.info.content,
      "content":"活动时间："+startTime + "--" + endTime
		};
		var cardString = {
			"title": "活动照片",
			"withHeader": "true",
			"withFoot": "false",
			"withContent": "true"
		};
		var gralleryString = {
			"itemList": res.imgs
		};

		var groupString = {
			"groupList": [{
				"tableList": [{
					"tag": "pecct://app/openActivity?id=" + params.id + "&pageName=page_shop_list",
					"title": "查看适用场馆"
				}]
			}, {
				"tableList": [{
					"tag": "pecct://app/openActivity?id="+params.id+"&pageName=page_list_comment",
					"title": "查看所有评论"
				}]
			}]
		};

  	var videoUrl = ""

		if (res.info.video_url != "" && typeof res.info.video_url != "undefined") {
			videoUrl = "pecct://activity/openVideo?id=" + res.info.id + "&type="+res.info.type+"&videoUri=" + res.info.video_url;
      var videoTab = {
  			"tableList": [{
					"tag": videoUrl,
					"title": "查看视频"
				}]
			}
      groupString.groupList.push(videoTab);
		}

		$L.D(this.TAG, "itemString   = " + Object.toJSON(gralleryString));
		var newParams = {
			"contentId": res.info.id,
			"itemString": itemString,
			"cardString": cardString,
			"gralleryString": gralleryString,
			"groupString": groupString
		};
    

		$O.postEvent("openActivity", "", "page_huodong_details", Object.toJSON(newParams), null);
	},
	//场馆
	showShopAndActivyInfoByShopId: function(params) {
		var newParams = this.getShopAndActivyInfoData(params);
		$O.postEvent("openActivity", "", "page_changguan_details", Object.toJSON(newParams), null);
	},
	//指导站
	showZhiDaoAndActivyInfoByShopId: function(params) {
		var res = this.getShopAndActivyInfoData(params);
		var videoUrl = ""
		if (res.itemString.video_url && res.itemString.video_url != null && typeof res.itemString.video_url != "undefined" && res.itemString.video_url != "") {
  			videoUrl = "pecct://activity/openVideo?id=" + res.itemString.id + "&type="+res.itemString.type+"&videoUri=" + res.itemString.video_url;
        var tableListData = {
  			"tableList": [{
  				"tag": videoUrl,
  				"title": "查看视频"
  			}]
  		}
  		res.shopInfo.groupList.push(tableListData);
		}
    
		$O.postEvent("openActivity", "", "page_zhidao_details", Object.toJSON(res), null);
	},
	getShopAndActivyInfo_default_params: {
		"type": "shop"
	},
	getShopAndActivyInfoData: function(params) {
		$L.D(this.TAG, "params.shopId = " + params.shopId);
		var requestparams = Object.cloneExtend(this.refreshWidget_default_params, params);
		var res = Ajax.callAPI("GetContentsActivitiesByShops/get", {
			shopid: params.shopId
		}, "1.0", true).evalJSON();
		var err = Ajax.checkError(res);
		if (err) return alert(err.errordes, "pecct://app/null");

		// 数据容错处理
		if (!res.data || res.data == null || typeof res.data.error != "undefined") res = {
			ListByType: []
		};
		else res = res.data;
		$L.D(this.TAG, "response string  = " + Object.toJSON(res));
    
		var photoTitle = "照片";

		var actionbardata = {
			'withActionBar': 'true',
			'withHomeItem': 'true',
			'withHomeAsUp': 'true',
			"actionBarBg": "test_actionbar_bg",
			'title': res.shop.title,
			"menuItemsData": {
				"menuList": [{
					"itemId": "action_bar_menu_item_comment",
					"clickTag": "menu_item_clickTag",
					"actionViewName": "",
					"neverCollapses": "false",
					"title": "评论",
					"iconName": "",
					"showAsAction": "always"
				}, {
					"itemId": "action_bar_menu_item_fav",
					"clickTag": "menu_item_clickTag",
					"actionViewName": "",
					"neverCollapses": "false",
					"title": "收藏",
					"iconName": "",
					"showAsAction": "always"
				}]
			}
		};

		var cardString = {
			"title": photoTitle,
			"withHeader": "true",
			"withFoot": "false",
			"withContent": "true"
		};
		var gralleryString = {
			"itemList": res.shopimgs
		};
		// new ActivityController().showMap({'address':'" + res.shop.address + "','longitude':'" + res.shop.longitude + "','latitude':'" + res.shop.latitude + "'})
		var shopInfo = {
			"groupList": [{
				"tableList": [{
						"tag": "pecct://activity/showMap?title=" + encodeURIComponent(res.shop.title) + "&address=" + encodeURIComponent(res.shop.address) + "&longitude=" + res.shop.longitude + "&latitude=" + res.shop.latitude + "",
						"title": res.shop.address
					}
					/*,{
							"tag": "pecct://activity/getShopInfoById?id="+res.shop.id+"&pageName=page_shop_details",
							"title": "查看场馆详情"
						}*/
				]
			}]
		};
    
    if(res.shop.phone_num!="" || res.shop.phone_num !=0){
      var phoneTab = {
  					"tag": "pecct://app/callPhoneNumber?number=" + res.shop.phone_num + "",
						"title": res.shop.phone_num
					}
      shopInfo.groupList[0].tableList.push(phoneTab);
    }
    
		var activityInfo = {
			"groupList": []
		};

		if (res.info && res.info.id) {
			var tableListData = {
				"tableList": [{
					"tag": "pecct://activity/getActivityInfoById?id=" + res.info.id + "",
					"title": res.info.title
				}]
			}
			activityInfo.groupList.push(tableListData);

		}

		var tableListDefaultData = {
			"tableList": [{
				"tag": "pecct://app/openActivity?id="+res.shop.id+"&pageName=page_list_comment",
				"title": "查看所有评论"
			}]
		}

		activityInfo.groupList.push(tableListDefaultData);
		var newParams = {
			"shopId": res.shop.id,
			"barconfig": actionbardata,
			"shopInfo": shopInfo,
			"cardString": cardString,
			"gralleryString": gralleryString,
			"activityInfo": activityInfo,
			"itemString": res.shop
		};
		return newParams;
	},
	showActiviyListInMap: function(params) {
		var res = this.getShowActiviyListInMapData(params);
		$O.postEvent("openActivity", "", "page_huodong", Object.toJSON(res), null);
	},
	refreshWidget_default_params: {
		"type": "shop",
		"target": "map_widget"
	},
	//获取当前位置信息。获取当前位置附近的活动信息
	showMapWithLocation: function(params) {
		var pageParams = {
			'nowSonId': params.sonid,
			'type': 'shop',
			'withCircle': 'true',
			'mapSize': params.size,
			'circleColor': params.circleColor
		};
    $O.postEvent("getWidget", "page_ecbutton1" ,"", "");
		$O.postEvent('putPageParams', Object.toJSON(pageParams), null, null);
		$O.postEvent("showLoadingDialog", "加载中", "正在加载……", false);
		$L.D(this.TAG, "showMapWithLocation params string  = " + Object.toJSON(params));
    $L.D(this.TAG, "-----showMapWithLocation  come in ------ ");
		var p = $H(params);
		$O.postEvent("getLocation", "pecct://activity/refreshWidget?" + p.toQueryString(), null, null);
	},
	refreshWidget: function(params) {
		// $L.D(this.TAG, "refreshWidget params :" + params);
    $L.D(this.TAG, "-----refreshWidget  come in ------ ");
		var newData;
		var requestparams = Object.cloneExtend(this.refreshWidget_default_params, params);
		if (requestparams.type == "activity") {
			newData = this.getMapData(params);
		} else if (requestparams.type == "shop") {
			newData = this.getShopMapData(params);
		} else if (requestparams.type == "coupon") {
			newData = this.getCouponData(params);
		}
    $L.D(this.TAG, "mapData tring  = " + Object.toJSON(newData));
		$O.postEvent("refershDataForWidget", Object.toJSON(newData), requestparams.target, "");
	},
	getMapData: function(params) {
    var requestparams = Object.cloneExtend(this.getShopMapData_default_params, params);
		var input = {};
		if (typeof params.sonid != "undefined" && params.sonid != 0) {
			input = {
				size: params.size,
				log: params.lontitude,
				lat: params.latitude,
				sonid: params.sonid,
				maptype: "baidu"
			};
		}
		if (typeof params.fatherid != "undefined" && params.fatherid != 0) {
			value = params.fatherid;
			input = {
				size: params.size,
				log: params.lontitude,
				lat: params.latitude,
				fatherid: params.fatherid,
				maptype: "baidu"
			};
		}

		var res = Ajax.callAPI("GetContentsShopsByActivitySorts/get", input, "1.0", true).evalJSON();
		
		var err = Ajax.checkError(res);
		if (err) return alert(err.errordes, "pecct://app/null");
		$O.postEvent("closeLoadingDianlog", null, null, null);
		// 数据容错处理
		if (!res.data || res.data == null || typeof res.data.error != "undefined") res = {
			ListByType: []
		};
		else res = res.data;
    if (res.errornum != "") {
    }
		$L.D(this.TAG, "res string  = " + Object.toJSON(res));
		// $L.D(this.TAG, "response string  = " + Object.toJSON(res));
		// var itemString = res.info;
    	var mapData = {
			"centerPointPosition": -1,
			mapDataList: []
		};
    $L.D(this.TAG, "requestparams  = " +  Object.toJSON(requestparams));
		if (requestparams.withCircle == "true") {
			var circleItem = {
				"color": requestparams.circleColor,
				"lineWidth": "3",
				"minute": requestparams.size / 100,
				"status": "1"
			}
			mapData.circleModel = circleItem
			mapData.zoom = requestparams.size
		}
		/*if (res.errornum != "") {
			$O.postEvent("closeLoadingDianlog", null, null, null);
			// return mapData;
		}*/
		$L.D(this.TAG, "res length: " + res.length);
    if (!res.errornum) {
  		res.each(function(obj) {
  			$L.D('JS-ActivityController', "obj string  = " + Object.toJSON(obj));
  			var newdata = {
  				ListByType: []
  			};
  			var mapItem = {
  				"id": obj.Info.id,
  				"title": obj.Info.title,
  				"address": obj.Info.address,
  				"longitude": obj.Info.baidu_longitude,
  				"latitude": obj.Info.baidu_latitude
  			};
  			mapData.mapDataList.push(mapItem);
  		});
    }
		return mapData;
	},
	//显示优惠列表
	showCouponListWithLocation: function(params) {
		$O.postEvent("showLoadingDialog", "加载中", "正在加载……", false);
		var p = $H(params);
		$O.postEvent("getLocation", "pecct://activity/showCouponList?" + p.toQueryString(), null, null);
	},
	showCouponList: function(params) {
		var newdata = this.getCouponData(params);
		var menuData = this.getCouponSecondaryMenuData(params);
		// var sortSecondaryMenu =  new ActivityController().couponSortSecondaryMenuList({'newdata':menuData,'ordername':params.ordername});
		// $L.D(this.TAG, "sortSecondaryMenu string  = " + Object.toJSON(sortSecondaryMenu));
		// var regionSecondaryMenu =  new ActivityController().defaultSecondaryMenuList(params);

		var newParams = {
			"tempSonid": params.sonid,
			"tempFatherid": params.fatherid,
			"tempOrdername": params.ordername,
			"couponParams": params,
			"couponInfo": newdata,
			"couponMenuData": menuData
			/*,
				"sortSecondaryMenu":sortSecondaryMenu,
				"regionSecondaryMenu":regionSecondaryMenu*/
		};
    $L.D(this.TAG, "showCouponList newParams  = " + Object.toJSON(newParams));
		$L.D(this.TAG, "newParams string  = " + Object.toJSON(newParams));
		$O.postEvent("openActivity", "", "page_shq", Object.toJSON(newParams), null);
		$O.postEvent("closeLoadingDianlog", null, null, null);
	},
	getCouponData_default_params: {
		"ordername": "id",
		"ordertype": "asc",
		"lastid": 0
	},
	getCouponData: function(params) {
		$L.D(this.TAG, "getCouponData Params string  = " + Object.toJSON(params));
		var input = {};
		var ordername;
		var ordertype;
		var requestparams = Object.cloneExtend(this.getCouponData_default_params, params);
		if (requestparams.ordername == "") {
			requestparams.ordername = "id"
		}
		$L.D(this.TAG, "getCouponData requestparams string  = " + Object.toJSON(requestparams));
		if (typeof params.sonid != "undefined" && params.sonid != 0) {
			input = {
				size: params.size,
				log: params.lontitude,
				lat: params.latitude,
				sonid: params.sonid,
				ordername: requestparams.ordername,
				ordertype: requestparams.ordertype,
				lastid: requestparams.lastid,
				maptype: "baidu"
			};
		}
		if (typeof params.fatherid != "undefined" && params.fatherid != 0) {
			// value = params.fatherid;
			input = {
				size: params.size,
				log: params.lontitude,
				lat: params.latitude,
				fatherid: params.fatherid,
				ordername: requestparams.ordername,
				ordertype: requestparams.ordertype,
				lastid: requestparams.lastid,
				maptype: "baidu"
			};
		}

		var res = Ajax.callAPI("GetContentsShopsByCouponSorts/get", input, "1.0", true).evalJSON();
		var err = Ajax.checkError(res);
		if (err) return alert(err.errordes, "pecct://app/null");
		$O.postEvent("closeLoadingDianlog", null, null, null);
		// 数据容错处理
		if (!res.data || res.data == null || typeof res.data.errornum != "undefined") res = {
			ListByType: []
		};
		else res = res.data;

		var mapData = {
			mapDataList: []
		};
		var newdata = {
			ListByType: []
		};

//    if (res.errornum == "") {
  		res.each(function(obj) {
  			$L.D(this.TAG, "obj string  = " + Object.toJSON(obj));
  			var item = {
  				title: obj.Info.title,
  				abstracts: obj.Info.abstract,
  				image_cover: obj.Info.image_cover,
  				id: obj.Info.id
  			};
  			newdata.ListByType.push(item);
  		});
//  }
		return newdata;
	},
	getShowActiviyListInMapData: function(params) {
		var mapData = this.getMapData(params);
		var newParams = {
			"mapData": mapData
		};
		return newParams;
	},
	activityAndShopList: function(params) {
		var data = {
			"control_id": "page_fragment_newslist_demo_list",
			"xtype": "ListViewWidget",
			"layout": "widget_listview_item_twoline_text.widget_listview_map",
			"position": [{
				"key": "parent",
				"value": "widget_map_popup_overlay_view_container_layout"
			}, {
				"key": "location",
				"value": "0"
			}, {
				"key": "insertType",
				"value": "1"
			}],
			"attr": [],
			"configs": [],
			"setEvent": [{
				"name": "itemClick",
				"params": [{
					"key": "activityId",
					"value": "{_widgetData.ListByType[position].id}",
					"defaultValue": ""
				}],
				"javascript": "new ActivityController().getActivityInfoById({'id':params.activityId})"
			}, {
				"name": "refresh",
				"params": [],
				"javascript": ""
			}, {
				"name": "loadMore",
				"id": "",
				"params": [{
					"key": "lastid",
					"value": "{_widgetData.ListByType[position].id}",
					"defaultValue": ""
				}],
				"javascript": ""
			}],
			"datasource": {
				"method": "",
				"params": [],
				"data": params.newdata
			}
		}
		return Object.toJSON(data);
	},
	getCouponSecondaryMenuData: function(params) {
		var _refresh = (typeof params._refresh != "undefined");
		var res = Ajax.callAPI("content.getsonsortlist", {
			sortid: params.secondaryMenuId
		}, "1.0", _refresh).evalJSON();
		// 数据容错处理
		if (!res.data || res.data == null || typeof res.data.error != "undefined") res = {
			menuModelList: []
		};
		else res = res.data;
		var newdata = {
			menuModelList: []
		};
		var allMenuItem = {
			title: "全部分类",
			id: params.fatherid
		};
		newdata.menuModelList.push(allMenuItem);
		res['SonSorList'].each(function(obj) {
			var menuItem = {
				title: obj.cnname,
				id: obj.id
			};
			newdata.menuModelList.push(menuItem);
		});
		return newdata;
	},
	putPageParams_default_params: {
		"sonid": "",
		"fatherid": "",
		"ordername": "id"
	},
	//根据条件，刷新优惠列表
	refreshCoupon: function(params) {
		var requestparams = Object.cloneExtend(this.putPageParams_default_params, params);

		var newData = {
			text: params.title
		}
		$L.D(this.TAG, "refreshCoupon requestparams = " + Object.toJSON(requestparams));
		$O.postEvent("refershDataForWidget", Object.toJSON(newData), "ec_button_widget", params.fatherWidgetId);

		/*if(requestparams.sonid == 0 || requestparams.sonid ==""){
			requestparams.sonid = requestparams.tempSonid
		}*/
		if (requestparams.fatherid == 0 || requestparams.fatherid == "") {
			requestparams.fatherid = requestparams.tempFatherid
		}

		if (requestparams.position == "[position(0)]") {
			requestparams.fatherid = requestparams.sonid
			// requestparams.sonid  = ""
		} else {
			if (requestparams.sonid != 0 || requestparams.sonid != "") {
				requestparams.fatherid = ""
			}
		}

		//临时处理的
		if (requestparams.sonid != 0 && requestparams.fatherid != 0) {
			requestparams.fatherid = requestparams.sonid
		}

		if (requestparams.ordername == "") {
			requestparams.ordername = requestparams.tempOrdername
		}

		$L.D(this.TAG, "refreshCoupon requestparams  sonid= " + requestparams.sonid);
		$L.D(this.TAG, "refreshCoupon requestparams  fatherid= " + requestparams.fatherid);
		this.putPageParams({
			"position": requestparams.position,
			"fatherid": requestparams.fatherid,
			"sonid": requestparams.sonid,
			"ordername": requestparams.ordername
		});
		// new ActivityController().putPageParams({\\\"ordername\\\":params.ordername});$O.postEvent(\\\"closePopupwindow\\\",null,null, null);new ActivityController().showMapWithLocation({\\\"sonid\\\":params.sortId,\\\"ordername\\\":params.ordername,\\\"ordertype\\\":\\\"asc\\\",\\\"type\\\":\\\"coupon\\\",\\\"target\\\":\\\"list_view_widget\\\"})
		$O.postEvent("closePopupwindow", null, null, null);
		this.showMapWithLocation({
			"sonid": requestparams.sonid,
			"fatherid": requestparams.fatherid,
			"ordername": requestparams.ordername,
			"ordertype": "asc",
			"type": "coupon",
			"target": "list_view_widget"
		})

	},
	putPageParams: function(params) {
		var newParams = {
			"sonid": params.sonid,
			"fatherid": params.fatherid,
			"ordername": params.ordername,
			"position": params.position
		};
		$O.postEvent("putPageParams", Object.toJSON(newParams), null, null);
	},
	showShopListInMap: function(params) {
		var res = this.getShopMapData(params);
		$O.postEvent("openActivity", "", "page_changguan", Object.toJSON(res), null);
	},
	getShopMapData_default_params: {
		"size": "30000",
		"maptype": "baidu",
		"withCircle": "false",
		"circleColor": "#20009933",
		"minute": "5",
		"sonid":"",
		"fatherid":""
	},
	getShopMapData: function(params) {
		var input = {};
		var requestparams = Object.cloneExtend(this.getShopMapData_default_params, params);
		input = {
			distance: requestparams.size,
			lon: params.lontitude,
			lat: params.latitude,
			sort_father_ids: requestparams.fatherid,
			sort_ids: requestparams.sonid,
			map_type: requestparams.maptype,
      page_size:300
		};
    $L.D(this.TAG, "-----getShopMapData  come in ------ ");
		var res = Ajax.callAPI("content/place/nearby_shops", input, "1.0.000001", true).evalJSON();
    $O.postEvent("closeLoadingDianlog", null, null, null);
//		var err = Ajax.checkError(res);
    $O.postEvent("checkError", Object.toJSON(res), null, null);
//		if (err) return alert(err.errordes, "pecct://app/null");
	
		// 数据容错处理
		if (!res.data || res.data == null || typeof res.data.error != "undefined") res = {
			ListByType: []
		};
		else res = res.data;
//		 $L.D(this.TAG, "res length  = " + res.length);
		// var itemString = res.info;
		var mapData = {
			"centerPointPosition": -1,
      "showSearchLayout":"false",
			mapDataList: []
		};
    $L.D(this.TAG, "requestparams  = " +  Object.toJSON(requestparams));
		if (requestparams.withCircle == "true") {
			var circleItem = {
				"color": requestparams.circleColor,
				"lineWidth": "3",
				"minute": requestparams.size / 100,
				"status": "1"
			}
			mapData.circleModel = circleItem
			mapData.zoom = requestparams.size
		}
     if (!res.errornum) {
  		res['shops'].each(function(obj) {
  			var mapItem = {
  				"id": obj.shop_id,
  				"title": obj.title,
  				"address": obj.address,
  				"longitude": obj.baidu_longitude,
  				"latitude": obj.baidu_latitude,
          "markIcon":""
  			};
        var value = obj.sorts.split("|");
        for (var i = 0; i < value.length; i++)
        {
            if (value[i] == "985")
            {
              mapItem.markIcon = "activity_map_location_mark_yuding";
            }
        }
  			mapData.mapDataList.push(mapItem);
  		});
    }
		return mapData;
	},
	getShopInfoById: function(params) {
		var res = Ajax.callAPI("GetContentsActivitiesByShops/get", {
			shopid: params.id
		}, "1.0", true).evalJSON();
		var err = Ajax.checkError(res);
		if (err) return alert(err.errordes, "pecct://app/null");
		// 数据容错处理
		if (!res.data || res.data == null || typeof res.data.error != "undefined") res = {
			ListByType: []
		};
		else res = res.data;

		if (typeof params.pageName == "undefined" || params.pageName == "undefined") {
			params.pageName = "page_shop_list_details";
		}

		var actionbardata = {
			'withActionBar': 'true',
			'withHomeItem': 'true',
			'withHomeAsUp': 'true',
			"actionBarBg": "test_actionbar_bg",
			'title': res.shop.title
		};
		var mapData = {
			'mapDataList': [{
				'address': res.shop.address,
				'longitude': res.shop.longitude,
				'latitude': res.shop.latitude,
				'popupItemString': "{#ecct://app/getJsonFile?fileName=assets/config/widget_map_list_demo.json}"
			}]
		}
		var mapParams = {
			'mapData': mapData
		};
		var groupString = {
			"groupList": [{
				"tableList": [{
					"tag": "new ActivityController().showMap({\\'address\\':\\'" + res.shop.address + "\\',\\'longitude\\':" + res.shop.longitude + ",\\'latitude\\':" + res.shop.latitude + "})",
					"title": res.shop.address
				}]
			}, {
				"tableList": [{
					"tag": "new AppController().callPhoneNumber({\\'number\\':" + res.shop.phone_num + "})",
					"title": res.shop.phone_num
				}]
			}]
		};
		$L.D(this.TAG, "getShopInfoById shop = " + Object.toJSON(res.shop));
		var newParams = {
			"contentInfo": res.shop,
			"groupString": groupString,
			"actionBarData": actionbardata
		};
		$O.postEvent("openActivity", "", params.pageName, Object.toJSON(newParams), null);
	},
	showMap: function(params) {
		var mapData = {
			'mapDataList': [{
        'title':params.title,
				'address': params.address,
				'longitude': params.longitude,
				'latitude': params.latitude
			}]
		}
		var mapParams = {
			'mapData': mapData
		};
		$O.postEvent("openActivity", "", "page_map", Object.toJSON(mapParams), null);
	},
	getActivityInfoByShop: function(params) {
		var groupString = {
			"groupList": [{
				"tableList": [{
					"tag": "pecct://app/alert?params=A",
					"title": "社会趣味比赛"
				}, {
					"tag": "pecct://app/alert?params=cccccccccccccc",
					"title": "趣味投篮比赛"
				}]
			}, {
				"tableList": [{
					"tag": "pecct://app/alert?params=cccccccccccccc",
					"title": "查看所有评论"
				}]
			}]
		};

		var newParams = {
			"groupString": groupString
		}

		$O.postEvent("openActivity", "", "page_changguan_details", Object.toJSON(newParams), null);
	},
	openVideo: function(params) {
		var videoUri = "http://qymhvideo.oss.aliyuncs.com/uploads/" + (params["type"] + "").underscore() + "/video_url/" + params.id + "/" + params.videoUri;
		$L.D(this.TAG, "videoUri  = " + videoUri);
		$O.openVideo(videoUri);
	},
	openQRCapture: function() {
		alert(0);
		$O.openQRCapture();
	},
  //
  openXiaoZhanDetails: function(params) {
      var input = {
      shopid:params.contentid
      };
      var res = Ajax.callAPI("ContentsCouponsByShops/get", input, "1.0", true).evalJSON();
      var orderErr = Ajax.checkError(res);
  		if (orderErr) return alert(orderErr.errordes, "pecct://app/null");
  //    $O.postEvent("closeLoadingDianlog", null, null, null);
  		res = res.data;
      var actionBartitle ={
        title:res['shop'].title
      } 
 
      var actListString = {
    		groupList: [{
  				tableList: [],
          sectionTitle:""
  			}]
		  };
      
      if(res['info'].length != 0){
          actListString.groupList[0].sectionTitle = "活动优惠";
        }
      res['info'].each(function(obj){
        var item = {
            id:obj.id,
  					title: obj.title
  				}
  			actListString.groupList[0].tableList.push(item);
  		});
      
      var itemString = {
        title:res['shop'].title,
        abstract:res['shop'].abstract,
        content:res['shop'].content,
        image_cover:res['shop'].image_cover
      }
     
      var contactString = {
      	groupList: [{
          sectionTitle:"联系方式",
  				tableList: [{
      			   title:"地址："+res['shop'].address
    				}
            ]
  			}]
		  };
      
      if(res['shop'].phone_num !=""){
        var item = {
          title:"电话："+res['shop'].phone_num
        }
        contactString.groupList[0].tableList.push(item);
      }
      
      //------------------------------//  
        
      var groupString = this.getChangGuanList(params);  
      var detailForm = {
        'actionBartitle':actionBartitle,
  			'contentid':params.contentid,
        'actListString':actListString,
        'contactString':contactString,
        'itemString':itemString,
        'groupString':groupString,
        'address':res['shop'].address,
        'baidu_latitude':res['shop'].baidu_latitude,
        'baidu_longitude':res['shop'].baidu_longitude,
        'title':res['shop'].title,
        'cms_sort_id':res['shop'].cms_sort_id,
        'phone_num':res['shop'].phone_num,
        'user_info_id':res['shop'].user_info_id
			}
		  $O.postEvent("openActivity", "" , params.pageName, Object.toJSON(detailForm));
  },
  getChangGuanList: function(params) {
//    alert(params.contentid);
     var input = {
      shop_id:params.contentid
      };
      var res = Ajax.callAPI("content/shops/products", input, "1.0.000001", true).evalJSON();
    	var orderErr = Ajax.checkError(res);
  		if (orderErr) return alert(orderErr.errordes, "pecct://app/null");
  //    $O.postEvent("closeLoadingDianlog", null, null, null);
  		res = res.data;
      
      var groupString = {
  			groupList: [{
  				tableList: [],
          sectionTitle:""
  			}]
		  };
    if(res['stadiums'].length != 0){
      groupString.groupList[0].sectionTitle = "在线预订";
    }
      res['stadiums'].each(function(obj){
      var item = {
  				id:obj.id,
					title: obj.title,
          tag:obj.price,
          expand:obj.gym_project,
          expand2:obj.stadium_type
				}
			groupString.groupList[0].tableList.push(item);
		});
    return groupString;
	},
	_e: null
});

