package tests;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.junit.BeforeClass;
import org.junit.Test;

import application.Car;
import application.Comparators;
import application.Race;
import application.RaceResult;

public class TestRankingOfCars {
	Logger log = LogManager.getRootLogger();
	@BeforeClass
	public static void initTest()
	{
		// import org.apache.logging.log4j.core.LoggerContext;
		 
		LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
		File file = new File("C:/projekte/sikuRacing/guiExample/loggingConfig.xml");
		 
		// this will force a reconfiguration
		context.setConfigLocation(file.toURI());
	}
	
	@Test
	public void test() {
		ArrayList<Car> cars = createCarsList(4);
		//Ranking by Qualifying Times 4, 2, 1, 3
		cars.get(0).setQualifyingTime(10);
		cars.get(1).setQualifyingTime(9);
		cars.get(2).setQualifyingTime(11);
		cars.get(3).setQualifyingTime(8);
		
		//add Race with Ranking: 1, 2, 3+4 
		Race race = new Race(cars);

		race.addLap(cars.get(0), 1);
		race.addLap(cars.get(1), 0.5);
		race.addLap(cars.get(2), 0.3);
		race.addLap(cars.get(3), 0.3);
		
		race.addLap(cars.get(0), 10);
		race.addLap(cars.get(1), 9);
		race.addLap(cars.get(2), 12);
		race.addLap(cars.get(3), 8);
		
		race.addLap(cars.get(0), 20);
		race.addLap(cars.get(1), 21);
		race.addLap(cars.get(2), 22);

		race.addLap(cars.get(0), 30);
		race.addLap(cars.get(1), 31);
		
		for( Car car : cars )
		{
			RaceResult result = new RaceResult();
			result.setFinishedLaps( race.getLapsForCar(car));
			result.setPosition( race.getRankingOfCar(car));
			result.setLapTimes(race.getLapTimesForCar(car) );
			result.setRaceId(1);
			result.setPoints( race.getPointsForCar( car ));
			car.getRaces().add(result);
			log.info( "Added Result to car: " + car.getDriverName() );
		}
		
		assertEquals("Ranking for Car 0:", 1, race.getRankingOfCar(cars.get(0)));
		assertEquals("Ranking for Car 1:", 2, race.getRankingOfCar(cars.get(1)));
		assertEquals("Ranking for Car 2:", 3, race.getRankingOfCar(cars.get(2)));
		assertEquals("Ranking for Car 3:", 3, race.getRankingOfCar(cars.get(3)));
		
		cars.sort(Comparators.carComparatorByTotalPoints);
		printRanking(cars);
		//assertTrue( "Car with ID 3 should be on third place.", cars.get(2).getCarId() == 3 );
		
		cars.sort(Comparators.carComparatorByQualifying);
		printRanking(cars);
//		assertTrue("Car with ID 3 should be on fourth place.", cars.get(3).getCarId() == 3 );

		cars.sort(Comparators.carComparatorByTotalPoints);
		printRanking(cars);
//	assertTrue( "Car with ID 3 should be on fourth place.", cars.get(3).getCarId() == 3 );
	}
	
	void printRanking( ArrayList<Car> cars)
	{
		log.info("Ranking: ");
		for( Car car : cars )
		{
			log.info(car.getCarId());
		}
	}
	
	ArrayList<Car> createCarsList(int count)
	{
		ArrayList<Car> cars = new ArrayList<Car>();
		for( int i = 0; i < count; i++ )
		{
			Car car = new Car();
			car.setCarId(i+1);
			car.setDriverName("Driver " + i);
			cars.add(car);
		}
		return cars;
	}
}
