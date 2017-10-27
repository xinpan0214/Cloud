package com.gvtv.android.cloud.util.crypto;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.gvtv.android.cloud.CloudApplication;

public class AESCrypto {
	Cipher ecipher;
	Cipher dcipher;
	String mPassword = null;
	public final static int SALT_LEN = 8;
	byte[] mInitVec = null;
	byte[] mSalt = null;
	Cipher mEcipher = null;
	Cipher mDecipher = null;

	/**
	 * AES加密 Input a string that will be md5 hashed to create the key.
	 * 
	 * @return void, cipher initialized
	 */

	public AESCrypto() {
		try {
			SecretKeySpec skey = new SecretKeySpec(
					// 此处需要注意：不能使用系统自带的.getBytes().因为其跟系统相关，在字节及位数处理上存在差异
					// 256位密码即KEY的长度是256位，不足256位的需要以0补齐
					CloudApplication.aeskey.getBytes(),
					"AES");//旧代码
			this.setupCrypto(skey);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public AESCrypto(String key) throws NoSuchAlgorithmException,
			InvalidKeySpecException {
		SecretKeySpec skey = new SecretKeySpec(getMD5(key), "AES");
		this.setupCrypto(skey);
	}

	public byte[] getSalt() {
		return (mSalt);
	}

	public byte[] getInitVec() {
		return (mInitVec);
	}

	private void setupCrypto(SecretKey key) throws NoSuchAlgorithmException,
			InvalidKeySpecException {
		try {
			ecipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			dcipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			ecipher.init(Cipher.ENCRYPT_MODE, key);
			dcipher.init(Cipher.DECRYPT_MODE, key);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Buffer used to transport the bytes from one stream to another
	byte[] buf = new byte[1024];
	public void encrypt(InputStream in, OutputStream out) {
		try {
			// Bytes written to out will be encrypted
			out = new CipherOutputStream(out, ecipher);
			int numRead = 0;
			while ((numRead = in.read(buf)) >= 0) {
				out.write(buf, 0, numRead);
			}
			out.close();
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 00
	 * 
	 * 
	 * Input is a string to encrypt.
	 * 
	 * @return a Hex string of the byte array
	 * @throws Exception
	 */
	public byte[] encrypt(String plaintext) throws Exception {
		try {
			byte[] ciphertext = ecipher.doFinal(plaintext.getBytes());
			return ciphertext;
			//return byteToHex(ciphertext);
		} catch (Exception e) {
			throw new Exception("加密种子URL异常");
		}

	}

	public void decrypt(InputStream in, OutputStream out) {
		try {
			// Bytes read from in will be decrypted
			in = new CipherInputStream(in, dcipher);
			// Read in the decrypted bytes and write the cleartext to out
			int numRead = 0;
			while ((numRead = in.read(buf)) >= 0) {
				out.write(buf, 0, numRead);
			}
			out.close();
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Input encrypted String represented in HEX
	 * 
	 * @return a string decrypted in plain text
	 */
	public String decrypt(String hexCipherText) {
		try {
			String plaintext = new String(
					dcipher.doFinal(hexToByte(hexCipherText)));
			return plaintext;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String decrypt(byte[] ciphertext) {
		AESCrypto encrypter = new AESCrypto();
		try {
			String plaintext = new String(encrypter.dcipher.doFinal(ciphertext));
			return plaintext;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static byte[] getMD5(String input) {
		try {
			byte[] bytesOfMessage = input.getBytes("UTF-8");
			MessageDigest md = MessageDigest.getInstance("MD5");
			return md.digest(bytesOfMessage);
		} catch (Exception e) {
			return null;
		}
	}

	static final String HEXES = "0123456789ABCDEF";

	public static String byteToHex(byte[] raw) {
		if (raw == null) {
			return null;
		}
		final StringBuilder hex = new StringBuilder(2 * raw.length);
		for (final byte b : raw) {
			hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(
					HEXES.charAt((b & 0x0F)));
		}
		return hex.toString();
	}

	public static byte[] hexToByte(String hexString) {
		int len = hexString.length();
		byte[] ba = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			ba[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character
					.digit(hexString.charAt(i + 1), 16));
		}
		return ba;
	}

	/**
	 * @throws Exception
	 *             加密
	 * @Title: AesCrypto
	 * @param url
	 * @return
	 * @return String
	 * @throws
	 */
	
	 public static byte[] aesCrypto(String url) throws Exception {
		 try {
			 AESCrypto encrypter = new AESCrypto(); 
			 return encrypter.encrypt(url); 
		 } catch (UnsupportedEncodingException e) {
			 throw new Exception("加密出错", e); 
		 }
	 }
	 
	/** 解密
	 * 
	 * @Title: AesDecryption
	 * 
	 * @param url
	 * 
	 * @return
	 * 
	 * @return String
	 * 
	 * @throws
	 */
	public static String aesDecryption(String url) {
		AESCrypto encrypter = new AESCrypto();
		url = encrypter.decrypt(url);
		return url;
	}
	
}
