package sftp;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import it.poste.pgc.gateway.ftp.FileTransfer;
import it.reply.sytel.utils.Utils;

public class FileTransferTest {
	private final String  REMOTEHOST = "10.46.90.188";
	private final String USER = "pgc";
	private final String PASS = "pgc";
	private FileTransfer ft;
	@Before
	public void create(){
		ft = new FileTransfer(REMOTEHOST, USER, PASS);
	}
	
	@Test
	public void sendFile(){
		boolean sended = false;
		Assert.assertTrue(sended = ft.send("C:/Users/m.marcotrigiano/Desktop/prova3.txt", "/home/pgc/ftp-repository/"));
		
		if(sended == false){
			ft.delete("/home/pgc/ftp-repository/prova3");
		}
	}
	
	@Test
	public void sendBigFile(){
		long t0 = System.currentTimeMillis();
		Assert.assertTrue(ft.send("D:/jboss-5.1.0.GA.rar", "/home/pgc/ftp-repository/"));
		long t1 = System.currentTimeMillis();
		System.out.println(t1-t0);
	}
	
	@Test
	public void getFile(){
		Assert.assertNotNull(ft.get("/home/pgc/ftp-repository/prova.txt", "C:/Users/m.marcotrigiano/Desktop/SGC.txt"));
	}
	
	@Ignore
	@Test
	public void readFileOnTheFly(){
		try {
			String s = Utils.inputStreamToString(ft.getFile("/home/pgc/ftp-repository/prova2.txt"));
			System.out.println(s);
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	@Test
	public void checkFilesTrue(){
		Assert.assertTrue(ft.checkFiles("/home/pgc/ftp-repository/prova2.txt", "C:/Users/m.marcotrigiano/Desktop/prova2.txt" ));
	}
	
	@Test
	public void checkFilesFalse(){
		Assert.assertFalse(ft.checkFiles("/home/pgc/ftp-repository/prova2.txt", "C:/Users/m.marcotrigiano/Desktop/prova3.txt" ));
	}
	
	@Test
	public void deleteRemoteFile(){
		Assert.assertTrue(ft.delete("/home/pgc/ftp-repository/prova3.txt"));
	}
	
}
