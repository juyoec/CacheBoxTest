package com.crassirostris.cache.refresh;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.InvalidObjectException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: crassirostris
 * Date: 2015-10-14
 * Time: 오후 8:27
 */
@Slf4j
public class ClusterCache extends RefreshableCache {
	private List<String> cluster;
	private final String thisCluster;
	private RestTemplate dataRestTemplate;

	public ClusterCache(String name) throws UnknownHostException {
		super(name);
		InetAddress addr = InetAddress.getLocalHost();
		thisCluster = addr.getHostAddress();
		setRestTemplate();
		cluster = Lists.newArrayList();
	}
	public ClusterCache(String name, String... cluster) throws UnknownHostException {
		this(name);
		this.cluster = Lists.newArrayList(cluster);
	}

	private void setRestTemplate() {
		RestTemplate template = new RestTemplate();
		template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		template.getMessageConverters().add(new StringHttpMessageConverter());
		dataRestTemplate = template;
	}

	@Override
	public ValueWrapper get(Object key) {
		Class cls = RefreshableCacheHelper.getTargetMethodReturnClase(this.getName());
		Object o = get(key, cls);
		return (o != null ? new SimpleValueWrapper(fromStoreValue(o)) : null);
	}

	@Override
	public <T> T get(Object key, Class<T> type) {
		if (isThisCluster(key)) {
			return super.get(key, type);
		}

		int targetClusterIndex = getTargetClusterIndex(key);
		String targetCluster = cluster.get(targetClusterIndex);

		T data = null;
		try {
			data = getData(targetCluster, key, type);
			if (data == null) {
				throw new InvalidObjectException(targetCluster + " is not found data ("+key+") retry local cluster");
			}
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			cluster.remove(targetClusterIndex);
			sortingCluster();
			return super.get(key, type);
		}

		return data;
	}

	private <T> T getData(String targetCluster, Object key, Class<T> type) {
		String url = getUrl(targetCluster, key);
		Object forObject = dataRestTemplate.getForObject(url, type);
		return (T)forObject;
	}

	private void sortingCluster() {
		Collections.sort(cluster);
	}

	private String getUrl(String targetCluster, Object key) {
		return String.format("%s%s/%s/%s", targetCluster, ClusterCacheAdapter.REQUEST_URI, this.getName(), key);
	}

	private boolean isThisCluster(Object key) {
		int targetClusterIndex = getTargetClusterIndex(key);
		int thisClusterIndex = getThisClusterIndex();
		return targetClusterIndex == thisClusterIndex;
	}

	private int getThisClusterIndex() {
		for(int i = 0; i< cluster.size(); i++) {
			if (StringUtils.equals(cluster.get(i), thisCluster)) {
				return i;
			}
		}
		throw new IllegalStateException("ClusterCache: Invalid setting Cluster properties!");
	}

	private int getTargetClusterIndex(Object key) {
		int binary = Integer.parseInt(key.toString());
		int targetClusterIndex = binary % cluster.size();
		return targetClusterIndex;
	}

	public void addCluster(String s) {
		if (cluster.contains(s)) {
			return;
		}
		cluster.add(s);
		sortingCluster();
	}
}
