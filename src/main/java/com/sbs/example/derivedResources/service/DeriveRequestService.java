package com.sbs.example.derivedResources.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sbs.example.derivedResources.dao.DeriveRequestDao;
import com.sbs.example.derivedResources.dto.DeriveRequest;
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

	public void save(String url, int width, int height, int maxWidth, String filePath) {
		Map<String, Object> param = Util.mapOf("url", url, "width", width, "height", height, "maxWidth", maxWidth);

		deriveRequestDao.saveMeta(param);
		int newDeriveRequestId = Util.getAsInt(param.get("id"), 0);

		String originFileName = Util.getFileNameFromUrl(url);

		genFileService.save("deriveRequest", newDeriveRequestId, "common", "origin", 1, originFileName, filePath);
	}
}