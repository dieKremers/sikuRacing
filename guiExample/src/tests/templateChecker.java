package tests;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class templateChecker 
{
	private static FilenameFilter pngFilter = new FilenameFilter() 
	{
        @Override
        public boolean accept(File dir, String name) {
            return name.toLowerCase().endsWith(".png");
        }
	};
	
	private static class Picture
	{
		public Picture(Mat matrix, String name) 
		{
			super();
			this.matrix = matrix;
			this.name = name;
		}
		public Mat matrix;
		public String name;

	}
	
	private static ArrayList<Picture> pictures = new ArrayList<Picture>();
	private static ArrayList<Picture> templates = new ArrayList<Picture>();

	public static void main(String[] args) throws MalformedURLException 
	{
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		loadImages();
		loadTemplates();
		System.out.println("Picture;Template;Percentage");
		for( Picture picture : pictures )
		{
			for( Picture template : templates )
			{
				Double percentage = getPercentage(picture.matrix, template.matrix);
				String result = String.format("%s;%s;%.2f", picture.name, template.name, percentage);
				System.out.println(result);
			}
		}
		
	}
	
	private static void loadTemplates() throws MalformedURLException
	{
		File imageFolder = new File("C:\\projekte\\sikuRacing\\farbtemplates");
		File[] files = imageFolder.listFiles( pngFilter );
		for( File file : files )
		{
			System.out.println( "Loading: " + file.getAbsolutePath() );
			Mat mat = Imgcodecs.imread(file.getAbsolutePath(), Imgcodecs.CV_LOAD_IMAGE_COLOR);
			templates.add( new Picture(mat, file.getName() ) );
		}
	}
	
	private static void loadImages() throws MalformedURLException
	{
		File imageFolder = new File("C:\\projekte\\sikuRacing\\farbbilder");
		File[] files = imageFolder.listFiles( pngFilter );
		for( File file : files )
		{
			System.out.println( "Loading: " + file.getAbsolutePath() );
			Mat mat = Imgcodecs.imread(file.getAbsolutePath(), Imgcodecs.CV_LOAD_IMAGE_COLOR);
			pictures.add( new Picture(mat, file.getName() ) );
		}
	}
	
	private static double getPercentage( Mat picture, Mat template )
	{
		Double percentage;
		Mat result = new Mat();
		int method = Imgproc.TM_CCOEFF_NORMED;
		
		//Template suchen
		Imgproc.matchTemplate(picture, template, result, method);	
		
		//Ergebnis auswerten
		MinMaxLocResult mmlresult = Core.minMaxLoc(result);
		if( method == Imgproc.TM_SQDIFF_NORMED )
		{
			percentage = 1-mmlresult.minVal;
			//location = mmlresult.minLoc;
		}
		else
		{
			percentage = mmlresult.maxVal;
			//location = mmlresult.maxLoc;
		}
		return percentage;
	}
}
