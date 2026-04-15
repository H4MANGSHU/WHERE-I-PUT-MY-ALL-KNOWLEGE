package com.ai.hackathon;

import Entity.User;
import JPA.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"com.ai.hackathon", "JPA","Redis","Test"})  // TRY
@EntityScan(basePackages = {"Entity","com.ai.hackathon"})
@ComponentScan(basePackages = {"com.ai.hackathon", "SecurityAuth", "Entity","Security","FireBase","Service","Test","Redis","WebSocket","DTO","WebClient"})
public class HackathonApplication {
	public static void main(String[] args) {
	SpringApplication application = (SpringApplication) SpringApplication.run(HackathonApplication.class, args);
    application.setWebApplicationType(WebApplicationType.SERVLET);
    application.run(args);


    }

    @Bean
    public CommandLineRunner commandLineRunner(UserRepository userRepository){
        return args -> {
            User user = new User();
            user.setUser_id(2);
            user.setEmail("XYZ44@GMAIL.COM");
            user.setPassword("xyz");
            user.setUsername("billa");
           userRepository.save(user);


        };

    }
    }

