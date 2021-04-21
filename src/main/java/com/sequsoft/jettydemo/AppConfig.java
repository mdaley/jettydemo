package com.sequsoft.jettydemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class AppConfig {
    private static final String SERVER_PORT = "SERVER_PORT";
    private static final int SERVER_PORT_DEFAULT = 8080;

    private final Environment environment;

    @Autowired
    public AppConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    WebServerFactoryCustomizer<JettyServletWebServerFactory> webServerConfig() {
        return factory -> {
            factory.setPort(environment.getProperty(SERVER_PORT, Integer.class, SERVER_PORT_DEFAULT));

        };
    }
}
