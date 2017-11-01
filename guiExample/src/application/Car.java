package application;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Car implements Serializable 
{
	@Override
	public String toString() {
		return "Car [driverName=" + driverName + ", carMask=" + carMask + ", carId=" + carId + "]";
	}
	private Logger log = LogManager.getRootLogger();
	/**
	 * 
	 */
	private static final long serialVersionUID = -8323328231861385337L;
	//Variables for DataModel of race-Table
	private String startPosition;
	private String pointsRace1;
	private String pointsRace2;
	private String pointsRace3;
	private String pointsRace4;
	private String pointsRace5;
	private String pointsRace6;
	private String pointsRace7;
	private String pointsRace8;
	private String pointsRace9;
	private String pointsRace10;
	private String pointsSum;
	
	private String driverName; //Name des Fahrers
	private String carMask;
	private int carId = -1;
	private List<QualifyingResult> qualifyingTimes = new ArrayList<QualifyingResult>(); // Zeiten aus Qualifying
	private List<RaceResult> raceResults = new ArrayList<RaceResult>(); // Liste mit Daten der einzelnen Rennen

	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		log.debug("Car " + carId + ": Set Driver Name to: " + driverName );
		this.driverName = driverName;
	}
	public File getCarMask() {
		return new File(carMask);
	}
	public void setCarMask(File carMask) {
		log.debug("Car " + carId + ": Set Mask to: " + carMask.getAbsolutePath() );
		this.carMask = carMask.getAbsolutePath();
	}
	public int getCarId() {
		return carId;
	}
	public void setCarId(int carId) {
		log.debug("Car " + carId + ": Set Car ID to: " + carId );
		this.carId = carId;
	}
	public List<QualifyingResult> getSortedQualifyingTimes() 
	{
		qualifyingTimes.sort(Comparators.qualifyingResultComparator);
		log.debug("Car " + carId + ": Returning sorted Qualifying Times: " + qualifyingTimes );
		return qualifyingTimes;
	}
	
	public Double getBestQualifyingTime()
	{
		if( qualifyingTimes.size() > 0 )
		{
			return getSortedQualifyingTimes().get(0).getTime();
		}
		return 100.0;
	}
	
	public void setQualifyingTime(double qualifyingTime) {
		int count = qualifyingTimes.size();
		QualifyingResult result = new QualifyingResult();
		result.setRound("Runde " + (count+1) );
		result.setTime(qualifyingTime);
		log.debug("Car " + carId + ": Adding QualifyingResult: " + result );
		qualifyingTimes.add(result);
	}
	
	public List<RaceResult> getRaces() {
		return raceResults;
	}
	public void setRaces(List<RaceResult> races) {
		this.raceResults = races;
	}
	
	public Double getTotalPoints()
	{
		double totalPoints = 0.0;
		for( RaceResult result : raceResults )
		{
			totalPoints += result.getPoints();
		}
		log.debug("Car " + carId + ": Returning total Points: " + totalPoints );
		return totalPoints;
	}
	
//	private static Comparator<QualifyingResult> comparator = new Comparator<QualifyingResult>(){
//		@Override
//		public int compare(QualifyingResult arg0, QualifyingResult arg1) {
//			return arg0.getTime().compareTo(arg1.getTime());
//		}
//	};

	
	/** Getter to provide DataModel to carRaceTable
	 * 
	 * @return
	 */
	public ObservableList<RaceResult> getObservableRaces() 
	{
		ObservableList<RaceResult> list = FXCollections.observableArrayList();// = ObservableList<RaceResult>
		list.addAll( raceResults );
		return list;
	}
	
	//Getter for Data Model of raceTable
	public String getStartPosition() {
		//Has to be set by owner of CarList. The Car itself does not know the other Cars
		return startPosition;
	}
	public void setStartPosition(String startPosition) {
		this.startPosition = startPosition;
	}
	public String getPointsRace1() {
		if(raceResults.size() >= 1 ) {
			return String.format("%.1f", raceResults.get(0).getPoints() );
		}
		return "";
	}
	public String getPointsRace2() {
		if(raceResults.size() >= 2 ) {
			return String.format("%.1f", raceResults.get(1).getPoints() );
		}
		return "";
	}
	public String getPointsRace3() {
		if(raceResults.size() >= 3 ) {
			return String.format("%.1f", raceResults.get(2).getPoints() );
		}
		return "";
	}
	public String getPointsRace4() {
		if(raceResults.size() >= 4 ) {
			return String.format("%.1f", raceResults.get(3).getPoints() );
		}
		return "";
	}
	public String getPointsRace5() {
		if(raceResults.size() >= 5 ) {
			return String.format("%.1f", raceResults.get(4).getPoints() );
		}
		return "";
	}
	public String getPointsRace6() {
		if(raceResults.size() >= 6 ) {
			return String.format("%.1f", raceResults.get(5).getPoints() );
		}
		return "";
	}
	public String getPointsRace7() {
		if(raceResults.size() >= 7 ) {
			return String.format("%.1f", raceResults.get(6).getPoints() );
		}
		return "";
	}
	public String getPointsRace8() {
		if(raceResults.size() >= 8 ) {
			return String.format("%.1f", raceResults.get(7).getPoints() );
		}
		return "";
	}
	public String getPointsRace9() {
		if(raceResults.size() >= 9 ) {
			return String.format("%.1f", raceResults.get(8).getPoints() );
		}
		return "";
	}
	public String getPointsRace10() {
		if(raceResults.size() >= 10 ) {
			return String.format("%.1f", raceResults.get(9).getPoints() );
		}
		return "";
	}
	public String getPointsSum() {
		return String.format("%.1f", getTotalPoints() );
	}

}
