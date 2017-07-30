package application;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.opencv.core.Mat;

public class Car 
{
	private String driverName; //Name des Fahrers
	private Mat carMask; //Bild, mit dem das Auto eindeutig erkannt wird
	private int carId;
	List<QualifyingResult> qualifyingTimes = new ArrayList<QualifyingResult>(); // Zeiten aus Qualifying
	List<RaceResult> races = new ArrayList<RaceResult>(); // Liste mit Daten der einzelnen Rennen

	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	public Mat getCarMask() {
		return carMask;
	}
	public void setCarMask(Mat carMask) {
		this.carMask = carMask;
	}
	public int getCarId() {
		return carId;
	}
	public void setCarId(int carId) {
		this.carId = carId;
	}
	public List<QualifyingResult> getSortedQualifyingTimes() 
	{
		qualifyingTimes.sort(comparator);
		return qualifyingTimes;
	}
	public void setQualifyingTime(double qualifyingTime) {
		int count = qualifyingTimes.size();
		QualifyingResult result = new QualifyingResult();
		result.setRound("Runde " + (count+1) );
		result.setTime(qualifyingTime);
		qualifyingTimes.add(result);
	}
	public List<RaceResult> getRaces() {
		return races;
	}
	public void setRaces(List<RaceResult> races) {
		this.races = races;
	}
	
	Comparator<QualifyingResult> comparator = new Comparator<QualifyingResult>(){
		@Override
		public int compare(QualifyingResult arg0, QualifyingResult arg1) {
			return arg0.getTime().compareTo(arg1.getTime());
		}
	};
}
