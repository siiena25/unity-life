package core;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by kirillf on 10/12/16.
 */

public class CryptoUtils {
    private static final String MD5 = "MD5";

    public static String md5(String s) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(MD5);
        byte[] bytes = digest.digest(s.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString();
    }
}
