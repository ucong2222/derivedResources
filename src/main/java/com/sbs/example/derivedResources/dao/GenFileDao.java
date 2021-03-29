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
}