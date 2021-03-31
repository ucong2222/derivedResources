package com.sbs.example.derivedResources.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sbs.example.derivedResources.app.App;
import com.sbs.example.derivedResources.dao.DeriveRequestDao;
import com.sbs.example.derivedResources.dto.DeriveRequest;
import com.sbs.example.derivedResources.dto.GenFile;
import com.sbs.example.derivedResources.util.Util;

@Service
public class DeriveRequestService {
	@Autowired
	private DeriveRequestDao deriveRequestDao;
	@Autowired
	private GenFileService genFileService;

	public DeriveRequest getDeriveRequestByUrl(String url) {
		return deriveRequestDao.getDeriveRequestByUrl(url);
	}

	public void save(String url, String originUrl, int width, int height, int maxWidth, String filePath) {
		Map<String, Object> param = Util.mapOf("url", url, "originUrl", originUrl, "width", width, "height", height,
				"maxWidth", maxWidth);

		deriveRequestDao.saveMeta(param);
		boolean isNewBornFile = App.isInGenFileDir(filePath) == false;

		if (isNewBornFile) {
			int newDeriveRequestId = Util.getAsInt(param.get("id"), 0);
			String originFileName = Util.getFileNameFromUrl(originUrl);
			genFileService.save("deriveRequest", newDeriveRequestId, "common", "origin", 1, originFileName, filePath);
		}
	}

	public GenFile getOriginGenFile(DeriveRequest deriveRequest) {
		DeriveRequest originDeriveRequest = deriveRequestDao.getDeriveRequestByOriginUrl(deriveRequest.getOriginUrl());

		return genFileService.getGenFile("deriveRequest", originDeriveRequest.getId(), "common", "origin", 1);
	}

	public String getFilePathOrDownloadByOriginUrl(String originUrl) {
		DeriveRequest deriveRequest = deriveRequestDao.getDeriveRequestByOriginUrl(originUrl);

		if (deriveRequest != null) {
			GenFile originGenFile = getOriginGenFile(deriveRequest);

			if (originGenFile != null) {
				return originGenFile.getFilePath();
			}
		}

		return Util.downloadFileByHttp(originUrl, App.getTmpDirPath());
	}

	public GenFile getDerivedGenFileByWidthAndHeightOrMake(DeriveRequest deriveRequest, int width, int height) {

		GenFile derivedGenFile = getDerivedGenFileByWidthAndHeight(deriveRequest, width, height);

		if (derivedGenFile != null) {
			return derivedGenFile;
		}

		return null;
	}

	public GenFile getDerivedGenFileByWidthOrMake(DeriveRequest deriveRequest, int width) {
		GenFile derivedGenFile = getDerivedGenFileByWidth(deriveRequest, width);

		if (derivedGenFile != null) {
			return derivedGenFile;
		}

		return null;
	}

	public GenFile getDerivedGenFileByMaxWidthOrMake(DeriveRequest deriveRequest, int maxWidth) {
		GenFile derivedGenFile = getDerivedGenFileByMaxWidth(deriveRequest, maxWidth);

		if (derivedGenFile != null) {
			return derivedGenFile;
		}

		return null;
	}

	public GenFile getDerivedGenFileByWidthAndHeight(DeriveRequest deriveRequest, int width, int height) {

		DeriveRequest originDeriveRequest = deriveRequestDao.getDeriveRequestByOriginUrl(deriveRequest.getOriginUrl());

		return genFileService.getGenFileByRelTypeCodeAndRelIdAndFileExtTypeCodeAndWidthAndHeight("deriveRequest",
				originDeriveRequest.getId(), "img", width, height);
	}

	public GenFile getDerivedGenFileByWidth(DeriveRequest deriveRequest, int width) {

		DeriveRequest originDeriveRequest = deriveRequestDao.getDeriveRequestByOriginUrl(deriveRequest.getOriginUrl());

		return genFileService.getGenFileByRelTypeCodeAndRelIdAndFileExtTypeCodeAndWidth("deriveRequest",
				originDeriveRequest.getId(), "img", width);
	}

	public GenFile getDerivedGenFileByMaxWidth(DeriveRequest deriveRequest, int maxWidth) {
		DeriveRequest originDeriveRequest = deriveRequestDao.getDeriveRequestByOriginUrl(deriveRequest.getOriginUrl());

		return genFileService.getGenFileByRelTypeCodeAndRelIdAndFileExtTypeCodeAndMaxWidth("deriveRequest",
				originDeriveRequest.getId(), "img", maxWidth);
	}
}