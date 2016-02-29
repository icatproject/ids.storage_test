package org.icatproject.ids.storage;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Properties;

import org.icatproject.ids.plugin.DfInfo;
import org.icatproject.ids.plugin.DsInfo;
import org.junit.BeforeClass;
import org.junit.Test;

public class MainFileStorageTest {

	public class DeleteVisitor extends SimpleFileVisitor<Path> {

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			Files.delete(file);
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
			if (e == null) {
				Files.delete(dir);
				return FileVisitResult.CONTINUE;
			} else {
				// directory iteration failed
				throw e;
			}
		}

	}

	private static DeleteVisitor treeDeleteVisitor = new MainFileStorageTest().new DeleteVisitor();

	private static MainFileStorage mainFileStorage;

	@BeforeClass
	public static void beforeClass() throws IOException, ClassNotFoundException, InterruptedException {
		File testPropertyFile = new File(MainFileStorageTest.class.getClassLoader().getResource("main.test.properties")
				.getFile());
		mainFileStorage = new MainFileStorage(testPropertyFile);
		Properties props = new Properties();
		props.load(MainFileStorageTest.class.getClassLoader().getResourceAsStream("main.test.properties"));
		Path dir = new File(props.getProperty("dir")).toPath();
		if (dir != null) {
			if (Files.exists(dir)) {
				Files.walkFileTree(dir, treeDeleteVisitor);
			}
			Files.createDirectories(dir);
		}

		Files.createDirectories(dir.resolve("1/1"));

		Files.createDirectories(dir.resolve("1/2"));

		Files.createDirectories(dir.resolve("2/3"));

		Files.copy(new ByteArrayInputStream("We don't like as very much f1".getBytes()), dir.resolve("1/1/f1"));
		Thread.sleep(1000);
		Files.copy(new ByteArrayInputStream("We don't like these much f2".getBytes()), dir.resolve("1/2/f2"));
		Thread.sleep(1000);
		Files.copy(new ByteArrayInputStream("We don't like these much f3".getBytes()), dir.resolve("1/2/f3"));
		Thread.sleep(1000);
		Files.copy(new ByteArrayInputStream("We don't like these much f4".getBytes()), dir.resolve("2/3/f4"));

	}

	@Test
	public void testGetDatafilesToArchive() throws Exception {
		List<DfInfo> dfInfos = mainFileStorage.getDatafilesToArchive(24670, 24690);
		assertEquals(0, dfInfos.size());

		dfInfos = mainFileStorage.getDatafilesToArchive(24670, 24680);
		assertEquals(1, dfInfos.size());
		assertEquals("1/1/f1", dfInfos.get(0).getDfLocation());

		dfInfos = mainFileStorage.getDatafilesToArchive(24650, 24680);
		assertEquals(2, dfInfos.size());
		assertEquals("1/1/f1", dfInfos.get(0).getDfLocation());
		assertEquals("1/2/f2", dfInfos.get(1).getDfLocation());

		dfInfos = mainFileStorage.getDatafilesToArchive(0, 24680);
		assertEquals(4, dfInfos.size());
	}

	@Test
	public void testGetDatasetsToArchive() throws Exception {
		List<DsInfo> dsInfos = mainFileStorage.getDatasetsToArchive(24670, 24690);
		assertEquals(0, dsInfos.size());
		
		dsInfos = mainFileStorage.getDatasetsToArchive(24670, 24680);
		assertEquals(1, dsInfos.size());
		assertEquals("1/1", dsInfos.get(0).toString());


		dsInfos = mainFileStorage.getDatasetsToArchive(24650, 24680);
		assertEquals(2, dsInfos.size());
		assertEquals("1/1", dsInfos.get(0).toString());
		assertEquals("1/2", dsInfos.get(1).toString());

		dsInfos = mainFileStorage.getDatasetsToArchive(0, 24680);
		assertEquals(3, dsInfos.size());
		assertEquals("1/1", dsInfos.get(0).toString());
		assertEquals("1/2", dsInfos.get(1).toString());
		assertEquals("2/3", dsInfos.get(2).toString());
	}

}
