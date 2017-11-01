package application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RaceResult implements Serializable
{
	@Override
	public String toString() {
		return "RaceResult [points=" + points + ", raceId=" + raceId + ", position=" + position + ", finishedLaps="
				+ finishedLaps + ", lapTimes=" + lapTimes + "]";
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	private String stringRaceId;
	private String stringPoints;
	private String stringPosition;
	private String stringLap1;
	private String stringLap2;
	private String stringLap3;
	
	private double points;
	private int raceId;
	private int position;
	private int finishedLaps;
	private List<Double> lapTimes = new ArrayList<Double>();

	public int getRaceId() {
		return raceId;
	}
	public void setRaceId(int raceId) {
		this.raceId = raceId;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public int getFinishedLaps() {
		return finishedLaps;
	}
	public void setFinishedLaps(int finishedLaps) {
		this.finishedLaps = finishedLaps;
	}
	public List<Double> getLapTimes() {
		return lapTimes;
	}
	public void setLapTimes(List<Double> lapTimes) {
		this.lapTimes = lapTimes;
	}
	public double getPoints() {
		return points;
	}
	public void setPoints(double d) {
		this.points = d;
	}
	
	// Getter for TableDataModel
	public String getStringRaceId() {
		stringRaceId = String.format("%2d", raceId);
		return stringRaceId;
	}
	public String getStringPoints() {
		stringPoints = String.format("%.1f", points);
		return stringPoints;
	}
	public String getStringPosition() {
		stringPosition = String.format("%2d", position);
		return stringPosition;
	}
	public String getStringLap1() {
		if( lapTimes.size() >= 1 )
		{
			stringLap1 = String.format("%.3f", lapTimes.get(0));			
		}
		else
		{
			stringLap1 = "not finished";
		}
		return stringLap1;
	}
	public String getStringLap2() {
		if( lapTimes.size() >= 2 )
		{
			stringLap2 = String.format("%.3f", lapTimes.get(1));			
		}
		else
		{
			stringLap2 = "not finished";
		}
		return stringLap2;
	}
	public String getStringLap3() {
		if( lapTimes.size() >= 3 )
		{
			stringLap3 = String.format("%.3f", lapTimes.get(2));			
		}
		else
		{
			stringLap3 = "not finished";
		}
		return stringLap3;
	}
	

}
