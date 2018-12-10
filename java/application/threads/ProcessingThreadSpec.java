package application.threads;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import application.commons.AudioCommon;

class ProcessingThreadSpec {
	private ProcessingThread mockThread;
	private AudioCommon audioCommon;
	
	@BeforeEach
	void init() {
		audioCommon=new AudioCommon();
		mockThread=new ProcessingThread(audioCommon);
	}

	@Test
	@DisplayName("does stop method works fine")
	void test1() {
		assertThat(mockThread.getState()).isEqualTo(Thread.State.RUNNABLE);
		//test znajduje siê w afterEach, iloœæ wpisów thread terminated musi byæ równa iloœci testów
	}
	
	@Test
	@DisplayName("does stop method works fine")
	void test2() throws InterruptedException {
		assertThat(mockThread.getState()).isEqualTo(Thread.State.RUNNABLE);
		mockThread.suspend();
		Thread.sleep(500);
		assertThat(mockThread.getState()).isEqualTo(Thread.State.WAITING);
		
	}
	
	@AfterEach
	void cleanUp() {
		mockThread.stop();
	}

}
