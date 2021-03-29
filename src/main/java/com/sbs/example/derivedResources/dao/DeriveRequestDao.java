package com.sbs.example.derivedResources.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.sbs.example.derivedResources.dto.DeriveRequest;

@Mapper
public interface DeriveRequestDao {
	DeriveRequest getDeriveRequestByUrl(@Param("url") String url);

	void saveMeta(Map<String, Object> param);
}