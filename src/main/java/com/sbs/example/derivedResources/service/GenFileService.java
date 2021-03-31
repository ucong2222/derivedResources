package com.sbs.example.derivedResources.service;

import java.io.File;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sbs.example.derivedResources.dao.GenFileDao;
import com.sbs.example.derivedResources.dto.GenFile;
import com.sbs.example.derivedResources.dto.ResultData;
import com.sbs.example.derivedResources.util.Util;

@Service
public class GenFileService {
	@Value("${custom.genFileDirPath}")
	private String genFileDirPath;

	@Autowired
	private GenFileDao genFileDao;

	public ResultData saveMeta(String relTypeCode, int relId, String typeCode, String type2Code, int fileNo,
			String originFileName, String fileExtTypeCode, String fileExtType2Code, String fileExt, int fileSize,
			String fileDir) {

		Map<String, Object> param = Util.mapOf("relTypeCode", relTypeCode, "relId", relId, "typeCode", typeCode,
				"type2Code", type2Code, "fileNo", fileNo, "originFileName", originFileName, "fileExtTypeCode",
				fileExtTypeCode, "fileExtType2Code", fileExtType2Code, "fileExt", fileExt, "fileSize", fileSize,
				"fileDir", fileDir);
		genFileDao.saveMeta(param);

		int id = Util.getAsInt(param.get("id"), 0);
		return new ResultData("S-1", "성공하였습니다.", "id", id);
	}

	private void deleteGenFile(GenFile genFile) {
		String filePath = genFile.getFilePath(genFileDirPath);
		Util.delteFile(filePath);

		genFileDao.deleteFile(genFile.getId());
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
		String fileDir = Util.getNowYearMonthDateStr();
		ResultData saveMetaRd = saveMeta(relTypeCode, relId, typeCode, type2Code, fileNo, originFileName,
				fileExtTypeCode, fileExtType2Code, fileExt, fileSize, fileDir);
		int newGenFileId = (int) saveMetaRd.getBody().get("id");

		String fileName = newGenFileId + "." + fileExt;

		String destFileDirPath = genFileDirPath + "/" + relTypeCode + "/" + fileDir;
		File destFileDir = new File(destFileDirPath);

		if (destFileDir.exists() == false) {
			destFileDir.mkdirs();
		}

		String destFilePath = destFileDirPath + "/" + fileName;

		Util.moveFile(filePath, destFilePath);

		return saveMetaRd;
	}

	GenFile getGenFile(String relTypeCode, int relId, String typeCode, String type2Code, int fileNo) {
		return genFileDao.getGenFile(relTypeCode, relId, typeCode, type2Code, fileNo);
	}
}