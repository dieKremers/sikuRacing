package application;

import org.opencv.core.Mat;
import org.opencv.core.Point;

/**
 * Das Teil speichert ein Ergebnis von einer MatchTemplate Operation, incl. Car und Picture.
 * Das nutzte ich um die einzelnen Treffer zu vergleichen und die besten auszusortieren.
 * @author dieKremers
 *
 */
public class MatchResult 
{
	private Car car;
	private Mat picture;
	private double percentage;
	private Point location;
	
	public double getPercentage() {
		return percentage;
	}
	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}
	public Point getLocation() {
		return location;
	}
	public void setLocation(Point location) {
		this.location = location;
	}
	public MatchResult(double percentage, Point location, Mat _picture, Car car) {
		super();
		this.percentage = percentage;
		this.location = location;
		this.picture = _picture;
		this.car = car;
	}

	public Car getCar() {
		return car;
	}
	public void setCar(Car car) {
		this.car = car;
	}
	public Mat getPicture() {
		return picture;
	}
	public void setPicture(Mat picture) {
		this.picture = picture;
	}
	public MatchResult() {
		percentage = 0.0;
		location = new Point(0, 0);
		picture = new Mat();
	}
	/**
	 * Checks if the Location of the Match is the same.
	 * A Template has a size of 30*30
	 * A different Car must be more than 30 pixels in every direction away.
	 * @param otherLocation
	 * @return 
	 */
	public boolean isSameLocation(Point otherLocation)
	{
		double xDiff = Math.abs( location.x - otherLocation.x );
		double yDiff = Math.abs( location.y - otherLocation.y );
		if( xDiff > 29.0 && yDiff > 29.0 )
		{
			return false;
		}
		return true;
	}

}
