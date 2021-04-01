package com.sbs.example.derivedResources.app;

import java.util.UUID;

public class App {
	private static String genFileDirPath;
	private static String tmpDirPath;

	public static void init(String genFileDirPath, String tmpDirPath) {
		App.genFileDirPath = genFileDirPath;
		App.tmpDirPath = tmpDirPath;
	}

	public static String getGenFileDirPath() {
		return genFileDirPath;
	}

	public static String getTmpDirPath() {
		return tmpDirPath;
	}

	public static boolean isInGenFileDir(String filePath) {
		return filePath.indexOf(genFileDirPath) != -1;
	}
	
	public static String getNewTmpFilePath(String fileExt) {
		return tmpDirPath + "/" + UUID.randomUUID() + "." + fileExt;
	}
}