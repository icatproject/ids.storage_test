package org.icatproject.ids.storage_test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

	static void checkDir(Path dir) throws IOException {
		if (!Files.isDirectory(dir)) {
			throw new IOException(dir + " as specified in run.properties is not a directory");
		}
	}

	static String resolveEnvs(String value) {
		if (null == value) {
			return null;
		}
		Pattern p = Pattern.compile("\\$\\{(\\w+)\\}|\\$(\\w+)");
		Matcher m = p.matcher(value);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			String envVarName = null == m.group(1) ? m.group(2) : m.group(1);
			String envVarValue = System.getenv(envVarName);
			m.appendReplacement(sb, null == envVarValue ? "" : Matcher.quoteReplacement(envVarValue));
		}
		m.appendTail(sb);
		return sb.toString();
	}

}