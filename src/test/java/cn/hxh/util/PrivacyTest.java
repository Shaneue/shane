package cn.hxh.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PrivacyTest {
    @Test
    public void decrypt() throws Exception {
        String code = Privacy.encrypt("test".getBytes(), "hxh".getBytes());
        assertEquals("test", new String(Privacy.decrypt(code, "hxh".getBytes())));
    }
}