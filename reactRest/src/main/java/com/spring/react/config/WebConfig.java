package com.spring.react.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	
    @Value("${file.root-dir}")
    private String rootDir;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins("http://localhost:3000")
			.allowedMethods("GET", "POST", "DELETE", "PUT", "PATCH", "OPTIONS");
	}
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")                      // 요청 URL
                .addResourceLocations("file:"+ rootDir + "/upload/images/");         // 실제 파일 경로
        
        registry.addResourceHandler("/file/**")                             // 파일 요청 URL
        .addResourceLocations("file:" + rootDir + "/upload/file/");   // 실제 파일 경로
    }
}
