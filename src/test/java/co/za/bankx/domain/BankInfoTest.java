package co.za.bankx.domain;

import static co.za.bankx.domain.BankInfoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import co.za.bankx.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BankInfoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BankInfo.class);
        BankInfo bankInfo1 = getBankInfoSample1();
        BankInfo bankInfo2 = new BankInfo();
        assertThat(bankInfo1).isNotEqualTo(bankInfo2);

        bankInfo2.setId(bankInfo1.getId());
        assertThat(bankInfo1).isEqualTo(bankInfo2);

        bankInfo2 = getBankInfoSample2();
        assertThat(bankInfo1).isNotEqualTo(bankInfo2);
    }
}
