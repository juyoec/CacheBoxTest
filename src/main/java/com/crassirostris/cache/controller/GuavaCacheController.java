package com.crassirostris.cache.controller;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Coupang
 * Date: 2015-09-08
 * Time: 오후 6:07
 */
@Slf4j
@RestController
public class GuavaCacheController extends AbstractCacheController {
	private ExecutorService executorService = Executors.newFixedThreadPool(4);
	LoadingCache<Long, String> cache = CacheBuilder.newBuilder().maximumSize(5).refreshAfterWrite(CACHE_REFRESH_DURATION, TimeUnit.MILLISECONDS).build(
			new CacheLoader<Long, String>() {
				public String load(Long key) throws InterruptedException { // no checked exception
					refreshLog();
					return getString();
				}

				public ListenableFuture<String> reload(final Long key, String prevNode) {
					ListenableFutureTask<String> task = ListenableFutureTask.create(new Callable<String>() {
						public String call() throws InterruptedException {
							refreshLog();
							return getString();
						}
					});
					executorService.execute(task);
					return task;
				}
			}
	);
	@RequestMapping("/guava")
	public String getCache() throws InterruptedException {
		return cache.getUnchecked(10L);
	}

	private String getString() throws InterruptedException {
		Thread.sleep(300L);
		return getData(this.getClass().getCanonicalName() + " getcache sleep 300ms");
	}

	private void refreshLog() {
		log.info("guava Cache refresh");
	}
}
