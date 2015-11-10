package com.crassirostris.cache.controller;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * User: crassirostris
 * Date: 2015-09-09
 * Time: 오후 2:21
 */
@RestController
public class GuavaCacheManagerController extends AbstractCacheController {
	@Cacheable("guavaCacheManager")
	@RequestMapping("/guavacachemanager")
	public String getCache() throws InterruptedException {
		Thread.sleep(300L);
		return getData(this.getClass().getCanonicalName() + " getcache sleep 300ms");
	}
}
