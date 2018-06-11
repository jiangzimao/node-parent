package com.ch.dcs.node.server;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(scanBasePackages = "com.ch.dcs.node")
public class App {
    public static void main(String[] args) {
        ApplicationContext ctx = new SpringApplicationBuilder(App.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
