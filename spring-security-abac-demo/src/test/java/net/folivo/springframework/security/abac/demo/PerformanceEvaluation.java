package net.folivo.springframework.security.abac.demo;

import java.time.Duration;

import org.junit.runner.JUnitCore;

public class PerformanceEvaluation {

	public static void main(String[] args) {

		JUnitCore jUnitCore = new JUnitCore();

		Duration abacDuration = Duration.ZERO;
		Duration stdDuration = Duration.ZERO;

		final int repetition = 100;

		for (int i = 0; i < repetition; i++) {

			stdDuration = stdDuration.plusMillis(jUnitCore.run(StdSecurityTest.class).getRunTime());

			abacDuration = abacDuration.plusMillis(jUnitCore.run(AbacSecurityTest.class).getRunTime());
		}

		System.out.println("\r\nThe tests run " + repetition + " times. The average time is:\r\n");
		System.out.println("\r\nStdSecurityTest.class Time (in ms): " + stdDuration.dividedBy(repetition).toMillis());
		System.out.println("\r\nAbacSecurityTest.class Time (in ms): " + abacDuration.dividedBy(repetition).toMillis());
	}

}
