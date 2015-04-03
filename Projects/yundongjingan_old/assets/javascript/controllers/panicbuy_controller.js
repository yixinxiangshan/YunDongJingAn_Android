
// 控制器逻辑部分
Panicbuycontroller = Class.create({
  initialize: function() {
    this.name = "panicbuy"
  },
	TAG: "JS-PanicbuyController",
	//标准版显示优惠业务
	getcontentinfo: function(params) {
		$O.postEvent("showLoadingDialog", "加载中", "正在加载……", false);
		$L.D(this.TAG, "couponid = " + params.contentid);
		var res = Ajax.callAPI("GetContentsShopsByCouponid/get", {
			couponid: params.contentid
		}, "1.0", true).evalJSON();
		var err = Ajax.checkError(res);
		if (err) return alert(err.errordes, "pecct://app/null");

		// 数据容错处理
		if (!res.data || res.data == null || typeof res.data.error != "undefined") res = {
			ListByType: []
		};
		else res = res.data;
		$L.D(this.TAG, "response string  = " + Object.toJSON(res));
		var actionbarString = {
			"withActionBar": "true",
			"withHomeItem": "true",
			"homeIcon": "",
			"actionBarBg": "",
			"title": res.Info.title,
			"withHomeAsUp": "true"
		};
		var panicbuyString = {
			"title": res.Info.title,
			"imageName": res.Info.image_cover,
			"summary": res.Info.abstract,
			"content": res.Info.content,
			"validStartTime": res.Info.apply_start_time,
			"validEndTime": res.Info.apply_end_time
		};
		var countString = {
			"timeString": res.Info.end_time
		};
		var cNum = res.Info.apply_code;
		$L.D(this.TAG, "couponOrderNum = " + cNum);
		var t = null;
		var bg = null;
		var cAble = null;
   //1：申领未开始
   //2：申领中
  //3：申领结束
  //4：该票已申领完
		if (cNum == "1") {
			t = "申领未开始";
			bg = "widget_panic_buy_applybt_inactive";
			cAble = "false";
		}else if (cNum == "2") {
  		t = "开始申请";
			bg = "widget_panic_buy_applybt_active_bg";
			cAble = "true";
		}else if (cNum == "3") {
    	t = "申领结束";
			bg = "widget_panic_buy_applybt_inactive";
			cAble = "false";
		}else if (cNum == "4") {
      t = "该票已申领完";
			bg = "widget_panic_buy_applybt_inactive";
			cAble = "false";
		} else {
			t = "优惠码：" + cNum;
			bg = "widget_panic_buy_applybt_inactive";
			cAble = "false";
		};
		var buttonString = {
			"text": t,
			"background": bg,
			"clickable": cAble
		};
		$L.D(this.TAG, "clickable = " + cAble);
		var startTime = /\d{4}-\d{1,2}-\d{1,2}/g.exec(res.Info.apply_start_time);
		var endTime = /\d{4}-\d{1,2}-\d{1,2}/g.exec(res.Info.apply_end_time);
		var groupString = {
			"groupList": [{
				"tableList": [{
					"tag": "",
					"title": startTime + "---" + endTime
				}]
			}]
		};

		if (res.Shop) {
			res.Shop.each(function(obj) {
				if (obj.baidu_longitude != "" && obj.baidu_latitude != "") {
					var tabItem = {
						"tableList": [{
							"tag": "pecct://activity/showMap?title="+res.Info.title+"&address=" + encodeURIComponent(obj.address) + "&longitude=" + obj.baidu_longitude + "&latitude=" + obj.baidu_latitude + "",
							"title": obj.address
						}]
					}
					groupString.groupList.push(tabItem);
				}
			});
		}



		var newParams = {
			"actionbarString": actionbarString,
			"panicbuyString": panicbuyString,
			"countString": countString,
			"buttonString": buttonString,
			"contentId": res.Info.id,
			"groupString": groupString
		};
		$O.postEvent("openActivity", "", "page_youhui_detail", Object.toJSON(newParams));
		$O.postEvent("closeLoadingDianlog", null, null, null);
	},
	//商户端验证优惠显示
	showCouponByInfo: function(params) {
		$O.postEvent("showLoadingDialog", "加载中", "正在加载……", false);
		var actionbarString = {
			"withActionBar": "true",
			"actionBarBg": "test_actionbar_bg",
			"title": params.title
		};
		var panicbuyString = {
			"title": params.title,
			"imageName": params.imageName,
			"summary": params.summary,
			"content": params.content,
			"validStartTime": params.validStartTime,
			"validEndTime": params.validEndTime
		};
		var countString = {
			"timeString": params.end_time
		};
		var cNum = params.couponOrderNum;
		$L.D(this.TAG, "couponOrderNum = " + cNum);
		var t = null;
		var bg = null;
		var cAble = null;

		t = "优惠码：" + cNum;
		bg = "widget_panic_buy_applybt_inactive";
		cAble = "false";

		var buttonString = {
			"text": t,
			"background": bg,
			"clickable": cAble
		};
		$L.D(this.TAG, "clickable = " + cAble);

		var newParams = {
			"actionbarString": actionbarString,
			"panicbuyString": panicbuyString,
			"countString": countString,
			"buttonString": buttonString
		};
		$O.postEvent("openActivity", "", "page_youhui_detail", Object.toJSON(newParams));
		$O.postEvent("closeLoadingDianlog", null, null, null);
	},
	getCouponInfo: function(params) {
		var res = Ajax.callAPI("content.getcontentinfo", {
			contentid: params.contentid
		}, "1.0", true).evalJSON();
		var err = Ajax.checkError(res);
		if (err) return alert(err.errordes, "pecct://app/null");

		// 数据容错处理
		if (!res.data || res.data == null || typeof res.data.error != "undefined") res = {
			ListByType: []
		};
		else res = res.data;
		var newParams = {
			'contentid': params.contentid
		};
		var cNum = res.ContentInfo.couponOrderNum;
		$L.D(this.TAG, "couponOrderNum = " + cNum);
		$O.postEvent("openActivity", "", "page_widget_listview_expand_yundong", Object.toJSON(newParams));
	},
	//判断是优惠还是乐动小站
	checkIsCoupon: function(params) {
		var newParams = {
			'contentid': params.contentid
		}
		if (params.cmsSortId == "481") {
			$O.postEvent("openActivity", "", "page_widget_listview_expand_kecheng", Object.toJSON(newParams));
		} else {
			this.getcontentinfo(params);
		}
	},
	//静安运动申领优惠业务
	showapplycoupon: function(params) {
		$L.D(this.TAG, "contentid = " + params.contentid);
		var applyres = Ajax.callAPI("activity.applycoupon", {
			contentid: params.contentid
		}, "1.0", true);
		$L.D(this.TAG, "applycouponinfo:" + applyres);
		applyres = applyres.evalJSON();
		var couNum = "0"
		if (applyres.data && applyres.data['Success']) { 
			couNum = "优惠码：" + applyres.data['Success'].code
			var newParams = {
				'contentid': params.contentid
			};
			$O.postEvent("openActivity", "", "page_widget_listview_expand_yundong", Object.toJSON(newParams));

		} else if (applyres.data && applyres.data['error']) {
			// couNum = applyres.data['error'].errordes
      var errorMsg = "购买课程失败！";
      if(applyres.data['error'].errornum == "100315"){
        errorMsg = "您购买课程的数量已经超过限制了！";
      }
      if(applyres.data['error'].errornum == "100314"){
        errorMsg = "该课程已被购完！";
      }
			alert(errorMsg, "pecct://app/null");
		}
	},
	//标准版申领优惠业务
	applyCoupon: function(params) {
		$L.D(this.TAG, "contentid = " + params.contentid);
		var applyres = Ajax.callAPI("activity.applycoupon", {
			contentid: params.contentid
		}, "1.0", true);
		$L.D(this.TAG, "applycouponinfo:" + applyres);
		applyres = applyres.evalJSON();
		var couNum = "0"
		if (applyres.data && applyres.data['Success']) {
			couNum = "优惠码：" + applyres.data['Success'].code
		} else if (applyres.data && applyres.data['error']) {
			couNum = applyres.data['error'].errordes
		}
		var newData = {
			text: couNum,
			background: "widget_panic_buy_applybt_inactive",
			clickable: "false"
		}
		$O.postEvent("refershDataForWidget", Object.toJSON(newData), "ec_button_widget", "");

	},
	//标准版倒计时结束刷新抢购按钮状态
	couponfinished: function() {
		var newData = {
			text: "抢购结束",
			background: "widget_panic_buy_applybt_inactive",
			clickable: "false"
		}
		$O.postEvent("refershDataForWidget", Object.toJSON(newData), "ec_button_widget", "");
	},
	_e: null
});
