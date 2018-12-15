package application.commons;

import java.util.Arrays;
import java.util.concurrent.Semaphore;

import application.threads.SongState;

public class AudioCommon {
	private byte[] audioSamples;
	private String audioFilePath = "G:/Pobieranie/Pobieranie/Muzyka/nowe9/David Guetta & Showtek - Your Love (Lyric video).mp3";
	private SongState state=SongState.STOPPED;
	private Semaphore playingSem = new Semaphore(1);
	private Semaphore processSem = new Semaphore(0);
	
	public String getAudioFilePath() {
		return audioFilePath;
	}

	public void setAudioFilePath(String audioFilePath) {
		this.audioFilePath = audioFilePath;
	}

	public SongState getState() {
		return state;
	}

	public void setState(SongState state) {
		this.state = state;
	}

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
//			e.printStackTrace();
		}
		playingSem.release();
		return audioSamples;
	}

}
