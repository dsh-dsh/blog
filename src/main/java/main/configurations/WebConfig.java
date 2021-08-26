package main.configurations;

import main.mappers.StringToEnumConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${upload.path}")
    private String uploadPathName;
    @Value("${avatar.path}")
    private String avatarPathName;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToEnumConverter());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(uploadPathName + "/**")
                .addResourceLocations("classpath:" + uploadPathName + "/");
        registry.addResourceHandler(avatarPathName + "/**")
                .addResourceLocations("classpath:" + avatarPathName + "/");
    }
}
