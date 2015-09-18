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
public class SimpleCacheController extends AbstractCacheController {
	@RequestMapping("/get")
	public String get() {
		return getData(this.getClass().getCanonicalName() + " get");
	}
	@Cacheable("simpleCache")
	@RequestMapping("/simple")
	public String getcache() throws InterruptedException {
		Thread.sleep(300L);
		return getData(this.getClass().getCanonicalName() + " getcache sleep 300ms");
	}
}
