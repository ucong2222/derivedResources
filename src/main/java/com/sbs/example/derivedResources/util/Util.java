package com.sbs.example.derivedResources.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.tika.Tika;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.sbs.example.derivedResources.exception.DownloadFileFailException;

public class Util {
	public static String downloadFileByHttp(String fileUrl, String outputDir) {
		String originFileName = getFileNameFromUrl(fileUrl);
		String fileExt = getFileExt(originFileName);

		if (fileExt.length() == 0) {
			fileExt = "tmp";
		}

		String tempFileName = UUID.randomUUID() + "." + fileExt;
		String filePath = outputDir + "/" + tempFileName;

		try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
			ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(fileUrl).openStream());
			FileChannel fileChannel = fileOutputStream.getChannel();
			fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
		} catch (FileNotFoundException e) {
			throw new DownloadFileFailException();
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
		
		if (fileExt.equals("tmp")) {
			String ext = getFileExt(new File(filePath));

			String newFilePath = filePath.replaceAll("\\.tmp", "\\." + ext);
			moveFile(filePath, newFilePath);
			filePath = newFilePath;
		}

		return filePath;
	}
	
	private static String getFileExt(File file) {
		Tika tika = new Tika();
		String mimeType = "";
		try {
			mimeType = tika.detect(file);
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
		String ext = mimeType.replaceAll("image/", "");
		ext = ext.replaceAll("jpeg", "jpg");

		return ext;
	}

	private static String getFileExt(String fileName) {
		int pos = fileName.lastIndexOf(".");

		if (pos == -1) {
			return "";
		}

		String ext = fileName.substring(pos + 1).trim();
		
		return ext;
	}

	public static String getFileNameFromUrl(String fileUrl) {
		try {
			return Paths.get(new URI(fileUrl).getPath()).getFileName().toString();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return "";
		}
	}

	public static Map<String, Object> mapOf(Object... args) {
		if (args.length % 2 != 0) {
			throw new IllegalArgumentException("인자를 짝수개 입력해주세요.");
		}

		int size = args.length / 2;

		Map<String, Object> map = new LinkedHashMap<>();

		for (int i = 0; i < size; i++) {
			int keyIndex = i * 2;
			int valueIndex = keyIndex + 1;

			String key;
			Object value;

			try {
				key = (String) args[keyIndex];
			} catch (ClassCastException e) {
				throw new IllegalArgumentException("키는 String으로 입력해야 합니다. " + e.getMessage());
			}

			value = args[valueIndex];

			map.put(key, value);
		}

		return map;
	}

	public static int getAsInt(Object object, int defaultValue) {
		if (object instanceof BigInteger) {
			return ((BigInteger) object).intValue();
		} else if (object instanceof Double) {
			return (int) Math.floor((double) object);
		} else if (object instanceof Float) {
			return (int) Math.floor((float) object);
		} else if (object instanceof Long) {
			return (int) object;
		} else if (object instanceof Integer) {
			return (int) object;
		} else if (object instanceof String) {
			return Integer.parseInt((String) object);
		}

		return defaultValue;
	}

	public static String getUrlEncoded(String str) {
		try {
			return URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return str;
		}
	}

	public static <T> T ifNull(T data, T defaultValue) {
		return data != null ? data : defaultValue;
	}

	public static <T> T reqAttr(HttpServletRequest req, String attrName, T defaultValue) {
		return (T) ifNull(req.getAttribute(attrName), defaultValue);
	}

	public static boolean isEmpty(Object data) {
		if (data == null) {
			return true;
		}

		if (data instanceof String) {
			String strData = (String) data;

			return strData.trim().length() == 0;
		} else if (data instanceof Integer) {
			Integer integerData = (Integer) data;

			return integerData != 0;
		} else if (data instanceof List) {
			List listData = (List) data;

			return listData.isEmpty();
		} else if (data instanceof Map) {
			Map mapData = (Map) data;

			return mapData.isEmpty();
		}

		return true;
	}

	public static <T> T ifEmpty(T data, T defaultValue) {
		if (isEmpty(data)) {
			return defaultValue;
		}

		return data;
	}

	public static String getFileExtTypeCodeFromFileName(String fileName) {
		String ext = getFileExtFromFileName(fileName).toLowerCase();

		switch (ext) {
		case "jpeg":
		case "jpg":
		case "gif":
		case "png":
		case "svg":
			return "img";
		case "mp4":
		case "avi":
		case "mov":
			return "video";
		case "mp3":
			return "audio";
		}

		return "etc";
	}

	public static String getFileExtType2CodeFromFileName(String fileName) {
		String ext = getFileExtFromFileName(fileName).toLowerCase();

		switch (ext) {
		case "jpeg":
		case "jpg":
			return "jpg";
		case "gif":
			return ext;
		case "png":
			return ext;
		case "mp4":
			return ext;
		case "mov":
			return ext;
		case "avi":
			return ext;
		case "mp3":
			return ext;
		case "svg":
			return ext;
		}

		return "etc";
	}

	public static String getFileExtFromFileName(String fileName) {
		int pos = fileName.lastIndexOf(".");
		String ext = fileName.substring(pos + 1);

		return ext;
	}

	public static String getNowYearMonthDateStr() {
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy_MM");

		String dateStr = format1.format(System.currentTimeMillis());

		return dateStr;
	}

	public static List<Integer> getListDividedBy(String str, String divideBy) {
		return Arrays.asList(str.split(divideBy)).stream().map(s -> Integer.parseInt(s.trim()))
				.collect(Collectors.toList());
	}

	public static boolean delteFile(String filePath) {
		java.io.File ioFile = new java.io.File(filePath);
		if (ioFile.exists()) {
			return ioFile.delete();
		}

		return true;
	}

	public static int getFileSize(String filePath) {
		return (int) new File(filePath).length();
	}

	public static void copyFile(String filePath, String destFilePath) {
		File file = new File(filePath);
		File destFile = new File(destFilePath);

		destFile.mkdirs();

		try {
			Files.copy(file.toPath(), destFile.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void moveFile(String filePath, String destFilePath) {
		Path file = Paths.get(filePath);
		Path destFile = Paths.get(destFilePath);

		try {
			Files.move(file, destFile, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getUrlFromHttpServletRequest(HttpServletRequest req) {
		String url = req.getRequestURI() + "?" + req.getQueryString();
		
		return url;
	}
	
	public static void resizeImgWidth(String filePath, String destFilePath, int width, int height) {
		try {
			BufferedImage bufferedImage = ImageIO.read(new File(filePath));

			int originWidth = bufferedImage.getWidth();
			int originHeight = bufferedImage.getHeight();

			int newWidth = originWidth;
			int newHeight = (originWidth * height) / width;

			if (newHeight > originHeight) {
				newWidth = (originHeight * width) / height;
				newHeight = originHeight;
			}

			BufferedImage cropedBufferedImage = Scalr.crop(bufferedImage, (originWidth - newWidth) / 2,
					(originHeight - newHeight) / 2, newWidth, newHeight);
			BufferedImage destBufferedImage = Scalr.resize(cropedBufferedImage, Method.ULTRA_QUALITY, Mode.AUTOMATIC,
					width, height);

			String destFileExt = Util.getFileExt(destFilePath);
			FileOutputStream fileOutputStream = new FileOutputStream(destFilePath);
			ImageIO.write(destBufferedImage, destFileExt, fileOutputStream);
			fileOutputStream.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static ResponseEntity httpResponseEntityToOther(String url) {
		URI redirectUri = null;
		try {
			redirectUri = new URI(url);
		} catch (URISyntaxException e1) {
			return null;
		}

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(redirectUri);
		return new ResponseEntity(httpHeaders, HttpStatus.MOVED_PERMANENTLY);
	}
}