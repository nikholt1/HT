package com.example.hometheater.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map /profileImages/** URL to ../data/profileImages/ folder
        registry.addResourceHandler("/profileImages/**")
                .addResourceLocations("file:../data/profileImages/");
    }
}