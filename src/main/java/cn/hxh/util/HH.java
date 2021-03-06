package cn.hxh.util;

import cn.hxh.common.Constants;

import java.io.File;
import java.time.LocalDateTime;

public class HH {
    private HH() {

    }

    public static void eraseArray(byte[] privacy) {
        if (privacy != null) {
            for (byte bt : privacy) {
                bt = -1;
            }
        }
    }

    public static String systemName() {
        return System.getProperty("os.name");
    }

    public static String workingDir() {
        return System.getProperty("user.dir");
    }

    public static String homeDir() {
        return System.getProperty("user.home");
    }

    public static String resourceFilePath(String filePath) {
        return resourceDir() + filePath;
    }

    public static String backupDir() {
        File backup = new File(HH.resourceFilePath("backup") + File.separator);
        if (!backup.exists()) {
            if (!backup.mkdirs()) {
                System.out.println("Failed to mkdir");
            }
        }
        return HH.resourceFilePath("backup") + File.separator;
    }

    public static String resourceDir() {
        String path = homeDir() + File.separator + Constants.USER_NAME + File.separator;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    public static String temporaryDir() {
        String path = homeDir() + File.separator + Constants.TEMPORARY + File.separator;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    public static String timeNow() {
        return LocalDateTime.now().withNano(0).toString();
    }

}
