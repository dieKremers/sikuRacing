package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import application.ImageWorker;

public class TestExtractTimeFromImageName 
{
	ImageWorker myImageWorker = new ImageWorker( );

	@Test
	public void test() 
	{
		double testValue = 12234.12345;
		String filename = "pic_12234.12345.png";
		double result = myImageWorker.getTimeFromFilename(filename);
		assertTrue("Values should be equal: Result is: " + result , testValue == result );
	}

}
