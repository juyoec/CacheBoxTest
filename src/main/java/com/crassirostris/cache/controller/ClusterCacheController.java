package com.crassirostris.cache.controller;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 * User: Coupang
 * Date: 2015-11-05
 * Time: 오후 6:34
 */
@RestController
public class ClusterCacheController extends AbstractCacheController {
	@Cacheable("clusterCache")
	@RequestMapping("/clustercache")
	public String getCache() throws InterruptedException {
		Thread.sleep(300L);
		return getData(this.getClass().getCanonicalName() + " getcache sleep 300ms");
	}
}
