package com.ch.dcs.node.server;

import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.*;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Import({
        // CodecsAutoConfiguration.class,
        DispatcherServletAutoConfiguration.class,
        EmbeddedWebServerFactoryCustomizerAutoConfiguration.class,
        ErrorMvcAutoConfiguration.class,
        // GenericCacheConfiguration.class,
        // GsonHttpMessageConvertersConfiguration.class,
        HttpEncodingAutoConfiguration.class,
        HttpMessageConvertersAutoConfiguration.class,
        JacksonAutoConfiguration.class,
        // JacksonHttpMessageConvertersConfiguration.class,
        // JmxAutoConfiguration.class,
        // MultipartAutoConfiguration.class,
        // NoOpCacheConfiguration.class,
        // PropertyPlaceholderAutoConfiguration.class,
        // RedisCacheConfiguration.class,
        // RestTemplateAutoConfiguration.class,
        ServletWebServerFactoryAutoConfiguration.class,
        // ServletWebServerFactoryConfiguration.class,
        // SimpleCacheConfiguration.class,
        WebMvcAutoConfiguration.class,
        // WebSocketMessagingAutoConfiguration.class,
        WebSocketServletAutoConfiguration.class
})
@ComponentScan("com.ch.dcs.node")
public class App {
    public static void main(String[] args) {
        ApplicationContext ctx = new SpringApplicationBuilder(App.class).run(args);
    }
}
