package application.dsp;

import java.util.stream.IntStream;
/*
 * Test ile zajmuje obliczenie FFT danej iloœci danych, rozmiar w zmiennej size
 */

public class test {
	public static void main(String[] args) {
		int size=65536;	//<=rozmiar
		
		byte[] data=new byte[size];
		IntStream.range(0, size)
				.forEach(i->data[i]=(byte)(Math.random()*200));
		DSP dsp=new DSP().withSamples(data);
		
		double start=System.currentTimeMillis();
		dsp.calculateFFT();
		System.out.println(System.currentTimeMillis()-start);
	}
}
