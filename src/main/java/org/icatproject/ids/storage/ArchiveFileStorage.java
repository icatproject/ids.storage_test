package org.icatproject.ids.storage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.icatproject.ids.plugin.ArchiveStorageInterface;
import org.icatproject.ids.plugin.DfInfo;
import org.icatproject.ids.plugin.DsInfo;
import org.icatproject.ids.plugin.MainStorageInterface;
import org.icatproject.utils.CheckedProperties;
import org.icatproject.utils.CheckedProperties.CheckedPropertyException;

public class ArchiveFileStorage implements ArchiveStorageInterface {

	Path baseDir;

	public ArchiveFileStorage(File properties) throws IOException {
		try {
			CheckedProperties props = new CheckedProperties();
			props.loadFromFile(properties.getPath());

			baseDir = props.getFile("dir").toPath();
			checkDir(baseDir, properties);

		} catch (CheckedPropertyException e) {
			throw new IOException("CheckedPropertException " + e.getMessage());
		}
	}

	private void checkDir(Path dir, File props) throws IOException {
		if (!Files.isDirectory(dir)) {
			throw new IOException(dir + " as specified in " + props + " is not a directory");
		}
	}

	@Override
	public void delete(DsInfo dsInfo) throws IOException {
		String location = dsInfo.getInvId() + "/" + dsInfo.getDsId();
		Path path = baseDir.resolve(location);
		Files.delete(path);
		path = path.getParent();
		try {
			Files.delete(path);
		} catch (IOException e) {
			// Investigation directory probably not empty
		}
	}

	@Override
	public void put(DsInfo dsInfo, InputStream inputStream) throws IOException {
		String location = dsInfo.getInvId() + "/" + dsInfo.getDsId();
		Path path = baseDir.resolve(location);
		Files.createDirectories(path.getParent());
		Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
	}

	@Override
	public void get(DsInfo dsInfo, Path path) throws IOException {
		String location = dsInfo.getInvId() + "/" + dsInfo.getDsId();
		Files.copy(baseDir.resolve(location), path, StandardCopyOption.REPLACE_EXISTING);
	}

	@Override
	public void delete(String location) throws IOException {
		Path path = baseDir.resolve(location);
		Files.delete(path);
		/* Try deleting empty directories */
		path = path.getParent();
		try {
			while (!path.equals(baseDir)) {
				Files.delete(path);
				path = path.getParent();
			}
		} catch (IOException e) {
			// Directory probably not empty
		}
	}

	@Override
	public void put(InputStream is, String location) throws IOException {
		Path path = baseDir.resolve(location);
		Files.createDirectories(path.getParent());
		Files.copy(new BufferedInputStream(is), path);
	}

	@Override
	public Set<DfInfo> restore(MainStorageInterface mainStorageInterface, List<DfInfo> dfInfos) {
		Set<DfInfo> failures = new HashSet<>();
		for (DfInfo dfInfo : dfInfos) {
			String location = dfInfo.getDfLocation();
			try {
				InputStream is = Files.newInputStream(baseDir.resolve(dfInfo.getDfLocation()));
				mainStorageInterface.put(is, location);
			} catch (IOException e) {
				failures.add(dfInfo);
			}
		}
		return failures;
	}

}
