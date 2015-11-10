package com.crassirostris.cache.controller;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * User: crassirostris
 * Date: 2015-10-14
 * Time: 오후 6:37
 */
@RestController
public class RefreshableCacheController extends AbstractCacheController {
	@Cacheable("refreshableCache")
	@RequestMapping("/refreshablecache")
	public String getCache() throws InterruptedException {
		Thread.sleep(300L);
		return getData(this.getClass().getCanonicalName() + " getcache sleep 300ms");
	}
}
