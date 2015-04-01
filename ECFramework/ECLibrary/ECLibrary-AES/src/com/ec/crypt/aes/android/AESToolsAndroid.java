package com.ec.crypt.aes.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
/**
 * Android AES Tools<br>
 * <ul>
 * <li>File enCrypt,deCrypt</li>
 * <li>Stream enCrypt,deCrypt</li>
 * <li>String enCrypt,deCrypt</li>
 * <li>byte[] enCrypt,deCrypt</li>
 * </ul>
 * @author DeveloperLee
 */
public class AESToolsAndroid {
	
	public static byte[] iv={0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};//length must 16
	/**
	 * 初始化Cipher，用来加密与解密<br>
	 * <ul>
	 * <li>传入秘钥</li>
	 * <li>传入密码模式</li>
	 * </ul>
	 * @param oriKey
	 * @param cipherMode
	 * @return Cipher
	 */
	public Cipher init(String oriKey,int cipherMode){
		KeyGenerator keyGenerator = null;
		Cipher cipher = null;
		try {
			keyGenerator = KeyGenerator.getInstance("AES");
			
			IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);  

			//在android端需要使用第二个参数 "Crypto"这样用
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG","Crypto");
			secureRandom.setSeed(oriKey.getBytes());
			
			keyGenerator.init(128, secureRandom);
			
			SecretKey secretKey = keyGenerator.generateKey();
			byte[] codeFormat = secretKey.getEncoded();
			
			SecretKeySpec key = new SecretKeySpec(codeFormat, "AES");
			
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(cipherMode, key,ivParameterSpec);
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} 
		return cipher;
	}
	/**
	 * 加密文件
	 * @param sourceFile 待加密文件
	 * @param fileType 加密临时文件的文件类型 
	 * @param oriKey 秘钥
	 * @return 已加密文件
	 */
	public File encryptFile(File sourceFile,String fileType,String oriKey){
		InputStream sourceStream = null;
		try {
			sourceStream = new FileInputStream(sourceFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return encryptStream(sourceStream,sourceFile.getName(),fileType,oriKey);
	}
	
	/**
	 * 加密Stream
	 * @param sourceStream 待加密Stream
	 * @param tempFileName 加密临时文件的文件名称,可任意命名
	 * @param fileType 加密临时文件的文件类型 
	 * @param oriKey 秘钥
	 * @return 已加密文件
	 */
	public File encryptStream(InputStream sourceStream,String tempFileName,String fileType,String oriKey){
		File encrypFile = null;
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try{
			inputStream  = sourceStream;
			encrypFile = File.createTempFile(tempFileName, fileType);
			outputStream = new FileOutputStream(encrypFile);
			Cipher cipher = init(oriKey,Cipher.ENCRYPT_MODE);
			
			CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher);
			
			byte[] cache = new byte[1024];
			int readLen = 0;
			while((readLen = cipherInputStream.read(cache))!=-1){
				outputStream.write(cache, 0, readLen);
				outputStream.flush();
			}
			
			cipherInputStream.close();
			
		}catch(FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				inputStream.close();
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return encrypFile;
	}
	
	/**
	 * 解密文件
	 * @param sourceFile 待解密文件
	 * @param fileType 解密临时文件的文件类型 
	 * @param oriKey 秘钥
	 * @return 已解密文件
	 */
	public File decryptFile(File sourceFile,String fileType,String oriKey){
		InputStream sourceStream = null;
		try {
			sourceStream = new FileInputStream(sourceFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return decryptFile(sourceStream,sourceFile.getName(),fileType,oriKey);
	}
	
	/**
	 * 解密Stream
	 * @param sourceFile 待解密Stream
	 * @param tempFileName 解密临时文件的文件名称,可任意命名
	 * @param fileType 解密临时文件的文件类型 
	 * @param oriKey 秘钥
	 * @return 已解密文件
	 */
	public File decryptFile(InputStream sourceStream,String tempFileName,String fileType,String oriKey){
		File decryptFile = null;
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			inputStream = sourceStream;
			decryptFile = File.createTempFile(tempFileName, fileType);
			outputStream = new FileOutputStream(decryptFile);
			
			Cipher cipher = init(oriKey, Cipher.DECRYPT_MODE);
			CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher);
			
			byte[] cache = new byte[1024];
			int readLen = 0;
			while((readLen = inputStream.read(cache)) >= 0){
				cipherOutputStream.write(cache, 0, readLen);
				cipherOutputStream.flush();
			}
			
			cipherOutputStream.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				inputStream.close();
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return decryptFile;
	}
	
	/**
	 * 加密字符串，需要cipher和待加密字符串
	 * @return cryptByte[] default value is null
	 */
	public byte[] encryptStr(Cipher cipher,String content){
		return encryptStr(cipher,content.getBytes());
	}
	
	/**
	 * 加密比特数组，需要cipher和待加密数组
	 * @return cryptByte[] default value is null
	 */
	public byte[] encryptStr(Cipher cipher,byte[] content){
		byte[] back=null;
		try {
			back = cipher.doFinal(content);
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return back;
	}
	/**
	 * 解密字符串，需要cipher和待解密字符串
	 * @return cryptByte[] default value is null
	 */
	public byte[] decryptStr(Cipher cipher,String content){
		return decryptStr(cipher,content.getBytes());
	}
	/**
	 * 解密比特数组，需要cipher和待解密数组
	 * @return cryptByte[] default value is null
	 */
	public byte[] decryptStr(Cipher cipher,byte[] content){
		byte[] back=null;
		try {
			back = cipher.doFinal(content);
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return back;
	}
	
}
