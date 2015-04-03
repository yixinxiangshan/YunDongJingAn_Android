// 控制器逻辑部分
OrderController = Class.create({
  initialize: function() {
    this.name = "order"
  },
  TAG: "JS-OrderController",
  //选择预订日期刷新事件
  dateButtonClick: function(params) 
  {
//       var newParams = { 
//      	"title": params.title,
//        "selectDate":params.selectDate,
//        "eventType":"refresh"
//  		};
//  		$O.postEvent("putPageParams", Object.toJSON(newParams), null, null);
    	var newData = {
	    	text: params.title
  		}
  		$O.postEvent("refershDataForWidget", Object.toJSON(newData), "ec_button_widget","");
      if(params.type == "yyg"){
        this.reserve(params);
      }else{
        this.refreshYmqReserve(params);
      }
      $O.postEvent("closePopupwindow", null, null, null);
  },
	//游泳馆预订
	reserve: function(params) {
    var shiduanListString = { ListByType:[] };
    var shichangListString = { ListByType:[] };
    var renshuListString = { ListByType:[] };
    var date = this.GetDate(0)
    if(params.selectDate){
      date = params.selectDate;
    }
    var input = {
      date  : date,
      product_id:params.productId
    };
    var res = Ajax.callAPI("jing_an_yd/content/products/with_option_info", input, "1.0", true).evalJSON();
		var orderErr = Ajax.checkError(res);
		if (orderErr) return alert(orderErr.errordes, "pecct://app/null");
//    $O.postEvent("closeLoadingDianlog", null, null, null);
		res = res.data;
    
    res['packages'].each(function(obj){
			var item = {
				title : obj.name,
				id : obj.price_id,
  			expandString : obj.price
			};
			shiduanListString.ListByType.push(item);
		});
    
    res['times'].each(function(obj){
  		var item = {
				title : obj.name
			};
			shichangListString.ListByType.push(item);
		});
    
    res['people_num'].each(function(obj){
    	var item = {
				title : obj.name
			};
			renshuListString.ListByType.push(item);
		});
    
    var title1 = res['packages'][0].name
    var title2 = res['times'][0].name
    var title3 = res['people_num'][0].name
    
    var yygPrice = res['packages'][0].price
    var price_old = res['packages'][0].price_old
    var price_id = res['packages'][0].price_id
    
    
    var orderDetails = {
        ListByType: [{
            abstracts:yygPrice+"元",
            expandString:" /原价：<font color=\"#787878\">"+price_old+"元</font>",
            title: title1,
            timeString: title3+"人运动"+title2+"分钟"
        }
  ]
  };
    
     var groupString = {
  		"groupList": [{
        "tableList": [{
					"tag": "pecct://app/null",
					"title": "时段及套餐",
          'customView':'group_item_version',
          'summary': ""
				},{
  				"tag": "pecct://app/null",
					"title": "运动时长",
          'customView':'group_item_version',
          'summary': ""
				},{
  				"tag": "pecct://app/null",
					"title": "人数",
          'customView':'group_item_version',
          'summary': ""
				}]
			}]
		};
    
    if(params.eventType == "refresh"){
      var newParams = {
      	"date": params.title,
        "cDate":params.selectDate,
        "shiduanListString":shiduanListString,
        "shichangListString":shichangListString,
        "renshuListString":renshuListString,
        "title1": title1,
      	"title2": title2,
  			"title3": title3,
        //游泳馆单价
        "yygPrice":yygPrice,
        "price_id":price_id
  		};
  		$O.postEvent("putPageParams", Object.toJSON(newParams), null, null);
      this.refreshYygReserve();
    }else{
         var actionbardata = {
      	  'title': params.title
    		};
        var menuData = {
    			menulList: []
    		};
    		
        for (var i=0;i<6;i++)
        {
          var allMenuItem = {
        		title: this.GetDateStr(i)+"  "+this.GetDayStr(i),
      			id: this.GetDate(i)
      		};
          menuData.menulList.push(allMenuItem);
        }
        
        var currentDate = {
          text:this.GetDateStr(0)+"  "+this.GetDayStr(0)
        }
        var newParams = {
          "cDate":this.GetDate(0),
          "actionbardata": actionbardata,
      		"couponMenuData": menuData,
          "groupString": groupString,
          "orderDetails": orderDetails,
          "currentDate": currentDate,
          "project":params.project,
          "title": params.title,
          "address": params.address,
          "title1": title1,
      		"title2": title2,
    			"title3": title3,
          //游泳馆现价
          "yygPrice":yygPrice,
          //游泳馆原价
          "price_old":price_old,
          "price_id":price_id,
          //提交订单参数
          "cms_sort_id": params.cms_sort_id,
      		"phone_num": params.phone_num,
          "user_info_id":params.user_info_id,
          "productId":params.productId,
          "shiduanListString":shiduanListString,
          "shichangListString":shichangListString,
          "renshuListString":renshuListString
    		};
    	  $O.postEvent("openActivity", "", "page_changguan_reserve",Object.toJSON(newParams), null);
    }
	},
  //羽毛球预订
	ymqReserve: function(params) {
    var actionbardata = {
			'title': params.title
		};
    
    var menuData = {
  		menulList: []
		};
		
    for (var i=0;i<6;i++)
    {
      var allMenuItem = {
    		title: this.GetDateStr(i)+"  "+this.GetDayStr(i),
        id: this.GetDate(i)
  		};
      menuData.menulList.push(allMenuItem);
    }
     var currentDate = {
      text:this.GetDateStr(0)+"  "+this.GetDayStr(0)
    }

     var orderDetails = {
         ListByType: [{
            content: "",
            title: ""
        }]
    };     
    
//    alert(this.GetDate(0));
    
     var newParams = {
      "cDate":this.GetDate(0),
      "actionbardata": actionbardata,
      "project":params.project,
      "title":params.title,
      "address":params.address,
      "currentDate": currentDate,
      "menuData": menuData,
      "orderDetails": orderDetails,
      //提交订单参数
      "cms_sort_id": params.cms_sort_id,
    	"phone_num": params.phone_num,
      "user_info_id":params.user_info_id,
      "productId":params.productId
     }
     $O.postEvent("openActivity", "", "page_yumaoqiu_reserve",Object.toJSON(newParams), null);
  },
  //刷新羽毛球预订
  refreshYmqReserve: function(params) {
    //清空订单缓存的全局数据
     var date = this.GetDate(0)
    if(params.selectDate){
      date = params.selectDate;
    }
    var newParams = {
      "listTempValue": "",
      "num": "0",
      "tPrice":"",
      "cDate":date,
  	};
		$O.postEvent("putPageParams", Object.toJSON(newParams), null, null);
    //清空订单详细内容数据
    var orderDetails = {
            abstracts:"",
            content: ""
    };
    $O.postEvent("refershDataForWidget", Object.toJSON(orderDetails),"item_news_widget", "");
    //刷新场地
    $O.postEvent("refershWidget","matrix_widget", "",null);
  },
  //刷新游泳馆预订
  refreshYygReserve: function(params) {
    //刷新订单详细内容为默认数据
     $O.postEvent("refershWidget","list_view_widget", "",null);
    //
    $O.postEvent("refershWidget","group_widget", "",null);
  },
  //羽毛球场地预定 item点击后刷新界面事件处理
  ymqMatrixItemCheck: function(params) {
  var num = 1;
    var tempvalue = "";
	  var tempJson = {};
	  var price = params.price;
	  if(typeof params.num != "undefined" && params.num !="" && params.num !=null){
	       num= parseInt(params.num)+1;
	  }

	  var tempItem = params.rowVlaue+"|"+params.columnVlaue+"|"+price+"|"+params.price_id;
	  var tempKey = params.rowVlaue+params.columnVlaue
	   if(params.checked == "true"){
	     if(params.listTempValue && typeof params.num != "undefined"){
	         tempJson=eval('('+params.listTempValue+')');  
	         tempJson[tempKey] = tempItem;
	     }else{
	         tempJson[tempKey] =tempItem
	     }
	   }else{
	   	tempJson=eval('('+params.listTempValue+')');  
	    tempJson[tempKey] = tempItem;
	     delete tempJson[tempKey];
	     num= parseInt(params.num)-1;
	   }
	    var tPrice = 0
	    var i=0;   
	    for(var key in tempJson){  
          var item =  tempJson[key];
          var value = item.split("|");
	  			var temp = value[0]+"场地  "+value[1]+" "+" <font color=\"red\">"+value[2]+"元</font>"; 
          i = parseInt(i)
          if(i==0){                   
             tempvalue = temp;
             tPrice = parseFloat(value[2]);
          }else if((i % 2)!=0){
             tempvalue = tempvalue+"\t\t\t"+temp;
             tPrice = tPrice +parseFloat(value[2]);
          }else{
             tempvalue = tempvalue+"<br/>"+temp; 
             tPrice = tPrice +parseFloat(value[2]);
          }
          i=parseInt(i)+1 ;
	    } 
	   
	    var totalPrice="";
		if(JSON.stringify(tempJson)!="{}"){
		  totalPrice = "总价：<font  color=\"red\">"+tPrice+"</font>元";
		  } 
	     var orderDetails = {
	            abstracts:totalPrice,
	            expandString: tempvalue
	    };
	    var newParams = {
	      "listTempValue": Object.toJSON(tempJson),
	      "num": num,
	      "tPrice":tPrice
	    };
	    $O.postEvent("putPageParams", Object.toJSON(newParams), null, null);
	    $O.postEvent("refershDataForWidget", Object.toJSON(orderDetails),"item_news_widget", "");
  },
  //时段及套餐、运动时长、人数 item点击后刷新界面事件处理
  itemClick: function(params) {
    var title1 = params.title1;
    var title2 = params.title2;
    var title3 = params.title3;
    
    $O.postEvent("closeDialog",null, null,null);
    
    var time = parseFloat(title2);
    var num = parseFloat(title3);

    var price = parseFloat(time/60*num*params.yygPrice);
    var price_old = parseFloat(time/60*num*params.price_old);
//    alert(time/60*num);
    var orderDetails = {
        pullable:false,
        ListByType: [{
            abstracts:price+"元",
            expandString:" /原价：<font color=\"#787878\">"+price_old+"元</font>",
            title: title1,
            timeString: title3+"人运动"+title2+"分钟"
        }
    ]
    };
     var newParams = {
  		"title1": title1,
			"title2": title2,
			"title3": title3,
      "orderDetails": params.orderDetails,
      //游泳馆单价
      "yygPrice":params.yygPrice,
      //游泳馆总价
      "price":price,
      "price_id":params.price_id
      
		};
    $O.putPageParams( Object.toJSON(newParams));
//		$O.postEvent("putPageParams", Object.toJSON(newParams), null, null);
    $O.postEvent("refershWidget","group_widget", "",null);
    //订单详细刷新有问题，有待修改
//     $O.postEvent("refershWidget","list_view_widget", "",null);
    $O.postEvent("refershDataForWidget", Object.toJSON(orderDetails),"list_view_widget", "");
  },
  order_default_params: {
		"renshu": "0"
	},
  //确认订单
  orderConfirm: function(params) 
  {
         //购买产品列表
      var productList = [] ;

      var check_time;
      if(params.date == ""){
          params.date = this.GetDateStr(0)+"  "+this.GetDayStr(0);
          check_time = this.GetDate(0);
      }
      check_time = new Date().getFullYear() + "-"+params.date.substring(0,2) + "-"+params.date.substring(3,5)
      params.date = new Date().getFullYear() + "年"+params.date.substring(0,6)
      
//      alert(check_time);
      var tempvalue = "";
      var title,abstracts;
      // var listTempValue = params.listTempValue;
      var jsonobj=eval('('+params.listTempValue+')');
      var listSize;
      if(params.type == "yyg"){
        title = params.shiduan;
        abstracts = params.renshu+"人运动   "+params.shichang+"分钟";
        tempvalue = params.shiduan+"<br/>"+params.renshu+"人运动   "+params.shichang;
        var product = {
            "product_id":parseInt(params.productId), 
            "price_id":parseInt(params.price_id),
            "num":1,
            "price":null 
          }
        productList.push(product);
      }else{
            var i=0;   
  	    for(var key in jsonobj){  
		            var item =  jsonobj[key];
		            var value = item.split("|");
		  			var temp = value[0]+"场地  "+value[1]+" "+" <font color=\"red\">"+value[2]+"元</font>"; 
		            if(i=="0"){                   
		               tempvalue = temp;
		            }else{
		               tempvalue = tempvalue+"<br/>"+temp; 
		            }
	                var product = {
                        "product_id":parseInt(params.productId), 
                        "price_id":parseInt(value[3]) ,
                        "num":1,
                        "price":null 
                      }
                    productList.push(product);

		            i=parseInt(i)+1 ;
		    } 

         title = Object.keys(jsonobj).length+"片场地";   
         abstracts = tempvalue;   
      }
         var details = {
        ListByType: [{
            title: "运动项目："+params.project
        },{
            title: "场馆名称："+params.title
        },{
            title: "场馆地址："+params.address
        },{
            title: "预订日期："+params.date
        },{
            title: "订单详情："+title,
            expandString:abstracts
        }
    ]
    };
    
    
    var price = {
      "footText3":"总计需要支付：",
      "footText4":""+params.tPrice+"元",
      "withHeader":"false",
      "withFoot":"true",
      "withContent":"true"
    };
    var requestparams = Object.cloneExtend(this.order_default_params, params);
    var newParams = {
      "orderDetails": details,
      "price": price,
       //订单需要的参数
      "sort_id": params.cms_sort_id,
      "title":params.title,
      "address":params.address,
      "user_consignee_id": params.user_info_id,
      "product_num":listSize,
      "product_price":params.tPrice,
      "must_price":params.tPrice,
      "phone":params.phone_num,
      "people_num" : requestparams.renshu,
      "check_time" :check_time,
      "product_items" :productList,
      "tempvalue":tempvalue,
      "type":params.type,
      "gym_project":params.project
		};
    
	  $O.postEvent("openActivity", "", "page_confirm_order",Object.toJSON(newParams), null);
    
  },
  redirectCommitOrder: function(params){
    
      if ("true" == $O.getPreference("isLogin")) {
          var p = $H(params);
          alert("\t\t\t\t\t\t\t\t提示\n\n\t1.请在15分钟内完成支付,超时系统强释放你选择的场地。\n\t2.请仔细核对你的预订信息,场地预订一旦购买成功,不退不换。\n", "pecct://order/commitOrder?" + p.toQueryString());
      }else{
          alert("请先登录", "pecct://user/isLogin?pageName=page_login");
      }
    

  },
  //提交订单
  commitOrder: function(params) 
  {
    //因为URL重定向后，将空格转义成“+”，所有需要将加号替换成空格
    params.tempvalue =params.tempvalue.replace(/\+/g,' ')
  
    var extended_json = {
      orderDetails:params.tempvalue,
      gym_project:params.gym_project
    }
       
    $O.postEvent("showLoadingDialog", "提交中", "正在提交信息……", false);
    input = {
			  	 //分类标识
          sort_id : params.sort_id,
          //订单标题
          title : params.title,
          //订单详细
          extended_json:Object.toJSON(extended_json),
          //支付方式
          payment_type : 1,
          //送货方式
          send_type  : 1,
          //收货人信息id
          user_consignee_id  : params.user_consignee_id,
          //运费
          fare_price : 0,
          //购买产品列表
          product_items : params.product_items,
          //产品总数量
          product_num : params.product_num,
          //产品总价
          product_price : params.product_price,
          //应付金额
          must_price : params.must_price,
          //备注
          remarks : "",
          //订单状态
          act_status_type_id : 4,
          //用户名
          user_name :"",
          //电话
          phone:params.phone,
          //人数
          people_num : params.people_num,
          //预计到点时间
          about_time :"",
          //入住时间
          check_time  :params.check_time,
          //离店时间
          departure_time :""
			};
      
      var method;
      
      if(params.type == "yyg"){
        method = "trade/orders/add_option_order";
      }else{
         method = "trade/orders/add_lock_order";
      }
      
      var res = Ajax.callAPI(method, input, "1.0", true).evalJSON();
      $L.D(this.TAG, "res string  = " + Object.toJSON(res));
  		var err = Ajax.checkError(res);
  		if (err) return alert(err.errordes, "pecct://app/null");
//  		 alert(Object.toJSON(res));
  		// 数据容错处理
  		if (res.data == null){
        $O.postEvent("closeLoadingDianlog", null, null, null);
    	  alert("订单提交失败！", "pecct://app/null");
  		}else{
//        alert("\t\t\t\t\t\t\t\t预订成功！\n\n\t请在我的订单里查看预订验证码\n", "pecct://app/null");
//          {\"partner\":\"\",\"seller\":\"\",\"out_trade_no\":\"36245236\",\"subject\":\"慕斯蛋糕-【金典巧克力慕斯】;房间二;房间一;\",\"body\":\"慕斯蛋糕-【金典巧克力慕斯】;房间二;房间一;\",\"total_fee\":9120,\"notify_url\":\"\"}
          var detailInput = {
          act_buy_order_id  : res.data.buy_order_id
        	};
          var orderRes = Ajax.callAPI("trade/order/detail", detailInput, "1.0.000001", true).evalJSON();
        		var orderErr = Ajax.checkError(orderRes);
        		if (orderErr) return alert(orderErr.errordes, "pecct://app/null");
            $O.postEvent("closeLoadingDianlog", null, null, null);
        		orderRes = orderRes.data;
          var payString = {
            partner:"",
            seller:"",
            out_trade_no:orderRes.ContentInfo.order_number,
            subject:params.title,
            body:params.title,
            total_fee:params.must_price,
            notify_url:""
          };
   
          $O.postEvent("submitPay", Object.toJSON(payString),null, null);
  		} 
  },
  
  //我的订单详细
  myOrderDetails: function(params) {
    input = {
          act_buy_order_id  : params.id
		};
    var res = Ajax.callAPI("trade/order/detail", input, "1.0.000001", true).evalJSON();
//      alert(Object.toJSON(res));
      $L.D(this.TAG, "res string  = " + Object.toJSON(res));
  		var err = Ajax.checkError(res);
  		if (err) return alert(err.errordes, "pecct://app/null");
      $O.postEvent("closeLoadingDianlog", null, null, null);
      
  		res = res.data;
    var jsonobj=eval('('+res.ContentInfo.json_property+')');  
    var orderDetails = {
        ListByType: [{
            title: "运动项目："+jsonobj.gym_project
        },{
            title: "场馆名称："+res.ContentInfo.title
        },{
            title: "预订时间："+res.ContentInfo.check_time
        },{
            title: "订单详情：",
            expandString: jsonobj.orderDetails
        },{
            title: "订单总额："+res.ContentInfo.must_price+"元"
        },{
            title: "订单日期："+res.ContentInfo.created_at
        }
    ]
    };

    //"title":"订单编号：123123123","content":"订单状态：已支付"
    var status="未支付";
    if(res.ContentInfo.act_status_type_id == "5"){
       status = "已支付";
    }
    var itemNews = {
      title:"订单编号："+res.ContentInfo.order_number,
      content:"订单状态："+status
    }
  
    var newParams = {
      "orderDetails": orderDetails,
      "itemNews":itemNews,
      //提交立即支付所需参数
      "act_status_type_id":res.ContentInfo.act_status_type_id,
      "order_number":res.ContentInfo.order_number,
      "subject":res.ContentInfo.title,
      "must_price":res.ContentInfo.must_price,
      "orderId":params.id
		};
	  $O.postEvent("openActivity", "", "page_list_order_details",Object.toJSON(newParams), null);
	},
  
  //获取我的订单列表
  getMyOrderList: function(params) {
    input = {
          act_status_type_id  : '4,5'
  	};
    var res = Ajax.callAPI("trade/orders/search", input, "1.0", true).evalJSON();
//      alert(Object.toJSON(res));
      $L.D(this.TAG, "res string  = " + Object.toJSON(res));
  		var err = Ajax.checkError(res);
  		if (err) return alert(err.errordes, "pecct://app/null");
      $O.postEvent("closeLoadingDianlog", null, null, null);
  		res = res.data;
      var orderList = { ListByType:[] };
      res['items'].each(function(obj){
    		var item = {
          title:obj.title,
  				abstracts : obj.order_number,
          expandString : "",
  				id : obj.id,
    			timeString : obj.created_at_
  			};
        if(obj.act_status_type_id == "5"){
          item.expandString = "已支付";
        }else{
          item.expandString = "未支付";
        }
  			orderList.ListByType.push(item);
  		});
      
       var newParams = {
      "orderList": orderList
  	};
    
     $O.postEvent("openActivity", "", "page_list_order",Object.toJSON(newParams), null);
  },
  //立即支付
  pay: function(params) {
    var payString = {
            partner:"",
            seller:"",
            out_trade_no:params.order_number,
            subject:params.subject,
            body:params.subject,
            total_fee:params.must_price,
            notify_url:""
          };
   
    $O.postEvent("submitPay", Object.toJSON(payString),null, null);
  },
  //重定向删除订单
  redirectRremoveOrder: function(params) {
     var p = $H(params);
      $O.showConfirm("\t\t\t\t\t\t\t\t提示\n\n\t您确定要取消这个订单吗？\n", "pecct://order/removeOrder?"+p.toQueryString(), "pecct://app/null");
  },
  //删除订单
  removeOrder: function(params) {
    input = {
          buy_order_id  : params.orderId
    };
    var res = Ajax.callAPI("trade/orders/destroy", input, "1.0", true).evalJSON();
//      alert(Object.toJSON(res));
  		var err = Ajax.checkError(res);
  		if (err) return alert(err.errordes, "pecct://app/null");
//      $O.postEvent("closeLoadingDianlog", null, null, null);
      if(res.data){
        alert("订单取消成功");
        $O.postEvent("closeActivity", "pecct://order/refreshOrderListView", null, null);
      }else{
        alert("订单取消失败");
      }  
  },
  //刷新订单列表
  refreshOrderListView: function(params) {
     $O.postEvent("refershWidget","list_view_widget", "",null);
  },
  //获取指定天数后的日期
  GetDateStr: function(AddDayCount) 
  { 
    var dd = new Date(); 
  	dd.setDate(dd.getDate()+AddDayCount);//获取AddDayCount天后的日期 
  	var y = dd.getYear(); 
  	var m = (dd.getMonth()+1)<10?"0"+(dd.getMonth()+1):(dd.getMonth()+1);//获取当前月份的日期，不足10补0
  	var d = dd.getDate()<10?"0"+dd.getDate():dd.getDate(); //获取当前几号，不足10补0
  	return m+"月"+d+"日"; 
  },
  //获取指定天数后的指定格式日期
  GetDate: function(AddDayCount) 
  { 
    var dd = new Date(); 
    dd.setDate(dd.getDate()+AddDayCount);//获取AddDayCount天后的日期 
  	var y = dd.getYear(); 
  	var m = (dd.getMonth()+1)<10?"0"+(dd.getMonth()+1):(dd.getMonth()+1);//获取当前月份的日期，不足10补0
  	var d = dd.getDate()<10?"0"+dd.getDate():dd.getDate(); //获取当前几号，不足10补0
  	return new Date().getFullYear() + "-"+m+"-"+d; 
  },
   //获取指定天数后的星期
  GetDayStr: function(AddDayCount) 
  { 
    var dd = new Date(); 
    dd.setDate(dd.getDate()+AddDayCount);//获取AddDayCount天后的日期 
    var myday=dd.getDay()//注:0-6对应为星期日到星期六 
    var xingqi 
    switch(myday) 
    { 
    case 0:xingqi="星期日";break; 
    case 1:xingqi="星期一";break; 
    case 2:xingqi="星期二";break; 
    case 3:xingqi="星期三";break; 
    case 4:xingqi="星期四";break; 
    case 5:xingqi="星期五";break; 
    case 6:xingqi="星期六";break; 
    default:xingqi="系统错误！" 
    } 
    return xingqi;
  },
	_e: null
});
//在数组中获取指定值的元素索引
Array.prototype.getIndexByValue= function(value)
  {
      var index = -1;
      for (var i = 0; i < this.length; i++)
      {
          if (this[i] == value)
          {
              index = i;
              break;
          }
      }
      return index;
  }
