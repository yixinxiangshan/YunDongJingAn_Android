package com.ecloudiot.framework.widget.model;

public class CartModel {
	private String title;
	private String subtilte;
	private String buttonBg;
	private String btTag; 
	private String footText1; 
	private String footText2; 
	private String footText3; 
	private String footText4; 
	private boolean withHeader;
	private boolean withContent = true;
	private boolean withFoot;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSubtilte() {
		return subtilte;
	}
	public void setSubtilte(String subtilte) {
		this.subtilte = subtilte;
	}
	public String getButtonBg() {
		return buttonBg;
	}
	public void setButtonBg(String buttonBg) {
		this.buttonBg = buttonBg;
	}
	public String getBtTag() {
		return btTag;
	}
	public void setBtTag(String btTag) {
		this.btTag = btTag;
	}
	public String getFootText1() {
		return footText1;
	}
	public void setFootText1(String footText1) {
		this.footText1 = footText1;
	}
	public String getFootText2() {
		return footText2;
	}
	public void setFootText2(String footText2) {
		this.footText2 = footText2;
	}
	public String getFootText3() {
		return footText3;
	}
	public void setFootText3(String footText3) {
		this.footText3 = footText3;
	}
	public String getFootText4() {
		return footText4;
	}
	public void setFootText4(String footText4) {
		this.footText4 = footText4;
	}
	public boolean isWithHeader() {
		return withHeader;
	}
	public void setWithHeader(boolean withHeader) {
		this.withHeader = withHeader;
	}
	public boolean isWithFoot() {
		return withFoot;
	}
	public void setWithFoot(boolean withFoot) {
		this.withFoot = withFoot;
	}
	public boolean isWithContent() {
		return withContent;
	}
	public void setWithContent(boolean withContent) {
		this.withContent = withContent;
	}

}
