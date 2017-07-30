package application;

import java.util.ArrayList;
import java.util.List;

public class RaceResult 
{
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
	

}
