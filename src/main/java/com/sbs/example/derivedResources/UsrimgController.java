package com.sbs.example.derivedResources;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sbs.example.derivedResources.util.Util;

@RestController
public class UsrimgController {
	@Value("${custom.tmpDirPath}")
	private String tmpDirPath;

	@RequestMapping("/img")
	public String showImg(HttpServletRequest req, @RequestParam Map<String, Object> param) {
		String queryString = req.getQueryString();
		String url = queryString.split("url=")[1];

		String filePath = Util.downloadFileByHttp(url, tmpDirPath);

		return filePath;
	}
}
