package com.sbs.example.derivedResources.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sbs.example.derivedResources.app.App;
import com.sbs.example.derivedResources.dao.DeriveRequestDao;
import com.sbs.example.derivedResources.dto.DeriveRequest;
import com.sbs.example.derivedResources.dto.GenFile;
import com.sbs.example.derivedResources.dto.ResultData;
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

	public GenFile getOriginGenFile(DeriveRequest deriveRequest) {
		DeriveRequest originDeriveRequest = null;

		if ( deriveRequest.isOriginStatus() ) {
			originDeriveRequest = deriveRequest;
		}
		else {
			originDeriveRequest = deriveRequestDao.getDeriveRequestByOriginUrl(deriveRequest.getOriginUrl());
		}

		return genFileService.getGenFile("deriveRequest", originDeriveRequest.getId(), "common", "origin", 1);
	}

	public String getFilePathOrDownloadByOriginUrl(String originUrl, DeriveRequest originDeriveRequest) {
		if (originDeriveRequest != null) {
			GenFile originGenFile = getOriginGenFile(originDeriveRequest);

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

		return makeDerivedGenFileByWidthAndHeight(deriveRequest, width, height);
	}

	public GenFile getDerivedGenFileByWidthOrMake(DeriveRequest deriveRequest, int width) {
		GenFile derivedGenFile = getDerivedGenFileByWidth(deriveRequest, width);

		if (derivedGenFile != null) {
			return derivedGenFile;
		}

		return makeDerivedGenFileByWidth(deriveRequest, width);
	}

	public GenFile getDerivedGenFileByMaxWidthOrMake(DeriveRequest deriveRequest, int maxWidth) {
		GenFile derivedGenFile = getDerivedGenFileByMaxWidth(deriveRequest, maxWidth);

		if (derivedGenFile != null) {
			return derivedGenFile;
		}

		return makeDerivedGenFileByWidth(deriveRequest, maxWidth);
	}

	public GenFile getDerivedGenFileByWidthAndHeight(DeriveRequest deriveRequest, int width, int height) {

		DeriveRequest originDeriveRequest = deriveRequestDao.getDeriveRequestByOriginUrl(deriveRequest.getOriginUrl());

		return genFileService.getGenFileByRelTypeCodeAndRelIdAndFileExtTypeCodeAndWidthAndHeight("deriveRequest",
				originDeriveRequest.getId(), "img", width, height);
	}

	public GenFile getDerivedGenFileByWidth(DeriveRequest deriveRequest, int width) {

		GenFile originGenFile = getOriginGenFile(deriveRequest);
		
		int originWidth = originGenFile.getWidth();
		int originHeight = originGenFile.getHeight();
		int height = originHeight * width / originWidth;

		return genFileService.getGenFileByRelTypeCodeAndRelIdAndFileExtTypeCodeAndWidthAndHeight("deriveRequest",
				deriveRequest.getId(), "img", width, height);
	}

	public GenFile getDerivedGenFileByMaxWidth(DeriveRequest deriveRequest, int maxWidth) {
		GenFile originGenFile = getOriginGenFile(deriveRequest);

		int originWidth = originGenFile.getWidth();

		if (originWidth <= maxWidth) {
			return originGenFile;
		}

		int originHeight = originGenFile.getHeight();
		int height = originHeight * maxWidth / originWidth;

		return genFileService.getGenFileByRelTypeCodeAndRelIdAndFileExtTypeCodeAndWidthAndHeight("deriveRequest",
				deriveRequest.getId(), "img", maxWidth, height);
	}

	private GenFile makeDerivedGenFileByWidthAndHeight(DeriveRequest deriveRequest, int width, int height) {
		GenFile originGenFile = getOriginGenFile(deriveRequest);

		String destFilePath = App.getNewTmpFilePath(originGenFile.getFileExt());

		Util.resizeImgWidth(originGenFile.getFilePath(), destFilePath, width, height);

		ResultData saveRd = genFileService.save("deriveRequest", deriveRequest.getId(), "common", "derived", 0,
				originGenFile.getOriginFileName(), destFilePath);
		int newGenId = (int) saveRd.getBody().get("id");

		return genFileService.getGenFile(newGenId);
	}

	private GenFile makeDerivedGenFileByWidth(DeriveRequest deriveRequest, int width) {
		GenFile originGenFile = getOriginGenFile(deriveRequest);

		String destFilePath = App.getNewTmpFilePath(originGenFile.getFileExt());

		int originWidth = originGenFile.getWidth();
		int originHeight = originGenFile.getHeight();

		int height = originHeight * width / originWidth;
		Util.resizeImgWidth(originGenFile.getFilePath(), destFilePath, width, height);

		ResultData saveRd = genFileService.save("deriveRequest", deriveRequest.getId(), "common", "derived", 0,
				originGenFile.getOriginFileName(), destFilePath);
		int newGenId = (int) saveRd.getBody().get("id");

		return genFileService.getGenFile(newGenId);
	}
	
	public DeriveRequest getDeriveRequestByOriginUrl(String originUrl) {
		return deriveRequestDao.getDeriveRequestByOriginUrl(originUrl);
	}

	public int save(String url, String originUrl, int width, int height, int maxWidth) {
		DeriveRequest originDeriveRequest = getDeriveRequestByOriginUrl(originUrl);
		boolean originStatus = originDeriveRequest == null;

		String filePath = getFilePathOrDownloadByOriginUrl(originUrl, originDeriveRequest);

		Map<String, Object> param = Util.mapOf("url", url, "originUrl", originUrl, "originStatus", originStatus, "width", width, "height", height,
				"maxWidth", maxWidth);

		deriveRequestDao.save(param);
		int newDeriveRequestId = Util.getAsInt(param.get("id"), 0);

		if (originStatus) {
			String originFileName = Util.getFileNameFromUrl(originUrl);
			
			if ( Util.getFileExtTypeCodeFromFileName(originFileName).equals("img") == false ) {
				originFileName = Util.getFileNameFromUrl(filePath);
			}
			
			genFileService.save("deriveRequest", newDeriveRequestId, "common", "origin", 1, originFileName, filePath);
		}

		return newDeriveRequestId;
	}

	public void updateDerivedGenFileId(int id, int genFileId) {
		deriveRequestDao.updateDerivedGenFileId(id, genFileId);
	}

	public DeriveRequest getDeriveRequestById(int id) {
		return deriveRequestDao.getDeriveRequestById(id);
	}
}