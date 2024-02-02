package co.za.bankx.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ContactTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Contact getContactSample1() {
        return new Contact()
            .id(1L)
            .streetAddress("streetAddress1")
            .postalCode("postalCode1")
            .city("city1")
            .stateProvince("stateProvince1")
            .email("email1")
            .phoneNumber("phoneNumber1");
    }

    public static Contact getContactSample2() {
        return new Contact()
            .id(2L)
            .streetAddress("streetAddress2")
            .postalCode("postalCode2")
            .city("city2")
            .stateProvince("stateProvince2")
            .email("email2")
            .phoneNumber("phoneNumber2");
    }

    public static Contact getContactRandomSampleGenerator() {
        return new Contact()
            .id(longCount.incrementAndGet())
            .streetAddress(UUID.randomUUID().toString())
            .postalCode(UUID.randomUUID().toString())
            .city(UUID.randomUUID().toString())
            .stateProvince(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .phoneNumber(UUID.randomUUID().toString());
    }
}
