package com.sequsoft.jettydemo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.auth0.jwt.impl.PublicClaims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.SocketUtils;

import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Map;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@Import(JettydemoApplicationTests.TestConfig.class)
@TestPropertySource(properties = {
		"USE_SSL=false",
		"KEYSTORE=src/test/resources/certs/server.p12",
		"KEYSTORE_PASSWORD=password",
		"KEYSTORE_TYPE=pkcs12",
		"TRUSTSTORE=src/test/resources/certs/truststore.jks",
		"TRUSTSTORE_PASSFILE=src/test/resources/certs/truststore_passfile",
		"TRUSTSTORE_TYPE=jks"
})
class JettydemoApplicationTests {
	private static final int SERVER_PORT;

	private static final JwtHelper jwtHelper = new JwtHelper();

	@TestConfiguration
	static class TestConfig {
		@Bean
		public JwtPublicKeyProvider testJwtPublicKeyProvider() {
			return new JwtPublicKeyProvider() {
				@Override
				public RSAPublicKey getPublicKey() {
					return jwtHelper.getPublicKey();
				}
			};
		}
	}

	static {
		// Note that Spring's environment gets values from Java Properties and _then_ from environment variables
		// so setting properties instead of environment variables is perfectly fine!
		SERVER_PORT = SocketUtils.findAvailableTcpPort();
		System.setProperty("SERVER_PORT", Integer.toString(SERVER_PORT));
	}

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void contextLoads() {
	}

	@Test
	void hello_responds() throws Exception {
		HttpHeaders headers = new HttpHeaders();

		headers.put(HttpHeaders.AUTHORIZATION, List.of("Bearer " + jwtHelper.createJwt(Map.of(PublicClaims.ISSUER, "issuer",
				PublicClaims.SUBJECT, "subject"))));

		ResponseEntity<String> response = restTemplate.exchange("http://localhost:" + SERVER_PORT + "/hello", HttpMethod.GET, new HttpEntity<>(headers), String.class);
		//String response = restTemplate.getForObject("http://localhost:" + SERVER_PORT + "/hello", String.class);
		assertThat(response.getStatusCode().value(), is(200));
		assertThat(response.getBody(), is("Hello World!"));
	}

}
