package com.sbs.example.derivedResources.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;

public class Util {
	public static String downloadFileByHttp(String fileUrl, String outputDir) {
		String filePath = outputDir + "/" + getFileNameFromUrl(fileUrl);

		try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
			ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(fileUrl).openStream());
			FileChannel fileChannel = fileOutputStream.getChannel();
			fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return "";
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}

		return filePath;
	}

	private static String getFileNameFromUrl(String fileUrl) {
		try {
			return Paths.get(new URI(fileUrl).getPath()).getFileName().toString();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return "";
		}
	}
}