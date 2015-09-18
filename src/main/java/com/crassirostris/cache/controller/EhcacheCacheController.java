package com.crassirostris.cache.controller;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 * User: Coupang
 * Date: 2015-09-17
 * Time: 오후 8:09
 */
@RestController
public class EhcacheCacheController extends AbstractCacheController {
	@Cacheable("ehcache")
	@RequestMapping("/ehcache")
	public String getCache() throws InterruptedException {
		Thread.sleep(300L);
		return getData(this.getClass().getCanonicalName() + " getcache sleep 300ms");
	}
}
