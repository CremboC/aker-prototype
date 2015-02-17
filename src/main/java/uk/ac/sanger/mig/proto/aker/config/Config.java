package uk.ac.sanger.mig.proto.aker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @author pi1
 * @since February 2015
 */
@Configuration
public class Config extends WebMvcConfigurationSupport {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
//		registry.addResourceHandler("/css/**").addResourceLocations("classpath:resources/css/");
//		registry.addResourceHandler("/js/**").addResourceLocations("classpath:resources/js/");
	}
}
