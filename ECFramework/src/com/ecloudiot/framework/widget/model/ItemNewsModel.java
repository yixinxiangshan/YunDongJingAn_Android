package com.ecloudiot.framework.widget.model;


public class ItemNewsModel {

	private String title = "";
	private String image_cover = "";
	private String abstracts = "";
	private String content = "";
	private String timeString = "";
	private String expandString = "";
	private String id = "";
	private boolean clickable = true;
	private String commentTag;
	private String withImage = "true";
	private String type = "";
	private String vote_all="";
	private String vote_count;
	
	public String getAbstracts() {
		return abstracts;
	}

	public void setAbstracts(String abstracts) {
		this.abstracts = abstracts;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getImage_cover() {
		return image_cover;
	}

	public void setImage_cover(String image_cover) {
		this.image_cover = image_cover;
	}


	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	public String getTimeString() {
		return timeString;
	}

	public void setTimeString(String timeString) {
		this.timeString = timeString;
	}

	public String getExpandString() {
		return expandString;
	}

	public void setExpandString(String expandString) {
		this.expandString = expandString;
	}
	
	public String getCommentTag() {
		return commentTag;
	}
	public void setCommentTag(String commentTag) {
		this.commentTag = commentTag;
	}

	public String getWithImage() {
		return withImage;
	}

	public void setWithImage(String withImage) {
		this.withImage = withImage;
	}

	public boolean isClickable() {
		return clickable;
	}

	public void setClickable(boolean clickable) {
		this.clickable = clickable;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVote_all() {
		return vote_all;
	}

	public void setVote_all(String vote_all) {
		this.vote_all = vote_all;
	}

	public String getVote_count() {
		return vote_count;
	}

	public void setVote_count(String vote_count) {
		this.vote_count = vote_count;
	}

	
}
