package application.commons;

import java.util.concurrent.Semaphore;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.SneakyThrows;

public class SpectrumCommon {
	private static final boolean DRAW_FROM_UP_TO_BOTTOM = false;

	private int[] spectrum;
	private int[] oldSpectrum;

	@Getter
	private int displayedBars;
	private int barWidth;
	private int canvasHeight;
	private int canvasWidth;
	private GraphicsContext gc;

	private Semaphore drawSem = new Semaphore(0);
	private Semaphore processSem = new Semaphore(1);

	private float attenuate = 250;
	private int decayValue = 5;

	public void setParams(int displayedBars, GraphicsContext gc) {
		this.displayedBars = displayedBars;
		this.gc = gc;
		this.barWidth = (int) (gc.getCanvas().getWidth() / displayedBars);
		this.canvasHeight = (int) gc.getCanvas().getHeight();
		this.canvasWidth = (int) gc.getCanvas().getWidth();
		oldSpectrum = new int[displayedBars];
	}

	public void updateSpectrum(int[] spectrum) {
		try {
			processSem.acquire();
		} catch (InterruptedException e) {
		}
		this.spectrum = spectrum;
		drawSem.release();
	}

	@SneakyThrows
	public void drawSpectrum() {
		drawSem.acquire();
		if(gc != null && spectrum != null)
			draw();
		processSem.release();
	}

	private void draw() {
		int level = 0;
		clearSpectrum();//otherwise, bars will ovelap from previous drawing
		drawBars(level, DRAW_FROM_UP_TO_BOTTOM);
	}

	private void drawBars(int level, boolean upToBottom) {
		for(int i = 0;i < displayedBars;i++) {
			level = (int) (spectrum[i] / attenuate);
			if(level < oldSpectrum[i])
				level = oldSpectrum[i] - decayValue;
			gc.setFill(Color.BLACK);
			gc.setLineWidth(3);
			gc.strokeRect(i * barWidth, upToBottom ? 0 : (canvasHeight - level), barWidth, level);
			gc.setFill(Color.ORANGE);
			gc.fillRect(i * barWidth, upToBottom ? 0 : (canvasHeight - level), barWidth, level);
			oldSpectrum[i] = level;
		}
	}

	public void clearSpectrum() {
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, canvasWidth, canvasHeight);
	}
}
