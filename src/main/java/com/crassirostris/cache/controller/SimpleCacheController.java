package com.crassirostris.cache.controller;

import lombok.extern.slf4j.*;
import org.springframework.cache.annotation.*;
import org.springframework.web.bind.annotation.*;

import java.text.*;
import java.util.*;

/**
 * Created by crassirostris on 15. 9. 8..
 */
@Slf4j
@RestController
@RequestMapping("/simple")
public class SimpleCacheController {
	@RequestMapping("/get")
	public String get() {
		DateFormat format1 = DateFormat.getDateInstance(DateFormat.FULL);
		Date time = Calendar.getInstance().getTime();
		log.info("call simple get ");
		return format1.format(time);
	}
	@Cacheable("simpleCache")
	@RequestMapping("/getcache")
	public String getcache() throws InterruptedException {
		DateFormat format1 = DateFormat.getDateInstance(DateFormat.FULL);
		Date time = Calendar.getInstance().getTime();
		log.info("call simple cache get thread sleep 300ms");
		Thread.sleep(300L);
		return format1.format(time);
	}
}
