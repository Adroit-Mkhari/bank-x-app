package co.za.bankx.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ProfileInfoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ProfileInfo getProfileInfoSample1() {
        return new ProfileInfo().profileNumber("profileNumber1").userId(1L);
    }

    public static ProfileInfo getProfileInfoSample2() {
        return new ProfileInfo().profileNumber("profileNumber2").userId(2L);
    }

    public static ProfileInfo getProfileInfoRandomSampleGenerator() {
        return new ProfileInfo().profileNumber(UUID.randomUUID().toString()).userId(longCount.incrementAndGet());
    }
}
