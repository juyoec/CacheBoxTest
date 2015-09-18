package com.crassirostris.cache.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Coupang
 * Date: 2015-09-08
 * Time: 오후 6:08
 */
@Slf4j
@RequestMapping("/getcache")
public abstract class AbstractCacheController {
	public static final int CACHE_REFRESH_DURATION = 60*1000;
	protected String getData(String cacheName) {
		DateFormat format1 = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
		Date time = Calendar.getInstance().getTime();
		log.info("call "+cacheName);
		return cacheName + "  "+format1.format(time);
	}
}
