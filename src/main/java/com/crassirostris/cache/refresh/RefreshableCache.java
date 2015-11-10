package com.crassirostris.cache.refresh;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.context.ApplicationContext;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * need regist BeanFactory
 * User: crassirostris
 * Date: 2015-10-14
 * Time: 오후 5:42
 */
@Slf4j
public class RefreshableCache extends GuavaCache {
	@Autowired
	@Setter // test 용
	private ApplicationContext ac;

	private final ConcurrentMap<Object, Object> tempStore = new ConcurrentHashMap<>(128);

	private Date lastScheduleWorkedTime = Calendar.getInstance().getTime();

	private ExecutorService executorService = Executors.newFixedThreadPool(3);

	// default FIXED_DELAY
	@Getter
	@Setter
	@Accessors(chain = true)
	private DelayType delayType = DelayType.FIXED_DELAY;
	// default 1hour
	@Getter
	@Setter
	@Accessors(chain = true)
	private long fixedInterval = 3600000;
	@Getter
	@Setter
	@Accessors(chain = true)
	private String cronExpression = "0 3 0/1 * * *";

	public RefreshableCache(String name) {
		this(name, true);
	}

	public RefreshableCache(String name, boolean allowNullValues) {
		super(name, CacheBuilder.newBuilder().build(), allowNullValues);
	}

	@Override
	public ValueWrapper get(Object key) {
		// refresh 중 요청이 들어올경우 old data를 뿌려준다
		Class cls = RefreshableCacheHelper.getTargetMethodReturnClase(this.getName());
		Object o = get(key, cls);
		return (o != null ? new SimpleValueWrapper(fromStoreValue(o)) : null);
	}

	@Override
	public <T> T get(Object key, Class<T> type) {
		// refresh 중 요청이 들어올경우 old data를 뿌려준다
		if (tempStore.containsKey(key)) {
			log.info(getName() + " is refreshing...to hit will be return old data...");
			Object oldValue = tempStore.get(key);
			if (oldValue != null && type != null && !type.isInstance(oldValue)) {
				return (T) oldValue;
			}
		}
		return super.get(key, type);
	}

	protected void setWorkTime() {
		this.lastScheduleWorkedTime = Calendar.getInstance().getTime();
	}

	public void processRefresh() {
		setWorkTime();
		log.info(String.format("%s refresh start... All Key counted %d.", this.getName(), getNativeCache().asMap().keySet().size()));
		Set<Object> objects = null;
		objects = getNativeCache().asMap().keySet();
		for (final Object key : objects) {
			log.info(String.format("%s refresh Cache in %s", key, this.getName()));
			Object ifPresent = getNativeCache().getIfPresent(key);
			if (ifPresent == null) {
				continue;
			}
			tempStore.put(key, ifPresent);
			getNativeCache().invalidate(key);
			executorService.execute(new Runnable() {
				@Override
				public void run() {
					try {
						Object result = RefreshableCacheHelper.excuteTargetMethod(ac, key, getName());
						Preconditions.checkArgument(result != null, String.format("key : %s is crash!! in %s", key, getName()));
						put(key, result);
						tempStore.remove(key);
						log.info(String.format("%s : %s ended refresh", getName(), key));
					} catch (Exception e) {
						put(key, tempStore.get(key));
						tempStore.remove(key);
						log.warn(String.format("%s : %s failed refresh!!", getName(), key));
					}
				}
			});
		}
	}
}
