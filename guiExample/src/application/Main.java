package application;
	
import org.opencv.core.Core;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		MainAppController controller = null;
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("MainApp.fxml") );
			AnchorPane root = (AnchorPane) loader.load();
			controller = loader.getController();
			controller.setStage(primaryStage);
			Scene scene = new Scene(root,800,600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
