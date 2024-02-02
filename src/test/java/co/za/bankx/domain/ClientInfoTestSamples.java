package co.za.bankx.domain;

import java.util.UUID;

public class ClientInfoTestSamples {

    public static ClientInfo getClientInfoSample1() {
        return new ClientInfo().idNumber("idNumber1").firstName("firstName1").lastName("lastName1");
    }

    public static ClientInfo getClientInfoSample2() {
        return new ClientInfo().idNumber("idNumber2").firstName("firstName2").lastName("lastName2");
    }

    public static ClientInfo getClientInfoRandomSampleGenerator() {
        return new ClientInfo()
            .idNumber(UUID.randomUUID().toString())
            .firstName(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString());
    }
}
