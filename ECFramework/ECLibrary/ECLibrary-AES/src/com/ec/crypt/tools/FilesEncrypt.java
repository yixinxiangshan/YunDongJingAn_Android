package com.ec.crypt.tools;

import java.io.File;

import com.ec.crypt.aes.android.AESToolsAndroid;

public class FilesEncrypt {
	public static final int ENCRYPT = 0;
	public static final int DECRYPT = 1;
	
	private String cryptPth = null;
	private String rootPath = null;
	private FilesEncyptStateListener encyptStateListener = null;
	private File[] childFiles = null;
	
	private String oriKey="";
	
	public FilesEncrypt(String rootPath,String oriKey) {
		this.rootPath = rootPath;
		this.oriKey = oriKey;
	}
	
	public void start(int method){
		File rootFile = new File(rootPath);
		if(rootFile.isDirectory()){
			childFiles = new File(rootPath+"/").listFiles();
			if(encyptStateListener!=null){
				encyptStateListener.total(childFiles.length);
			}
			switch(method){
			case ENCRYPT:
				cryptPth = "EncryptFiles";
				makeEncryptDir();
				encryptFiles();
				break;
			case DECRYPT:
				cryptPth = "DecryptFiles";
				makeEncryptDir();
				decryptFiles();
				break;
			}
		}
	}
	
	private void encryptFiles(){
		AESToolsAndroid aesTools = new AESToolsAndroid();
		for(int i=0;i<childFiles.length;i++){
			if(encyptStateListener!=null){
				encyptStateListener.currentNum(i);;
			}
			String name = childFiles[i].getName();
			if(name.matches("\\w+.js")){
				File enFile = aesTools.encryptFile(childFiles[i], null, oriKey);
				FileTools.SaveByOutput(enFile, rootPath+"/"+cryptPth, name);
			}
		}
		if(encyptStateListener!=null){
			encyptStateListener.finish();
		}
	}
	
	private void decryptFiles(){
		AESToolsAndroid aesTools = new AESToolsAndroid();
		for(int i=0;i<childFiles.length;i++){
			if(encyptStateListener!=null){
				encyptStateListener.currentNum(i);;
			}
			String name = childFiles[i].getName();
			if(name.matches("\\w+.js")){
				File enFile = aesTools.decryptFile(childFiles[i], null, oriKey);
				FileTools.Save(enFile, rootPath+"/"+cryptPth, name);
			}
		}
		if(encyptStateListener!=null){
			encyptStateListener.finish();
		}
	}
	
	private final void makeEncryptDir(){
		File file = new File(rootPath+"/"+cryptPth);
		if(!file.exists()){
			file.mkdir();
		}
	}
	
	public void setListener(FilesEncyptStateListener encyptStateListener){
		this.encyptStateListener = encyptStateListener;
	}
}
