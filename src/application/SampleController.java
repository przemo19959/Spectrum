package application;

import java.io.File;

import application.commons.AudioCommon;
import application.commons.SpectrumCommon;
import application.threads.Drawer;
import application.threads.PlayingThread;
import application.threads.ProcessingThread;
import application.threads.SongState;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.shape.Circle;
import javafx.scene.Group;

/**
 * Responsibilites:
 * <ul>
 * <li></li>
 * </ul>
 * @author hex
 *
 */
public class SampleController {
	//@formatter:off
	@FXML private HBox box;
	@FXML private Canvas canvas;
	@FXML private BorderPane mainPane;
	@FXML private Screen screen;
	@FXML private Label songInfo;
	@FXML private Circle playButton;
	@FXML private Circle stopButton;
	@FXML private Circle pauseButton;
	@FXML private Group playGroup;
	@FXML private Group pauseGroup;
	@FXML private Group stopGroup;
	@FXML private Group fileGroup;
	@FXML private Circle fileButton;
	//@formatter:on
	
	private int displayedBars=420;
	private GraphicsContext gc;
	
	private PlayingThread t1;
	private ProcessingThread t2;
	private Drawer t3;

	private AudioCommon audioCommon = new AudioCommon();
	private SpectrumCommon spectrumCommon=new SpectrumCommon();
	
	private Circle[] circles;
	private Group[] buttons;
	private Stage mainStage;
	private FileChooser chooser=new FileChooser();
	
	private void init() {
		spectrumCommon.setParams(displayedBars, gc);
		t1 = new PlayingThread(audioCommon);
		t1.start();
		
		songInfo.setText(t1.getAudioInfo());
		
		t2 = new ProcessingThread(audioCommon, spectrumCommon);
		t3 = new Drawer(spectrumCommon);
	}
	
	void terminateThreads() {//@formatter:off
		if(t1!=null)t1.stop();
		if(t2!=null)t2.stop();
		if(t3!=null)t3.stop();
	}//@formatter:on
	
	void setup(Stage stage) {
		mainStage=stage;
		fileGroup.setOnMouseClicked(val->{
			File file=chooser.showOpenDialog(stage);
			if(file!=null) {
				String path=file.getAbsolutePath();
				if(path.endsWith(".mp3") || path.endsWith(".wav")) {
					if(t1!=null && t1.isAlive())
						 t1.stop();
					audioCommon.setAudioFilePath(path);
				}
			}
		});
	}

	@FXML
	private void initialize() {
		buttons=new Group[] {playGroup,pauseGroup,stopGroup,fileGroup};
		circles=new Circle[] {playButton,pauseButton,stopButton,fileButton};
		addButtonsEffect();
		
		gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight()); //fill with black color
		
		playGroup.setOnMouseClicked(val -> {
			if(audioCommon.getState().equals(SongState.STOPPED))
				init();// inicjalizacja
			else if(audioCommon.getState().equals(SongState.PAUSED)) {
				t2.resume();
				t3.start();
			}
			audioCommon.setState(SongState.PLAYING);
			disableGroups(true, false, false);
		});
		
		stopGroup.setOnMouseClicked(val->{
			if(audioCommon.getState().equals(SongState.PLAYING))
				terminateThreads();
			audioCommon.setState(SongState.STOPPED);
			disableGroups(false, true, true);
		});
		
		pauseGroup.setOnMouseClicked(val->{
			audioCommon.setState(SongState.PAUSED);
			if(audioCommon.getState().equals(SongState.PAUSED)) {
				t2.suspend();
				t3.stop();
			}
			disableGroups(false, true, true);
		});
	}
	
	private void disableGroups(boolean play, boolean pause, boolean stop) {
		playGroup.setDisable(play);
		stopGroup.setDisable(pause);
		pauseGroup.setDisable(stop);
	}
	
	private void addButtonsEffect() {
		for(int i=0;i<buttons.length;i++) {
			final int iterator=i;
			buttons[i].setOnMouseEntered(val->circles[iterator].setFill(Color.ORANGE));
			buttons[i].setOnMouseExited(val->circles[iterator].setFill(Color.DARKORANGE));
			buttons[i].setOnMousePressed(val->circles[iterator].setRadius(circles[iterator].getRadius()-4));
			buttons[i].setOnMouseReleased(val->circles[iterator].setRadius(circles[iterator].getRadius()+4));
		}
	}
}
