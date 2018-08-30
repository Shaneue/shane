package cn.hxh.util.file;

import cn.hxh.TestUtil;
import cn.hxh.constant.TestConstants;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class ZipUtilTest {
    String parentPath = TestConstants.TEST_RESOURCES_PATH + "ZipTest/";

    @Test
    public void zip() throws Exception {
        File toZipped = new File(parentPath);
        String zip = TestConstants.TEMP_DIR + "test.zip";
        ZipUtil.zip(toZipped, zip);
        String md5 = TestUtil.computeFileMd5(new File(zip));
        FileUtil.deleteFile(zip);
        assertEquals("d6D8hnPnaH4Jioh/MFgqIQ==", md5);
    }

    @Test
    public void unZipFiles() throws Exception {
        File toZipped = new File(parentPath);
        String zip = TestConstants.TEMP_DIR + "test.zip";
        ZipUtil.zip(toZipped, zip);
        String unzip = TestConstants.TEMP_DIR + "unzip";
        ZipUtil.unZipFiles(new File(zip), unzip);
        String test1 = FileUtil.readFile(TestConstants.TEMP_DIR + "unzip/txt");
        String test2 = FileUtil.readFile(TestConstants.TEMP_DIR + "unzip/folder/txt");
        FileUtil.deleteDirRecursively(unzip);
        FileUtil.deleteFile(zip);
        assertEquals("to test zip file", test1);
        assertEquals("to test zip file", test2);
    }

}