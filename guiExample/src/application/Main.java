package application;
	
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencv.core.Core;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class Main extends Application {
	MainAppController controller = null;
	@Override
	public void start(Stage primaryStage) {
		
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("MainApp.fxml") );
			AnchorPane root = (AnchorPane) loader.load();
			controller = loader.getController();
			controller.setStage(primaryStage);
			Scene scene = new Scene(root,1200,620);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("SIKU RACING App                   (by Michael Kremer 2017)");
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void stop()
	{
	    System.out.println("Stage is closing");
	    controller.shutdown();
	}
	
	public static void main(String[] args) {
		Logger log = LogManager.getRootLogger();
    	log.info("Configuration File Defined To Be :: "+ System.getProperty("log4j.configurationFile"));
		launch(args);
	}
}
