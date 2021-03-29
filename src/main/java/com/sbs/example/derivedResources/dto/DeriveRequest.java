package com.sbs.example.derivedResources.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeriveRequest {
	private int id;
	private String regDate;
	private String updateDate;
	private String url;
	private String originUrl;
	private int width;
	private int height;
	private int maxWidth;
}