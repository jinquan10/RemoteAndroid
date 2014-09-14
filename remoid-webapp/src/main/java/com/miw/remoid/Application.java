package com.miw.remoid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import com.fasterxml.jackson.databind.ObjectMapper;

@ComponentScan
@EnableAutoConfiguration
public class Application {
	public static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
