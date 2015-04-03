
// 控制器逻辑部分
SignController = Class.create({
  initialize: function() {
    this.name = "sign"
  },
  TAG: "JS-signController",
  userInfo: function() {
       var res = Ajax.callAPI("user.getuserinfo", {}, "1.0", true).evalJSON();
//      alert(Object.toJSON(res));
      $L.D(this.TAG, "loginform res string  = " + Object.toJSON(res));
      var err = Ajax.checkError(res);
  		if (err) return alert(err.errordes, "pecct://app/null");
  		res = res.data;
    
			userInfo = {
				'title': res.MyInfo.phone_number
			};
   
		return userInfo.title;
	},
  loginform: function() {
    var valid_status = this.getUsersBindStatus();
     var status = "";
      if("0" ==valid_status){
        status = "未验证";
      }else if("1" ==valid_status){
        status = "已验证";
      }else if("2" ==valid_status){
        status = "正在审核";
      }
		loginform = {
			'groupList': [{
				'tableList': [{
    				'title': '身份证验证',
						'tag': '',
            'customView':'group_item_version',
            'summary':status
					},{
						'title': '登录',
						'tag': 'pecct://user/loginPage'
					}, {
						'title': '用户注册',
						'tag': 'pecct://user/regPage'
					}

				]
			}]
		};
		$L.D(this.TAG, "loginform isLogin:" + $O.getPreference("isLogin"));
		if ("true" == $O.getPreference("isLogin")) {
			loginform = {
				'groupList': [{
					'tableList': [{
  					'title': '身份证验证',
						'tag': '',
            'customView':'group_item_version',
            'summary':status
					},{
						'title': '注销登录',
						'tag': 'pecct://user/logout'
					}]
				}]
			};
		};
   
		return loginform.groupList;
	},
  //查询用户验证状态
  getUsersBindStatus: function() {
       var res = Ajax.callAPI("user/users/bind_status", {}, "1.0", true).evalJSON();
//      alert(Object.toJSON(res));
      $L.D(this.TAG, "loginform res string  = " + Object.toJSON(res));
    	var err = Ajax.checkError(res);
  		if (err) return alert(err.errordes, "pecct://app/null");
  		res = res.data;
      //0或没有记录为未验证，2为正在审核，1为已验证
     
      if(res['sorts'].length == 0){
         valid_status = "0";
      }else{
        valid_status = res['sorts'][0]['valid_status'];
      }
           
         //保存验证状态
      $O.setPreference("valid_status",valid_status)
      return valid_status;
  },
  //检测用户验证状态
  checkUsersBindStatus: function() {
    var valid_status = 0
     if($O.getPreference("valid_status") == "0"){
        var valid_status = new SignController().getUsersBindStatus();
        if(valid_status == "0"){
            alert("\t\t\t\t\t\t\t\t提示\n\t未验证身份证。\n\t请先进入我的界面,验证身份证", "pecct://sign/redirectSFYZ")
        } 
    }else if($O.getPreference("valid_status") == "1"){
        valid_status = 1;
    }else if($O.getPreference("valid_status") == "2"){
        valid_status = 2;
        alert("工作人员正在审核您的身份证,请稍后！", "pecct://app/null");
    }
    return valid_status;
  },
  //跳转到身份验证前，判断是否登录
  redirectSFYZ: function(params){
      if ("true" == $O.getPreference("isLogin")) {
          if($O.getPreference("valid_status") == "0"){
            $O.postEvent("openActivity", "", "page_shengfenyangzheng", "", null);
          }else if($O.getPreference("valid_status") == "1"){
            alert("身份证已验证！", "pecct://app/null");
          }else if($O.getPreference("valid_status") == "2"){
            alert("工作人员正在审核您的身份证,请稍后！", "pecct://app/null");
          }
      }else{
          alert("请先登录", "pecct://user/isLogin?pageName=page_login");
      }
  },
  //刷新我的界面
  refreshWode : function(params){
    $O.postEvent("refershWidget","group_widget", "",null);
  },  
  finishNowActivity : function(params){
		$O.finishNowActivity("pecct://sign/refreshWode");
	},
	_e: null
});
