package com.sbs.example.derivedResources.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sbs.example.derivedResources.dto.DeriveRequest;
import com.sbs.example.derivedResources.dto.GenFile;
import com.sbs.example.derivedResources.service.DeriveRequestService;
import com.sbs.example.derivedResources.service.GenFileService;
import com.sbs.example.derivedResources.util.Util;

@RestController
public class UsrImgController {
	@Value("${custom.genFileDirPath}")
	private String genFileDirPath;

	@Value("${custom.tmpDirPath}")
	private String tmpDirPath;

	@Autowired
	private GenFileService genFileService;
	@Autowired
	private DeriveRequestService deriveRequestService;

	@RequestMapping("/img")
	public ResponseEntity<Resource> showImg(HttpServletRequest req, @RequestParam Map<String, Object> param)
			throws FileNotFoundException {
		String currentUrl = Util.getUrlFromHttpServletRequest(req);
		String queryString = req.getQueryString();
		String originUrl = queryString.split("url=")[1];

		DeriveRequest deriveRequest = deriveRequestService.getDeriveRequestByUrl(currentUrl);

		if (deriveRequest == null) {
			int width = Util.getAsInt(param.get("width"), 0);
			int height = Util.getAsInt(param.get("height"), 0);
			int maxWidth = Util.getAsInt(param.get("maxWidth"), 0);
			String filePath = deriveRequestService.getFilePathOrDownloadByOriginUrl(originUrl);

			deriveRequestService.save(currentUrl, originUrl, width, height, maxWidth, filePath);
		}

		deriveRequest = deriveRequestService.getDeriveRequestByUrl(currentUrl);
		GenFile originGenFile = deriveRequestService.getOriginGenFile(deriveRequest);

		return getClientCachedResponseEntity(originGenFile, req);
	}

	@GetMapping("/imgById")
	public ResponseEntity<Resource> downloadFile(int id, HttpServletRequest req) throws IOException {
		GenFile genFile = genFileService.getGenFile(id);

		return getClientCachedResponseEntity(genFile, req);
	}

	private ResponseEntity<Resource> getClientCachedResponseEntity(GenFile genFile, HttpServletRequest req)
			throws FileNotFoundException {
		String filePath = genFile.getFilePath();

		Resource resource = new InputStreamResource(new FileInputStream(filePath));

		// Try to determine file's content type
		String contentType = req.getServletContext().getMimeType(new File(filePath).getAbsolutePath());

		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok().cacheControl(CacheControl.maxAge(60 * 60 * 24 * 30, TimeUnit.SECONDS))
				.contentType(MediaType.parseMediaType(contentType)).body(resource);
	}
}