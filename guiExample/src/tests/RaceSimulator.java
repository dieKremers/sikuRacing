package tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import application.OpenCvUtils;
import javafx.scene.image.Image;

public class RaceSimulator {
	private static Logger log = LogManager.getRootLogger();
	
	public static File sourceFolder = new File("C:\\projekte\\sikuRacing\\logging\\rohdaten_25.02.2018\\race_10");
	public static File targetFolder = new File("C:\\projekte\\sikuRacing\\simulation");

	private static FilenameFilter pngFilter = new FilenameFilter() 
	{
        @Override
        public boolean accept(File dir, String name) {
            return name.toLowerCase().endsWith(".png");
        }
	};
	
	public static void main(String[] args) 
	{
		File[] files = null;
		try {
			files = sourceFolder.listFiles( pngFilter );
			if( files.length == 0 )
			{
				log.error("No files found");
				return;
			}
			log.info("Number of Files in Folder: " + files.length + "\tTime: " + System.currentTimeMillis());
			HashMap<Double, File> fileHash = new HashMap<Double, File>();
			ArrayList<Double> times = new ArrayList<Double>();
			for( File file : files )
			{
				Double imgTime = getTimeFromFilename( file.getName() );
				times.add(imgTime);
				fileHash.put(imgTime, file);
			}
			Collections.sort(times);
			for( int i = 0; i < times.size(); i++ ) {
				File copyFile = fileHash.get( times.get(i) );
				log.info("Copying File: " + copyFile.getName() );
				File targetFile = new File(targetFolder.getPath() + "\\" + copyFile.getName());
				copyFile(copyFile, targetFile);
				if( i < (times.size()-2) ) {
					double currentPictureTime = times.get(i);
					double nextPictureTime = times.get(i+1);
					int delay = (int)((nextPictureTime*1000) - (currentPictureTime*1000));
					log.info("Sleeping " + delay + "ms");
					Thread.sleep(delay);
				}
			}
		} catch (InterruptedException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
    public static void copyFile(File in, File out) throws IOException { 
        FileChannel inChannel = null; 
        FileChannel outChannel = null; 
        try { 
            inChannel = new FileInputStream(in).getChannel(); 
            outChannel = new FileOutputStream(out).getChannel(); 
            inChannel.transferTo(0, inChannel.size(), outChannel); 
        } catch (IOException e) { 
            throw e; 
        } finally { 
            try { 
                if (inChannel != null) 
                    inChannel.close(); 
                if (outChannel != null) 
                    outChannel.close(); 
            } catch (IOException e) {} 
        } 
    } 
	
	public static Double getTimeFromFilename(String name) 
	{
		Double time = 0.0;
		String timeString = name.substring((name.lastIndexOf("_") + 1), name.indexOf(".png") );
		time = Double.parseDouble(timeString);
		return time;
	}

}
