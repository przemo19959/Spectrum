package application.threads;


import java.util.concurrent.atomic.AtomicBoolean;

import application.commons.AudioCommon;
import application.commons.SpectrumCommon;
import application.dsp.DSP;

public class ProcessingThread implements Runnable {
	private Thread t;
	private AtomicBoolean isSuspended=new AtomicBoolean(false);
	private AtomicBoolean isRunning=new AtomicBoolean(true);
	
	private AudioCommon audioCommon;
	private DSP dsp;
	
	private SpectrumCommon spectrumCommon;
	
	public ProcessingThread(AudioCommon audioCommon, DSP dsp, SpectrumCommon spectrumCommon) {
		this.dsp=dsp;
		this.audioCommon=audioCommon;
		this.spectrumCommon=spectrumCommon;
		t=new Thread(this, "Processor");
		t.start();
	}
	
	public void stop() {
		isRunning.set(false);
	}
	
	public void suspend() {
		isSuspended.set(true);;
	}
	
	public synchronized void resume() {
		isSuspended.set(false);
		notify();
	}
	
	public synchronized void process() {
		dsp.withSamples(audioCommon.getSamples());
		if(!dsp.isSamplesArrayNull()) {
			dsp.calculateFFT();
		}
	}
	
	public Thread.State getState() {
		return t.getState();
	}
	
	@Override
	public void run() {
		try {
			while(true) {
				synchronized (this) {
					while(isSuspended.get() && isRunning.get())
						wait();
				}
				if(!isRunning.get())
					break;
				process();
				spectrumCommon.updateSpectrum(dsp.getSpectrum(spectrumCommon.getDisplayedBars()));	//wczeœniej w process
			}
		}catch(InterruptedException ie) {
			ie.printStackTrace();
		}
		System.out.println(Thread.currentThread().getName()+" terminated");
	}
}
