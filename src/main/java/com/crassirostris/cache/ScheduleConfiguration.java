package com.crassirostris.cache;

import com.crassirostris.cache.controller.AbstractCacheController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * User: crassirostris
 * Date: 2015-09-09
 * Time: 오후 1:06
 */
@Configuration
@EnableScheduling
@Slf4j
public class ScheduleConfiguration {
	@Qualifier("cacheManager")
	@Autowired
	private CacheManager cacheManager;
	@Scheduled(fixedRate = AbstractCacheController.CACHE_REFRESH_DURATION)
	public void expireSimpleCache() {
		Cache simpleCache = cacheManager.getCache("simpleCache");
		log.info("simple Cache clear");
		simpleCache.clear();
	}
}
