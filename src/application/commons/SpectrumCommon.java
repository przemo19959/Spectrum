package application.commons;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SpectrumCommon {
	private int[] spectrum;
	private int displayedBars;
	private int barWidth;
	private int canvasHeight;
	private int canvasWidth;
	private GraphicsContext gc;
//	private Semaphore drawSem = new Semaphore(0);
//	private Semaphore processSem = new Semaphore(1);

	public void setParams(int displayedBars, GraphicsContext gc) {
		this.displayedBars = displayedBars;
		this.gc = gc;
		this.barWidth = (int) (gc.getCanvas().getWidth()/ displayedBars);
		this.canvasHeight = (int) gc.getCanvas().getHeight();
		this.canvasWidth=(int)gc.getCanvas().getWidth();
	}

	public int getDisplayedBars() {
		return displayedBars;
	}

	public void updateSpectrum(int[] spectrum) {
//		try {
//			processSem.acquire();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		synchronized (this) {
			this.spectrum = spectrum;
		}
//		this.spectrum = spectrum;
//		drawSem.release();
	}

	public void drawSpectrum() {
//		try {
//			drawSem.acquire();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		if(gc!= null && spectrum!=null)
			draw(gc);
//		processSem.release();
	}

	private void draw(GraphicsContext gc) {
		int level = 0;
		
		//t³o na czarno, aby zacz¹æ rysowaæ s³upki od nowa, inaczej s³upki bêd¹ nachodziæ na siebie z ka¿dego wywo³ania funkcji draw()
		clearSpectrum();
		
		synchronized (this) {
			for(int i = 0;i< displayedBars;i++) {
				level = (int) (spectrum[i]/ 500.0);
				gc.setStroke(Color.BLACK);
				gc.setLineWidth(3);
				gc.strokeRoundRect(i* barWidth, -12, barWidth, level, 12, 12);
				gc.setFill(Color.CYAN);
				gc.fillRoundRect(i*barWidth, -12, barWidth, level, 12, 12);
			}
		}
	}

	public void clearSpectrum() {
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, canvasWidth, canvasHeight);
	}
}
