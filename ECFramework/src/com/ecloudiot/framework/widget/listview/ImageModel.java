package com.ecloudiot.framework.widget.listview;

public class ImageModel {
	private boolean _clickable = false;
	private String imageType = "";
	private String imageSize = "";
	private String imageSrc = "";

	public boolean is_clickable() {
		return _clickable;
	}
	public void set_clickable(boolean _clickable) {
		this._clickable = _clickable;
	}
	public String getImageType() {
		return imageType;
	}
	public void setImageType(String imageType) {
		this.imageType = imageType;
	}
	public String getImageSize() {
		return imageSize;
	}
	public void setImageSize(String imageSize) {
		this.imageSize = imageSize;
	}
	public String getImageSrc() {
		return imageSrc;
	}
	public void setImageSrc(String imageSrc) {
		this.imageSrc = imageSrc;
	}
}
