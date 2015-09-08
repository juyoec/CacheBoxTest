package com.crassirostris.cache;

import com.github.jknack.handlebars.*;
import com.github.jknack.handlebars.springmvc.*;
import com.google.common.collect.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.cache.*;
import org.springframework.cache.annotation.*;
import org.springframework.cache.concurrent.*;
import org.springframework.cache.support.*;
import org.springframework.context.annotation.*;
import org.springframework.core.env.*;
import org.springframework.scheduling.annotation.*;

import static org.springframework.util.Assert.notNull;

/**
 * Created by crassirostris on 15. 9. 8..
 */
@Slf4j
@EnableAutoConfiguration
@Configuration
@ComponentScan("com.crassirostris.cache")
@EnableCaching
@EnableScheduling
public class SpringServletContext {
	private String simpleCache = "simpleCache";
	private CacheManager cacheManager;
	public static void main(String[] args) throws Exception {
		SpringApplication.run(SpringServletContext.class, args);
	}

	@Bean
	public HandlebarsViewResolver viewResolver() {
		final HandlebarsViewResolver viewResolver = new HandlebarsViewResolver();
		viewResolver.registerHelper("@json", Jackson2Helper.INSTANCE);
		viewResolver.setCache(false);
		viewResolver.setSuffix(".hbs");

		return viewResolver;
	}

	@Bean
	public CacheManager getSimpleCacheManager() {

		SimpleCacheManager cacheManager = new SimpleCacheManager();
		Cache cache = new ConcurrentMapCache("simpleCache");
		cacheManager.setCaches(Lists.newArrayList(cache));
		this.cacheManager = cacheManager;
		return cacheManager;
	}

	@Scheduled(fixedRate = 60*1000)
	public void expireSimpleCache() {
		Cache simpleCache = cacheManager.getCache(this.simpleCache);
		log.info("simple Cache clear");
		simpleCache.clear();
	}
}
