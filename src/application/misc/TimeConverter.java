package application.misc;

import javafx.util.StringConverter;

public class TimeConverter extends StringConverter<Long> {
	private int[] times = new int[3];
	
	@Override
	public String toString(Long object) {
		double timeInSeconds = object* 1e-6;
		times[0] = (int) (timeInSeconds/ 3600); // ilo�� godzin
		times[1] = (int) ((timeInSeconds- times[0]* 3600)/ 60); // ilo�� minut
		times[2] = (int) (timeInSeconds- times[0]* 3600- times[1]* 60); // ilo�� sekund
		return String.format("%02d:%02d:%02d", times[0], times[1], times[2]);
	}

	@Override
	public Long fromString(String string) {
		return null;
	}

}
