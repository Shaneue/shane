package cn.hxh.util.file;

import cn.hxh.util.HH;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class JarIOUtil {
    private static Logger log = LoggerFactory.getLogger(JarIOUtil.class);

    public static void copyToShane(String filePath) throws Exception {
        File file = new File(HH.resourceDir() + filePath);
        if (file.exists()) {
            log.info("Already exists and does not be copied: " + filePath);
            return;
        }
        Resource resource = new ClassPathResource(filePath);
        InputStream is = resource.getInputStream();
        FileOutputStream fos = new FileOutputStream(file);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = is.read(bytes)) > 0) {
            fos.write(bytes, 0, length);
        }
        is.close();
        fos.close();
    }
}
