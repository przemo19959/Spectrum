package application;
	
import javafx.application.Application;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;


public class Main extends Application {
	private SampleController mainCon;
	private Rectangle2D monitor;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader=new FXMLLoader(getClass().getResource("Sample.fxml"));
			BorderPane root = loader.load();
			mainCon=loader.getController();
			monitor=Screen.getPrimary().getVisualBounds();
			Scene scene = new Scene(root,monitor.getWidth()-20,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setResizable(false);
			
			mainCon.setup(primaryStage);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void stop() throws Exception {
		super.stop();
		mainCon.terminateThreads();
	}
}
