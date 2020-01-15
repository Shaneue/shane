package cn.hxh.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Constants {

    public static String ENCRYPTED;
    public static String USER_NAME;
    public static String DIARY;
    public static String TEMPORARY;

    @Value("${file.passwords}")
    public void setEncrypted(String encrypted) {
        Constants.ENCRYPTED = encrypted;
    }

    @Value("${owner.name}")
    public void setUserName(String userName) {
        USER_NAME = userName;
    }

    @Value("${file.diary}")
    public void setDiary(String diary) {
        Constants.DIARY = diary;
    }

    @Value("${file.tmp}")
    public void setTemporary(String temporary) {
        Constants.TEMPORARY = temporary;
    }

    public static final String SUCCESS = "SUCCESS";
    public static final String FAILURE = "FAILURE";
    public static final String FAILURE_CODE = "Code is wrong";

    public static final int SUCCESS_STATUS = 0;
    public static final int FAILURE_STATUS = -1;

}
