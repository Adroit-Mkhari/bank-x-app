package co.za.bankx.domain;

import static co.za.bankx.domain.ClientInboxTestSamples.*;
import static co.za.bankx.domain.ProfileInfoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import co.za.bankx.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClientInboxTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClientInbox.class);
        ClientInbox clientInbox1 = getClientInboxSample1();
        ClientInbox clientInbox2 = new ClientInbox();
        assertThat(clientInbox1).isNotEqualTo(clientInbox2);

        clientInbox2.setId(clientInbox1.getId());
        assertThat(clientInbox1).isEqualTo(clientInbox2);

        clientInbox2 = getClientInboxSample2();
        assertThat(clientInbox1).isNotEqualTo(clientInbox2);
    }

    @Test
    void profileInfoTest() throws Exception {
        ClientInbox clientInbox = getClientInboxRandomSampleGenerator();
        ProfileInfo profileInfoBack = getProfileInfoRandomSampleGenerator();

        clientInbox.setProfileInfo(profileInfoBack);
        assertThat(clientInbox.getProfileInfo()).isEqualTo(profileInfoBack);

        clientInbox.profileInfo(null);
        assertThat(clientInbox.getProfileInfo()).isNull();
    }
}
