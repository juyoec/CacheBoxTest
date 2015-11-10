package com.crassirostris.cache;

import com.github.jknack.handlebars.springmvc.HandlebarsViewResolver;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

/**
 * Created by crassirostris on 15. 9. 8..
 */
@Slf4j
@EnableAutoConfiguration
@EnableAspectJAutoProxy
@Configuration
@ComponentScan({"com.crassirostris.cache"})
public class SpringServletContext extends SpringBootServletInitializer {
	public static final String HANDLEBARS_VIEW_BASE_PATH = "/WEB-INF/views/";
	public static final String HANDLEBARS_VIEW_SUFFIX = ".hbs";
	public static final boolean FAIL_ON_MISSING_FILE = false;
	public static final int ORDER = 1;
	private String simpleCache = "simpleCache";
	public static void main(String[] args) throws Exception {
		SpringApplication.run(SpringServletContext.class, args);
	}

	@Bean
	public HandlebarsViewResolver handlebarsViewResolver() {
		HandlebarsViewResolver viewResolver = new HandlebarsViewResolver();
		viewResolver.setOrder(ORDER);
		viewResolver.setFailOnMissingFile(FAIL_ON_MISSING_FILE);
		viewResolver.setCache(false);

		viewResolver.setPrefix(HANDLEBARS_VIEW_BASE_PATH);
		viewResolver.setSuffix(HANDLEBARS_VIEW_SUFFIX);

		return viewResolver;
	}
	@Bean
	public MappingJackson2JsonView jsonView() {
		MappingJackson2JsonView mappingJacksonJsonView = new MappingJackson2JsonView();
		return mappingJacksonJsonView;
	}

	@Bean
	public BeanNameViewResolver beanNameViewResolver() {
		BeanNameViewResolver beanNameViewResolver = new BeanNameViewResolver();
		beanNameViewResolver.setOrder(0);
		return beanNameViewResolver;
	}

	@Bean
	public ExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver() {
		ExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver = new ExceptionHandlerExceptionResolver();
		exceptionHandlerExceptionResolver.setOrder(1);
		exceptionHandlerExceptionResolver.setMessageConverters(Lists.newArrayList(getJson2MessageConverter()));
		return exceptionHandlerExceptionResolver;
	}

	@Bean
	public MappingJackson2HttpMessageConverter getJson2MessageConverter() {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		return converter;
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(SpringServletContext.class);
	}
}
