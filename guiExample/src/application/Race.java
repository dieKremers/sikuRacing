package application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Race 
{
	public Race(ArrayList<application.Car> cars) 
	{
		super();
		// Add all Cars to hash-table
		for( Car car : cars )
		{
			results.put(car, new ArrayList<Double>() );			
		}
		
		//generate Points for Ranks (Rank 1000 is "not finished")
		// hashtable: Key = Rank; Value = Points
		points.put(1, 10.0);
		points.put(2, 9.0);
		points.put(3, 8.0);
		points.put(4, 7.0);
		points.put(5, 6.0);
		points.put(6, 5.0);
		points.put(7, 4.0);
		points.put(8, 3.0);
		points.put(9, 2.0);
		points.put(10, 1.0);
		points.put(1000, 0.0);
	}

	private double startTime;
	private Hashtable<Car,ArrayList<Double>> results = new Hashtable<Car, ArrayList<Double>>();
	private Hashtable<Car, Integer> ranking = new Hashtable<Car, Integer>();
	private Hashtable<Integer, Double> points = new Hashtable<Integer,Double>();
	
	public void setStartTime( double value )
	{
		startTime = value;
	}
	
	public int getLapsForCar( Car car )
	{
		if( results.containsKey(car) )
			return results.get( car ).size()-1;
		return 0;
	}
	
	public int getRankingOfCar( Car car )
	{
		if( ranking.isEmpty() )
		{
			generateRanking();
		}
		int rank = ranking.get(car);
		if( rank < 1000 ) //ranking bigger 1000 means: not finished
		{
			return ranking.get(car);
		}
		else
		{
			return ranking.get(car) - 1000;
		}
	}
	
	private void generateRanking()
	{

		//1. Find cars that reached finish after first car finished 3 laps
		//1.1 Find car that first fisnished 3rd lap
		double fastestFinishTime = 0.0;
		for( Car actCar : results.keySet())
		{
			if( results.get(actCar).size() == ( 4 ) )
			{
				double actFinishTime = results.get(actCar).get(3);
				if( fastestFinishTime == 0.0 || (actFinishTime < fastestFinishTime) )
				{
					fastestFinishTime = actFinishTime;
				}
			}
		}
		
		//2. Create Lists with the Cars that finished 1,2,3 laps or did not finish
		HashMap<Car, Double> carsWith3Laps = new HashMap<Car, Double>();
		HashMap<Car, Double> carsWith2Laps = new HashMap<Car, Double>();
		HashMap<Car, Double> carsWith1Lap = new HashMap<Car, Double>();
		ArrayList<Car> carsNotFinished = new ArrayList<Car>();
		for( Car actCar : results.keySet())
		{
			if( results.get(actCar).size() == 4 ) {
				carsWith3Laps.put(actCar, results.get(actCar).get(3));
			}
			else if( results.get( actCar).size() == 3 
					&& results.get(actCar).get(2) > fastestFinishTime ) //check if last lap was finished after finish of first car
			{
				carsWith2Laps.put( actCar, results.get(actCar).get(2) );
			}
			else if( results.get( actCar).size() == 2 
					&& results.get(actCar).get(1) > fastestFinishTime ) //check if last lap was finished after finish of first car
			{
				carsWith1Lap.put( actCar, results.get(actCar).get(1) );
			}
			else
			{
				carsNotFinished.add( actCar );
			}
		}
		//Sort each Map by Finish-Time
		carsWith3Laps = (HashMap<application.Car, Double>) sortByValue(carsWith3Laps);
		carsWith2Laps = (HashMap<application.Car, Double>) sortByValue(carsWith2Laps);
		carsWith1Lap = (HashMap<application.Car, Double>) sortByValue(carsWith1Lap);
		
		int rank = 1;
		for( Car actCar : carsWith3Laps.keySet() )
		{
			ranking.put(actCar, rank );
			rank++;
		}
		for( Car actCar : carsWith2Laps.keySet() )
		{
			ranking.put(actCar, rank );
			rank++;
		}
		for( Car actCar : carsWith1Lap.keySet() )
		{
			ranking.put(actCar, rank );
			rank++;
		}
		for( Car actCar : carsNotFinished )
		{
			ranking.put(actCar, rank + 1000 );  //rank bigger 1000 indicates: "not finished"
			// don't increment rank because not finished cars do all have the same rank
		}
		calculatePointsNotFinished( carsNotFinished.size() );
	}
	
	/**
	 * This method calculates how many points will be given to the cars that not finished.
	 * Rules: 
	 * 1. In every Race the same number of points should be distributed.
	 * 2. All cars that did not finish get the same number of points
	 *
	 * Example: 8 cars did the race and 2 cars did not finish.
	 * So the points for the 7th (4 Points) and the 8th (3 Points) are distributed
	 * to the two not finished cars. ==> Each car gets 3.5 Points.
	 * 
	 * @param numberOfcarsNotFinished
	 */
	private void calculatePointsNotFinished(int numberOfcarsNotFinished )
	{
		int numberOfCars = results.keySet().size();
		double pointsNotAssigned = 0.0;
		for( int i = (numberOfCars-numberOfcarsNotFinished+1); i<=numberOfCars; i++)
		{
			pointsNotAssigned += points.get( i );
		}
		double pointsForNotFinished = pointsNotAssigned / numberOfcarsNotFinished;
		points.put(1000, pointsForNotFinished );  //Rank 1000 is used for the points for not finished cars
	}
	
	/**
	 * adds a Lap to the Race.
	 * That means that the data from a picture is added to the measurements.
	 * This method ensures that two measurements for one car have to have a difference of
	 * at least one second. That should ensure that multiple pictures from one car are
	 * not counted as multiple laps
	 * 
	 * @param car
	 * @param time
	 */
	public void addLap( Car car, double time )
	{
		if( results.containsKey( car ) )
		{
			ArrayList<Double> times = results.get(car);
			int size = times.size();
			if( size == 0 )
			{
				times.add( time );
			}
			else
			{
				double lastLapTime = times.get( size-1 );
				if( (time - lastLapTime) > 1.0 ) //Rundenzeit nur eintragen wenn letzter Zeitstempel mehr als zwei Sekunden her ist
				{
					times.add( time );
				}
			}
			if( size != times.size())
			{
				results.put(car, times);
			}
		}
		else
		{
			ArrayList<Double> times = new ArrayList<Double>();
			times.add( time );
			results.put( car,  times );
		}
	}
	
	public static <Car, Double extends Comparable<? super Double>> Map<Car, Double> sortByValue(Map<Car, Double> map) {
	    return map.entrySet()
	              .stream()
	              .sorted(Map.Entry.comparingByValue(/*Collections.reverseOrder()*/))
	              .collect(Collectors.toMap(
	                Map.Entry::getKey, 
	                Map.Entry::getValue, 
	                (e1, e2) -> e1, 
	                LinkedHashMap::new
	              ));
	}

	public List<Double> getLapTimesForCar( Car car ) 
	{
		if( results.containsKey(car) )
		{
			List<Double> laptimes = new ArrayList<Double>();
			for( int i = 0; i<(results.get(car).size()-1);i++)
			{
				if( i == 0 ){ //Measure first Lap against Start-Time
					laptimes.add(results.get(car).get(i+1) - startTime);
				}
				else {
					laptimes.add(results.get(car).get(i+1) - results.get(car).get(i));					
				}
			}
			return laptimes;			
		}
		return new ArrayList<Double>();
	}

	/**
	 * returns the numer of points a car achived in this race
	 * @param car
	 * @return
	 */
	public double getPointsForCar(application.Car car) 
	{
		if( ranking.isEmpty() )
		{
			generateRanking();
		}
		if( !ranking.containsKey(car) ) { //wenn Auto keine Runde gefahren hat, hat es dennoch teilgenommen.
			return points.get(1000);
		}
		int rank = ranking.get(car);
		if( rank > 1000 )
		{
			return points.get(1000);
		}
		return points.get(rank);
	}
}