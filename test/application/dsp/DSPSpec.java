package application.dsp;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import application.dspService.Complex;
import application.dspService.DSP;

class DSPSpec {
	private DSP dsp=new DSP();
	private byte[] data1= {23,34,54,67,78,89,25,121,2,45,23,43,44,56,67,22};
	
	@BeforeEach
	void init() {
		dsp.withSamples(data1);
		dsp.turnTrueFFT(false);
	}
	
	@Test
	@DisplayName("array is null")
	void test1() {
		DSP dsp1=new DSP();
		assertThat(dsp1.isSamplesArrayNull()).isTrue();
	}
	
	@Test
	@DisplayName("array is not null after adding samples")
	void test2() {
		assertThat(dsp.isSamplesArrayNull()).isFalse();
	}

	@Test
	@DisplayName("realToComplex method works fine")
	void test3() {
		dsp.realToComplex();
		Complex[] tmp=new Complex[data1.length];
		IntStream.range(0, data1.length).forEach(i->tmp[i]=new Complex(data1[i], 0));
		assertThat(dsp.equalsComplexSamples(tmp)).isEqualTo(true);
	}
	
	@Test
	@DisplayName("fft works fine")
	void test4() {
		dsp.calculateFFT();
		Complex[] correct= {new Complex(793.000000f, 0.000000f),new Complex(-32.452104f, -112.559101f),new Complex(-120.334524f, 85.003571f),new Complex(-64.399057f, -17.710269f),
		                    new Complex(-22.000000f, 29.000000f),new Complex(3.161467f, -101.266618f),new Complex(-73.665476f, 55.003571f), new Complex(177.689694f, -60.115450f),
		                    new Complex(-161.000000f, 0.000000f), new Complex(177.689694f, 60.115450f), new Complex(-73.665476f, -55.003571f), new Complex(3.161467f, 101.266618f),
		                    new Complex(-22.000000f, -29.000000f), new Complex(-64.399057f, 17.710269f), new Complex(-120.334524f, -85.003571f), new Complex(-32.452104f, 112.559101f)};
		assertThat(dsp.equalsComplexSamples(correct)).isEqualTo(true);
	}
	
	@Test
	@DisplayName("Spectrum is correct")
	void test5() {
		dsp.calculateFFT();
		Complex[] correct= {new Complex(793.000000f, 0.000000f),new Complex(-32.452104f, -112.559101f),new Complex(-120.334524f, 85.003571f),new Complex(-64.399057f, -17.710269f),
		                    new Complex(-22.000000f, 29.000000f),new Complex(3.161467f, -101.266618f),new Complex(-73.665476f, 55.003571f), new Complex(177.689694f, -60.115450f),
		                    new Complex(-161.000000f, 0.000000f), new Complex(177.689694f, 60.115450f), new Complex(-73.665476f, -55.003571f), new Complex(3.161467f, 101.266618f),
		                    new Complex(-22.000000f, -29.000000f), new Complex(-64.399057f, 17.710269f), new Complex(-120.334524f, -85.003571f), new Complex(-32.452104f, 112.559101f)};
		int[] correctSpectrum=new int[correct.length];
		for(int i=0;i<correct.length;i++)
			correctSpectrum[i]=correct[i].abs();
		assertThat(dsp.spectrumEquals(correctSpectrum)).isTrue();
	}
	
	@Test
	@DisplayName("True FFT gives right results")
	void test6() {
		dsp.turnTrueFFT(true);
		dsp.calculateFFT();
		Complex[] correct= {new Complex(793.000000f, 0.000000f),new Complex(-32.452104f, -112.559101f),new Complex(-120.334524f, 85.003571f),new Complex(-64.399057f, -17.710269f),
		                    new Complex(-22.000000f, 29.000000f),new Complex(3.161467f, -101.266618f),new Complex(-73.665476f, 55.003571f), new Complex(177.689694f, -60.115450f),
		                    new Complex(-161.000000f, 0.000000f)};
		int[] correctSpectrum=new int[correct.length];
		for(int i=0;i<correct.length;i++)
			correctSpectrum[i]=correct[i].abs();
		assertThat(dsp.spectrumEquals(correctSpectrum)).isTrue();
		
	}
}
