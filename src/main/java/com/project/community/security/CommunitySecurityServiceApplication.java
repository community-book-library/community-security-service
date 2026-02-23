package com.project.community.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.project.community",
})
@EnableJpaRepositories(basePackages = {
        "com.project.community"
})
@EntityScan(basePackages = {
        "com.project.community"
})
public class CommunitySecurityServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommunitySecurityServiceApplication.class, args);
	}

}
