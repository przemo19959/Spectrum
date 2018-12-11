package application.dsp;

import java.util.Arrays;
import java.util.stream.Collectors;

//dodanie uœredniania nic nie da³o
public class DSP {
	private byte[] samples;
	private int N;
	private Complex[] complexSamples;
	private int[] spectrum;
	private boolean isTrueFFTOn=true;

	public DSP withSamples(byte[] samples) {
		this.samples = samples;
		this.N = samples.length;
		complexSamples = new Complex[N];
		spectrum=new int[N];
		return this;
	}
	
	//TODO Ogólnie dodaæ jeszcze funkcjê, która dodatkowo filtruje próbki przed podaniem na FFT(mo¿e zastosowanie okna)
	//dodanie rysowania uœrednionego - próba dodania wyg³adzenia
	
	public DSP turnTrueFFT(boolean isOn) {
		this.isTrueFFTOn=isOn;
		return this;
	}

	public boolean isSamplesArrayNull() {
		return (samples== null) ? true : false;
	}

	// na potrzeby testów
	public boolean equalsComplexSamples(Complex[] samples) {
		if(samples.length!= this.samples.length)
			return false;
		for(int i = 0;i< N;i++) {
			if(!complexSamples[i].equals(samples[i]))
				return false;
		}
		return true;
	}
	
	//Krok 1 - filtrowanie próbek
	private void filter() {
		
	}

	// krok 2 FFT
	private void rearrangeSamples() {
		int a = 1;
		for(int b = 1;b< N;b++) {
			if(b< a) {
				byte tmp = samples[a- 1];
				samples[a- 1] = samples[b- 1];
				samples[b- 1] = tmp;
			}
			int c = (int) (N/ 2.0);
			while (c< a) {
				a -= c;
				c = (int) (c/ 2.0);
			}
			a += c;
		}
	}

	// krok 3 FFT
	public void realToComplex() {
		for(int i = 0;i< N;i++) {
			complexSamples[i] = new Complex(samples[i], 0);
		}
	}

	// krok 4 FFT - w³aœciwe FFT
	private void fft() {
		for(int e = 1;e<= (int) (Math.log10(N)/ Math.log10(2));e++) {
			int L = (int) Math.pow(2, e);
			int M = (int) Math.pow(2, e- 1);
			Complex Wi = new Complex(1, 0);
			Complex W = new Complex((float) Math.cos((2* Math.PI)/ L), (float) (-Math.sin((2* Math.PI)/ L)));
			for(int m = 1;m<= M;m++) {
				for(int g = m;g<= N;g = g+ L) {
					int d = g+ M;
					Complex tmp = complexSamples[d- 1].multiply(Wi);
					complexSamples[d- 1] = complexSamples[g- 1].minus(tmp);
					complexSamples[g- 1] = complexSamples[g- 1].addTo(tmp);
				}
				Wi = Wi.multiply(W);
			}
		}
	}

	//sygna³ rzeczywisty, ma widmo symetryczne wzglêdem fp/2, wiêc nie ma potrzeby liczenia drugiej po³owy
	//st¹d ta wersja FFT liczy jedynie po³owê widma, bowiem tylko to nas interesuje.
	private void trueFFT() {
		for(int e = 1;e<= (int) (Math.log10(N)/ Math.log10(2));e++) {
			int L = (int) Math.pow(2, e);
			int M = (int) Math.pow(2, e- 1);
			Complex Wi = new Complex(1, 0);
			Complex W = new Complex((float) Math.cos((2* Math.PI)/ L), (float) (-Math.sin((2* Math.PI)/ L)));
			for(int m = 1;m<= M;m++) {
				for(int g = m;g<= N;g = g+ L) {
					int d = g+ M;
					Complex tmp = complexSamples[d- 1].multiply(Wi);
					if(e==(int) (Math.log10(N)/ Math.log10(2)) && m>1)
						complexSamples[g- 1] = complexSamples[g- 1].addTo(tmp);
					else {
						complexSamples[d- 1] = complexSamples[g- 1].minus(tmp);
						complexSamples[g- 1] = complexSamples[g- 1].addTo(tmp);
					}
				}
				Wi = Wi.multiply(W);
			}
		}
		complexSamples=Arrays.copyOf(complexSamples, (int)(complexSamples.length/2.0)+1);
	}
	
	//Krok 5- obliczenie widma
	private void calculateSpectrum() {
		for(int i = 0;i< complexSamples.length;i++) {
			spectrum[i]=complexSamples[i].abs();
		}
	}

	// na potrzeby testów
	public boolean spectrumEquals(int[] spectrum) {
		if(spectrum.length!= this.complexSamples.length)
			return false;
		for(int i = 0;i< complexSamples.length;i++) {
			if(this.spectrum[i]!= spectrum[i])
				return false;
		}
		return true;
	}

	public int[] getSpectrum(int n) {
		if(spectrum!= null)
			return Arrays.copyOf(spectrum, n);
		return null;
	}

	public void calculateFFT() {
		filter();
		rearrangeSamples();
		realToComplex();
		if(isTrueFFTOn)
			trueFFT();
		else
			fft();
		calculateSpectrum();
	}

	@Override
	public String toString() {
		return Arrays.stream(complexSamples).map(item -> item.toString()).collect(Collectors.joining(", "));
	}
}
