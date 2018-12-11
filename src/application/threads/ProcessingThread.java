package application.threads;


import java.util.concurrent.atomic.AtomicBoolean;

import application.commons.AudioCommon;
import application.commons.SpectrumCommon;
import application.dsp.DSP;

public class ProcessingThread implements Runnable {
	private Thread t;
	private AtomicBoolean isSuspended=new AtomicBoolean(false);
	private AtomicBoolean isStopped=new AtomicBoolean(false);
	
	private AudioCommon audioCommon;
	private DSP dsp;
	
	private SpectrumCommon spectrumCommon;
	
	//na potrzeby testów
//	private long startTime;
	
	public ProcessingThread(AudioCommon audioCommon, DSP dsp, SpectrumCommon spectrumCommon) {
		this.dsp=dsp;
		this.audioCommon=audioCommon;
		this.spectrumCommon=spectrumCommon;
		t=new Thread(this, "Processor");
		t.start();
	}
	
	public void stop() {
		t.interrupt();
		isStopped.set(true);
	}
	
	public void suspend() {
		isSuspended.set(true);
	}
	
	public synchronized void resume() {
		isSuspended.set(false);
		notify();
	}
		
	public void process() {
		dsp.withSamples(audioCommon.getSamples());	//pobierz próbki
		if(!dsp.isSamplesArrayNull()) {
			dsp.calculateFFT();	//oblicz FFT
		}
	}
		
	@Override
	public void run() {
		try {
			while(true) {
				synchronized (this) {
					while(isSuspended.get())
						wait();
				}
				if(isStopped.get())
					break;
//				startTime=System.currentTimeMillis();
				process();
				spectrumCommon.updateSpectrum(dsp.getSpectrum(spectrumCommon.getDisplayedBars())); //aktualizuj widmo do rysowania
//				System.out.println("Processor: "+(System.currentTimeMillis()-startTime)+"[ms]");
			}
		}catch(InterruptedException ie) {
			ie.printStackTrace();
		}
		System.out.println(Thread.currentThread().getName()+" terminated");
	}
}
