package dev.coms4156.project.kebabcase.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * A place to configure CORS parameters.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

  /**
   * Disable CORS.
   */
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**").allowedMethods("*");
  }
}