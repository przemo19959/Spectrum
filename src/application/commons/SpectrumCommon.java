package application.commons;

import java.util.concurrent.Semaphore;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SpectrumCommon {
	private int[] spectrum;
	private int displayedBars;
	private int barWidth;
	private int canvasHeight;
	private int canvasWidth;
	private GraphicsContext gc;
	private Semaphore drawSem = new Semaphore(0);
	private Semaphore processSem = new Semaphore(1);
	
	private boolean upToBottom=false;
	private float attenuate=500;

	public void setParams(int displayedBars, GraphicsContext gc) {
		this.displayedBars = displayedBars;
		this.gc = gc;
		this.barWidth = (int) (gc.getCanvas().getWidth()/ displayedBars);
		this.canvasHeight = (int) gc.getCanvas().getHeight();
		this.canvasWidth = (int) gc.getCanvas().getWidth();
	}

	public int getDisplayedBars() {
		return displayedBars;
	}

	public void updateSpectrum(int[] spectrum) {
		try {
			processSem.acquire();
		} catch (InterruptedException e) {
//			e.printStackTrace();
		}
		this.spectrum = spectrum;
		drawSem.release();
	}

	public void drawSpectrum() {
		try {
			drawSem.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(gc!= null&& spectrum!= null)
			draw();
		processSem.release();
	}

	private void draw() {
		int level = 0;
		// t³o na czarno, aby zacz¹æ rysowaæ s³upki od nowa, inaczej s³upki bêd¹ nachodziæ na siebie z ka¿dego wywo³ania funkcji draw()
		clearSpectrum();
		if(upToBottom)
			drawUpToBottom(level);
		else
			drawBottomToUp(level);
	}

	// rysowanie pasków z góry na dó³
	private void drawUpToBottom(int level) {
		for(int i = 0;i< displayedBars;i++) {
			level = (int) (spectrum[i]/ attenuate);
			gc.setFill(Color.BLACK);
			gc.setLineWidth(3);
			gc.strokeRect(i* barWidth, 0, barWidth, level);
			gc.setFill(Color.ORANGE);
			gc.fillRect(i* barWidth, 0, barWidth, level);
		}
	}

	// rysowanie pasków z do³u do góry
	private void drawBottomToUp(int level) {
		for(int i = 0;i< displayedBars;i++) {
			level = (int)(spectrum[i]/ attenuate);
			gc.setFill(Color.BLACK);
			gc.setLineWidth(3);
			gc.strokeRect(i* barWidth, canvasHeight- level, barWidth, level);
			gc.setFill(Color.ORANGE);
			gc.fillRect(i* barWidth, canvasHeight- level, barWidth, level);
		}
	}

	public void clearSpectrum() {
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, canvasWidth, canvasHeight);
	}
}
