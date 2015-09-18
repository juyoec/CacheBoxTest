package com.crassirostris.cache;

import com.coupang.configuration.cache.GuavaCacheManager;
import com.coupang.configuration.cache.RefreshableCache;
import com.coupang.configuration.schedule.DelayType;
import com.coupang.configuration.schedule.SpringScheduling;
import com.crassirostris.cache.controller.AbstractCacheController;
import com.google.common.collect.Lists;
import net.sf.ehcache.config.CacheConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheResolver;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created with IntelliJ IDEA.
 * User: Coupang
 * Date: 2015-09-09
 * Time: 오전 11:35
 */
@Configuration
@EnableCaching
@EnableAsync
public class CacheConfig implements CachingConfigurer{
	@Autowired
	@Qualifier("simpleCacheManager")
	private SimpleCacheManager simpleCacheManager;
	@Autowired
	@Qualifier("guavaCacheManager")
	private GuavaCacheManager guavaCacheManager;
	@Autowired
	@Qualifier("ehcacheCacheManager")
	private EhCacheCacheManager ehcacheManager;

	@Bean
	public EhCacheCacheManager ehcacheCacheManager() {
		CacheConfiguration cacheConfiguration = new CacheConfiguration();
		cacheConfiguration.setName("ehcache");
		cacheConfiguration.setMemoryStoreEvictionPolicy("LRU");
		cacheConfiguration.setMaxEntriesLocalHeap(100L);

		net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
		config.addCache(cacheConfiguration);

		net.sf.ehcache.CacheManager cacheManager = net.sf.ehcache.CacheManager.newInstance(config);
		return new EhCacheCacheManager(cacheManager);
	}
	@Bean
	public SimpleCacheManager simpleCacheManager() {
		SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
		Cache cache = new ConcurrentMapCache("simpleCache");
		simpleCacheManager.setCaches(Lists.newArrayList(cache));
		return simpleCacheManager;
	}

	@Bean
	public GuavaCacheManager guavaCacheManager() {
		GuavaCacheManager cacheManager = new GuavaCacheManager();
		RefreshableCache cache = new RefreshableCache("refreshableCache", DelayType.FIXED_RATE);
		cache.setFixedInterval(AbstractCacheController.CACHE_REFRESH_DURATION);
		cacheManager.setCaches(Lists.newArrayList(cache));
		return cacheManager;
	}

	@Bean
	@Override
	public CacheManager cacheManager() {
		List<CacheManager> cacheManagers = Stream.of(simpleCacheManager, guavaCacheManager, ehcacheManager).filter(Objects::nonNull).collect(Collectors.toList());

		CompositeCacheManager cacheManager = new CompositeCacheManager();

		cacheManager.setCacheManagers(cacheManagers);
		cacheManager.setFallbackToNoOpCache(false);

		return cacheManager;
	}

	@Override
	public CacheResolver cacheResolver() {
		return new SimpleCacheResolver(cacheManager());
	}

	@Override
	public KeyGenerator keyGenerator() {
		return new SimpleKeyGenerator();
	}

	@Override
	public CacheErrorHandler errorHandler() {
		return new SimpleCacheErrorHandler();
	}
}
