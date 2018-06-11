package com.ch.dcs.node.client;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(scanBasePackages = "com.ch.dcs.node")
public class App {
    public static void main(String[] args) {
        ApplicationContext ctx = new SpringApplicationBuilder(App.class).run(args);
    }
}
