package com.ecloudiot.framework.utility;


import android.os.Parcel;
import android.os.Parcelable;

public class FilePath implements Parcelable {
	private String localFilePaths;
	private String netFilePaths;

	public FilePath(){
		
	}

	public String getLocalFilePaths() {
		return localFilePaths;
	}

	public void setLocalFilePaths(String localFilePaths) {
		this.localFilePaths = localFilePaths;
	}

	public String getNetFilePaths() {
		return netFilePaths;
	}

	public void setNetFilePaths(String netFilePaths) {
		this.netFilePaths = netFilePaths;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(localFilePaths);
		dest.writeString(netFilePaths);
	}

	public static final Parcelable.Creator<FilePath> CREATOR = new Parcelable.Creator<FilePath>() {
		public FilePath createFromParcel(Parcel in) {
			return new FilePath(in);
		}

		public FilePath[] newArray(int size) {
			return new FilePath[size];
		}
	};
	private FilePath(Parcel in) {
         localFilePaths = in.readString();
         netFilePaths = in.readString();
     }
}
