package com.miw.remoid.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import com.miw.remoid.handler.RemoidHandler;
import com.miw.remoid.util.EnvironmentPropertyPlaceholderConfigurer;

@Configuration
@EnableWebMvc
@EnableWebSocket
@ComponentScan(basePackages = "com.miw.remoid")
public class AppConfig extends WebMvcConfigurerAdapter implements WebSocketConfigurer {
    @Bean
    public static EnvironmentPropertyPlaceholderConfigurer environmentPropertyPlaceholderConfigurer() {
        EnvironmentPropertyPlaceholderConfigurer eppc = new EnvironmentPropertyPlaceholderConfigurer();
        
        Resource location = new ClassPathResource("service.properties");
        eppc.setLocation(location);
        
        return eppc;
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("/public-resources/");
    }
    
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myHandler(), "/update");
    }

    @Bean
    public WebSocketHandler myHandler() {
        return new RemoidHandler();
    }
    
    
    
    // @Bean
    // public MongoTemplate mongoTemplate(MongoCredentials mongoCredentials)
    // throws Exception {
    // MongoTemplate template = new MongoTemplate(new
    // Mongo(mongoCredentials.getHost(), mongoCredentials.getPort()),
    // mongoCredentials.getName(), mongoCredentials.getUserCredentials());
    //
    // return template;
    // }
}
