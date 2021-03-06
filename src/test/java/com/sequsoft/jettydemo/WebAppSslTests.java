package com.sequsoft.jettydemo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.auth0.jwt.impl.PublicClaims;
import com.sequsoft.jettydemo.plugins.JwtPublicKeyPlugin;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.SocketUtils;

import java.io.File;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLContext;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@Import(WebAppSslTests.TestConfig.class)
class WebAppSslTests {
    private static final int SERVER_PORT;

    private static final JwtHelper jwtHelper = new JwtHelper();

    @TestConfiguration
    static class TestConfig {
        @Bean
        public JwtPublicKeyPlugin testJwtPublicKeyProvider() {
            return () -> jwtHelper.getPublicKey();
        }
    }

    static {
        // Note that Spring's environment gets values from Java Properties and _then_ from environment variables
        // so setting properties instead of environment variables is perfectly fine!
        SERVER_PORT = SocketUtils.findAvailableTcpPort();
        System.setProperty("SERVER_PORT", Integer.toString(SERVER_PORT));
        System.setProperty("USE_SSL", "true");
        System.setProperty("KEYSTORE", "src/test/resources/certs/server.p12");
        System.setProperty("KEYSTORE_PASSWORD", "password");
        System.setProperty("KEYSTORE_TYPE", "pkcs12");
        System.setProperty("TRUSTSTORE", "src/test/resources/certs/truststore.jks");
        System.setProperty("TRUSTSTORE_PASSFILE", "src/test/resources/certs/truststore_passfile");
        System.setProperty("TRUSTSTORE_TYPE", "jks");
    }

    private static TestRestTemplate restTemplate;

    @BeforeAll
    static void before() throws Exception {
        SSLContext sslContext = new SSLContextBuilder()
                .loadTrustMaterial(new File("src/test/resources/certs/truststore.jks"), "password".toCharArray())
                .build();

        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);
        HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
        ClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        RestTemplateBuilder builder = new RestTemplateBuilder()
                .requestFactory(() -> factory);
        restTemplate = new TestRestTemplate(builder, null, null, TestRestTemplate.HttpClientOption.SSL);
    }

    @Test
    void contextLoads() {
    }

    @Test
    void hello_responds() {
        HttpHeaders headers = new HttpHeaders();

        headers.put(HttpHeaders.AUTHORIZATION, List.of("Bearer " + jwtHelper.createJwt(Map.of(PublicClaims.ISSUER, "issuer",
                PublicClaims.SUBJECT, "subject"))));

        ResponseEntity<String> response = restTemplate.exchange("https://localhost:" + SERVER_PORT + "/hello", HttpMethod.GET, new HttpEntity<>(headers), String.class);

        assertThat(response.getStatusCode().value(), is(200));
        assertThat(response.getBody(), is("Hello World!"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Bearer THIS_IS_NOT_A_VALID_TOKEN",
            "THIS_IS_NOT_A_VALID_AUTHORIZATION_HEADER",
            "AUTH_HEADER SHOULD_HAVE ONLY_TWO_PARTS"})
    void invalid_token_results_in_unauthorized(String headerValue) {
        HttpHeaders headers = new HttpHeaders();

        headers.put(HttpHeaders.AUTHORIZATION, List.of(headerValue));

        ResponseEntity<String> response = restTemplate.exchange("https://localhost:" + SERVER_PORT + "/hello", HttpMethod.GET, new HttpEntity<>(headers), String.class);

        assertThat(response.getStatusCode().value(), is(401));
    }
}

