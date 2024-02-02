package co.za.bankx.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class BankInfoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static BankInfo getBankInfoSample1() {
        return new BankInfo().id(1L).swiftCode("swiftCode1").bankName("bankName1");
    }

    public static BankInfo getBankInfoSample2() {
        return new BankInfo().id(2L).swiftCode("swiftCode2").bankName("bankName2");
    }

    public static BankInfo getBankInfoRandomSampleGenerator() {
        return new BankInfo()
            .id(longCount.incrementAndGet())
            .swiftCode(UUID.randomUUID().toString())
            .bankName(UUID.randomUUID().toString());
    }
}
