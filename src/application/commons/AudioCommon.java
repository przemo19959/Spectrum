package application.commons;

import java.util.Arrays;
import java.util.concurrent.Semaphore;

import application.threads.SongState;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

public class AudioCommon {
	private byte[] audioSamples;
	@Setter
	@Getter
	private String audioFilePath = "G:/Pobieranie/Pobieranie/Muzyka/nowe9/David Guetta & Showtek - Your Love (Lyric video).mp3";
	@Setter
	@Getter
	private SongState state=SongState.STOPPED;
	private Semaphore playingSem = new Semaphore(1);
	private Semaphore processSem = new Semaphore(0);
	
	@SneakyThrows
	public void withSamples(byte[] samples) {
		playingSem.acquire();	//playing thread enters
		audioSamples = Arrays.copyOf(samples, samples.length); //copy from playing thread
		processSem.release();	//processing thread is allowed to take action
	}

	public byte[] getSamples() {
		try {
			processSem.acquire();	//processing thread enters
		} catch (InterruptedException e) {
//			e.printStackTrace();
		}
		playingSem.release();//playing thread is allowed to take action
		return audioSamples; //samples for processing thread
	}

}
