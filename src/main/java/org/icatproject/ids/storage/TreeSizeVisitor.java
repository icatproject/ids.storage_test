package org.icatproject.ids.storage;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeSizeVisitor extends SimpleFileVisitor<Path> {

	Comparator<Path> dateComparator = new Comparator<Path>() {

		@Override
		public int compare(Path o1, Path o2) {
			long m1 = baseDir.resolve(o1).toFile().lastModified();
			long m2 = baseDir.resolve(o2).toFile().lastModified();
			return (m1 < m2) ? -1 : ((m1 == m2) ? 0 : 1);
		}
	};

	private List<Path> dates = new ArrayList<>();

	private long size;

	private Map<Path, Long> sizes = new HashMap<>();

	private Path baseDir;

	public TreeSizeVisitor(Path baseDir) {
		this.baseDir = baseDir;
	}

	public List<Path> getDates() {
		Collections.sort(dates, dateComparator);
		return dates;
	}

	public long getSize() {
		return size;
	}

	public Map<Path, Long> getSizes() {
		return sizes;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
		if (e == null) {
			try {
				size += Files.size(dir);
			} catch (IOException e1) {
				// Ignore it
			}
			return FileVisitResult.CONTINUE;
		} else {
			// directory iteration failed
			throw e;
		}
	}

	@Override
	public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
		try {
			long s = Files.size(path);
			size += s;
			path = baseDir.relativize(path);
			sizes.put(path, s);
			dates.add(path);
		} catch (IOException e) {
			// Ignore it
		}
		return FileVisitResult.CONTINUE;

	}

}