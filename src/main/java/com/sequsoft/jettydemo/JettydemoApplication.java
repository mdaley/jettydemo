package com.sequsoft.jettydemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class JettydemoApplication {
	private static final Logger LOGGER = LoggerFactory.getLogger(JettydemoApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(JettydemoApplication.class, args);
	}

	@GetMapping("/hello")
	public String hello() {
		LOGGER.info("Hello requested");

		SecurityContext ctx = SecurityContextHolder.getContext();
		Authentication auth = ctx.getAuthentication();
		System.out.println("AUTHORIZED USER = " + auth.getName());

		return String.format("Hello World!");
	}
}
