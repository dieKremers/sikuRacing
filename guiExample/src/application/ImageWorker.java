package application;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.imgproc.Imgproc;

import javafx.scene.image.Image;

public class ImageWorker 
{
	private ScheduledExecutorService frameGrabberTimer;
	private Runnable frameGrabber;
	private ScheduledExecutorService imageWorkerTimer;
	private Runnable imageWorker;
	Pictures pictureList = new Pictures();
//	private ArrayList<Mat> frames = new ArrayList<Mat>();
	public File imageFolder = new File("\\\\KREMERSPI\\ShareRaspberry");
	private String qualifyingStartFile = "start_qualifying.txt";
	private String raceStartFile = "start_race.txt";
	private String raceFinishedFile = "finished";
	private String startTimeFile = "startTime";
    private boolean saveImages = true;
    private double templateThreshold = 0.80;
    
	private ArrayList<Car> cars = new ArrayList<Car>();
	private Race currentRace = null;
	private static int idleTick = 250;
	private static int raceTick = 50;
	private Double startTime = 0.0;
	private Double finishTime = 0.0;
	private boolean raceRunning = false;
	
	private FilenameFilter pngFilter = new FilenameFilter() 
	{
        @Override
        public boolean accept(File dir, String name) {
            return name.toLowerCase().endsWith(".png");
        }
	};
	
	public ImageWorker()
	{
		startFrameGrabber();
		startImageWorker();
		frameGrabberTimer.scheduleAtFixedRate(frameGrabber, 0, idleTick, TimeUnit.MILLISECONDS);
		imageWorkerTimer.scheduleAtFixedRate(imageWorker, 0, idleTick, TimeUnit.MILLISECONDS);
	}
	
	public void startRace( ArrayList<Car> _cars, boolean isQualifying )
	{
		cars = _cars;
		raceRunning = true;
		if( isQualifying ) {
			currentRace = null;			
		}
		else {
			currentRace = new Race( cars );	
		}
		frameGrabberTimer.scheduleAtFixedRate(frameGrabber, 0, raceTick, TimeUnit.MILLISECONDS);
		imageWorkerTimer.scheduleAtFixedRate(imageWorker, 0, raceTick, TimeUnit.MILLISECONDS);		
		startRaspberry( isQualifying );
	}
	
	public void stopRace() throws InterruptedException
	{
		raceRunning = false;
		stopRaspberry();
		File[] files = imageFolder.listFiles( pngFilter );
		while( files.length > 0 )
		{
			Thread.sleep(100);
			files = imageFolder.listFiles( pngFilter );
		}
		frameGrabberTimer.scheduleAtFixedRate(frameGrabber, 0, idleTick, TimeUnit.MILLISECONDS);
		imageWorkerTimer.scheduleAtFixedRate(imageWorker, 0, idleTick, TimeUnit.MILLISECONDS);				
	}
	
	public void shutdown()
	{
		stopRaspberry();
		frameGrabberTimer.shutdown();
		imageWorkerTimer.shutdown();
	}
	
	public boolean isRaceRunning()
	{
		return raceRunning;
	}
	
	public double getStartTime()
	{
		return startTime;
	}
	
	public double getFinishTime()
	{
		return finishTime;
	}
        
	private void startFrameGrabber()
	{
		frameGrabber = new Runnable() 
		{
			File[] files = null;
			@Override
			public void run() 
			{
				boolean stopRace = false;
				try {
					files = imageFolder.listFiles( pngFilter );
					if( files.length == 0 )
					{
						return;
					}
					int numberOfFiles = files.length;
					long frameGrabberStartTime =  System.currentTimeMillis();
					System.out.println("Number of Files in Folder: " + files.length + "\tTime: " + System.currentTimeMillis());
					for( File file : files )
					{
						Double imgTime = getTimeFromFilename( file.getName() );
						if( file.getName().contains(startTimeFile))
						{
							startTime = imgTime;
							if( currentRace != null )
								currentRace.setStartTime(imgTime);
						}
						else if (file.getName().contains(raceFinishedFile))
						{
							finishTime = imgTime;
							stopRace = true;
						}
						else
						{
							Image value = new Image(file.toURI().toURL().toString());
							pictureList.addPicture( OpenCvUtils.imageToMat( value, true ), imgTime );
						}
						file.delete();
					}
					long dt = System.currentTimeMillis() - frameGrabberStartTime;
					System.out.println("Bildbearbeitungszeit: " + dt/numberOfFiles + "ms per file");
					if( stopRace )
					{
						stopRace();
					}
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		};
		frameGrabberTimer = Executors.newSingleThreadScheduledExecutor();
        frameGrabberTimer.scheduleAtFixedRate(frameGrabber, 0, idleTick, TimeUnit.MILLISECONDS);
	}
	
	private void startImageWorker()
	{
		imageWorker = new Runnable() 
		{
			@Override public void run() 
	        { 
	        	while( pictureList.picturesAvailable() )
				{
					Picture pic = pictureList.getNext();
					for( Car car : cars )
					{
						checkPictureForCar( car, pic);
					}
				}
	        }

		};
		imageWorkerTimer = Executors.newSingleThreadScheduledExecutor();
        imageWorkerTimer.scheduleAtFixedRate(imageWorker, 0, idleTick, TimeUnit.MILLISECONDS);
	}
	
	public Double getTimeFromFilename(String name) 
	{
		Double time = 0.0;
		String timeString = name.substring((name.lastIndexOf("_") + 1), name.indexOf(".png") );
		time = Double.parseDouble(timeString);
		return time;
	}
	
	public Race getCurrentRaceData()
	{
		return currentRace;
	}
	

	
	private void startRaspberry( boolean isQualifying )
	{
		String filename;
		if( isQualifying )
		{
			filename = "\\" + qualifyingStartFile;
		}
		else
		{
			filename = "\\" + raceStartFile;
		}
		File file = new File(imageFolder+filename);
		try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void stopRaspberry()
	{
		File startFile = new File(imageFolder + "\\" + raceStartFile);
		File qualifyingFile = new File( imageFolder + "\\" + qualifyingStartFile);
		File finishFile = new File(imageFolder + "\\" + raceFinishedFile );
		
		if( startFile.exists()) startFile.delete();
		if( qualifyingFile.exists() ) qualifyingFile.delete();
		if( finishFile.exists()) finishFile.delete();
	}
	
	private void checkPictureForCar(Car car, Picture pic) 
	{
		int method = Imgproc.TM_CCOEFF_NORMED;
		double percentage = 0.0;
		String percentageInfo;
		Point location;
		Mat frame = new Mat();
		Mat result = new Mat();
		Mat template = car.getCarMask().clone();
		for( int i = 0; i < 3; i++ )
		{
			if( i == 0 ) {
				frame = pic.getMat().clone();
			}
			if( i == 1 ) {
				frame = OpenCvUtils.rotate(pic.getMat(), 20 ).clone();
			}
			if( i == 2 ) {
				frame = OpenCvUtils.rotate(pic.getMat(), -20 ).clone(); 
			}
			//Template suchen
			Imgproc.matchTemplate(frame, template, result, method);	
			
			//Ergebnis auswerten
			MinMaxLocResult mmlresult = Core.minMaxLoc(result);
			if( method == Imgproc.TM_SQDIFF_NORMED )
			{
				percentage = 1-mmlresult.minVal;
				location = mmlresult.minLoc;
			}
			else
			{
				percentage = mmlresult.maxVal;
				location = mmlresult.maxLoc;
			}
			percentageInfo = String.format("Percentage: %.2f", percentage);
			
			//add result to current Race data and create picture for debugging 
			if( percentage > templateThreshold )
			{
				currentRace.addLap( car, pic.getTime() );
				if( saveImages )
				{
					Point point2 = new Point();
					Point point4 = new Point(10,80);
					point2.x = location.x + template.width();
					point2.y = location.y + template.height();
					Scalar colour = new Scalar(Imgproc.COLORMAP_PINK);
					Imgproc.rectangle(frame, location, point2, colour);
					Imgproc.putText(frame, percentageInfo , point4 , 0, 1, colour);
					String filename = "C:\\projekte\\sikuRacing\\logging\\" + car.getDriverName() + "_"  +i+ "_" + (pic.getTime()-startTime) + ".png";
					Imgcodecs.imwrite(filename, frame);		
				}
			}
			if( percentage > templateThreshold ) {
				break;
			}
		}
	}

}
