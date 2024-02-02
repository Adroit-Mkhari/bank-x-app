package co.za.bankx.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AccountInfoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static AccountInfo getAccountInfoSample1() {
        return new AccountInfo().id(1L).accountNumber("accountNumber1");
    }

    public static AccountInfo getAccountInfoSample2() {
        return new AccountInfo().id(2L).accountNumber("accountNumber2");
    }

    public static AccountInfo getAccountInfoRandomSampleGenerator() {
        return new AccountInfo().id(longCount.incrementAndGet()).accountNumber(UUID.randomUUID().toString());
    }
}
