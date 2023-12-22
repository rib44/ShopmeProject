package com.shopme.admin;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String dirName = "user-photos";
        Path userPhotos = Paths.get(dirName);

        String userPhotosPath = userPhotos.toFile().getAbsolutePath();

        // '/**' double asterisks allows all the files in this to directory to
        // be available to the web client
        registry.addResourceHandler("/" + dirName + "/**")
                .addResourceLocations("file://" + userPhotosPath + "/");
    }

}
