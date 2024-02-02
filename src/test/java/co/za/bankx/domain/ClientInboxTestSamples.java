package co.za.bankx.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ClientInboxTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ClientInbox getClientInboxSample1() {
        return new ClientInbox().id(1L).message("message1");
    }

    public static ClientInbox getClientInboxSample2() {
        return new ClientInbox().id(2L).message("message2");
    }

    public static ClientInbox getClientInboxRandomSampleGenerator() {
        return new ClientInbox().id(longCount.incrementAndGet()).message(UUID.randomUUID().toString());
    }
}
