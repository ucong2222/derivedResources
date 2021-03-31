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

		if ( deriveRequest != null ) {
			GenFile originGenFile = getOriginGenFile(deriveRequest);

			if ( originGenFile != null ) {
				return originGenFile.getFilePath();
			}
		}

		return Util.downloadFileByHttp(originUrl, App.getTmpDirPath());
	}
}