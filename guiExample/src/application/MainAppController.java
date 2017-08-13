package application;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import exceptions.NoValueEnteredException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainAppController 
{
	private Stage primaryStage = null;
	private ArrayList<Car> cars = new ArrayList<Car>();
	private ArrayList<Car> sortedCars = new ArrayList<Car>();
	private ImageWorker imageWorker = new ImageWorker();
	private int raceCounter = 0;
	
	@FXML
	private Button createCarButton;
	@FXML
	private Button deleteCarButton;
	@FXML
	private Button buttonStartQualifying;
	@FXML
	private Button removeQualifyingButton;
	@FXML
	private Button startRaceButton;
	@FXML
	private ListView<String> carListView;
	@FXML
	private TextField driverNameTextField;
	@FXML
	private TextField textRaceStatus;
	@FXML
	private ImageView carTemplateImage;
	@FXML
	private ListView<String> qualifyingTimesListView;
	@FXML
	private TableView<RaceResult> racesOfCarTable;
	@FXML
	private TableView<Car> raceTable;
	@FXML
	private Tab tabRennen;
	
	public MainAppController() 
	{
		super();	
	}

	@FXML
	public void createNewCar()
	{
		try 
		{
			Car car = new Car();
			car.setCarId( cars.size()+1 );

			TextInputDialog dialog = new TextInputDialog("Driver Name");
			dialog.setTitle("Bitte Fahrer-Namen eingeben");
			dialog.setHeaderText(null);
			dialog.setContentText("Bitte Fahrer Namen eingeben:");		
			Optional<String> result = dialog.showAndWait();
			if (result.isPresent()){
			    car.setDriverName(result.get());
			}
			else
			{
				throw new NoValueEnteredException();
			}
			//Open Template Picture
			FileChooser fileDialog = new FileChooser();
			fileDialog.setTitle("Bitte Bild zum Wiedererkennen des Autos auswählen");
			fileDialog.setInitialDirectory(new File("C:\\projekte\\sikuRacing\\templates"));
			File file = fileDialog.showOpenDialog(primaryStage);
			Image value;
			value = new Image(file.toURI().toURL().toString());
			car.setCarMask(OpenCvUtils.imageToMat( value, true ));
			cars.add(car);
			updateCarListView();
		} catch (Exception e) 
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("Car could not be created!");
			alert.setHeaderText("ERROR: " + e.getClass() );
			alert.setTitle("ERROR");
			alert.showAndWait();
			e.printStackTrace();
		}

	}

	@FXML
	public void deleteCar()
	{
		int index  = carListView.getSelectionModel().getSelectedIndex();
		if( index >= 0 )
		{
			cars.remove( index );
		}
		updateCarListView();
	}

	@FXML
	public void carSelected()
	{
		int index  = carListView.getSelectionModel().getSelectedIndex();
		if( index >= 0 )
		{
			updateMainFrame( cars.get(index) );
		}
	}
	
	@FXML
	public void runQualifying() throws InterruptedException
	{
		int index  = carListView.getSelectionModel().getSelectedIndex();
		if( index >= 0 )
		{
			Car car = cars.get(index);
			imageWorker.startRace( cars, true );
			while( imageWorker.isRaceRunning() )
			{
				Thread.sleep(100);
			}
			double lapTime = imageWorker.getFinishTime() - imageWorker.getStartTime();
			car.setQualifyingTime(lapTime);
			updateMainFrame( car );
		}		
	}
	
	@FXML
	public void removeQualifyingTime()
	{		
		int timeIndex  = qualifyingTimesListView.getSelectionModel().getSelectedIndex();
		int carIndex  = carListView.getSelectionModel().getSelectedIndex();
		if( timeIndex >= 0 && carIndex >= 0 )
		{
			cars.get( carIndex ).getSortedQualifyingTimes().remove(timeIndex);
		}
		updateCarListView();		
	}
	
	@FXML
	public void startRace()
	{
		if( cars.isEmpty() )
		{
			textRaceStatus.setText("!!! keine Autos angelegt !!!");
			textRaceStatus.setAlignment( Pos.CENTER);
			return;
		}
		textRaceStatus.setText("Rennen läuft !!!");
		textRaceStatus.setAlignment( Pos.CENTER);
		raceCounter++;
		imageWorker.startRace(cars, false );
	}
	
	@FXML
	public void stopRace() throws InterruptedException
	{
		imageWorker.stopRace();
		textRaceStatus.setText("warte...");
		textRaceStatus.setAlignment( Pos.CENTER);
		Race raceResult = imageWorker.getCurrentRaceData();
		for( Car car : cars )
		{
			RaceResult result = new RaceResult();
			result.setFinishedLaps( raceResult.getLapsForCar(car));
			result.setPosition( raceResult.getRankingOfCar(car));
			result.setLapTimes(raceResult.getLapTimesForCar(car) );
			result.setRaceId(raceCounter);
			result.setPoints( raceResult.getPointsForCar( car ));
			car.getRaces().add(result);
			System.out.println( "Added Result to car: " + car.getDriverName() );
		}
		updateRaceTable();
	}
	
	@FXML
	private void tabRennenSelected()
	{
		System.out.println("Tab Rennen entered");
		updateRaceTable();
	}
	
	private void updateRaceTable()
	{
		sortedCars = cars;
		if( raceCounter == 0 )
		{
			sortedCars.sort(carComparatorByQualifying);
		}
		else
		{
			sortedCars.sort(carComparatorByTotalPoints);
			Collections.reverse(cars);
		}
		int i = 1;
		for( Car car : sortedCars )
		{
			car.setStartPosition( String.format("%2d", i) );
			i++;
		}
		if( raceTable.getColumns().isEmpty() )
		{
			initraceOverviewTable();
		}
		ObservableList<Car> list = getObservableCars();
		raceTable.setItems(list);
	}
	
	private ObservableList<Car> getObservableCars() 
	{
		ObservableList<Car> list = FXCollections.observableArrayList();// = ObservableList<RaceResult>
		list.addAll( sortedCars );
		return list;
	}

	private void updateMainFrame(Car car) 
	{
		driverNameTextField.setText( car.getDriverName() );
		carTemplateImage.setImage( OpenCvUtils.mat2Image( car.getCarMask() ));
		qualifyingTimesListView.getItems().clear();
		for( QualifyingResult result : car.getSortedQualifyingTimes() )
		{
			qualifyingTimesListView.getItems().add(result.getResultString());		
		}
		// Fill Table with Race-Data
	
		if( racesOfCarTable.getColumns().isEmpty() )
		{
			initCarRaceTable();
		}
		ObservableList<RaceResult> list = car.getObservableRaces();
		racesOfCarTable.setItems(list);
	}

	private void updateCarListView() 
	{
		carListView.getItems().clear();
		for( Car car : cars )
		{
			carListView.getItems().add( car.getDriverName() );			
		}
	}

	public void setStage(Stage _primaryStage) 
	{
		primaryStage = _primaryStage;		
	}
	
	public void shutdown()
	{
		imageWorker.shutdown();
	}
	
	public List<Car> getCarsSortedByQualifyingTimes() 
	{
		cars.sort(carComparatorByQualifying);
		return cars;
	}
	
	private void initCarRaceTable()
	{
		TableColumn<RaceResult, String> colRaceId = new TableColumn<RaceResult, String>("Rennen");
		TableColumn<RaceResult, String> colrank = new TableColumn<RaceResult, String>("Position");
		TableColumn<RaceResult, String> colPoints = new TableColumn<RaceResult, String>("Punkte");
		TableColumn<RaceResult, String> colLap1 = new TableColumn<RaceResult, String>("Runde 1");
		TableColumn<RaceResult, String> colLap2 = new TableColumn<RaceResult, String>("Runde 2");
		TableColumn<RaceResult, String> colLap3 = new TableColumn<RaceResult, String>("Runde 3");
		
		colRaceId.setCellValueFactory( new PropertyValueFactory<>("stringRaceId"));
		colrank.setCellValueFactory( new PropertyValueFactory<>("stringPosition"));
		colPoints.setCellValueFactory( new PropertyValueFactory<>("stringPoints"));
		colLap1.setCellValueFactory( new PropertyValueFactory<>("stringLap1"));
		colLap2.setCellValueFactory( new PropertyValueFactory<>("stringLap2"));
		colLap3.setCellValueFactory( new PropertyValueFactory<>("stringLap3"));

		racesOfCarTable.getColumns().addAll( colRaceId, colrank, colPoints, colLap1, colLap2, colLap3);
	}
	
	private void initraceOverviewTable()
	{
		TableColumn<Car, String> colPosition  = new TableColumn<Car, String>("Position");
		TableColumn<Car, String> colDriver    = new TableColumn<Car, String>("Fahrer");
		TableColumn<Car, String> colRace1     =  new TableColumn<Car, String>("Rennen 1");
		TableColumn<Car, String> colRace2     =  new TableColumn<Car, String>("Rennen 2");
		TableColumn<Car, String> colRace3     =  new TableColumn<Car, String>("Rennen 3");
		TableColumn<Car, String> colRace4     =  new TableColumn<Car, String>("Rennen 4");
		TableColumn<Car, String> colRace5     =  new TableColumn<Car, String>("Rennen 5");
		TableColumn<Car, String> colRace6     =  new TableColumn<Car, String>("Rennen 6");
		TableColumn<Car, String> colRace7     =  new TableColumn<Car, String>("Rennen 7");
		TableColumn<Car, String> colRace8     =  new TableColumn<Car, String>("Rennen 8");
		TableColumn<Car, String> colRace9     =  new TableColumn<Car, String>("Rennen 9");
		TableColumn<Car, String> colRace10    = new TableColumn<Car, String>("Rennen 10");
		TableColumn<Car, String> colPointsSum = new TableColumn<Car, String>("Ges. Punkte");
		
		colPosition.setCellValueFactory( new PropertyValueFactory("startPosition"));
		colDriver.setCellValueFactory( new PropertyValueFactory("driverName"));
		colRace1.setCellValueFactory( new PropertyValueFactory("pointsRace1"));
		colRace2.setCellValueFactory( new PropertyValueFactory("pointsRace2"));
		colRace3.setCellValueFactory( new PropertyValueFactory("pointsRace3"));
		colRace4.setCellValueFactory( new PropertyValueFactory("pointsRace4"));
		colRace5.setCellValueFactory( new PropertyValueFactory("pointsRace5"));
		colRace6.setCellValueFactory( new PropertyValueFactory("pointsRace6"));
		colRace7.setCellValueFactory( new PropertyValueFactory("pointsRace7"));
		colRace8.setCellValueFactory( new PropertyValueFactory("pointsRace8"));
		colRace9.setCellValueFactory( new PropertyValueFactory("pointsRace9"));
		colRace10.setCellValueFactory( new PropertyValueFactory("pointsRace10))"));
		colPointsSum.setCellValueFactory( new PropertyValueFactory("pointsSum"));

		raceTable.getColumns().addAll( 
				colPosition, 
				colDriver,   
				colRace1,    
				colRace2,    
				colRace3,    
				colRace4,    
				colRace5,    
				colRace6,    
				colRace7,    
				colRace8,    
				colRace9,    
				colRace10,   
				colPointsSum );
										
	}
	
	Comparator<Car> carComparatorByQualifying = new Comparator<Car>(){
		@Override
		public int compare(Car arg0, Car arg1) {
			return arg0.getBestQualifyingTime().compareTo(arg1.getBestQualifyingTime());
		}
	};
	
	Comparator<Car> carComparatorByTotalPoints = new Comparator<Car>(){
		@Override
		public int compare(Car arg0, Car arg1) {
			return arg0.getTotalPoints().compareTo(arg1.getTotalPoints());
		}
	};
	
}
