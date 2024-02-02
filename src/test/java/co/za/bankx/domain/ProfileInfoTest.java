package co.za.bankx.domain;

import static co.za.bankx.domain.AccountInfoTestSamples.*;
import static co.za.bankx.domain.ClientInboxTestSamples.*;
import static co.za.bankx.domain.ClientInfoTestSamples.*;
import static co.za.bankx.domain.ProfileInfoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import co.za.bankx.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ProfileInfoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProfileInfo.class);
        ProfileInfo profileInfo1 = getProfileInfoSample1();
        ProfileInfo profileInfo2 = new ProfileInfo();
        assertThat(profileInfo1).isNotEqualTo(profileInfo2);

        profileInfo2.setProfileNumber(profileInfo1.getProfileNumber());
        assertThat(profileInfo1).isEqualTo(profileInfo2);

        profileInfo2 = getProfileInfoSample2();
        assertThat(profileInfo1).isNotEqualTo(profileInfo2);
    }

    @Test
    void clientInfoTest() throws Exception {
        ProfileInfo profileInfo = getProfileInfoRandomSampleGenerator();
        ClientInfo clientInfoBack = getClientInfoRandomSampleGenerator();

        profileInfo.addClientInfo(clientInfoBack);
        assertThat(profileInfo.getClientInfos()).containsOnly(clientInfoBack);
        assertThat(clientInfoBack.getProfileInfo()).isEqualTo(profileInfo);

        profileInfo.removeClientInfo(clientInfoBack);
        assertThat(profileInfo.getClientInfos()).doesNotContain(clientInfoBack);
        assertThat(clientInfoBack.getProfileInfo()).isNull();

        profileInfo.clientInfos(new HashSet<>(Set.of(clientInfoBack)));
        assertThat(profileInfo.getClientInfos()).containsOnly(clientInfoBack);
        assertThat(clientInfoBack.getProfileInfo()).isEqualTo(profileInfo);

        profileInfo.setClientInfos(new HashSet<>());
        assertThat(profileInfo.getClientInfos()).doesNotContain(clientInfoBack);
        assertThat(clientInfoBack.getProfileInfo()).isNull();
    }

    @Test
    void accountInfoTest() throws Exception {
        ProfileInfo profileInfo = getProfileInfoRandomSampleGenerator();
        AccountInfo accountInfoBack = getAccountInfoRandomSampleGenerator();

        profileInfo.addAccountInfo(accountInfoBack);
        assertThat(profileInfo.getAccountInfos()).containsOnly(accountInfoBack);
        assertThat(accountInfoBack.getProfileInfo()).isEqualTo(profileInfo);

        profileInfo.removeAccountInfo(accountInfoBack);
        assertThat(profileInfo.getAccountInfos()).doesNotContain(accountInfoBack);
        assertThat(accountInfoBack.getProfileInfo()).isNull();

        profileInfo.accountInfos(new HashSet<>(Set.of(accountInfoBack)));
        assertThat(profileInfo.getAccountInfos()).containsOnly(accountInfoBack);
        assertThat(accountInfoBack.getProfileInfo()).isEqualTo(profileInfo);

        profileInfo.setAccountInfos(new HashSet<>());
        assertThat(profileInfo.getAccountInfos()).doesNotContain(accountInfoBack);
        assertThat(accountInfoBack.getProfileInfo()).isNull();
    }

    @Test
    void clientInboxTest() throws Exception {
        ProfileInfo profileInfo = getProfileInfoRandomSampleGenerator();
        ClientInbox clientInboxBack = getClientInboxRandomSampleGenerator();

        profileInfo.addClientInbox(clientInboxBack);
        assertThat(profileInfo.getClientInboxes()).containsOnly(clientInboxBack);
        assertThat(clientInboxBack.getProfileInfo()).isEqualTo(profileInfo);

        profileInfo.removeClientInbox(clientInboxBack);
        assertThat(profileInfo.getClientInboxes()).doesNotContain(clientInboxBack);
        assertThat(clientInboxBack.getProfileInfo()).isNull();

        profileInfo.clientInboxes(new HashSet<>(Set.of(clientInboxBack)));
        assertThat(profileInfo.getClientInboxes()).containsOnly(clientInboxBack);
        assertThat(clientInboxBack.getProfileInfo()).isEqualTo(profileInfo);

        profileInfo.setClientInboxes(new HashSet<>());
        assertThat(profileInfo.getClientInboxes()).doesNotContain(clientInboxBack);
        assertThat(clientInboxBack.getProfileInfo()).isNull();
    }
}
