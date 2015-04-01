package com.ec.crypt.aes.java;

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
 * Java AES Tools
 * @author DeveloperLee
 */
public class AESTools {

	public static String oriKey = "12345678901234567890";
	public static byte[] iv={0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};//length must 16
	public Cipher init(String oriKey,int cipherMode){
		KeyGenerator keyGenerator = null;
		Cipher cipher = null;
		try {
			keyGenerator = KeyGenerator.getInstance("AES");
			IvParameterSpec zeroIv = new IvParameterSpec(iv);
			//Java plateform
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
			secureRandom.setSeed(oriKey.getBytes());
			
			keyGenerator.init(128, secureRandom);
			
			SecretKey secretKey = keyGenerator.generateKey();
			byte[] codeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(codeFormat, "AES");
			
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			
			cipher.init(cipherMode, key,zeroIv);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
		return cipher;
	}
	
	public File encryptFile(File sourceFile,String fileType,String oriKey){
		File encrypFile = null;
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try{
			inputStream  = new FileInputStream(sourceFile);
			encrypFile = File.createTempFile(sourceFile.getName(), fileType);
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
	
	public File decryptFile(File sourceFile,String fileType,String oriKey){
		File decryptFile = null;
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			decryptFile = File.createTempFile(sourceFile.getName(), fileType);
			Cipher cipher = init(oriKey, Cipher.DECRYPT_MODE);
			inputStream = new FileInputStream(sourceFile);
			outputStream = new FileOutputStream(decryptFile);
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
	 * 
	 * @param cipher
	 * @param content
	 * @return cryptByte[]
	 */
	public byte[] encryptStr(Cipher cipher,String content){
		return encryptStr(cipher,content.getBytes());
	}
	
	/**
	 * 
	 * @param cipher
	 * @param content
	 * @return cryptByte[]
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
	
	public byte[] decryptStr(Cipher cipher,String content){
		return decryptStr(cipher,content.getBytes());
	}
	
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
