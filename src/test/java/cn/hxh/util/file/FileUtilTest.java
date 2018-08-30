package cn.hxh.util.file;

import cn.hxh.constant.TestConstants;
import cn.hxh.util.file.FileUtil;
import org.junit.Test;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class FileUtilTest {
    private String fileParentPath = TestConstants.TEST_RESOURCES_PATH + "FileUtil/";

    @Test
    public void test_write_read() {
        String fileName = "fileUtilTest.txt";
        FileUtil.writeOut(fileParentPath + fileName, "test at 2017年10月29日23:58:45\nline 2");
        String[] tmp = FileUtil.readLines(fileParentPath + fileName);
        assert tmp != null;
        assertEquals("test at 2017年10月29日23:58:45", tmp[0]);
        assertEquals("line 2", tmp[1]);
    }

    @Test
    public void test_delete() throws IOException {
        String fileName_testDelete = "fileUtilTestDelete.txt";
        String filePath = fileParentPath + fileName_testDelete;
        File file = new File(filePath);
        try {
            assertEquals(true, file.createNewFile());
            FileUtil.deleteFile(filePath);
            assertEquals(false, file.exists());
        } finally {
            if (file.exists()) {
                file.delete();
            }
        }
    }

    @Test
    public void test_delete_dir() throws IOException {
        String dirPath = fileParentPath + "testDir/";
        File dir = new File(dirPath);
        File file = new File(dirPath + "test");
        try {
            assertEquals(true, dir.mkdir());
            assertEquals(true, file.createNewFile());
            FileUtil.deleteDirRecursively(dir.getAbsolutePath());
            assertEquals(false, dir.exists());
        } finally {
            if (dir.exists()) {
                FileSystemUtils.deleteRecursively(dir.getAbsoluteFile());
            }
        }
    }
}