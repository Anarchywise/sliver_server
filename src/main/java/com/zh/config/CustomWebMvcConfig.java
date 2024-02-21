package com.zh.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CustomWebMvcConfig implements WebMvcConfigurer {

    @Value("${custom.web.resource.path1}")
    private String customStaticResourcePath1;

    @Value("${custom.web.resource.path2}")
    private String customStaticResourcePath2;

    @Value("${custom.web.resource.path3}")
    private String customStaticResourcePath3;


    @Override
    public void addResourceHandlers(@SuppressWarnings("null") ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/post/images/**")
                .addResourceLocations(customStaticResourcePath1);

        registry.addResourceHandler("/user/headPortrait/**")
                .addResourceLocations(customStaticResourcePath2);

        registry.addResourceHandler("/spotsPicture/**")
                .addResourceLocations(customStaticResourcePath3);
    }

}
