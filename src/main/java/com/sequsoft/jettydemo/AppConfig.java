package com.sequsoft.jettydemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
public class AppConfig {
    private static final String SERVER_PORT = "SERVER_PORT";
    private static final int SERVER_PORT_DEFAULT = 8080;

    private static final String USE_SSL = "USE_SSL";

    private static final String TRUSTSTORE = "TRUSTSTORE";
    private static final String TRUSTSTORE_TYPE = "TRUSTSTORE_TYPE";
    private static final String TRUSTSTORE_PASSFILE = "TRUSTSTORE_PASSFILE";
    private static final String TRUSTSTORE_PASSWORD = "TRUSTSTORE_PASSWORD";

    private static final String KEYSTORE = "KEYSTORE";
    private static final String KEYSTORE_TYPE = "KEYSTORE_TYPE";
    private static final String KEYSTORE_PASSFILE = "KEYSTORE_PASSFILE";
    private static final String KEYSTORE_PASSWORD = "KEYSTORE_PASSWORD";

    private final Environment environment;

    @Autowired
    public AppConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    @ConditionalOnProperty(value = USE_SSL, havingValue = "true")
    Ssl sslConfig() {
        Ssl ssl = new Ssl();

        ssl.setKeyStore(environment.getProperty(KEYSTORE));
        ssl.setKeyStorePassword(keystorePassword());
        ssl.setKeyStoreType(environment.getProperty(KEYSTORE_TYPE));

        ssl.setTrustStore(environment.getProperty(TRUSTSTORE));
        ssl.setTrustStorePassword(truststorePassword());
        ssl.setTrustStoreType(environment.getProperty(TRUSTSTORE_TYPE));

        return ssl;
    }

    @Bean
    WebServerFactoryCustomizer<JettyServletWebServerFactory> webServerConfig() {
        return factory -> {
            factory.setPort(environment.getProperty(SERVER_PORT, Integer.class, SERVER_PORT_DEFAULT));
            if (environment.getProperty(USE_SSL, Boolean.class, false)) {
                factory.setSsl(sslConfig());
            }
        };
    }

    private String truststorePassword() {
        return password(TRUSTSTORE_PASSWORD, TRUSTSTORE_PASSFILE);
    }

    private String keystorePassword() {
        return password(KEYSTORE_PASSWORD, KEYSTORE_PASSFILE);
    }

    private String password(String environmentPropertyToRead, String fileToRead) {
        String fromEnvPassword = environment.getProperty(environmentPropertyToRead);
        if (StringUtils.hasText(fromEnvPassword)) {
            return fromEnvPassword;
        }

        String filePath = environment.getProperty(fileToRead);
        if (StringUtils.hasText(filePath)) {
            try {
                return Files.readString(Paths.get(filePath), Charset.defaultCharset());
            } catch (IOException e) {
                throw new RuntimeException("Could not read password from file " + filePath, e);
            }
        }

        throw new RuntimeException("Neither " + environmentPropertyToRead + " nor " + fileToRead + " have been set.");
    }
}
