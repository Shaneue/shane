package cn.hxh.object;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class PasswordTest {

    @Test
    public void testCompare() {
        Password[] passwords = new Password[3];
        passwords[0] = new Password();
        passwords[1] = new Password();
        passwords[2] = new Password();
        passwords[0].setWhere(new byte[]{2, 2});
        passwords[1].setWhere(new byte[]{1, 3});
        passwords[2].setWhere(new byte[]{2, 1});
        Arrays.sort(passwords);
        assertEquals(1, passwords[0].getWhere()[0]);
        assertEquals(1, passwords[1].getWhere()[1]);
        assertEquals(2, passwords[2].getWhere()[1]);
    }

}