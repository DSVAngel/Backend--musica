package com.uv.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FileUploadConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    @Value("${app.upload.audio.dir:./uploads/audio}")
    private String audioUploadDir;

    @Value("${app.upload.images.dir:./uploads/images}")
    private String imageUploadDir;

    @Value("${app.upload.videos.dir:./uploads/videos}")
    private String videoUploadDir;

    @Value("${app.upload.waveforms.dir:./uploads/waveforms}")
    private String waveformUploadDir;

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // General uploads
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/");

        // Audio files
        registry.addResourceHandler("/audio/**")
                .addResourceLocations("file:" + audioUploadDir + "/");

        // Images
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + imageUploadDir + "/");

        // Avatars
        registry.addResourceHandler("/avatars/**")
                .addResourceLocations("file:" + imageUploadDir + "/avatars/");

        // Cover images
        registry.addResourceHandler("/covers/**")
                .addResourceLocations("file:" + imageUploadDir + "/covers/");

        // Thumbnails
        registry.addResourceHandler("/thumbnails/**")
                .addResourceLocations("file:" + imageUploadDir + "/thumbnails/");

        // Videos
        registry.addResourceHandler("/videos/**")
                .addResourceLocations("file:" + videoUploadDir + "/");

        // Waveforms
        registry.addResourceHandler("/waveforms/**")
                .addResourceLocations("file:" + waveformUploadDir + "/");

        // Media files (general)
        registry.addResourceHandler("/media/**")
                .addResourceLocations("file:" + uploadDir + "/media/");

        // Static files
        registry.addResourceHandler("/static/**")
                .addResourceLocations("file:" + uploadDir + "/static/");
    }

    // Getters para los directorios de subida
    public String getUploadDir() {
        return uploadDir;
    }

    public String getAudioUploadDir() {
        return audioUploadDir;
    }

    public String getImageUploadDir() {
        return imageUploadDir;
    }

    public String getVideoUploadDir() {
        return videoUploadDir;
    }

    public String getWaveformUploadDir() {
        return waveformUploadDir;
    }
}