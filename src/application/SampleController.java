package application;

import application.commons.AudioCommon;
import application.commons.SpectrumCommon;
import application.dsp.DSP;
import application.threads.Drawer;
import application.threads.PlayingThread;
import application.threads.ProcessingThread;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;

public class SampleController {

	@FXML
	private Label info;
	@FXML
	private Button button;
	@FXML
	private HBox box;
	@FXML
	private Canvas canvas;
//	private int barWidth;
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
		t1.stop();
		t2.stop();
		t3.stop();
	}

	@FXML
	private void initialize() {
		info.setText("n: "+displayedBars);
		
		gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight()); //wype³nij t³o na czarno
		
		button.setOnAction(val -> {
			if(button.getText().equals("Play")) {
				button.setText("Pause");
				init();// inicjalizacja
			} else {
				button.setText("Play");
				terminateThreads();
			}
		});
	}
}
