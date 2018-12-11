package application.threads;

import application.commons.SpectrumCommon;
import javafx.animation.AnimationTimer;

public class Drawer extends AnimationTimer{
	private SpectrumCommon spectrumCommon;
	
	//na potrzeby testów
//	private long startTime;
	
	public Drawer(SpectrumCommon spectrumCommon) {
		super();
		this.spectrumCommon=spectrumCommon;
		this.start();
	}
				
	public void stop() {
		super.stop();
		spectrumCommon.clearSpectrum();
		System.out.println("Drawer terminated");
	}
	
	@Override
	public void handle(long now) {
//		startTime=System.currentTimeMillis();
		spectrumCommon.drawSpectrum();
//		System.out.println("Drawer: "+(System.currentTimeMillis()-startTime)+"[ms]");
	}
}
