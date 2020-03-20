package application.dsp;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Complex {
	private final float re;
	private final float im;

	//@formatter:off
	public Complex addTo(Complex num) {return new Complex(re+num.re, im+num.im);}
	public Complex minus(Complex num) {return new Complex(re-num.re, im-num.im);}
	public Complex multiply(Complex num) {return new Complex(re*num.re-im*num.im, re*num.im+im*num.re);}
	public int abs() {return (int)Math.sqrt(Math.pow(re, 2)+Math.pow(im, 2));}
	public static boolean floatEqualWithError(float a, float b, float error) {return Math.abs(a - b) < error;}
	//@formatter:on

	public Complex divideBy(Complex num) {
		float denominator = (float) (Math.pow(num.re, 2) + Math.pow(num.im, 2));
		return new Complex((re * num.re + im * num.im) / denominator, (-re * num.im + im * num.re) / denominator);
	}

	@Override
	public String toString() {
		return re + ((im < 0) ? "-j" : "+j") + Math.abs(im);
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(!(obj instanceof Complex))
			return false;
		Complex that = (Complex) obj;
		if(floatEqualWithError(re, that.re, 0.01f)==false || floatEqualWithError(im, that.im, 0.01f)==false)
			return false;
		return true;
	}
}
