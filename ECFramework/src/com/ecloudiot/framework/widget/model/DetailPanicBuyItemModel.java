package com.ecloudiot.framework.widget.model;

public class DetailPanicBuyItemModel {
	private String id;
	private String title;
	private String summary;
	private String imageName;
	private String imageTag;
	private String applyStartTime;
	private String applyEndTime;
	private String applyBtTag;
	private String content;
	private String validStartTime;
	private String validEndTime;
	private String couponNum;
	private String _hascomment = "";
	private String _hasshare = "";
	private String _hasup = "";
	private String commentTag;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String get_hascomment() {
		return _hascomment;
	}
	public void set_hascomment(String _hascomment) {
		this._hascomment = _hascomment;
	}
	public String get_hasshare() {
		return _hasshare;
	}
	public void set_hasshare(String _hasshare) {
		this._hasshare = _hasshare;
	}
	public String get_hasup() {
		return _hasup;
	}
	public void set_hasup(String _hasup) {
		this._hasup = _hasup;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	public String getImageTag() {
		return imageTag;
	}
	public void setImageTag(String imageTag) {
		this.imageTag = imageTag;
	}
	public String getApplyStartTime() {
		return applyStartTime;
	}
	public void setApplyStartTime(String applyStartTime) {
		this.applyStartTime = applyStartTime;
	}
	public String getApplyEndTime() {
		return applyEndTime;
	}
	public void setApplyEndTime(String applyEndTime) {
		this.applyEndTime = applyEndTime;
	}
	public String getApplyBtTag() {
		return applyBtTag;
	}
	public void setApplyBtTag(String applyBtTag) {
		this.applyBtTag = applyBtTag;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getValidStartTime() {
		return validStartTime;
	}
	public void setValidStartTime(String validStartTime) {
		this.validStartTime = validStartTime;
	}
	public String getValidEndTime() {
		return validEndTime;
	}
	public void setValidEndTime(String validEndTime) {
		this.validEndTime = validEndTime;
	}
	public String getCouponNum() {
		return couponNum;
	}
	public void setCouponNum(String couponNum) {
		this.couponNum = couponNum;
	}
	public String getCommentTag() {
		return commentTag;
	}
	public void setCommentTag(String commentTag) {
		this.commentTag = commentTag;
	}
}
