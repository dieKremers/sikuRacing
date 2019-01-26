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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
	private Logger log = LogManager.getRootLogger();
	private ScheduledExecutorService frameGrabberTimer;
	private Runnable frameGrabber;
	private ScheduledExecutorService imageWorkerTimer;
	private Runnable imageWorker;
	Pictures pictureList = new Pictures();
//	private ArrayList<Mat> frames = new ArrayList<Mat>();
//TODO	public File imageFolder = new File("\\\\KREMERSPI\\ShareRaspberry");
	public File imageFolder = new File("C:\\projekte\\sikuRacing\\simulation");
	private String qualifyingStartFile = "start_qualifying.txt";
	private String raceStartFile = "start_race.txt";
	private String raceFinishedFile = "finished";
	private String startTimeFile = "startTime";
    public boolean saveImages = true;
    public boolean logAllImages = true;
    public double templateThreshold = 0.70;
    
	public ArrayList<Car> cars = new ArrayList<Car>();
	Hashtable<Car, Mat> carMats = new Hashtable<Car, Mat>();
	Race currentRace = null;
	private static int idleTick = 1000;
	private static int raceTick = 250;
	public Double startTime = 0.0;
	private Double finishTime = 0.0;
	private boolean raceRunning = false;
	private boolean fehlstart = false;
	
	private int racecounter = 0;

	public void setRacecounter(int racecounter) {
		this.racecounter = racecounter;
	}

	PictureAnalyzer pictureAnalyzer = null; 
	
	private FilenameFilter pngFilter = new FilenameFilter() 
	{
        @Override
        public boolean accept(File dir, String name) {
            return name.toLowerCase().endsWith(".png");
        }
	};
	private String logpath;
	
	public ImageWorker()
	{
		pictureAnalyzer = new PictureAnalyzer(this);
		startFrameGrabber();
		startImageWorker();
		frameGrabberTimer.scheduleWithFixedDelay(frameGrabber, 0, idleTick, TimeUnit.MILLISECONDS);
		imageWorkerTimer.scheduleWithFixedDelay(imageWorker, 0, idleTick, TimeUnit.MILLISECONDS);
	}
	
	public void startRace( ArrayList<Car> _cars, boolean isQualifying )
	{
		fehlstart = false;
		cars = _cars;
		loadCarPictures();
		raceRunning = true;
		if( isQualifying ) {
			log.debug("Starting Qualifying");
			currentRace = null;			
		}
		else {
			log.debug("Starting Race");
			racecounter++;
			currentRace = new Race( cars );	
		}
		if( logAllImages ) {
			logpath = "C:\\projekte\\sikuRacing\\logging\\all\\" + "race_"+racecounter +"\\";
			File logfilePath = new File(logpath);
			if( !logfilePath.exists() )
			{
				logfilePath.mkdir();
			}
		}
		frameGrabberTimer.scheduleWithFixedDelay(frameGrabber, 0, raceTick, TimeUnit.MILLISECONDS);
		imageWorkerTimer.scheduleWithFixedDelay(imageWorker, 33, raceTick, TimeUnit.MILLISECONDS);		
		startRaspberry( isQualifying );
	}
	
	private void loadCarPictures() 
	{
		for( Car car : cars )
		{
			try {
				Image value;
				value = new Image(car.getCarMask().toURI().toURL().toString());
				carMats.put(car, OpenCvUtils.imageToMat( value, true ));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}

	public void stopRace() throws InterruptedException
	{
		raceRunning = false;
		stopRaspberry();
		File[] files = imageFolder.listFiles( pngFilter );
		if( files != null ) {
			while( files.length > 0 )
			{
				Thread.sleep(100);
				files = imageFolder.listFiles( pngFilter );
			}			
		}
		frameGrabberTimer.scheduleWithFixedDelay(frameGrabber, 0, idleTick, TimeUnit.MILLISECONDS);
		imageWorkerTimer.scheduleWithFixedDelay(imageWorker, 0, idleTick, TimeUnit.MILLISECONDS);				
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
						log.trace("No files found");
						return;
					}
					int numberOfFiles = files.length;
					long frameGrabberStartTime =  System.currentTimeMillis();
					log.info("Number of Files in Folder: " + files.length + "\tTime: " + System.currentTimeMillis());
					for( File file : files )
					{
						log.debug("Checking File... ");
						Double imgTime = getTimeFromFilename( file.getName() );
						if( file.getName().contains(startTimeFile))
						{
							log.debug("Start Picture detected");
							startTime = imgTime;
							if( currentRace != null )
								currentRace.setStartTime(imgTime);
						}
						else if (file.getName().contains(raceFinishedFile))
						{
							log.debug("Finish Picture detected");
							finishTime = imgTime;
							stopRace = true;
						}
						else if( file.getName().contains("fehlstart"))
						{
							log.info("Fehlstart");
							stopRace = true;
							setFehlstart(true);
						}
						else
						{
							log.debug("Found valid picture (" + file.getName() + "). Adding to List!!");
							Image value = new Image(file.toURI().toURL().toString());
							pictureList.addPicture( OpenCvUtils.imageToMat( value, true ), imgTime );
						}
						if(logAllImages)
						{
							File logfile = new File(logpath + file.getName());
							file.renameTo(logfile);
						}
						log.info("Deleting File: " + file.getName() );
						file.delete();
					}
					long dt = System.currentTimeMillis() - frameGrabberStartTime;
					log.info("Bildbearbeitungszeit: " + dt/numberOfFiles + "ms per file");
					if( stopRace )
					{
						stopRace();
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		frameGrabberTimer = Executors.newSingleThreadScheduledExecutor();
        frameGrabberTimer.scheduleWithFixedDelay(frameGrabber, 0, idleTick, TimeUnit.MILLISECONDS);
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
					pictureAnalyzer.checkPicture( pic );
				}
	        }
		};
		imageWorkerTimer = Executors.newSingleThreadScheduledExecutor();
        imageWorkerTimer.scheduleWithFixedDelay(imageWorker, 0, idleTick, TimeUnit.MILLISECONDS);
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
			log.debug("Creating File: " + file.getPath() );
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void stopRaspberry()
	{
		File startFile = new File(imageFolder + "\\" + raceStartFile);
		File qualifyingFile = new File( imageFolder + "\\" + qualifyingStartFile);
		File finishFile = new File(imageFolder + "\\" + raceFinishedFile );
		log.debug("Stopping Raspi");
		if( startFile.exists()) {
			log.debug("Deleting File: " + startFile.getPath() );
			startFile.delete();
		}
		if( qualifyingFile.exists() ) {
			log.debug("Deleting File: " + qualifyingFile.getPath() );
			qualifyingFile.delete();
		}
		if( finishFile.exists()) {
			log.debug("Deleting File: " + finishFile.getPath() );
			finishFile.delete();
		}
	}
	

	/**
	 * Creates a file in the shared folder and Raspberry should delete it if it's available.
	 * @return
	 */
	public boolean checkConnection() {
		File file = new File(imageFolder+"\\checkConnection.txt" );
		try {
			file.createNewFile();
			Thread.sleep(2000);
			if( file.exists() )
			{
				file.delete();
				return false;
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean isFehlstart() {
		return fehlstart;
	}

	public void setFehlstart(boolean fehlstart) {
		this.fehlstart = fehlstart;
	}
}
