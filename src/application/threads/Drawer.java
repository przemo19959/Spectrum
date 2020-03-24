package application.threads;

import application.commons.SpectrumCommon;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Drawer{
	private SpectrumCommon spectrumCommon;
	private Timeline drawingTimer=new Timeline(new KeyFrame(Duration.millis(10), val->{
		spectrumCommon.drawSpectrum();
	}));
	
	//na potrzeby test�w
//	private long startTime;
	
	public Drawer(SpectrumCommon spectrumCommon) {
		super();
		this.spectrumCommon=spectrumCommon;
		drawingTimer.setAutoReverse(true);
		drawingTimer.setCycleCount(Timeline.INDEFINITE);
		start();
	}
	
	public void start() {
		drawingTimer.playFromStart();
		System.out.println("Drawer started");
	}
				
	public void stop() {
		drawingTimer.stop();
		spectrumCommon.clearSpectrum();
		System.out.println("Drawer terminated");
	}	
}
