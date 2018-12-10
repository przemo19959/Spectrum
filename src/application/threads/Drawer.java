package application.threads;

import application.commons.SpectrumCommon;
import javafx.animation.AnimationTimer;

public class Drawer extends AnimationTimer{
	private SpectrumCommon spectrumCommon;
	private long lastUpdate=0;
	
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
		System.out.println(((now-lastUpdate)*1e-6)+"[ms]");
//		if(now-lastUpdate>=20_000_000) {	//delta T >= 30ms
			spectrumCommon.drawSpectrum();
			lastUpdate=now;
//		}
	}
}
