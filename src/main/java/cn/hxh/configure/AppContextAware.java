package cn.hxh.configure;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class AppContextAware extends ApplicationObjectSupport {
    private static ApplicationContext context;

    @PostConstruct
    public void setAppContext() {
        context = getApplicationContext();
    }

    public static Object getBean(Class<?> clazz) {
        return context.getBean(clazz);
    }
}
