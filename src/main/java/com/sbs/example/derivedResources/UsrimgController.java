package com.sbs.example.derivedResources;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsrimgController {
	@RequestMapping("/img")
	public String showImg() {
		return "안녕";
	}
}
