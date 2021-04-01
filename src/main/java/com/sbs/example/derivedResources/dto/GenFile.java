package com.sbs.example.derivedResources.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sbs.example.derivedResources.app.App;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GenFile {
	private int id;
	private String regDate;
	private String updateDate;
	private boolean delStatus;
	private String delDate;
	private String typeCode;
	private String type2Code;
	private String relTypeCode;
	private int relId;
	private String fileExtTypeCode;
	private String fileExtType2Code;
	private int fileSize;
	private int fileNo;
	private String fileExt;
	private String fileDir;
	private String originFileName;
	private int width;
	private int height;

	@JsonIgnore
	public String getFilePath() {
		return App.getGenFileDirPath() + getBaseFileUri();
	}

	@JsonIgnore
	private String getBaseFileUri() {
		return "/" + relTypeCode + "/" + fileDir + "/" + getFileName();
	}

	public String getFileName() {
		return id + "." + fileExt;
	}

	public String getForPrintUrl() {
		return "/gen" + getBaseFileUri() + "?updateDate=" + updateDate;
	}

	public String getDownloadUrl() {
		return "/common/genFile/doDownload?id=" + id;
	}
}