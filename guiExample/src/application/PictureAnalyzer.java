package application;

import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class PictureAnalyzer 
{
	ImageWorker imageWorker = null;
	
	public PictureAnalyzer(ImageWorker _imageWorker) {
		imageWorker = _imageWorker;
	}

	public void checkPicture( Picture pic ) 
	{
		ArrayList<MatchResult> results = new ArrayList<MatchResult>();
		// Hier iteriere ich durch alle Autos und suche für jedes Auto nach dem besten Treffer
		for( Car car : imageWorker.cars )
		{
			MatchResult tempResult = checkPictureForCar(car, pic);
			//Wenn ich noch keinen Treffer habe kann ich das direkt speichern
			if( results.isEmpty() )
			{
				results.add(tempResult);
				continue;
			}
			//Jetzt gucke ich ob ich zu der Position schon einen Treffer habe und ob der aktuelle Treffer besser ist. 
			boolean locationAlreadyKnown = false;
			for( int i = 0; i < results.size(); i++ )
			{
				if( results.get(i).isSameLocation(tempResult.getLocation() ))
				{
					locationAlreadyKnown = true;
					if( results.get(i).getPercentage() < tempResult.getPercentage() )
					{
						results.set(i, tempResult );
					}
					break;
				}
			}
			//Wenn ich an der Position noch nichts habe, dann speichere ich den Treffer
			if( !locationAlreadyKnown )
			{
				results.add(tempResult);
			}
		}
		
		//Jetzt hab ich alle besten Treffer auf dem Bild
		//Für alle Treffer > [templateThreshold] trage ich einen Treffer ein
		for( MatchResult result : results )
		{
			//add result to current Race data and create picture for debugging 
			if( result.getPercentage() > imageWorker.templateThreshold )
			{
				String percentageInfo = String.format("Percentage: %.2f", result.getPercentage() );
				imageWorker.currentRace.addLap( result.getCar(), pic.getTime() );
				if( imageWorker.saveImages )
				{
					Point location = result.getLocation();
					Mat template = imageWorker.carMats.get(result.getCar());
					Mat frame = result.getPicture();
					Point point2 = new Point();
					Point point4 = new Point(10,80);
					point2.x = location.x + template.width();
					point2.y = location.y + template.height();
					Scalar colour = new Scalar(Imgproc.COLORMAP_PINK);
					Imgproc.rectangle(frame, location, point2, colour);
					Imgproc.putText(frame, percentageInfo , point4 , 0, 1, colour);
					String filename = "C:\\projekte\\sikuRacing\\logging\\" + result.getCar().getDriverName() + "_"  + (pic.getTime()-imageWorker.startTime) + ".png";
					Imgcodecs.imwrite(filename, frame);		
				}
			}	
		}
	}

	private MatchResult checkPictureForCar(Car car, Picture pic) 
	{
		int method = Imgproc.TM_CCOEFF_NORMED;
		double percentage = 0.0;
		Point location;
		Mat frame = new Mat();
		Mat result = new Mat();
		Mat template = imageWorker.carMats.get(car).clone();
		MatchResult matchResult = new MatchResult();
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
			//check if current result is the best
			if( percentage > matchResult.getPercentage() )
			{
				matchResult = new MatchResult(percentage, location, frame.clone(), car );
			}
		}
		return matchResult;
	}

}
