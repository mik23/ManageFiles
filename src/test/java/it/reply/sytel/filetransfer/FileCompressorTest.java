package it.reply.sytel.filetransfer;

import java.io.IOException;

import org.junit.Test;

import it.poste.pgc.gateway.compress.FileCompressor;

public class FileCompressorTest {

	/**
	 * test per la compressione di più files in archivio zip
	 */
	@Test
	public void compressionZipDirectoryTest(){
		try {
			FileCompressor.createZip("C:/Users/m.marcotrigiano/Desktop/ciao/", "C:/Users/m.marcotrigiano/Desktop/zippone.zip");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * test per la compressione di una directory all'archivio tar.gz
	 */
	@Test
	public void compressTarGzDirectoryTest(){
		String dirPath = "C:/Users/m.marcotrigiano/Desktop/ciao/";
        String tarGzPath = "C:/Users/m.marcotrigiano/Desktop/molti.tar.gz";
        FileCompressor f = new FileCompressor();
        f.createTarGZ(dirPath, tarGzPath);
	}
	
	/**
	 * test per la compressione di una directory all'archivio tar.gz
	 */
	@Test
	public void compressTarGzFileTest(){
		String dirPath = "C:/Users/m.marcotrigiano/Desktop/ciao/GIT.txt";
        String tarGzPath = "C:/Users/m.marcotrigiano/Desktop/uno.tar.gz";
        FileCompressor f = new FileCompressor();
        f.createTarGZ(dirPath, tarGzPath);
	}
}
