package cn.hxh.common.configure;

import cn.hxh.common.Constants;
import cn.hxh.util.file.JarIOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InitRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        try {
            JarIOUtil.copyToShane(Constants.ENCRYPTED);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
