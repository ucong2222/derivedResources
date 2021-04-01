package com.sbs.example.derivedResources.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.sbs.example.derivedResources.dto.DeriveRequest;

@Mapper
public interface DeriveRequestDao {
	DeriveRequest getDeriveRequestByUrl(@Param("url") String url);

	int save(Map<String, Object> param);
	
	DeriveRequest getDeriveRequestByOriginUrl(@Param("originUrl") String originUrl);
	
	void updateDerivedGenFileId(@Param("id") int id, @Param("genFileId") int genFileId);

	DeriveRequest getDeriveRequestById(@Param("id") int id);
}