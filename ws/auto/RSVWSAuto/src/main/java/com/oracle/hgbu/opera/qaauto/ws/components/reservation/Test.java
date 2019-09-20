package com.oracle.hgbu.opera.qaauto.ws.components.reservation;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class Test {
	public static void main(String args[]) {
		Instant start = Instant.now();
		System.out.println(start);
		for(int i=0;i <800000;i++) {
			int j=0;
		}
		Instant end = Instant.now();
		System.out.println(end);
		Duration timeElapsed = Duration.between(start, end);

		long timeInMilliseconds = timeElapsed.toMillis();
		long timeInSeconds = timeElapsed.getSeconds();
		long timeInminutes = TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds);
		System.out.println("timeInminutes: "+timeInminutes);
		System.out.println("timeInSeconds: "+timeInSeconds);
		System.out.println("timeInMilliseconds: "+timeInMilliseconds);
	}
}
