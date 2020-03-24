package application.commons;

import java.util.concurrent.Semaphore;

import application.threads.PlayingThread;
import application.threads.SongState;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

public class AudioCommon {
	private byte[] audioSamples=new byte[PlayingThread.BUFFER_SIZE];
	
	@Setter
	@Getter
	private String audioFilePath = "G:/Pobieranie/Pobieranie/Muzyka/nowe9/David Guetta & Showtek - Your Love (Lyric video).mp3";
	@Setter
	@Getter
	private SongState state=SongState.STOPPED;
	
	private Semaphore playingSem = new Semaphore(1);
	private Semaphore processSem = new Semaphore(0);
		
	public void withSamples(byte[] samples) {
		try {
			playingSem.acquire();
		} catch (InterruptedException e) {
			//so that, thread can terminate
		}
		System.arraycopy(samples, 0, audioSamples, 0, samples.length);
		processSem.release();
	}

	public byte[] getSamples() {
		try {
			processSem.acquire();
		} catch (InterruptedException e) {
			//so that, thread can terminate
		}
		playingSem.release();
		return audioSamples; 
	}

}
