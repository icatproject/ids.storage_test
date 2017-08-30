package org.icatproject.ids.storage_test;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import org.icatproject.ids.plugin.ArchiveStorageInterface;
import org.icatproject.ids.plugin.DfInfo;
import org.icatproject.ids.plugin.DsInfo;
import org.icatproject.ids.plugin.MainStorageInterface;

/**
 * File storage which emulates an unreliable tape backend.
 * 
 * The reliability is a number between 0 and 1 which is read from ~/reliability
 *
 */
public class ArchiveFileStorage implements ArchiveStorageInterface {

	private static Random rand = new Random();

	Path testHome;
	Path baseDir;

	public ArchiveFileStorage(Properties props) throws IOException {
		String fname = props.getProperty("testHome");
		if (fname == null) {
			fname = System.getProperty("user.home");
		}
		testHome = Paths.get(fname);
		Utils.checkDir(testHome);
		fname = Utils.resolveEnvs(props.getProperty("plugin.archive.dir"));
		if (fname == null) {
			throw new IOException("\"plugin.archive.dir\" is not defined");
		}
		baseDir = testHome.resolve(fname);
		Utils.checkDir(baseDir);
	}

	@Override
	public void delete(DsInfo dsInfo) throws IOException {
		think();
		String location = dsInfo.getInvId() + "/" + dsInfo.getDsId();
		Path path = baseDir.resolve(location);
		try {
			Files.delete(path);
			Files.delete(path.getParent());
		} catch (DirectoryNotEmptyException | NoSuchFileException e) {
		}
	}

	@Override
	public void delete(String location) throws IOException {
		think();
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
	public void get(DsInfo dsInfo, Path path) throws IOException {
		think();
		String location = dsInfo.getInvId() + "/" + dsInfo.getDsId();
		Files.copy(baseDir.resolve(location), path, StandardCopyOption.REPLACE_EXISTING);
	}

	@Override
	public void put(DsInfo dsInfo, InputStream inputStream) throws IOException {
		think();
		String location = dsInfo.getInvId() + "/" + dsInfo.getDsId();
		Path path = baseDir.resolve(location);
		Files.createDirectories(path.getParent());
		Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
	}

	@Override
	public void put(InputStream is, String location) throws IOException {
		think();
		Path path = baseDir.resolve(location);
		Files.createDirectories(path.getParent());
		Files.copy(new BufferedInputStream(is), path);
	}

	@Override
	public Set<DfInfo> restore(MainStorageInterface mainStorageInterface, List<DfInfo> dfInfos) {
		Set<DfInfo> failures = new HashSet<>();
		for (DfInfo dfInfo : dfInfos) {
			String location = dfInfo.getDfLocation();
			try (InputStream is = Files.newInputStream(baseDir.resolve(dfInfo.getDfLocation()))) {
				think();
				mainStorageInterface.put(is, location);
			} catch (IOException e) {
				failures.add(dfInfo);
			}
		}
		return failures;
	}

	private void think() throws IOException {
		Path p = testHome.resolve("reliability");
		try (BufferedReader in = Files.newBufferedReader(p)) {
			double reliability = Double.parseDouble(in.readLine());
			if (rand.nextFloat() > reliability) {
				throw new IOException("Simulated with reliability " + reliability);
			}
		}
	}

}
