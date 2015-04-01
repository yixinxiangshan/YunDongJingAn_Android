package com.ecloudiot.framework.widget.model;
import java.io.Serializable;
import java.util.List;

public class GridModel implements Serializable{
	private static final long serialVersionUID = 4140010476873687658L;
	public List<GriditemModel> ListByType;
	public List<GriditemModel> getListByType() {
		return ListByType;
	}
	public void setListByType(List<GriditemModel> listByType) {
		ListByType = listByType;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
