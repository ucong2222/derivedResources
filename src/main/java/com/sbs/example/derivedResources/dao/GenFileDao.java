package com.sbs.example.derivedResources.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.sbs.example.derivedResources.dto.GenFile;

@Mapper
public interface GenFileDao {
	void saveMeta(Map<String, Object> param);

	GenFile getGenFileById(@Param("id") int id);

	void deleteFile(@Param("id") int id);

	GenFile getGenFile(@Param("relTypeCode") String relTypeCode, @Param("relId") int relId,
			@Param("typeCode") String typeCode, @Param("type2Code") String type2Code, @Param("fileNo") int fileNo);

	GenFile getGenFileByRelTypeCodeAndRelIdAndFileExtTypeCodeAndWidthAndHeight(@Param("relTypeCode") String relTypeCode,
			@Param("relId") int relId, @Param("fileExtTypeCode") String fileExtTypeCode, @Param("width") int width,
			@Param("height") int height);

	GenFile getGenFileByRelTypeCodeAndRelIdAndFileExtTypeCodeAndWidth(@Param("relTypeCode") String relTypeCode,
			@Param("relId") int relId, @Param("fileExtTypeCode") String fileExtTypeCode, @Param("width") int width);

	GenFile getGenFileByRelTypeCodeAndRelIdAndFileExtTypeCodeAndMaxWidth(@Param("relTypeCode") String relTypeCode,
			@Param("relId") int relId, @Param("fileExtTypeCode") String fileExtTypeCode,
			@Param("maxWidth") int maxWidth);
}