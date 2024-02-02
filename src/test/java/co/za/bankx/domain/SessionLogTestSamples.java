package co.za.bankx.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class SessionLogTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static SessionLog getSessionLogSample1() {
        return new SessionLog().id(1L);
    }

    public static SessionLog getSessionLogSample2() {
        return new SessionLog().id(2L);
    }

    public static SessionLog getSessionLogRandomSampleGenerator() {
        return new SessionLog().id(longCount.incrementAndGet());
    }
}
