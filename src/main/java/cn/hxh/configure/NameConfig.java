package cn.hxh.configure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NameConfig {
    @Value("${owner.name}")
    public String userName;

    @Value("${file.passwords}")
    public String passwordDir;

    @Value("${file.diary}")
    public String diary;
}
