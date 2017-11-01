package application;

import java.util.Comparator;

public final class Comparators {
	public static Comparator<Car> carComparatorByQualifying = new Comparator<Car>(){
		@Override
		public int compare(Car arg0, Car arg1) {
			return arg0.getBestQualifyingTime().compareTo(arg1.getBestQualifyingTime());
		}
	};
	
	public static Comparator<Car> carComparatorByTotalPoints = new Comparator<Car>(){
		@Override
		public int compare(Car arg0, Car arg1) {
			return arg0.getTotalPoints().compareTo(arg1.getTotalPoints());
		}
	};

	public static Comparator<QualifyingResult> qualifyingResultComparator = new Comparator<QualifyingResult>(){
		@Override
		public int compare(QualifyingResult arg0, QualifyingResult arg1) {
			return arg0.getTime().compareTo(arg1.getTime());
		}
	};
}
