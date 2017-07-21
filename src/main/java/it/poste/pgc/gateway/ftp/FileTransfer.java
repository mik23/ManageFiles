package it.poste.pgc.gateway.ftp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import it.reply.sytel.utils.Utils;

public class FileTransfer {
	
//	private static final String ALG_DIGEST = "SHA-256";
	private final Logger LOGGER = LogManager.getLogger(FileTransfer.class);
	private final int SFTPPORT = 22;
	
	private String host;
	private String SFTPUser;
	private String SFTPPass;
	
	private Session session = null;
	private Channel channel = null;
	private ChannelSftp channelSftp = null;
	
	public FileTransfer(String host,  String sFTPUser, String sFTPPass) {
		super();
		this.host = host;
		this.SFTPUser = sFTPUser;
		this.SFTPPass = sFTPPass;
		BasicConfigurator.configure();
	}

	/**
	 * Connect to host and open channel SFTP using JSch
	 * @return
	 */
	public boolean connect() {
		LOGGER.debug("Connect");
		LOGGER.trace("preparing the host information for sftp.");
		try {
			JSch jsch = new JSch();
			this.session = jsch.getSession(SFTPUser, host, this.SFTPPORT);
			this.session.setPassword(SFTPPass);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			this.session.setConfig(config);
			this.session.connect();
			LOGGER.info("Host Connected");
			this.channel = this.session.openChannel("sftp");
       	 	this.channel.connect();
       	 	LOGGER.info("sftp channel opened and connected.");
            this.channelSftp = (ChannelSftp) channel;
			return true;
		} catch (Exception ex) {
			LOGGER.error("Errore durante la connessione", ex);
			return false;
		}
	}
	
	public void disconnect(){
		channelSftp.disconnect();
		channel.disconnect();
		session.disconnect();
	}
	

	
	/***
	 * Send a file to a remot host via sftp
	 * destPath must end with "/" character
	 * fileName is the full absolute path of the file
	 */
	public  boolean send (String fileName, String destPath) {
		boolean returnSt = false;

		LOGGER.debug("send input parameters: [fileName=" + fileName + ", destPath=" + destPath + "]");
        if(!connect())
        	return returnSt;
        
         try {
			 this.channelSftp.cd(destPath);
			 File f = new File(fileName);
	         this.channelSftp.put(new FileInputStream(f), f.getName());
	         
	         //check
	         returnSt = checkFiles(destPath+f.getName(), fileName);
	         
		} catch (SftpException e) {
			LOGGER.error(e);
		} catch (FileNotFoundException e) {
			LOGGER.error(e);
		} catch (Exception e) {
			LOGGER.error(e);
		}
        return returnSt;
    }  
	
	/**
	 * Check files using MD5sum
	 * Assumption: remote machine is unix based
	 * @param pathRemoteFile
	 * @param pathLocalFile
	 * @return
	 */
	public boolean checkFiles(String pathRemoteFile, String pathLocalFile){

		Utils utils = new Utils();
		
		String digest1;
		String digest0;
		try {
			digest1 = utils.checkSum(pathLocalFile, "MD5"); //SHA-256
			digest0 = remoteCommand("md5sum", pathRemoteFile); //sha256sum
			return digest0.equalsIgnoreCase(digest1);
		} catch (Exception e) {
			LOGGER.error("Errore durante il calcolo del CRC", e);
		}
		return false;
		
	}
	
	/**
	 * ssh session to execute remote command
	 * @param command
	 * @return
	 */
	public String execRemoteCommand(String command){
		try {
			if(!connect())
				return null;
			
			this.channel = session.openChannel("exec");
			ChannelExec channel=(ChannelExec) session.openChannel("exec");
			BufferedReader in=new BufferedReader(new InputStreamReader(channel.getInputStream()));
			((ChannelExec) channel).setCommand(command);
			channel.connect();

			String msg=null;
			StringBuffer sb = new StringBuffer();
			while((msg=in.readLine())!=null){
			  sb.append(msg);
			}
			in.close();
			return sb.toString();
		} catch (JSchException e) {
			LOGGER.error("Errore durante l'esecuzione del comando remoto " + command, e);
		}catch (Exception e) {
			LOGGER.error(e);
		}
		return null;
	     
	}
	
	public String remoteCommand(String command, String remoteFile){
		String s = execRemoteCommand(command + " " + remoteFile);
		return s.split(" ")[0];
	}
	
	
	/**
	 * utility to get a remote file
	 * @param fileName
	 * @param destPath
	 * @return
	 */
	public boolean get(String fileName, String destPath){
		boolean returnSt = false;
		
		LOGGER.debug("get");
		if(!connect())
        	return returnSt;
		
        OutputStream output = null;
		try {
			File tmpFile = new File(destPath);
			output = new FileOutputStream(tmpFile.isDirectory()?(destPath+fileName):destPath);
			channelSftp.get(fileName,output);
			LOGGER.info("File " + fileName + " scaricato");
			returnSt = true;
		} catch (FileNotFoundException e) {
			LOGGER.error(e);
		} catch (SftpException e) {
			LOGGER.error("Errore durante il download del file", e);
		} catch(Exception e){
			LOGGER.error("Errore durante il download del file", e);
		}
		disconnect();
		return returnSt;
	}
	
	/**
	 * utility to get input stream of a remote file
	 * @param fileName
	 * @return
	 */
	public InputStream getFile(String fileName){ 
		if(!connect())
        	return null;
		try {
			return channelSftp.get(fileName);
		} catch (SftpException e) {
			LOGGER.error("Errore durante la ricezione dello stream del file", e);
		} catch(Exception e){
			LOGGER.error("Errore durante la ricezione dello stream del file", e);
		}
		disconnect();
		return null;
	}
	
	/**
	 * delete a remote file
	 * @param remoteFilePath
	 * @return
	 */
	public boolean delete(String remoteFilePath) {
		boolean returnSt = false;
		
		LOGGER.debug("get");
		if(!connect())
        	return returnSt;
		
		try {
			channelSftp.rm(remoteFilePath);
			LOGGER.info("File " + remoteFilePath + " cancellato");
			returnSt = true;
		} catch (SftpException e) {
			LOGGER.error("Errore durante la cancellazione del file", e);
		} catch(Exception e){
			LOGGER.error("Errore durante la cancellazione del file", e);
		}
		disconnect();
		return returnSt;
	}
	
	
}
