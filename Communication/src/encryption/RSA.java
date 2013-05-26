package encryption;

import java.security.Key;
import javax.crypto.Cipher;

import encoder.BASE64Decoder;
import encoder.BASE64Encoder;

/**
 * Created with IntelliJ IDEA.
 * User: weijinshi
 * Date: 5/17/13
 * Time: 10:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class RSA {

    public static String encrypt(String source, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);  // 这里用私钥加密
        byte[] b = source.getBytes();
        byte[] b1 = cipher.doFinal(b);
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(b1);
    }

    public static String decrypt(String cryptograph, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] b1 = decoder.decodeBuffer(cryptograph);
        byte[] b = cipher.doFinal(b1);
        return new String(b);
    }

}
