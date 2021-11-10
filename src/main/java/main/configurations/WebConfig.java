package main.configurations;

import main.mappers.StringToUpperCaseModerationStatusConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${images.dir}")
    private String imagesPath;
    @Value("${avatars.dir}")
    private String avatarsPath;
    @Value("${upload.dir}")
    private String uploadPath;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToUpperCaseModerationStatusConverter());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(imagesPath + "/**")
                .addResourceLocations("file:///" + uploadPath + imagesPath + "/");
        registry.addResourceHandler("/avatars/**")
                .addResourceLocations("file:///" + uploadPath + avatarsPath + "/");
    }
}
