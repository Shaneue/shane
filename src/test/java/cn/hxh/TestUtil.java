package cn.hxh;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.Base64;

public class TestUtil {
    public static String computeFileMd5(File file) throws Exception {
        MessageDigest digester = MessageDigest.getInstance("md5");
        byte[] bytes = new byte[1024];
        FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.read(bytes);
        digester.update(bytes);
        fileInputStream.close();
        byte[] md5 = digester.digest();
        return new String(Base64.getEncoder().encode(md5));
    }
}
