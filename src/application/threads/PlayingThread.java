package application.threads;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.tritonus.share.sampled.file.TAudioFileFormat;

import application.commons.AudioCommon;
import application.misc.TimeConverter;
import lombok.Getter;
import lombok.SneakyThrows;

public class PlayingThread implements Runnable {
	private Thread t;
	private AtomicBoolean terminate = new AtomicBoolean(false);

	private AudioCommon audioCommon;
	private SourceDataLine audioLine;

	// private String audioFilePath = "D:/Audio.mp3";
	// private String audioFilePath = "G:/Pobieranie/Pobieranie/Muzyka/20Hz - to.wav";
	//	private String audioFilePath = "G:/Pobieranie/Pobieranie/Muzyka/nowe9/David Guetta & Showtek - Your Love (Lyric video).mp3";
	public static final int BUFFER_SIZE = 8192; // do FFT, liczba musi by� N=2^k, k-liczba naturalna -> k=log2(N) dla 88200 mamy k=16,428...
	// Test wp�ywu ilo�ci pr�bek na prac� aplikacji
	// 32768 - Ok, ale tutaj z kolei, paski jakby nie by�y zsynchronizowane z dzwi�kiem
	// 16384 - Ok, czyli brak zacinania
	// 8192 - Ok ->22 lub 33ms w�tek Player
	// 4096 - Ok,ale drawer chodzi zbyt szybko i paski nawet nie nad��aj� si� narysowa�, a ju� pojawiaj� si� nowe warto�ci
	// 2048 - Nie OK, to jest granica, czas zbierania pr�bek, jest tak kr�tki, �e pozosta�e w�tki dzia�aj� d�u�ej ni� samo granie pr�bek, w efekcie
	// wyst�puj� przerwy w dzwi�ku, dodanie funkcji trueFFT te� nic nie da�o
	private byte[] bytesBuffer = new byte[BUFFER_SIZE];
	@Getter
	private String audioInfo;
	private File audioFile;
	private AudioInputStream din;
	private AudioFormat decodedFormat;

	// na potrzeby test�w
	private long startTime;

	public PlayingThread(AudioCommon audioCommon) {
		this.audioCommon = audioCommon;
		t = new Thread(this, "Player");
		loadFile(audioCommon.getAudioFilePath());
	}

	@SneakyThrows
	private void loadFile(String path) {
		audioFile = new File(path);
		audioInfo = extractFileInfo(audioFile);
	}

	//@formatter:off
	public boolean isAlive() {return t.isAlive();}
	public void start() {t.start();}
	//@formatter:on

	private String extractFileInfo(File file) throws UnsupportedAudioFileException, IOException {
		AudioFileFormat baseFileFormat = AudioSystem.getAudioFileFormat(file);
		if(baseFileFormat instanceof TAudioFileFormat) {
			List<String> items = new ArrayList<>();
			Map<String, Object> properties = ((TAudioFileFormat) baseFileFormat).properties();
			items.add(properties.get("author") + " - " + properties.get("title"));
			items.add(new TimeConverter().toString((long) properties.get("duration")));
			items.add(String.format("%.1f kHz", ((int) properties.get("mp3.frequency.hz") / 1000.0)));
			return items.stream().collect(Collectors.joining(" :: "));
		}
		return "no info";
	}

	private void getAudioLine() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		AudioInputStream in = AudioSystem.getAudioInputStream(audioFile);
		AudioFormat baseFormat = in.getFormat();
		decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(), 16, baseFormat.getChannels(), //
										baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);
		din = AudioSystem.getAudioInputStream(decodedFormat, in);
		audioLine = (SourceDataLine) AudioSystem.getLine(new DataLine.Info(SourceDataLine.class, decodedFormat));
	}

	private void openAudioLine(SourceDataLine audioLine, AudioFormat audioFormat) throws LineUnavailableException {
		synchronized (audioLine) {
			audioLine.open(audioFormat, BUFFER_SIZE);
			audioLine.start();
		}
	}

	private void closeAudioLine(SourceDataLine audioLine) throws IOException {
		synchronized (audioLine) {
			audioLine.drain();
			audioLine.stop();
			audioLine.close();
		}
		din.close();
	}

	private void playSong() throws IOException {
		int bytesRead = -1;
		while (terminate.get() == false) {
			bytesRead = din.read(bytesBuffer, 0, BUFFER_SIZE); //read from stream
			if(bytesRead == -1)
				break;
			audioCommon.withSamples(bytesBuffer); //send data to audioCommons
			if(terminate.get() == false)
				audioLine.write(bytesBuffer, 0, bytesRead); //play samples
		}
		audioLine.stop();
	}

	public void stop() {
		terminate.set(true);
		t.interrupt();
	}

	@SneakyThrows
	private void play() {
		getAudioLine();
		openAudioLine(audioLine, decodedFormat);
		playSong();
		closeAudioLine(audioLine);
	}

	@Override
	public void run() {
		System.out.println(t.getName() + " started");
		play();
		System.out.println(t.getName() + " terminated");
	}
}
