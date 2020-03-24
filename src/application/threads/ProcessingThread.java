package application.threads;

import java.util.concurrent.atomic.AtomicBoolean;

import application.commons.AudioCommon;
import application.commons.SpectrumCommon;
import application.dspService.DSP;
import lombok.SneakyThrows;

public class ProcessingThread implements Runnable {
	private Thread t;
	private AtomicBoolean suspend = new AtomicBoolean(false);
	private AtomicBoolean terminate = new AtomicBoolean(false);

	private DSP dsp;
	private AudioCommon audioCommon;
	private SpectrumCommon spectrumCommon;

	public ProcessingThread(AudioCommon audioCommon, SpectrumCommon spectrumCommon) {
		this.dsp = new DSP();
		this.audioCommon = audioCommon;
		this.spectrumCommon = spectrumCommon;
		t = new Thread(this, "Processor");
		t.start();
	}
	
	//@formatter:off
	public void stop() {terminate.set(true);resume();}
	public void suspend() {suspend.set(true);}
	public synchronized void resume() {
		if(suspend.get()) {
			suspend.set(false);
			notify();
		}
	}
	//@formatter:on

	private void process() {
		dsp.withSamples(audioCommon.getSamples());
		if(dsp.isSamplesArrayNull()==false) {
			dsp.calculateFFT();
			spectrumCommon.updateSpectrum(dsp.getSpectrum(spectrumCommon.getDisplayedBars()));
		}
	}

	@Override
	@SneakyThrows
	public void run() {
		System.out.println(t.getName() + " started");
		while (true) {
			synchronized (this) {
				while (suspend.get())
					wait();
				
			}
//			System.out.println("a");
			if(terminate.get()) {
//				System.out.println("wyszło");
				break;
			}
			process();
		}
		System.out.println(t.getName() + " terminated");
	}
}
