package com.sbs.example.derivedResources.config;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.sbs.example.derivedResources.app.App;

@Component
@Order(0)
class MyApplicationListener implements ApplicationListener<ApplicationReadyEvent> {
	@Value("${custom.genFileDirPath}")
	private String genFileDirPath;

	@Value("${custom.tmpDirPath}")
	private String tmpDirPath;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		App.init(genFileDirPath, tmpDirPath);

		File genFileDir = new File(App.getGenFileDirPath());
		File tmpDir = new File(App.getTmpDirPath());

		if (genFileDir.exists() == false) {
			genFileDir.mkdirs();
		}

		if (tmpDir.exists() == false) {
			tmpDir.mkdirs();
		}
	}
}