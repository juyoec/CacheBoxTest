package com.crassirostris.cache;

import com.github.jknack.handlebars.springmvc.HandlebarsViewResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

/**
 * Created by crassirostris on 15. 9. 8..
 */
@Slf4j
@EnableAutoConfiguration
@EnableAspectJAutoProxy
@Configuration
@ComponentScan({"com.crassirostris.cache", "com.coupang.configuration"})
public class SpringServletContext {
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
}
