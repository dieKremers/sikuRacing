package tests;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.junit.BeforeClass;
import org.junit.Test;

import application.Car;
import application.Comparators;
import application.Race;

public class TestRaceResultHandling {
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
	public void testRaceWithAllLapsFinished()
	{
		/**
		 * Test with 3 Cars, all finish 3 laps
		 * first Car gets 10 point
		 * second Car gets 9 points
		 * third Car gets 8 points
		 */
		ArrayList<Car> cars = createCarsList(3);
		Race race = new Race(cars);

		race.addLap(cars.get(0), 1);
		race.addLap(cars.get(1), 0.5);
		race.addLap(cars.get(2), 0.3);
		
		race.addLap(cars.get(0), 10);
		race.addLap(cars.get(1), 9);
		race.addLap(cars.get(2), 12);
		race.addLap(cars.get(0), 20);
		race.addLap(cars.get(1), 21);
		race.addLap(cars.get(2), 22);
		race.addLap(cars.get(0), 30);
		race.addLap(cars.get(1), 31);
		race.addLap(cars.get(2), 32);
		assertEquals("Laps for Car 0:", 3, race.getLapsForCar(cars.get(0)));
		assertEquals("Laps for Car 1:", 3, race.getLapsForCar(cars.get(1)));
		assertEquals("Laps for Car 2:", 3, race.getLapsForCar(cars.get(2)));
		
		assertEquals("Points for Car 0:", 10.0, race.getPointsForCar(cars.get(0)), 0.0);
		assertEquals("Points for Car 1:", 9.0, race.getPointsForCar(cars.get(1)), 0.0);
		assertEquals("Points for Car 2:", 8.0, race.getPointsForCar(cars.get(2)), 0.0);
		
		assertEquals("Ranking for Car 0:", 1, race.getRankingOfCar(cars.get(0)));
		assertEquals("Ranking for Car 1:", 2, race.getRankingOfCar(cars.get(1)));
		assertEquals("Ranking for Car 2:", 3, race.getRankingOfCar(cars.get(2)));
	}

	@Test
	public void testRaceWithDifferenNumberOfFinishedLaps()
	{
		/**
		 * Test with 3 Cars, car 1 finished 3 laps, car 2 finished 2 laps, car 1 finished 1 lap
		 * first Car gets 10 point
		 * second Car gets 9 points
		 * third Car gets 8 points
		 */
		ArrayList<Car> cars = createCarsList(3);
		Race race = new Race(cars);

		race.addLap(cars.get(0), 1);
		race.addLap(cars.get(1), 0.5);
		race.addLap(cars.get(2), 0.3);
		
		race.addLap(cars.get(0), 10);
		race.addLap(cars.get(1), 9);
		
		race.addLap(cars.get(0), 20);
		
		race.addLap(cars.get(0), 30);
		race.addLap(cars.get(1), 31);
		race.addLap(cars.get(2), 32);
		
		assertEquals("Laps for Car 0:", 3, race.getLapsForCar(cars.get(0)));
		assertEquals("Laps for Car 1:", 2, race.getLapsForCar(cars.get(1)));
		assertEquals("Laps for Car 2:", 1, race.getLapsForCar(cars.get(2)));
		
		assertEquals("Points for Car 0:", 10.0, race.getPointsForCar(cars.get(0)), 0.0);
		assertEquals("Points for Car 1:", 9.0, race.getPointsForCar(cars.get(1)), 0.0);
		assertEquals("Points for Car 2:", 8.0, race.getPointsForCar(cars.get(2)), 0.0);
		
		assertEquals("Ranking for Car 0:", 1, race.getRankingOfCar(cars.get(0)));
		assertEquals("Ranking for Car 1:", 2, race.getRankingOfCar(cars.get(1)));
		assertEquals("Ranking for Car 2:", 3, race.getRankingOfCar(cars.get(2)));
	}

	@Test
	public void testRaceWith2of4AllLapsFinished()
	{
		/**
		 * Test with 4 Cars, only two cars finished 3 laps
		 * first Car gets 10 point
		 * second Car gets 9 points
		 * third Car gets 7.5 points
		 * fourth Car gets 7.5 points
		 */
		ArrayList<Car> cars = createCarsList(4);
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
		
		
		assertEquals("Laps for Car 0:", 3, race.getLapsForCar(cars.get(0)));
		assertEquals("Laps for Car 1:", 3, race.getLapsForCar(cars.get(1)));
		assertEquals("Laps for Car 2:", 2, race.getLapsForCar(cars.get(2)));
		assertEquals("Laps for Car 3:", 1, race.getLapsForCar(cars.get(3)));
		
		assertEquals("Points for Car 0:", 10.0, race.getPointsForCar(cars.get(0)), 0.0);
		assertEquals("Points for Car 1:", 9.0, race.getPointsForCar(cars.get(1)), 0.0);
		assertEquals("Points for Car 2:", 7.5, race.getPointsForCar(cars.get(2)), 0.0);
		assertEquals("Points for Car 3:", 7.5, race.getPointsForCar(cars.get(3)), 0.0);
		
		assertEquals("Ranking for Car 0:", 1, race.getRankingOfCar(cars.get(0)));
		assertEquals("Ranking for Car 1:", 2, race.getRankingOfCar(cars.get(1)));
		assertEquals("Ranking for Car 2:", 3, race.getRankingOfCar(cars.get(2)));
		assertEquals("Ranking for Car 3:", 3, race.getRankingOfCar(cars.get(3)));
	}
	
	@Test
	public void testRankingAfterQualifying() 
	{
		ArrayList<Car> cars = createCarsList(3);
		cars.get(0).setQualifyingTime(14);
		cars.get(1).setQualifyingTime(14);
		cars.get(2).setQualifyingTime(14);
		cars.get(0).setQualifyingTime(10);
		cars.get(1).setQualifyingTime(11);
		cars.get(2).setQualifyingTime(9);
		cars.get(0).setQualifyingTime(11);
		cars.get(1).setQualifyingTime(12);
		cars.get(2).setQualifyingTime(13);
		
		cars.sort(Comparators.carComparatorByQualifying);
		assertTrue("Car with ID 3 should be first", cars.get(0).getCarId() == 3 );
		assertTrue("Car with ID 1 should be second", cars.get(1).getCarId() == 1 );
		assertTrue("Car with ID 2 should be third", cars.get(2).getCarId() == 2 );		
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
