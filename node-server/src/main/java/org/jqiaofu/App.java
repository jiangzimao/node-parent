package org.jqiaofu;

import org.jqiaofu.core.config.WebSocketConfig;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

@EnableAutoConfiguration
@SpringBootApplication
@Import(WebSocketConfig.class)
public class App {
    public static void main(String[] args) {
        ApplicationContext ctx = new SpringApplicationBuilder(App.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
        javax.servlet.ServletContext s;
    }
}
