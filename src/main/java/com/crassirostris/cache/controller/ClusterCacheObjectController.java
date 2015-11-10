package com.crassirostris.cache.controller;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 * User: Coupang
 * Date: 2015-11-09
 * Time: 오후 9:44
 */
@RestController
public class ClusterCacheObjectController extends AbstractCacheController {
	@Cacheable("clusterCacheObject")
	@RequestMapping("/clustercacheobject")
	public CustomObject getCache(@RequestParam("key") String key) throws InterruptedException {
		Thread.sleep(300L);
		String data = getData(this.getClass().getCanonicalName() + " getcache sleep 300ms");
		CustomObject obj = CustomObject.create(Integer.parseInt(key), "user" + key, "user" + key, "userNick" + key, data);
		return obj;
	}
}
