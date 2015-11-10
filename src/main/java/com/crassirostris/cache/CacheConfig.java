package com.crassirostris.cache;

import com.crassirostris.cache.controller.AbstractCacheController;
import com.crassirostris.cache.refresh.ClusterCache;
import com.crassirostris.cache.refresh.RefreshableCache;
import com.crassirostris.cache.refresh.RefreshableCacheManager;
import com.crassirostris.cache.refresh.RefreshableCacheScheduleResistrar;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import net.sf.ehcache.config.CacheConfiguration;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.cache.guava.GuavaCacheManager;
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
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created with IntelliJ IDEA.
 * User: crassirostris
 * Date: 2015-09-09
 * Time: 오전 11:35
 */
@Configuration
@Import(RefreshableCacheScheduleResistrar.class)
public class CacheConfig implements CachingConfigurer{

	@Bean
	public EhCacheCacheManager ehcacheCacheManager() {
		CacheConfiguration cacheConfiguration = new CacheConfiguration();
		cacheConfiguration.setName("ehcache");
		cacheConfiguration.setMemoryStoreEvictionPolicy("LRU");
		cacheConfiguration.setMaxEntriesLocalHeap(100L);
		cacheConfiguration.setTimeToLiveSeconds(5*60*1000);

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
	public GuavaCache refreshableCache() {
		return new RefreshableCache("refreshableCache").setFixedInterval(AbstractCacheController.CACHE_REFRESH_DURATION);
	}

	@Bean
	public GuavaCache clusterCacheObject() {
		RefreshableCache clusterCache = new ClusterCache("clusterCacheObject", "127.0.0.1:8080","127.0.0.1:8081").setFixedInterval(AbstractCacheController.CACHE_REFRESH_DURATION);
		return clusterCache;
	}

	@Bean
	public GuavaCache clusterCache() {
		RefreshableCache clusterCache = new ClusterCache("clusterCache", "127.0.0.1:8080","127.0.0.1:8081").setFixedInterval(AbstractCacheController.CACHE_REFRESH_DURATION);
		return clusterCache;
	}

	@Bean
	public GuavaCacheManager guavaCacheManager() {
		//GuavaCacheManager cacheManager = new GuavaCacheManager();
		RefreshableCacheManager cacheManager = new RefreshableCacheManager();
		CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder().expireAfterWrite(AbstractCacheController.CACHE_REFRESH_DURATION, TimeUnit.MILLISECONDS).maximumSize(100);
		cacheManager.setCacheBuilder(builder);// 기본적인 GuavaCache 사용할때

		GuavaCache[] guavaCaches = Stream.of(refreshableCache(), clusterCache(), clusterCacheObject()).filter(Objects::nonNull).toArray(GuavaCache[]::new);
		cacheManager.setCaches( guavaCaches);

		return cacheManager;
	}

	@Bean
	@Override
	public CacheManager cacheManager() {
		List<CacheManager> cacheManagers = Stream.of(simpleCacheManager(), guavaCacheManager(), ehcacheCacheManager()).filter(Objects::nonNull).collect(Collectors.toList());

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
