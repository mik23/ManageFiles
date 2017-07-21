package it.reply.sytel.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

@SuppressWarnings("restriction")
public class Utils {
	private final Logger LOGGER = LogManager.getLogger(Utils.class);
	
	public Utils(){
		BasicConfigurator.configure();
	}
	
	public String checkSum(String pathFile, String algorithm){
		LOGGER.debug("Inizio checksum sul file " + pathFile);
		
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("Algoritmo non trovato " + algorithm);
		}
		
		try {
			md.update(Files.readAllBytes(Paths.get(pathFile)));
		} catch (IOException e) {
			LOGGER.error("File non trovato " + pathFile);
		}
		byte[] digest = md.digest();
		String digestString = DatatypeConverter.printHexBinary(digest).toUpperCase();
		LOGGER.info(algorithm + " sul file " + pathFile + ", digest = " + digestString);
		return digestString;
	}
	
	public String checkSum(InputStream stream, String algorithm){
		LOGGER.debug("Inizio checksum su stream ");
		
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("Algoritmo non trovato " + algorithm);
		}
		
		try {
			md.update(IOUtils.toByteArray(stream));
		} catch (IOException e) {
			LOGGER.error(e);
		}
		byte[] digest = md.digest();
		String digestString = DatatypeConverter.printHexBinary(digest).toUpperCase();
		LOGGER.info(algorithm + " sul file stream, digest = " + digestString);
		return digestString;
	}
	
	
	public static String inputStreamToString(InputStream inputStream) throws IOException{
		int ch;
		StringBuilder sb = new StringBuilder();
		while((ch = inputStream.read()) != -1)
		    sb.append((char)ch);
		return sb.toString();
	}
	
	public String execLocalCommand(String command){
		Runtime r = Runtime.getRuntime();
		Process p;
		try {
			p = r.exec(command);
			p.waitFor();
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String msg=null;
			StringBuffer sb = new StringBuffer();
			while((msg=in.readLine())!=null){
			  sb.append(msg);
			}
			in.close();
			return sb.toString();
		} catch (IOException e) {
			LOGGER.error(e);
		} catch (InterruptedException e) {
			LOGGER.error(e);
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return command;
		
	}
}
