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
	
	@Test
	@DisplayName("created number is not null")
	void test5() {
		assertThat(num1).isNotNull();
	}
	
	@Test
	@DisplayName("created number has right values - toString test")
	void test6() {
		num1=new Complex(23.2f, -2);
		assertThat(num1.toString()).isEqualTo("23.2-j2.0");
	}
	
	@Test
	@DisplayName("equals returns false, for different numbers")
	void test7() {
		assertThat(num1.equals(num2)).isFalse();
	}
	
	@Test
	@DisplayName("equals returns tru, for same values")
	void test8() {
		Complex num=new Complex(23.009f, -12.009f);
		assertThat(num1.equals(num)).isTrue();
	}
	
	@Test
	@DisplayName("abs return right value")
	void test9() {
		//funkcja licz¹ca modu³, zwraca czêœæ ca³kowit¹, dla uproszczenia
		assertThat(num1.abs()).isEqualTo((int)25.9422f);
	}

}
