package com.gvtv.android.cloud.util.vcard;

import java.io.File;
import java.io.FilenameFilter;

public class FileNameSelector implements FilenameFilter {
	String extension = ".";
	public final static String FILE_DIR_NAME = "/com.gvtv.android.cloud";

	public FileNameSelector(String fileExtensionNoDot) {
		extension += fileExtensionNoDot;
	}

	public boolean accept(File dir, String name) {
		return name.endsWith(extension);
	}
}
