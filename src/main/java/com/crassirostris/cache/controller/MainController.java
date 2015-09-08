package com.crassirostris.cache.controller;

import org.springframework.web.bind.annotation.*;

/**
 * Created by crassirostris on 15. 9. 8..
 */
@RestController
public class MainController {
	@RequestMapping("/")
	public String hello() {
		return "CacheManagerTest";
	}
}
