package encryption;

import java.security.*; 
import javax.crypto.Cipher; 
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey; 
import javax.crypto.spec.SecretKeySpec;



/** 

 * 字符串工具集合 

 * @author Liudong 

 */ 

public class AES
{
	//private static final String PASSWORD_CRYPT_KEY = "__jDlog_"; 
	//private final static String DES = "AES"; 
	
	/** 
	 * 加密 
	 * @param src 数据源 
	 * @param key 密钥，长度必须是8的倍数 
	 * @return  返回加密后的数据 
	 * @throws Exception 
	 */ 
	public static byte[] encrypt(byte[] src, byte[] password)throws Exception
	{ 
		 KeyGenerator kgen = KeyGenerator.getInstance("AES");  
         kgen.init(128, new SecureRandom(password));  
         SecretKey secretKey = kgen.generateKey();  
         byte[] enCodeFormat = secretKey.getEncoded();  
         SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");  
         Cipher cipher = Cipher.getInstance("AES");// 创建密码器   
         byte[] byteContent = src;  
         cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化   
         byte[] result = cipher.doFinal(byteContent);  
         return result;
     }
	
     /** 
     * 解密 
     * @param src 数据源 
     * @param key 密钥，长度必须是8的倍数 
     * @return   返回解密后的原始数据 
     * @throws Exception 
     */ 
     public static byte[] decrypt(byte[] src, byte[] password)throws Exception
     {
    	 KeyGenerator kgen = KeyGenerator.getInstance("AES");  
         kgen.init(128, new SecureRandom(password));  
         SecretKey secretKey = kgen.generateKey();  
         byte[] enCodeFormat = secretKey.getEncoded();  
         SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");              
         Cipher cipher = Cipher.getInstance("AES");// 创建密码器   
         cipher.init(Cipher.DECRYPT_MODE, key);// 初始化   
         byte[] result = cipher.doFinal(src);  
         return result; 
     } 

     /** 
      * 密码解密 
      * @param data 
      * @return 
      * @throws Exception 
      */ 
     public final static String decrypt(String data, String key)
     { 
    	 try
    	 { 
    		 return new String(decrypt(hex2byte(data.getBytes()), key.getBytes())); 
    	 }
    	 catch(Exception e)
    	 { 
    	 } 
    	 return null; 
     } 

     /** 
      * 密码加密 
      * @param password 
      * @return 
      * @throws Exception 
      */ 
     public final static String encrypt(String text, String password)
     { 
    	 try
    	 { 
    		 return byte2hex(encrypt(text.getBytes(),password.getBytes()));
    	 }
    	 catch(Exception e)
    	 { 
    	 } 
    	 return null; 
     } 

     /** 
      * 二行制转字符串 
      * @param b 
      * @return 
      */ 
     public static String byte2hex(byte[] b)
     { 
        String hs = ""; 
        String stmp = ""; 
        for (int n = 0; n < b.length; n++)
        { 
            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF)); 
            if (stmp.length() == 1) 
                hs = hs + "0" + stmp; 
            else 
                hs = hs + stmp; 
        } 
        return hs.toUpperCase(); 
     } 

     public static byte[] hex2byte(byte[] b)
     { 
    	 if((b.length%2)!=0) 
    		 throw new IllegalArgumentException("长度不是偶数"); 
    	 byte[] b2 = new byte[b.length/2]; 
    	 for (int n = 0; n < b.length; n+=2)
    	 { 
    		 String item = new String(b,n,2); 
    		 b2[n/2] = (byte)Integer.parseInt(item,16); 
    	 } 
    	 return b2;
     }
}