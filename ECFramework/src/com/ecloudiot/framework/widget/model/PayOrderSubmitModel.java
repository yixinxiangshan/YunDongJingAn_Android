package com.ecloudiot.framework.widget.model;

public class PayOrderSubmitModel {
	private String partner;
	private String seller;
	private String out_trade_no;
	private String subject;
	private String body;
	private float total_fee;
	private String notify_url;
	
	public String getPartner() {
		return partner;
	}
	public void setPartner(String partner) {
		this.partner = partner;
	}
	public String getSeller() {
		return seller;
	}
	public void setSeller(String seller) {
		this.seller = seller;
	}
	public String getOut_trade_no() {
		return out_trade_no;
	}
	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public float getTotal_fee() {
		return total_fee;
	}
	public void setTotal_fee(float total_fee) {
		this.total_fee = total_fee;
	}
	public String getNotify_url() {
		return notify_url;
	}
	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}
	
	

}
