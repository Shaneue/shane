package cn.hxh.util.file;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
    private ZipUtil() {
    }

    public static boolean zip(File file, String targetPath) {
        try {
            ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(targetPath));
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                zipRecursively(zipOut, "", files);
            } else {
                zipRecursively(zipOut, "", file);
            }
            zipOut.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private static void zipRecursively(ZipOutputStream out, String path, File... sourceFiles) throws Exception {
        byte[] buf = new byte[1024];
        for (File file : sourceFiles) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                String basePath = path + file.getName() + File.separator;
                ZipEntry zipEntry = new ZipEntry(basePath);
                zipEntry.setTime(file.lastModified());
                out.putNextEntry(zipEntry);
                zipRecursively(out, basePath, files);
            } else {
                FileInputStream in = new FileInputStream(file);
                ZipEntry zipEntry = new ZipEntry(path + file.getName());
                zipEntry.setTime(file.lastModified());
                out.putNextEntry(new ZipEntry(zipEntry));
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.closeEntry();
            }
        }
    }

    public static void unZipFiles(File zipFile, String descDir) throws Exception {
        File destination = new File(descDir);
        if (!destination.exists() && !destination.mkdirs()) {
            throw new Exception("Directory does not exist.");
        }
        ZipFile zip = new ZipFile(zipFile);
        for (Enumeration entries = zip.entries(); entries.hasMoreElements(); ) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            if (entry.getName().endsWith(File.separator)) {
                String zipEntryName = entry.getName();
                String path = descDir + File.separator + zipEntryName;
                new File(path).mkdirs();
                System.out.println(path);
            } else {
                String zipEntryName = entry.getName();
                InputStream in = zip.getInputStream(entry);
                String pathOfUpZipped = descDir + File.separator + zipEntryName;
                File fileZipped = new File(pathOfUpZipped);
                if (!fileZipped.getParentFile().exists()) {
                    fileZipped.getParentFile().mkdirs();
                }
                OutputStream out = new FileOutputStream(fileZipped);
                byte[] buff = new byte[1024];
                int len;
                while ((len = in.read(buff)) > 0) {
                    out.write(buff, 0, len);
                }
                in.close();
                out.close();
                System.out.println(pathOfUpZipped);
            }
        }
        zip.close();
    }
}
