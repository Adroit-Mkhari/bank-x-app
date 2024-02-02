package co.za.bankx.domain;

import java.util.UUID;

public class TransactionLogTestSamples {

    public static TransactionLog getTransactionLogSample1() {
        return new TransactionLog()
            .uniqueTransactionId(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .debtorAccount("debtorAccount1")
            .creditorAccount("creditorAccount1");
    }

    public static TransactionLog getTransactionLogSample2() {
        return new TransactionLog()
            .uniqueTransactionId(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .debtorAccount("debtorAccount2")
            .creditorAccount("creditorAccount2");
    }

    public static TransactionLog getTransactionLogRandomSampleGenerator() {
        return new TransactionLog()
            .uniqueTransactionId(UUID.randomUUID())
            .debtorAccount(UUID.randomUUID().toString())
            .creditorAccount(UUID.randomUUID().toString());
    }
}
