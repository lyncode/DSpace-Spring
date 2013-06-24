package org.dspace.util;

import java.io.File;

public class DirectoryUtils {

	public static String getPathWithParent (String parent, String child) {
		return new File(parent, child).getPath();
	}
}
