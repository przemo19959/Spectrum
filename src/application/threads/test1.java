package application.threads;

import application.commons.AudioCommon;
import application.commons.SpectrumCommon;
import application.dsp.DSP;

public class test1 {
	public static void main(String[] args) throws InterruptedException {
		AudioCommon audioCommon=new AudioCommon();
		SpectrumCommon spectrumCommon=new SpectrumCommon();
		DSP dsp=new DSP();
		ProcessingThread t1=new ProcessingThread(audioCommon,dsp,spectrumCommon);
		System.out.println(t1.getState());
		t1.stop();
		//Thread.sleep(500);
		
		t1=new ProcessingThread(audioCommon,dsp, spectrumCommon);
		t1.suspend();
//		Thread.sleep(500);
		System.out.println(t1.getState());
		
		t1.resume();
		//Thread.sleep(500);
		System.out.println(t1.getState());
		
		t1.stop();
	}
}
