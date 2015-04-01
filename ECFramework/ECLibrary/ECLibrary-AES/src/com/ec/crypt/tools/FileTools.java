package com.ec.crypt.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileTools {
	private static final String CHAR_SET = "utf-8";
	/**
	 * 将加密文件以字符串形式保存于文件
	 * @param file
	 * @param path
	 * @param fileName
	 */
	public static void Save(File file,String path,String fileName){
		File saveFile = new File(path+"/"+fileName);
		FileInputStream fileInputStream = null;
		BufferedReader reader = null;
		FileOutputStream fileOutputStream = null;
		BufferedWriter writer = null;
		
		try {
			
			fileInputStream = new FileInputStream(file);
			reader = new BufferedReader(new InputStreamReader(fileInputStream));
			
			fileOutputStream = new FileOutputStream(saveFile);
			writer = new BufferedWriter(new OutputStreamWriter(fileOutputStream, CHAR_SET));
			
			String temp = null;
			while((temp = reader.readLine())!=null){
				writer.write(temp);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fileInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 对文件原样处理
	 * @param file
	 * @param path
	 * @param fileName
	 */
	public static void SaveByOutput(File file,String path,String fileName){
		File saveFile = new File(path+"/"+fileName);
		FileInputStream fileInputStream = null;
		FileOutputStream fileOutputStream = null;
		try {
			
			fileInputStream = new FileInputStream(file);
			fileOutputStream = new FileOutputStream(saveFile);
			
			byte[] cache = new byte[1024];
			int readLen = 0;
			while((readLen = fileInputStream.read(cache))!=-1){
				fileOutputStream.write(cache, 0, readLen);
				fileOutputStream.flush();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fileInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
