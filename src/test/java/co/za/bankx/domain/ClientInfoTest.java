package co.za.bankx.domain;

import static co.za.bankx.domain.ClientInfoTestSamples.*;
import static co.za.bankx.domain.ContactTestSamples.*;
import static co.za.bankx.domain.ProfileInfoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import co.za.bankx.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClientInfoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClientInfo.class);
        ClientInfo clientInfo1 = getClientInfoSample1();
        ClientInfo clientInfo2 = new ClientInfo();
        assertThat(clientInfo1).isNotEqualTo(clientInfo2);

        clientInfo2.setIdNumber(clientInfo1.getIdNumber());
        assertThat(clientInfo1).isEqualTo(clientInfo2);

        clientInfo2 = getClientInfoSample2();
        assertThat(clientInfo1).isNotEqualTo(clientInfo2);
    }

    @Test
    void contactTest() throws Exception {
        ClientInfo clientInfo = getClientInfoRandomSampleGenerator();
        Contact contactBack = getContactRandomSampleGenerator();

        clientInfo.setContact(contactBack);
        assertThat(clientInfo.getContact()).isEqualTo(contactBack);

        clientInfo.contact(null);
        assertThat(clientInfo.getContact()).isNull();
    }

    @Test
    void profileInfoTest() throws Exception {
        ClientInfo clientInfo = getClientInfoRandomSampleGenerator();
        ProfileInfo profileInfoBack = getProfileInfoRandomSampleGenerator();

        clientInfo.setProfileInfo(profileInfoBack);
        assertThat(clientInfo.getProfileInfo()).isEqualTo(profileInfoBack);

        clientInfo.profileInfo(null);
        assertThat(clientInfo.getProfileInfo()).isNull();
    }
}
