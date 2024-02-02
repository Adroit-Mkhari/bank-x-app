package co.za.bankx.domain;

import static co.za.bankx.domain.ClientInfoTestSamples.*;
import static co.za.bankx.domain.ContactTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import co.za.bankx.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ContactTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Contact.class);
        Contact contact1 = getContactSample1();
        Contact contact2 = new Contact();
        assertThat(contact1).isNotEqualTo(contact2);

        contact2.setId(contact1.getId());
        assertThat(contact1).isEqualTo(contact2);

        contact2 = getContactSample2();
        assertThat(contact1).isNotEqualTo(contact2);
    }

    @Test
    void clientInfoTest() throws Exception {
        Contact contact = getContactRandomSampleGenerator();
        ClientInfo clientInfoBack = getClientInfoRandomSampleGenerator();

        contact.setClientInfo(clientInfoBack);
        assertThat(contact.getClientInfo()).isEqualTo(clientInfoBack);
        assertThat(clientInfoBack.getContact()).isEqualTo(contact);

        contact.clientInfo(null);
        assertThat(contact.getClientInfo()).isNull();
        assertThat(clientInfoBack.getContact()).isNull();
    }
}
