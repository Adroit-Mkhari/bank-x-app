package co.za.bankx.domain;

import static co.za.bankx.domain.SessionLogTestSamples.*;
import static co.za.bankx.domain.TransactionLogTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import co.za.bankx.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SessionLogTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SessionLog.class);
        SessionLog sessionLog1 = getSessionLogSample1();
        SessionLog sessionLog2 = new SessionLog();
        assertThat(sessionLog1).isNotEqualTo(sessionLog2);

        sessionLog2.setId(sessionLog1.getId());
        assertThat(sessionLog1).isEqualTo(sessionLog2);

        sessionLog2 = getSessionLogSample2();
        assertThat(sessionLog1).isNotEqualTo(sessionLog2);
    }

    @Test
    void transactionLogTest() throws Exception {
        SessionLog sessionLog = getSessionLogRandomSampleGenerator();
        TransactionLog transactionLogBack = getTransactionLogRandomSampleGenerator();

        sessionLog.setTransactionLog(transactionLogBack);
        assertThat(sessionLog.getTransactionLog()).isEqualTo(transactionLogBack);

        sessionLog.transactionLog(null);
        assertThat(sessionLog.getTransactionLog()).isNull();
    }
}
