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
import javafx.util.StringConverter;

public class PlayingThread implements Runnable {
	private Thread t;
	private AtomicBoolean isRunning = new AtomicBoolean(true);

	private AudioCommon audioCommon;
	private SourceDataLine audioLine;

	private String audioFilePath = "D:/Audio.mp3";
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
	private String audioInfo;
	private File audioFile;
	
	private StringConverter<Long> timeConverter=new StringConverter<Long>() {
		private int[] times=new int[3];
		@Override
		public String toString(Long object) {
			double timeInSeconds=object*1e-6;
			times[0]=(int)(timeInSeconds/3600); //iloœæ godzin
			times[1]=(int)((timeInSeconds-times[0]*3600)/60); //iloœæ minut
			times[2]=(int)(timeInSeconds-times[0]*3600-times[1]*60); //iloœæ sekund
			return String.format("%02d:%02d:%02d", times[0],times[1],times[2]);
		}
		
		@Override
		public Long fromString(String string) {
			return null;
		}
	};

	public PlayingThread(AudioCommon audioCommon) {
		this.audioCommon = audioCommon;
		t = new Thread(this, "Player");
		audioFile= new File(audioFilePath);
		try {
			extractFileInfo(audioFile);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		synchronized (audioLine) {
			audioLine.stop();
		}
		isRunning.set(false);
	}
	
	public void start() {
		t.start();
	}
	
	private void extractFileInfo(File file) throws UnsupportedAudioFileException, IOException {
		AudioFileFormat baseFileFormat=AudioSystem.getAudioFileFormat(file);
		if(baseFileFormat instanceof TAudioFileFormat) {
			List<String> items=new ArrayList<>();
			Map<String, Object> properties=((TAudioFileFormat)baseFileFormat).properties();
			items.add(properties.get("author")+" - "+properties.get("title"));
			items.add(timeConverter.toString((long)properties.get("duration")));
			items.add(String.format("%.1f kHz",((int)properties.get("mp3.frequency.hz")/1000.0)));
			audioInfo=items.stream().collect(Collectors.joining(" :: "));
		}
	}
	
	public String getSongInfo() {
		return audioInfo;
	}

	private void play() {
		try {
			AudioInputStream in = AudioSystem.getAudioInputStream(audioFile);
			AudioInputStream din=null;
			AudioFormat baseFormat = in.getFormat();
			AudioFormat decodedFormat=new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
			                                          baseFormat.getSampleRate(), 16,
			                                          baseFormat.getChannels(),
			                                          baseFormat.getChannels()*2,
			                                          baseFormat.getSampleRate(),
			                                          false);
			din=AudioSystem.getAudioInputStream(decodedFormat, in);
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, decodedFormat);
			audioLine = (SourceDataLine) AudioSystem.getLine(info);
			synchronized (audioLine) {
				audioLine.open(decodedFormat, BUFFER_SIZE); // domyœlny rozmiar bufora wynosi 88200 bajtów, koniecznie nale¿y
				// przy otwieraniu linii definiowaæ rozmiar bufora, wtedy nie ma pominiêæ przy funkcji audio.write() linia 41
				audioLine.start();
			}
			System.out.println("Playback started.");

			int bytesRead = -1;

			while (isRunning.get() && (bytesRead = din.read(bytesBuffer,0,bytesBuffer.length))!= -1) {
				audioCommon.withSamples(bytesBuffer);
				synchronized (audioLine) {
					audioLine.write(bytesBuffer, 0, bytesRead);
				}
			}
			synchronized (audioLine) {
				audioLine.drain();
				audioLine.stop();
				audioLine.close();
			}
			din.close();

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
		play();
		System.out.println(Thread.currentThread().getName()+ " terminated");
	}
}
