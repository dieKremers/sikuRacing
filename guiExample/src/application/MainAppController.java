package application;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import exceptions.NoValueEnteredException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainAppController 
{
	private Stage primaryStage = null;
	private ArrayList<Car> cars = new ArrayList<Car>();
	private ImageWorker imageWorker = new ImageWorker();
	
	@FXML
	private Button createCarButton;
	@FXML
	private Button deleteCarButton;
	@FXML
	private Button buttonStartQualifying;
	@FXML
	private Button removeQualifyingButton;
	@FXML
	private ListView<String> carListView;
	@FXML
	private TextField driverNameTextField;
	@FXML
	private ImageView carTemplateImage;
	@FXML
	private ListView<String> qualifyingTimesListView;
	
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
			updateCarListView();
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
	
	private void updateMainFrame(Car car) 
	{
		driverNameTextField.setText( car.getDriverName() );
		carTemplateImage.setImage( OpenCvUtils.mat2Image( car.getCarMask() ));
		qualifyingTimesListView.getItems().clear();
		for( QualifyingResult result : car.getSortedQualifyingTimes() )
		{
			qualifyingTimesListView.getItems().add(result.getResultString());		
		}
	}

	private void updateCarListView() 
	{
		carListView.getItems().clear();
		for( Car car : getCarsSortedByQualifyingTimes() )
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
		cars.sort(carComparator);
		return cars;
	}
	
	Comparator<Car> carComparator = new Comparator<Car>(){
		@Override
		public int compare(Car arg0, Car arg1) {
			return arg0.getBestQualifyingTime().compareTo(arg1.getBestQualifyingTime());
		}
	};
}
