package com.crassirostris.cache.refresh;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 * User: Coupang
 * Date: 2015-10-14
 * Time: 오후 9:51
 */
@RestController
public class ClusterCacheController {
	@Autowired
	private CacheManager cacheManager;
	@RequestMapping("/clustercache/{cacheName}/{key}")
	public Object get(@PathVariable("cacheName") String cacheName, @PathVariable("key") String key) {
		Preconditions.checkArgument(StringUtils.isNotEmpty(cacheName) && StringUtils.isNotEmpty(key));
		Cache cache = cacheManager.getCache(cacheName);
		return cache.get(key);
	}
}
