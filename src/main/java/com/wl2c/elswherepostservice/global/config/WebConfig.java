package com.wl2c.elswherepostservice.global.config;

import com.wl2c.elswherepostservice.global.interceptor.VoidSuccessResponseInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Clock;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.cors}")
    private String corsList;

    private final VoidSuccessResponseInterceptor vsrInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(vsrInterceptor)
                .addPathPatterns("/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(parseCorsList(corsList))
                .allowedHeaders("*")
                .allowedMethods("*");
    }

    private static String[] parseCorsList(String corsList) {
        String[] corsOrigins = corsList.split(",");
        for (int i = 0; i < corsOrigins.length; i++) {
            corsOrigins[i] = corsOrigins[i].strip();
        }
        return corsOrigins;
    }

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
