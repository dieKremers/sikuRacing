package application;

import java.util.ArrayList;

import org.opencv.core.Mat;



/**
 * This class is a List of Pictures (the combination of the timestamp of a picture and the matrix of the pixels).
 * Every picture can also be marked as start-photo or stop-photo)
 * These are special pictures that indicate the start of a race and the end of a race.
 * 
 * @author dieKremers
 *
 */
public class Pictures 
{
	private Picture startPicture = null;
	private Picture finishPicture = null;


	private ArrayList<Picture> pictures = new ArrayList<Picture>();
	
	public void addPicture( Mat matrix, Double timestamp)
	{
		Picture pic = new Picture( matrix, timestamp );
		pictures.add(pic);
	}
	
	public void addStartPicture( Mat matrix, Double timestamp )
	{
		startPicture = new Picture( matrix, timestamp );
	}
	
	public void addFinishPicture( Mat matrix, Double timestamp )
	{
		finishPicture = new Picture( matrix, timestamp );
	}
	
	public Double getStartTime()
	{
		if( startPicture != null )
		{
			return startPicture.getTime();
		}
		return 0.0;
	}
	
	public Double getFinishTime()
	{
		if( finishPicture != null )
		{
			return finishPicture.getTime();
		}
		return 0.0;
	}
	
	public Picture getNext()
	{
		if( picturesAvailable() )
		{
			return pictures.remove( 0 );
		}
		return null;
	}
	
	public boolean picturesAvailable()
	{
		return !pictures.isEmpty();
	}
}
