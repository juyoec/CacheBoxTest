package com.crassirostris.cache.controller;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 * User: Coupang
 * Date: 2015-09-09
 * Time: 오후 2:21
 */
@RestController
public class RefreshCacheController extends AbstractCacheController {
	@Cacheable("refreshableCache")
	@RequestMapping("/refresh")
	public String getCache() throws InterruptedException {
		Thread.sleep(300L);
		return getData(this.getClass().getCanonicalName() + " getcache sleep 300ms");
	}
}
