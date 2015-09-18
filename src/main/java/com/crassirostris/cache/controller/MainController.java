package com.crassirostris.cache.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by crassirostris on 15. 9. 8..
 */
@Controller
public class MainController {
	@RequestMapping("/")
	public String hello() {
		return "hello";
	}
}
