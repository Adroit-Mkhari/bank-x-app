package co.za.bankx.domain;

import static co.za.bankx.domain.SessionLogTestSamples.*;
import static co.za.bankx.domain.TransactionLogTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import co.za.bankx.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TransactionLogTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TransactionLog.class);
        TransactionLog transactionLog1 = getTransactionLogSample1();
        TransactionLog transactionLog2 = new TransactionLog();
        assertThat(transactionLog1).isNotEqualTo(transactionLog2);

        transactionLog2.setUniqueTransactionId(transactionLog1.getUniqueTransactionId());
        assertThat(transactionLog1).isEqualTo(transactionLog2);

        transactionLog2 = getTransactionLogSample2();
        assertThat(transactionLog1).isNotEqualTo(transactionLog2);
    }

    @Test
    void sessionLogTest() throws Exception {
        TransactionLog transactionLog = getTransactionLogRandomSampleGenerator();
        SessionLog sessionLogBack = getSessionLogRandomSampleGenerator();

        transactionLog.addSessionLog(sessionLogBack);
        assertThat(transactionLog.getSessionLogs()).containsOnly(sessionLogBack);
        assertThat(sessionLogBack.getTransactionLog()).isEqualTo(transactionLog);

        transactionLog.removeSessionLog(sessionLogBack);
        assertThat(transactionLog.getSessionLogs()).doesNotContain(sessionLogBack);
        assertThat(sessionLogBack.getTransactionLog()).isNull();

        transactionLog.sessionLogs(new HashSet<>(Set.of(sessionLogBack)));
        assertThat(transactionLog.getSessionLogs()).containsOnly(sessionLogBack);
        assertThat(sessionLogBack.getTransactionLog()).isEqualTo(transactionLog);

        transactionLog.setSessionLogs(new HashSet<>());
        assertThat(transactionLog.getSessionLogs()).doesNotContain(sessionLogBack);
        assertThat(sessionLogBack.getTransactionLog()).isNull();
    }
}
