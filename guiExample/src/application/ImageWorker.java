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
	private ArrayList<Car> cars = new ArrayList<Car>();
	private Hashtable<Car,ArrayList<Double>> results = new Hashtable<Car, ArrayList<Double>>();
	private static int idleTick = 250;
	private static int raceTick = 10;
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
		results.clear();
		frameGrabberTimer.scheduleAtFixedRate(frameGrabber, 0, raceTick, TimeUnit.MILLISECONDS);
		imageWorkerTimer.scheduleAtFixedRate(imageWorker, 0, raceTick, TimeUnit.MILLISECONDS);		
		startRaspberry( isQualifying );
	}
	
	public void stopRace()
	{
		raceRunning = false;
		stopRaspberry();
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
						}
						else if (file.getName().contains(raceFinishedFile))
						{
							finishTime = imgTime;
							stopRace();
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
					
				} catch (MalformedURLException e) {
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

			private void checkPictureForCar(Car car, Picture pic) 
			{
				// Initialize Variables
    			Mat result = new Mat();
				int method = Imgproc.TM_CCORR_NORMED;
				double percentage = 0.1;
				String percentageInfo;
				Point location;
				
				Mat frame = pic.getMat();
				Mat template = car.getCarMask();
				
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
				
				//Ergebnis ins Bild eintragen
				if( percentage > 0.9 )
				{
					addTimeToResults(car, pic.getTime() );
					Point point2 = new Point();
					Point point4 = new Point(10,80);
					point2.x = location.x + template.width();
					point2.y = location.y + template.height();
					Scalar colour = new Scalar(Imgproc.COLORMAP_PINK);
					Imgproc.rectangle(frame, location, point2, colour);
					Imgproc.putText(frame, percentageInfo , point4 , 0, 1, colour);
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
	
	private void addTimeToResults( Car car, double time )
	{
		if( results.containsKey( car ) )
		{
			ArrayList<Double> times = results.get(car);
			int size = times.size();
			if( size == 0 )
			{
				times.add( time );
			}
			else
			{
				double lastLapTime = times.get( size-1 );
				if( (time - lastLapTime) > 1.0 ) //Rundenzeit nur eintragen wenn letzter Zeitstempel mehr als eine Sekunde her ist
				{
					times.add( time );
				}
			}
			if( size != times.size())
			{
				results.put(car, times);
			}
		}
		else
		{
			ArrayList<Double> times = new ArrayList<Double>();
			times.add( time );
			results.put( car,  times );
		}
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
}
