package application;

import application.commons.AudioCommon;
import application.commons.SpectrumCommon;
import application.dsp.DSP;
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
import javafx.stage.Screen;
import javafx.scene.shape.Circle;
import javafx.scene.Group;

public class SampleController {
	@FXML
	private HBox box;
	@FXML
	private Canvas canvas;
	private int displayedBars=210;
	private GraphicsContext gc;

	private AudioCommon audioCommon = new AudioCommon();
	private SpectrumCommon spectrumCommon=new SpectrumCommon();
	private DSP dsp = new DSP();
	private PlayingThread t1;
	private ProcessingThread t2;
	private Drawer t3;
	@FXML
	private BorderPane mainPane;
	@FXML
	private Screen screen;
	@FXML Label songInfo;
	
	@FXML Circle playButton;
	@FXML Circle stopButton;
	@FXML Circle pauseButton;
	private Circle[] circles;
	
	@FXML Group playGroup;
	@FXML Group pauseGroup;
	@FXML Group stopGroup;
	private Group[] buttons;
	
	public void setSongInfo(String input) {
		songInfo.setText(input);
	}

	private void init() {
		spectrumCommon.setParams(displayedBars, gc);
		t1 = new PlayingThread(audioCommon);
		songInfo.setText(t1.getSongInfo());
		t1.start();
		t2 = new ProcessingThread(audioCommon, dsp, spectrumCommon);
		t3 = new Drawer(spectrumCommon);
	}
	
	public void terminateThreads() {
		if(t1!=null)
			t1.stop();
		if(t2!=null)
			t2.stop();
		if(t3!=null)
			t3.stop();
	}

	@FXML
	private void initialize() {
		buttons=new Group[] {playGroup,pauseGroup,stopGroup};
		circles=new Circle[] {playButton,pauseButton,stopButton};
		
		addButtonsEffect();
		
		gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight()); //wype³nij t³o na czarno
		
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
