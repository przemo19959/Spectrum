package application.commons;

import java.util.Arrays;
import java.util.concurrent.Semaphore;

import application.threads.SongState;

public class AudioCommon {
	private byte[] audioSamples;
	private SongState state=SongState.STOPPED;
	private Semaphore playingSem = new Semaphore(1);
	private Semaphore processSem = new Semaphore(0);
	
	public SongState getState() {
		return state;
	}

	public void setState(SongState state) {
		this.state = state;
	}

	public void withSamples(byte[] samples) {
		try {
			playingSem.acquire();	//w�tek graj�cy wchodzi
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		audioSamples = Arrays.copyOf(samples, samples.length);
		processSem.release();	//miejsce dla w�tku przetwarzaj�cego
	}

	public byte[] getSamples() {
		try {
			processSem.acquire();	//w�tek przetwarzaj�cy stoi
		} catch (InterruptedException e) {
//			e.printStackTrace();
		}
		playingSem.release();
		return audioSamples;
	}

}
