package intervals;

import static org.junit.Assert.assertEquals;

import java.time.LocalTime;

import org.junit.Test;

import hierarchies.Interval;

public class IntervalTesting {
	
	@Test
	public void test() {
		Interval int1 = new Interval();
		assertEquals("L'orario doveva essere corretto",
				int1.setStartAt(LocalTime.of(18, 0, 0)), true);
		assertEquals("L'orario doveva essere corretto",
				int1.setEndAt(LocalTime.of(20, 30, 0)), true);
		Interval int2 = new Interval();
		assertEquals("L'orario doveva essere corretto",
				int2.setStartAt(LocalTime.of(18, 30, 0)), true);
		assertEquals("L'orario NON doveva essere corretto (la fine viene prima dell'inzio",
				int2.setEndAt(LocalTime.of(18, 0, 0)), false);
	}
	
}
