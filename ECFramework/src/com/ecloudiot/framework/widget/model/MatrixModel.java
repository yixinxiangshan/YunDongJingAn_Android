package com.ecloudiot.framework.widget.model;
import java.util.List;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.StringUtil;

public class MatrixModel {
	private final static String TAG = "MatrixModel";
	private List<CoordinateItem> xCoordList;
	private List<CoordinateItem> yCoordList;
	private List<MatrixItemModel> checkableList;
	private List<MatrixItemModel> nullList;
	
	public class MatrixItemModel{
		private int id;
		private String name;
		private int x = -1;
		private int y = -1;
		private String xValue;
		private String yValue;
		private String price;
		private String expand0;
		private String expand1;
		
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getX(MatrixModel matrixModel) {
			if (x<=0 && StringUtil.isNotEmpty(getXValue())) {
				for (int i = 0; i <  matrixModel.xCoordList.size(); i++) {
					CoordinateItem xItem = matrixModel.xCoordList.get(i);
					if (xItem.getValue().equalsIgnoreCase(getXValue())) {
						x = i;
						break;
					}
				}
			}
			return x;
		}
		public int getX() {
			if (x<=0 && StringUtil.isNotEmpty(getXValue())) {
				for (int i = 0; i <  MatrixModel.this.xCoordList.size(); i++) {
					CoordinateItem xItem = MatrixModel.this.xCoordList.get(i);
					if (xItem.getValue().equalsIgnoreCase(getXValue())) {
						x = i;
						break;
					}
				}
			}
			return x;
		}
		public void setX(int x) {
			this.x = x;
		}
		
		public int getY() {
			return y;
		}
		public int getY(MatrixModel matrixModel) {
			if (y<=0 && StringUtil.isNotEmpty(getYValue())) {
				for (int i = 0; i <  matrixModel.getYCoordList().size(); i++) {
					CoordinateItem yItem = matrixModel.getYCoordList().get(i);
					if (yItem.getValue().equalsIgnoreCase(getYValue())) {
						y = i;
						break;
					}
				}
			}
			return y;
		}
		public void setY(int y) {
			this.y = y;
		}
		public String getXValue() {
			return xValue;
		}
		public void setxValue(String xValue) {
			this.xValue = xValue;
		}
		public String getYValue() {
			return yValue;
		}
		public void setyValue(String yValue) {
			this.yValue = yValue;
		}
		public String getPrice() {
			return price;
		}
		public void setPrice(String price) {
			this.price = price;
		}
		public String getExpand0() {
			return expand0;
		}
		public void setExpand0(String expand0) {
			this.expand0 = expand0;
		}
		public String getExpand1() {
			return expand1;
		}
		public void setExpand1(String expand1) {
			this.expand1 = expand1;
		}
		
	}
	public class CoordinateItem{
		private int id;
		private String value;
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
	}
	
	public List<CoordinateItem> getXCoordList() {
		return xCoordList;
	}
	public void setxCoordList(List<CoordinateItem> xCoordList) {
		this.xCoordList = xCoordList;
	}
	public List<CoordinateItem> getYCoordList() {
		return yCoordList;
	}
	public void setyCoordList(List<CoordinateItem> yCoordList) {
		this.yCoordList = yCoordList;
	}
	public List<MatrixItemModel> getCheckableList() {
		return checkableList;
	}
	public void setCheckableList(List<MatrixItemModel> checkableList) {
		this.checkableList = checkableList;
	}
	public List<MatrixItemModel> getNullList() {
		return nullList;
	}
	public void setNullList(List<MatrixItemModel> nullList) {
		this.nullList = nullList;
	}
	public MatrixItemModel getMatrixItemModel(String xValue,String yValue) {
		if (StringUtil.isEmpty(yValue) || StringUtil.isEmpty(xValue)) {
			return null;
		}
		for (MatrixItemModel itemModel : getCheckableList()) {
			LogUtil.d(TAG, "xValue:"+itemModel.getXValue()+",xValue:"+itemModel.getYValue());
			if (xValue.equalsIgnoreCase(itemModel.getXValue()) && yValue.equalsIgnoreCase(itemModel.getYValue())) {
				return itemModel;
			}
		}
		return null;
	}
	
}
