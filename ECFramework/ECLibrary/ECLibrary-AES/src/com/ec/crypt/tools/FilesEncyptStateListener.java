package com.ec.crypt.tools;

public interface FilesEncyptStateListener {
	/**
	 * start from 0
	 * @param total
	 */
	public void total(int total);
	public void currentNum(int currentNum);
	public void finish();
}
