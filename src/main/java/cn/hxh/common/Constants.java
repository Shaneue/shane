package cn.hxh.common;

import cn.hxh.configure.AppContextAware;
import cn.hxh.configure.NameConfig;

public class Constants {
    public static final String ENCRYPTED = nameConfig().passwordDir;
    public static final String USER_NAME = nameConfig().userName;
    public static final String DIARY = nameConfig().diary;
    public static final String TEMPORARY = USER_NAME + "_tmp";


    private static NameConfig nameConfig() {
        return (NameConfig) AppContextAware.getBean(NameConfig.class);
    }
}
