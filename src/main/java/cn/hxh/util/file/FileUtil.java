package cn.hxh.util.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.FileSystemUtils;

import java.io.*;

@Slf4j
public class FileUtil {

    private FileUtil() {

    }

    public static void writeOut(String filePath, String content) {
        BufferedWriter bw = null;
        try {
            try {
                bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "utf-8"));
                bw.write(content);
            } finally {
                if (bw != null) {
                    bw.flush();
                    bw.close();
                }
            }
        } catch (IOException ioe) {
            log.error("Error of writing out", ioe);
        }
    }

    public static String readFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return "";
        }
        long length = file.length();
        byte[] bytes = new byte[(int) length];
        try (FileInputStream fis = new FileInputStream(file)) {
            int tmp = fis.read(bytes);
            if (tmp != length) {
                throw new Exception();
            }
            return new String(bytes);
        } catch (Exception e) {
            log.error("Error of reading file", e);
            return "";
        }
    }


    public static void deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            if (!file.delete()) {
                log.error("Error of deleting file");
            }
        }
    }

    public static void deleteDirRecursively(String dir) {
        if (!FileSystemUtils.deleteRecursively(new File(dir))) {
            log.error("Error of deleting directory");
        }
    }
}
