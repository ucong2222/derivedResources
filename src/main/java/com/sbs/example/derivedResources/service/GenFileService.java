package com.sbs.example.derivedResources.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sbs.example.derivedResources.app.App;
import com.sbs.example.derivedResources.dao.GenFileDao;
import com.sbs.example.derivedResources.dto.GenFile;
import com.sbs.example.derivedResources.dto.ResultData;
import com.sbs.example.derivedResources.util.Util;

@Service
public class GenFileService {
	@Autowired
	private GenFileDao genFileDao;

	public ResultData saveMeta(String relTypeCode, int relId, String typeCode, String type2Code, int fileNo,
			String originFileName, String fileExtTypeCode, String fileExtType2Code, String fileExt, int fileSize,
			String fileDir, int width, int height) {

		Map<String, Object> param = Util.mapOf("relTypeCode", relTypeCode, "relId", relId, "typeCode", typeCode,
				"type2Code", type2Code, "fileNo", fileNo, "originFileName", originFileName, "fileExtTypeCode",
				fileExtTypeCode, "fileExtType2Code", fileExtType2Code, "fileExt", fileExt, "fileSize", fileSize,
				"fileDir", fileDir, "width", width, "height", height);
		genFileDao.saveMeta(param);

		int id = Util.getAsInt(param.get("id"), 0);
		return new ResultData("S-1", "성공하였습니다.", "id", id);
	}

	public GenFile getGenFile(int id) {
		return genFileDao.getGenFileById(id);
	}

	public ResultData save(String relTypeCode, int relId, String typeCode, String type2Code, int fileNo,
			String originFileName, String filePath) {
		String fileExtTypeCode = Util.getFileExtTypeCodeFromFileName(originFileName);
		String fileExtType2Code = Util.getFileExtType2CodeFromFileName(originFileName);
		String fileExt = Util.getFileExtFromFileName(originFileName);
		int fileSize = Util.getFileSize(filePath);

		int width = 0;
		int height = 0;

		if (fileExtTypeCode.equals("img")) {
			try {
				BufferedImage bufferedImage = ImageIO.read(new File(filePath));
				width = bufferedImage.getWidth();
				height = bufferedImage.getHeight();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		String fileDir = Util.getNowYearMonthDateStr();

		ResultData saveMetaRd = saveMeta(relTypeCode, relId, typeCode, type2Code, fileNo, originFileName,
				fileExtTypeCode, fileExtType2Code, fileExt, fileSize, fileDir, width, height);
		int newGenFileId = (int) saveMetaRd.getBody().get("id");

		if (fileDir.length() > 0)
			saveOnDisk(newGenFileId, relTypeCode, filePath, fileDir, fileExt);

		return saveMetaRd;
	}

	private void saveOnDisk(int newGenFileId, String relTypeCode, String filePath, String fileDir, String fileExt) {
		String fileName = newGenFileId + "." + fileExt;

		String destFileDirPath = App.getGenFileDirPath() + "/" + relTypeCode + "/" + fileDir;
		File destFileDir = new File(destFileDirPath);

		if (destFileDir.exists() == false) {
			destFileDir.mkdirs();
		}
		String destFilePath = destFileDirPath + "/" + fileName;

		Util.moveFile(filePath, destFilePath);
	}

	GenFile getGenFile(String relTypeCode, int relId, String typeCode, String type2Code, int fileNo) {
		return genFileDao.getGenFile(relTypeCode, relId, typeCode, type2Code, fileNo);
	}

	public GenFile getGenFileByRelTypeCodeAndRelIdAndFileExtTypeCodeAndWidthAndHeight(String relTypeCode, int relId,
			String fileExtTypeCode, int width, int height) {
		GenFile genFile = null;
		genFile = genFileDao.getGenFileByRelTypeCodeAndRelIdAndFileExtTypeCodeAndWidthAndHeight(relTypeCode, relId,
				fileExtTypeCode, width, height);

		if (genFile != null) {
			return genFile;
		}

		genFile = genFileDao.getGenFileByRelTypeCodeAndRelIdAndFileExtTypeCodeAndWidthAndHeight(relTypeCode, relId,
				fileExtTypeCode, width, height - 1);

		if (genFile != null) {
			return genFile;
		}

		genFile = genFileDao.getGenFileByRelTypeCodeAndRelIdAndFileExtTypeCodeAndWidthAndHeight(relTypeCode, relId,
				fileExtTypeCode, width, height + 1);

		if (genFile != null) {
			return genFile;
		}
		
		return null;
	}

}