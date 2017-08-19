package application;

import java.io.Serializable;

public class QualifyingResult implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String getRound() {
		return round;
	}
	public void setRound(String round) {
		this.round = round;
	}
	public Double getTime() {
		return time;
	}
	public void setTime(double time) {
		this.time = time;
	}
	private String round;
	private Double time;
	public String getResultString() 
	{
		String result = "";
		result += round + ":";
		result += "\t";
		result += String.format("%.2f", time);
		return result;
	}


}
