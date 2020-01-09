package cn.hxh.common;

import cn.hxh.common.configure.AppContextAware;
import cn.hxh.common.configure.NameConfig;

public class Constants {
    public static final String ENCRYPTED = nameConfig().passwordDir;
    public static final String USER_NAME = nameConfig().userName;
    public static final String DIARY = nameConfig().diary;
    public static final String TEMPORARY = USER_NAME + "_tmp";

    private static NameConfig nameConfig() {
        return (NameConfig) AppContextAware.getBean(NameConfig.class);
    }


    public static final String SUCCESS = "SUCCESS";
    public static final String FAILURE = "FAILURE";
    public static final String FAILURE_CODE = "Code is wrong";

    public static final int SUCCESS_STATUS = 0;
    public static final int FAILURE_STATUS = -1;

}
