package cn.hxh.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Base64;

public class Privacy {

    public static String encrypt(byte[] source, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] prefixSource = prefixArray(source);
        SecretKey secretKey = md5(key);
        HH.eraseArray(key);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] rt = cipher.doFinal(prefixSource);
        HH.eraseArray(prefixSource);
        return new String(Base64.getEncoder().encode(rt));
    }

    public static byte[] decrypt(String source, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKey secretKey = md5(key);
        HH.eraseArray(key);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(new byte[16]));
        byte[] text = cipher.doFinal(Base64.getDecoder().decode(source.getBytes()));
        byte[] real = new byte[text.length - 16];
        System.arraycopy(text, 16, real, 0, text.length - 16);
        HH.eraseArray(text);
        return real;
    }

    private static byte[] prefixArray(byte[] source) {
        byte[] rt = new byte[source.length + 16];
        System.arraycopy(source, 0, rt, 16, source.length);
        HH.eraseArray(source);
        return rt;
    }


    private static SecretKey md5(byte[] key) throws Exception {
        MessageDigest digester = MessageDigest.getInstance("md5");
        digester.update(key);
        return new SecretKeySpec(digester.digest(), "AES");
    }
}