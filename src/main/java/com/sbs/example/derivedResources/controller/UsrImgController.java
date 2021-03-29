package com.sbs.example.derivedResources.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sbs.example.derivedResources.dto.DeriveRequest;
import com.sbs.example.derivedResources.service.DeriveRequestService;
import com.sbs.example.derivedResources.service.GenFileService;
import com.sbs.example.derivedResources.util.Util;

@RestController
public class UsrImgController {
	@Value("${custom.tmpDirPath}")
	private String tmpDirPath;

	@Autowired
	private GenFileService genFileService;
	@Autowired
	private DeriveRequestService deriveRequestService;

	@RequestMapping("/img")
	public DeriveRequest showImg(HttpServletRequest req, @RequestParam Map<String, Object> param) {
		String queryString = req.getQueryString();
		String url = queryString.split("url=")[1];

		DeriveRequest deriveRequest = deriveRequestService.getDeriveRequestByUrl(url);

		if (deriveRequest == null) {
			int width = Util.getAsInt(param.get("width"), 0);
			int height = Util.getAsInt(param.get("height"), 0);
			int maxWidth = Util.getAsInt(param.get("maxWidth"), 0);
			String filePath = Util.downloadFileByHttp(url, tmpDirPath);

			deriveRequestService.save(url, width, height, maxWidth, filePath);
		}

		deriveRequest = deriveRequestService.getDeriveRequestByUrl(url);

		return deriveRequest;
	}
}