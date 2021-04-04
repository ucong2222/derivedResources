package com.sbs.example.derivedResources.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sbs.example.derivedResources.dto.DeriveRequest;
import com.sbs.example.derivedResources.dto.GenFile;
import com.sbs.example.derivedResources.exception.DownloadFileFailException;
import com.sbs.example.derivedResources.service.DeriveRequestService;
import com.sbs.example.derivedResources.service.GenFileService;
import com.sbs.example.derivedResources.util.Util;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class UsrImgController {
	@Value("${spring.profiles.active:Unknown}")
	private String activeProfile;

	@Value("${custom.genFileDirPath}")
	private String genFileDirPath;

	@Value("${custom.tmpDirPath}")
	private String tmpDirPath;

	@Autowired
	private GenFileService genFileService;
	@Autowired
	private DeriveRequestService deriveRequestService;

	@GetMapping("/img")
	public ResponseEntity showImg(HttpServletRequest req, @RequestParam("url") String originUrl,
			@RequestParam(defaultValue = "0") int width, @RequestParam(defaultValue = "0") int height,
			@RequestParam(defaultValue = "0") int maxWidth,
			@RequestParam(defaultValue = "No Image", required = false) String failText,
			@RequestParam(defaultValue = "300", required = false) int failWidth,
			@RequestParam(defaultValue = "300", required = false) int failHeight,
			@RequestParam(defaultValue = "A3A3A3", required = false) String failTextColor,
			@RequestParam(defaultValue = "CCCCCC", required = false) String failBgColor) throws FileNotFoundException {
		String currentUrl = Util.getUrlFromHttpServletRequest(req);

		log.debug("activeProfile : " + activeProfile);

		if (activeProfile.equals("production") && currentUrl.contains("://localhost")) {
			return fallbackImgEntity(failWidth, failHeight, failText, failTextColor, failBgColor);
		}
		
		DeriveRequest deriveRequest = deriveRequestService.getDeriveRequestByUrl(currentUrl);

		if (deriveRequest == null) {
			int newDeriveRequestId = 0;
			try {
				newDeriveRequestId = deriveRequestService.save(currentUrl, originUrl, width, height, maxWidth);
			} catch (Exception e) {
				return fallbackImgEntity(failWidth, failHeight, failText, failTextColor, failBgColor);
			}
			
			deriveRequest = deriveRequestService.getDeriveRequestById(newDeriveRequestId);

			DeriveRequest originDeriveRequest = null;

			if (deriveRequest.isOriginStatus()) {
				originDeriveRequest = deriveRequest;
			} else {
				originDeriveRequest = deriveRequestService.getDeriveRequestByOriginUrl(originUrl);
			}

			GenFile derivedGenFile = null;

			if (width > 0 && height > 0) {
				derivedGenFile = deriveRequestService.getDerivedGenFileByWidthAndHeightOrMake(originDeriveRequest,
						width, height);
			} else if (width > 0) {
				derivedGenFile = deriveRequestService.getDerivedGenFileByWidthOrMake(originDeriveRequest, width);
			} else if (maxWidth > 0) {
				derivedGenFile = deriveRequestService.getDerivedGenFileByMaxWidthOrMake(originDeriveRequest, maxWidth);
			} else {
				derivedGenFile = deriveRequestService.getOriginGenFile(originDeriveRequest);
			}

			deriveRequestService.updateDerivedGenFileId(newDeriveRequestId, derivedGenFile.getId());

			return getClientCachedResponseEntity(derivedGenFile, req);
		}

		GenFile originGenFile = genFileService.getGenFile(deriveRequest.getGenFileId());
		return getClientCachedResponseEntity(originGenFile, req);

	}
	
	private ResponseEntity fallbackImgEntity(int failWidth, int failHeight, String failText, String failTextColor,
			String failBgColor) {
		return Util.httpResponseEntityToOther("https://via.placeholder.com/" + failWidth + "x" + failHeight + "/"
				+ failBgColor + "/" + failTextColor + "?text=" + Util.getUrlEncoded(failText));
	}

	@ApiOperation(value = "이미지번호로 이미지 출력", notes = "입력받은 id에 해당하는 genFile을 출력합니다.")
	@GetMapping("/imgById")
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", value = "genFileId", required = true) })
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