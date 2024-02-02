package co.za.bankx.domain;

import static co.za.bankx.domain.AccountInfoTestSamples.*;
import static co.za.bankx.domain.ProfileInfoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import co.za.bankx.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AccountInfoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AccountInfo.class);
        AccountInfo accountInfo1 = getAccountInfoSample1();
        AccountInfo accountInfo2 = new AccountInfo();
        assertThat(accountInfo1).isNotEqualTo(accountInfo2);

        accountInfo2.setId(accountInfo1.getId());
        assertThat(accountInfo1).isEqualTo(accountInfo2);

        accountInfo2 = getAccountInfoSample2();
        assertThat(accountInfo1).isNotEqualTo(accountInfo2);
    }

    @Test
    void profileInfoTest() throws Exception {
        AccountInfo accountInfo = getAccountInfoRandomSampleGenerator();
        ProfileInfo profileInfoBack = getProfileInfoRandomSampleGenerator();

        accountInfo.setProfileInfo(profileInfoBack);
        assertThat(accountInfo.getProfileInfo()).isEqualTo(profileInfoBack);

        accountInfo.profileInfo(null);
        assertThat(accountInfo.getProfileInfo()).isNull();
    }
}
