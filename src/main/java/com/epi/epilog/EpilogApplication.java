package com.epi.epilog;

import jakarta.persistence.EntityListeners;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.Base64;

@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.epi.epilog")
public class EpilogApplication {

	public static void main(String[] args) throws Exception{
		SpringApplication.run(EpilogApplication.class, args);
	}
}
