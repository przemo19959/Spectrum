package application.threads;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import application.commons.AudioCommon;

public class PlayingThread implements Runnable {
	private Thread t;
	private AtomicBoolean isRunning = new AtomicBoolean(true);

	private AudioCommon audioCommon;
	private SourceDataLine audioLine;

	private String audioFilePath = "D:/Audio.wav";
	// private String audioFilePath = "G:/Pobieranie/Pobieranie/Muzyka/20Hz - to.wav";
	private static final int BUFFER_SIZE = 8192; // do FFT, liczba musi byæ N=2^k, k-liczba naturalna -> k=log2(N) dla 88200 mamy k=16,428...
	//Test wp³ywu iloœci próbek na pracê aplikacji
	//32768 - Ok, ale tutaj z kolei, paski jakby nie by³y zsynchronizowane z dzwiêkiem
	//16384 - Ok, czyli brak zacinania
	//8192 - Ok
	//4096 - Ok,ale drawer chodzi zbyt szybko i paski nawet nie nad¹¿aj¹ siê narysowaæ, a ju¿ pojawiaj¹ siê nowe wartoœci
	//2048 - Nie OK, to jest granica, czas zbierania próbek, jest tak krótki, ¿e pozosta³e w¹tki dzia³aj¹ d³u¿ej ni¿ samo granie próbek, w efekcie
	//wystêpuj¹ przerwy w dzwiêku, dodanie funkcji trueFFT te¿ nic nie da³o
	private byte[] bytesBuffer = new byte[BUFFER_SIZE];

	public PlayingThread(AudioCommon audioCommon) {
		this.audioCommon = audioCommon;
		t = new Thread(this, "Player");
		t.start();
	}

	public void stop() {
		synchronized (audioLine) {
			audioLine.stop();
		}
		isRunning.set(false);
	}
	
	private void play(String audioFilePath) {
		File audioFile = new File(audioFilePath);
		try {
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
			AudioFormat format = audioStream.getFormat();			
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
			audioLine = (SourceDataLine) AudioSystem.getLine(info);
			synchronized (audioLine) {
				audioLine.open(format, BUFFER_SIZE); // domyœlny rozmiar bufora wynosi 88200 bajtów, koniecznie nale¿y
				// przy otwieraniu linii definiowaæ rozmiar bufora, wtedy nie ma pominiêæ przy funkcji audio.write() linia 41

				audioLine.start();
			}
			System.out.println("Playback started.");

			int bytesRead = -1;

			while (isRunning.get()&& (bytesRead = audioStream.read(bytesBuffer))!= -1) {
				audioCommon.withSamples(bytesBuffer);
				synchronized (audioLine) {
					audioLine.write(bytesBuffer, 0, bytesRead);
				}
			}
			synchronized (audioLine) {
				audioLine.drain();
				audioLine.close();
			}
			audioStream.close();

			System.out.println("Playback completed.");

		} catch (UnsupportedAudioFileException ex) {
			System.out.println("The specified audio file is not supported.");
			ex.printStackTrace();
		} catch (LineUnavailableException ex) {
			System.out.println("Audio line for playing back is unavailable.");
			ex.printStackTrace();
		} catch (IOException ex) {
			System.out.println("Error playing the audio file.");
			ex.printStackTrace();
		}
	}

	@Override
	public void run() {
		play(audioFilePath);
		System.out.println(Thread.currentThread().getName()+ " terminated");
	}
}
