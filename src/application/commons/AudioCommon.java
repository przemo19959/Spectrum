package application.commons;

import java.util.Arrays;
import java.util.concurrent.Semaphore;

public class AudioCommon {
	private byte[] audioSamples;
	private Semaphore playingSem = new Semaphore(1);
	private Semaphore processSem = new Semaphore(0);

	public void withSamples(byte[] samples) {
		try {
			playingSem.acquire();	//w¹tek graj¹cy wchodzi
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		audioSamples = Arrays.copyOf(samples, samples.length);
		processSem.release();	//miejsce dla w¹tku przetwarzaj¹cego
	}

	public byte[] getSamples() {
		try {
			processSem.acquire();	//w¹tek przetwarzaj¹cy stoi
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		playingSem.release();
		return audioSamples;
	}

}
