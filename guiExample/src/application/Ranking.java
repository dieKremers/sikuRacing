package application;

import java.io.Serializable;

public class Ranking implements Serializable{

	private Car car;
	private Integer rank;
	/**
	 * 
	 */
	private static final long serialVersionUID = 865511146552485530L;
	private String stringDriver;
	private String stringRank;

	public String getStringDriver() {
		return car.getDriverName();
	}
	
	public String getStringRank() {
		return Integer.toString(rank);
	}
	
	public Car getCar() {
		return car;
	}
	public void setCar(Car car) {
		this.car = car;
	}
	public Integer getRank() {
		return rank;
	}
	public void setRank(Integer rank) {
		this.rank = rank;
	}
	

	
}
