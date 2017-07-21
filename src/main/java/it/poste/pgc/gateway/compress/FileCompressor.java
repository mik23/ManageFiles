package it.poste.pgc.gateway.compress;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class FileCompressor {
	private final Logger LOGGER = LogManager.getLogger(FileCompressor.class);

	public FileCompressor() {
		BasicConfigurator.configure();
	}

	/**
	 * Creates a tar.gz file at the specified path with the contents of the
	 * specified directory. NB:
	 *
	 * @param dirPath
	 *            The path of the directory where the archive will be created.
	 *            eg. c:/temp
	 * @param tarGzPath
	 *            The full path of the archive to create. eg.
	 *            c:/temp/archive.tar.gz
	 */
	public void createTarGZ(String dirPath, String tarGzPath) {
		FileOutputStream fOut = null;
		BufferedOutputStream bOut = null;
		GzipCompressorOutputStream gzOut = null;
		TarArchiveOutputStream tOut = null;
		try {
			System.out.println(new File(".").getAbsolutePath());
			LOGGER.info("inizio compressione tar.gz del file " + dirPath);
			fOut = new FileOutputStream(new File(tarGzPath));
			bOut = new BufferedOutputStream(fOut);
			gzOut = new GzipCompressorOutputStream(bOut);
			tOut = new TarArchiveOutputStream(gzOut);
			addFileToTarGz(tOut, dirPath, "");
			LOGGER.info("fine compressione tar.gz del file " + dirPath);
		} catch (FileNotFoundException e) {
			LOGGER.error("Errore durante la compressione tar.gz", e);
		} catch (Exception e) {
			LOGGER.error("Errore durante la compressione tar.gz", e);
		} finally {
			try {
				tOut.finish();
				tOut.close();
				gzOut.close();
				bOut.close();
				fOut.close();
			} catch (Exception e) {
				LOGGER.error("Errore durante la chiusura del file", e);
			}

		}
	}

	private void addFileToTarGz(TarArchiveOutputStream tOut, String path, String base) throws IOException {
		File f = new File(path);
		String entryName = base + f.getName();
		TarArchiveEntry tarEntry = new TarArchiveEntry(f, entryName);
		tOut.putArchiveEntry(tarEntry);

		if (f.isFile()) {
			IOUtils.copy(new FileInputStream(f), tOut);
			tOut.closeArchiveEntry();
		} else {
			tOut.closeArchiveEntry();
			File[] children = f.listFiles();
			if (children != null) {
				for (File child : children) {
					LOGGER.debug("Aggiunto file: " + child.getName());
					addFileToTarGz(tOut, child.getAbsolutePath(), entryName + "/");
				}
			}
		}
	}

	/**
	 * Creates a zip file at the specified path with the contents of the
	 * specified directory. NB:
	 *
	 * @param directoryPath
	 *            The path of the directory where the archive will be created.
	 *            eg. c:/temp
	 * @param zipPath
	 *            The full path of the archive to create. eg.
	 *            c:/temp/archive.zip
	 * @throws IOException
	 *             If anything goes wrong
	 */
	public static void createZip(String directoryPath, String zipPath) throws IOException {
		FileOutputStream fOut = null;
		BufferedOutputStream bOut = null;
		ZipArchiveOutputStream tOut = null;

		try {
			fOut = new FileOutputStream(new File(zipPath));
			bOut = new BufferedOutputStream(fOut);
			tOut = new ZipArchiveOutputStream(bOut);
			addFileToZip(tOut, directoryPath, "");
		} finally {
			tOut.finish();
			tOut.close();
			bOut.close();
			fOut.close();
		}

	}

	/**
	 * Creates a zip entry for the path specified with a name built from the
	 * base passed in and the file/directory name. If the path is a directory, a
	 * recursive call is made such that the full directory is added to the zip.
	 *
	 * @param zOut
	 *            The zip file's output stream
	 * @param path
	 *            The filesystem path of the file/directory being added
	 * @param base
	 *            The base prefix to for the name of the zip file entry
	 *
	 * @throws IOException
	 *             If anything goes wrong
	 */
	private static void addFileToZip(ZipArchiveOutputStream zOut, String path, String base) throws IOException {
		File f = new File(path);
		String entryName = base + f.getName();
		ZipArchiveEntry zipEntry = new ZipArchiveEntry(f, entryName);

		zOut.putArchiveEntry(zipEntry);

		if (f.isFile()) {
			FileInputStream fInputStream = null;
			try {
				fInputStream = new FileInputStream(f);
				IOUtils.copy(fInputStream, zOut);
				zOut.closeArchiveEntry();
			} finally {
				IOUtils.closeQuietly(fInputStream);
			}

		} else {
			zOut.closeArchiveEntry();
			File[] children = f.listFiles();

			if (children != null) {
				for (File child : children) {
					addFileToZip(zOut, child.getAbsolutePath(), entryName + "/");
				}
			}
		}
	}
}
