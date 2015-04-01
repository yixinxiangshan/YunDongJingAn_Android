package com.ecloudiot.framework.widget.model;

import java.io.Serializable;
import java.util.List;

public class ListviewModel implements Serializable {
	private static final long serialVersionUID = 4140010476873687658L;
	public List<ListviewitemTwolineModel> ListByType;

	public List<ListviewitemTwolineModel> getListByType() {
		return ListByType;
	}

	public void setListByType(List<ListviewitemTwolineModel> listByType) {
		ListByType = listByType;
	}
}
