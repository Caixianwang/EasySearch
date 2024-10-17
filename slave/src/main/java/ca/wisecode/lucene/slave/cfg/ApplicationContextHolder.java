package ca.wisecode.lucene.slave.cfg;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author: caixianwang2022@gmail.com
 * @date: 10/15/2024 1:48 PM
 * @Version: 1.0
 * @description:
 */
@Slf4j
public class ApplicationContextHolder {
    private static ConfigurableApplicationContext context;

    public static ConfigurableApplicationContext getContext() {
        return context;
    }

    public static void setContext(ConfigurableApplicationContext context) {
        ApplicationContextHolder.context = context;
    }

    public static void shutdown() {
        SpringApplication.exit(context, () -> 0);
    }

}
