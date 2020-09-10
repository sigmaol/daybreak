package com.odenktools.resourceserver.service.impl;

import com.odenktools.resourceserver.model.ImageFile;
import com.odenktools.resourceserver.service.AbstractStorageService;
import org.joda.time.LocalDateTime;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Calendar;

@Service
@Component
@ConditionalOnProperty(name = "storage.type", havingValue = "filesystem")
@ConfigurationProperties(prefix = "storage.filesystem")
public class FileSystemStorageService extends AbstractStorageService {

	private String imgFolder;

	@Override
	public String upload(InputStream is, String filename, String contentType, long length) throws IOException {
		String savePath = imgFolder;
		String saveDate = getCurrentSaveDate();
		Path targetPath = new File(savePath + File.separator + saveDate, filename).toPath();
		Files.copy(is, targetPath);
		return saveDate.replace("\\", "/") + "/" + filename;
	}

	/**
	 * Get folder for today and will create it if not exist
	 *
	 * @return generated date folder
	 */
	public String getCurrentSaveDate() {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);

		// I don't known why calendar start with 0
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		String monthStr = String.valueOf(month);
		if (month < 10) {
			monthStr = "0" + monthStr;
		}
		String dayStr = String.valueOf(day);
		if (day < 10) {
			dayStr = "0" + dayStr;
		}
		String path = year + File.separator + monthStr + File.separator
				+ dayStr;

		String savePath = this.imgFolder;
		if (savePath.endsWith(File.separator)) {
		} else {
			savePath += File.separator;
		}
		savePath += path;
		File file = new File(savePath);
		if (!file.exists()) {
			file.mkdirs();
		}

		return path;
	}

	public void deleteFile(String name) {
		ImageFile file = imageFileRepository.findByShortName(name);

		//super.deleteFile(name);

		Instant dateInstant = file.getCreatedAt().toInstant();
		LocalDateTime dateTime = LocalDateTime.fromDateFields(file.getCreatedAt());
		int year = dateTime.getYear();
		int month = dateTime.getMonthOfYear();
		int day = dateTime.getDayOfMonth();

		String monthStr = String.format("00", month);
		String dayStr = String.format("00", day);

		String path = this.imgFolder
				+ File.separator + year + File.separator
				+ monthStr + File.separator + dayStr
				+ File.separator + name;
		File f = new File(path);
		if (f.exists()) {
			f.delete();
		}
	}

	public String getImgFolder() {
		return imgFolder;
	}

	public void setImgFolder(String imgFolder) {
		this.imgFolder = imgFolder;
	}

}
