package com.example.admin.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {
    
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        // Serve static resources from frontend build
        registry.addResourceHandler("/**")
            .addResourceLocations("classpath:/static/")
            .setCachePeriod(3600)
        
        // Serve assets with longer cache period
        registry.addResourceHandler("/assets/**")
            .addResourceLocations("classpath:/static/assets/")
            .setCachePeriod(31536000) // 1 year
    }
    
    override fun addViewControllers(registry: ViewControllerRegistry) {
        // Forward all non-API routes to React app (SPA support)
        registry.addViewController("/{spring:\\w+}")
            .setViewName("forward:/index.html")
        registry.addViewController("/**/{spring:\\w+}")
            .setViewName("forward:/index.html")
        registry.addViewController("/{spring:\\w+}/**{spring:?!(\\.js|\\.css|\\.png|\\.jpg|\\.jpeg|\\.gif|\\.ico|\\.svg|\\.woff|\\.woff2|\\.ttf|\\.eot)$}")
            .setViewName("forward:/index.html")
    }
}