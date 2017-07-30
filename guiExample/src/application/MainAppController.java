package application;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import exceptions.NoValueEnteredException;
import javafx.event.ActionEvent;
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
	private List<Car> cars = new ArrayList<Car>();
	private OpenCvUtils cvUtil = null;
	
	@FXML
	private Button createCarButton;
	@FXML
	private Button deleteCarButton;
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
		cvUtil = new OpenCvUtils();
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
			fileDialog.setInitialDirectory(new File(".\\..\\templates"));
			File file = fileDialog.showOpenDialog(primaryStage);
			Image value;
			value = new Image(file.toURI().toURL().toString());
			car.setCarMask(cvUtil.imageToMat( value, true ));
			
			//TODO: Remove this. Just to have some sample Qualifying Results
			car.setQualifyingTime(23);
			car.setQualifyingTime(88);
			car.setQualifyingTime(42);
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
	
	private void updateMainFrame(Car car) 
	{
		driverNameTextField.setText( car.getDriverName() );
		carTemplateImage.setImage( cvUtil.mat2Image( car.getCarMask() ));
		qualifyingTimesListView.getItems().clear();
		for( QualifyingResult result : car.getSortedQualifyingTimes() )
		{
			qualifyingTimesListView.getItems().add(result.getResultString());		
		}
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
}
