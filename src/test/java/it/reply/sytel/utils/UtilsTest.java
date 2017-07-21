package it.reply.sytel.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class UtilsTest {
	
	private String bigFile;
	private Utils utils;
	
	@Before
	public void create(){
		this.utils = new Utils();
		bigFile = "D:/jboss-5.1.0.GA.rar";
	}
	
	@Ignore
	@Test
	public void checksum(){
		String file = "C:/Users/m.marcotrigiano/Desktop/prova3.txt";
		String file1 = "C:/Users/m.marcotrigiano/Desktop/prova4.txt"; //stesso contentuto di prova3 solo nome diverso
		
		Utils util = new Utils();
		String s1 = util.checkSum(file, "MD5");
		String s2 = util.checkSum(file1, "MD5");
		Assert.assertEquals(s1, s2);
		System.out.println(s1 + " = " + s2 + " ==> " + s1.equals(s2));
	}
	
	@Ignore
	@Test
	public void cksumLocal(){
		String file = "D:/jboss-5.1.0.GA.rar";
		Utils utils = new Utils();
		String command = "cksum " + file;
		utils.execLocalCommand(command);
	}
	
	
	@Test
	public void testMd5BigFile(){
		Assert.assertNotNull(utils.checkSum(bigFile, "MD5"));
	}
}

