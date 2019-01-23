package cn.hxh.common.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MyLog implements Log {
    private static final Logger log = LoggerFactory.getLogger("Shane");

    @Override
    public void record(String operation) {
        log.info(operation);
    }
}
