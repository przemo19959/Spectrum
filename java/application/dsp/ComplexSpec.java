package application.dsp;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ComplexSpec {
	private Complex num1;
	private Complex num2;
	
	@BeforeEach
	public void init() {
		num1=new Complex(23, -12);
		num2=new Complex(-37, 8);
	}

	@Test
	@DisplayName("add method works fine")
	void test1() {
		assertThat(num1.addTo(num2)).isEqualTo(new Complex(-14, -4));
	}
	
	@Test
	@DisplayName("substract method works fine")
	void test2() {
		assertThat(num1.minus(num2)).isEqualTo(new Complex(60, -20));
	}
	
	@Test
	@DisplayName("multiply method works fine")
	void test3() {
		assertThat(num1.multiply(num2)).isEqualTo(new Complex(-755, 628));
	}
	
	@Test
	@DisplayName("divide method works fine")
	void test4() {
		Complex tmp=num1.divideBy(num2);
		assertThat(tmp).isEqualTo(new Complex(-0.6608513608f, 0.1814375436f));
	}
}
