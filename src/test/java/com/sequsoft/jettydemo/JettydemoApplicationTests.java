package com.sequsoft.jettydemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
		"KEYSTORE=src/test/resources/certs/server.p12",
		"KEYSTORE_PASSWORD=password",
		"KEYSTORE_TYPE=pkcs12",
		"TRUSTSTORE=src/test/resources/certs/truststore.jks",
		"TRUSTSTORE_PASSFILE=src/test/resources/certs/truststore_passfile",
		"TRUSTSTORE_TYPE=jks"
})
class JettydemoApplicationTests {

	@Test
	void contextLoads() {
	}

}
