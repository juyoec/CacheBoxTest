package com.crassirostris.cache.refresh;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: Coupang
 * Date: 2015-11-06
 * Time: 오후 4:04
 */
@RestController
public class ClusterCacheAdapter {
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private CacheManager cacheManager;

	protected static final String REQUEST_URI = "/clustercache";
	@RequestMapping(name = REQUEST_URI + "/{cacheName}/{key}")
	public Object getClusterCache(@PathVariable("cacheName") String cacheName, @PathVariable("key") String key) throws ClassNotFoundException {

		Cache cache = cacheManager.getCache(cacheName);
		if (cache instanceof ClusterCache) {
			ClusterCache clusterCache = (ClusterCache)cache;
			String remoteAddr = request.getRemoteAddr();
			int remotePort = request.getRemotePort();
			clusterCache.addCluster(remoteAddr + ":" + remotePort);
			return clusterCache.get(key).get();
		}

		throw new ClassNotFoundException("ClusterCacheManager Not Found " + "cacheName : " + key );
	}
}
