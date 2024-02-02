package co.za.bankx.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import co.za.bankx.IntegrationTest;
import co.za.bankx.domain.ClientInbox;
import co.za.bankx.repository.ClientInboxRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ClientInboxResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ClientInboxResourceIT {

    private static final String DEFAULT_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/client-inboxes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ClientInboxRepository clientInboxRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClientInboxMockMvc;

    private ClientInbox clientInbox;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClientInbox createEntity(EntityManager em) {
        ClientInbox clientInbox = new ClientInbox().message(DEFAULT_MESSAGE);
        return clientInbox;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClientInbox createUpdatedEntity(EntityManager em) {
        ClientInbox clientInbox = new ClientInbox().message(UPDATED_MESSAGE);
        return clientInbox;
    }

    @BeforeEach
    public void initTest() {
        clientInbox = createEntity(em);
    }

    @Test
    @Transactional
    void createClientInbox() throws Exception {
        int databaseSizeBeforeCreate = clientInboxRepository.findAll().size();
        // Create the ClientInbox
        restClientInboxMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clientInbox)))
            .andExpect(status().isCreated());

        // Validate the ClientInbox in the database
        List<ClientInbox> clientInboxList = clientInboxRepository.findAll();
        assertThat(clientInboxList).hasSize(databaseSizeBeforeCreate + 1);
        ClientInbox testClientInbox = clientInboxList.get(clientInboxList.size() - 1);
        assertThat(testClientInbox.getMessage()).isEqualTo(DEFAULT_MESSAGE);
    }

    @Test
    @Transactional
    void createClientInboxWithExistingId() throws Exception {
        // Create the ClientInbox with an existing ID
        clientInbox.setId(1L);

        int databaseSizeBeforeCreate = clientInboxRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restClientInboxMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clientInbox)))
            .andExpect(status().isBadRequest());

        // Validate the ClientInbox in the database
        List<ClientInbox> clientInboxList = clientInboxRepository.findAll();
        assertThat(clientInboxList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllClientInboxes() throws Exception {
        // Initialize the database
        clientInboxRepository.saveAndFlush(clientInbox);

        // Get all the clientInboxList
        restClientInboxMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clientInbox.getId().intValue())))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)));
    }

    @Test
    @Transactional
    void getClientInbox() throws Exception {
        // Initialize the database
        clientInboxRepository.saveAndFlush(clientInbox);

        // Get the clientInbox
        restClientInboxMockMvc
            .perform(get(ENTITY_API_URL_ID, clientInbox.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(clientInbox.getId().intValue()))
            .andExpect(jsonPath("$.message").value(DEFAULT_MESSAGE));
    }

    @Test
    @Transactional
    void getNonExistingClientInbox() throws Exception {
        // Get the clientInbox
        restClientInboxMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingClientInbox() throws Exception {
        // Initialize the database
        clientInboxRepository.saveAndFlush(clientInbox);

        int databaseSizeBeforeUpdate = clientInboxRepository.findAll().size();

        // Update the clientInbox
        ClientInbox updatedClientInbox = clientInboxRepository.findById(clientInbox.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedClientInbox are not directly saved in db
        em.detach(updatedClientInbox);
        updatedClientInbox.message(UPDATED_MESSAGE);

        restClientInboxMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedClientInbox.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedClientInbox))
            )
            .andExpect(status().isOk());

        // Validate the ClientInbox in the database
        List<ClientInbox> clientInboxList = clientInboxRepository.findAll();
        assertThat(clientInboxList).hasSize(databaseSizeBeforeUpdate);
        ClientInbox testClientInbox = clientInboxList.get(clientInboxList.size() - 1);
        assertThat(testClientInbox.getMessage()).isEqualTo(UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void putNonExistingClientInbox() throws Exception {
        int databaseSizeBeforeUpdate = clientInboxRepository.findAll().size();
        clientInbox.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientInboxMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clientInbox.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clientInbox))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientInbox in the database
        List<ClientInbox> clientInboxList = clientInboxRepository.findAll();
        assertThat(clientInboxList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchClientInbox() throws Exception {
        int databaseSizeBeforeUpdate = clientInboxRepository.findAll().size();
        clientInbox.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientInboxMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clientInbox))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientInbox in the database
        List<ClientInbox> clientInboxList = clientInboxRepository.findAll();
        assertThat(clientInboxList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamClientInbox() throws Exception {
        int databaseSizeBeforeUpdate = clientInboxRepository.findAll().size();
        clientInbox.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientInboxMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clientInbox)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClientInbox in the database
        List<ClientInbox> clientInboxList = clientInboxRepository.findAll();
        assertThat(clientInboxList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateClientInboxWithPatch() throws Exception {
        // Initialize the database
        clientInboxRepository.saveAndFlush(clientInbox);

        int databaseSizeBeforeUpdate = clientInboxRepository.findAll().size();

        // Update the clientInbox using partial update
        ClientInbox partialUpdatedClientInbox = new ClientInbox();
        partialUpdatedClientInbox.setId(clientInbox.getId());

        partialUpdatedClientInbox.message(UPDATED_MESSAGE);

        restClientInboxMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClientInbox.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedClientInbox))
            )
            .andExpect(status().isOk());

        // Validate the ClientInbox in the database
        List<ClientInbox> clientInboxList = clientInboxRepository.findAll();
        assertThat(clientInboxList).hasSize(databaseSizeBeforeUpdate);
        ClientInbox testClientInbox = clientInboxList.get(clientInboxList.size() - 1);
        assertThat(testClientInbox.getMessage()).isEqualTo(UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void fullUpdateClientInboxWithPatch() throws Exception {
        // Initialize the database
        clientInboxRepository.saveAndFlush(clientInbox);

        int databaseSizeBeforeUpdate = clientInboxRepository.findAll().size();

        // Update the clientInbox using partial update
        ClientInbox partialUpdatedClientInbox = new ClientInbox();
        partialUpdatedClientInbox.setId(clientInbox.getId());

        partialUpdatedClientInbox.message(UPDATED_MESSAGE);

        restClientInboxMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClientInbox.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedClientInbox))
            )
            .andExpect(status().isOk());

        // Validate the ClientInbox in the database
        List<ClientInbox> clientInboxList = clientInboxRepository.findAll();
        assertThat(clientInboxList).hasSize(databaseSizeBeforeUpdate);
        ClientInbox testClientInbox = clientInboxList.get(clientInboxList.size() - 1);
        assertThat(testClientInbox.getMessage()).isEqualTo(UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void patchNonExistingClientInbox() throws Exception {
        int databaseSizeBeforeUpdate = clientInboxRepository.findAll().size();
        clientInbox.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientInboxMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, clientInbox.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(clientInbox))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientInbox in the database
        List<ClientInbox> clientInboxList = clientInboxRepository.findAll();
        assertThat(clientInboxList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchClientInbox() throws Exception {
        int databaseSizeBeforeUpdate = clientInboxRepository.findAll().size();
        clientInbox.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientInboxMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(clientInbox))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientInbox in the database
        List<ClientInbox> clientInboxList = clientInboxRepository.findAll();
        assertThat(clientInboxList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamClientInbox() throws Exception {
        int databaseSizeBeforeUpdate = clientInboxRepository.findAll().size();
        clientInbox.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientInboxMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(clientInbox))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClientInbox in the database
        List<ClientInbox> clientInboxList = clientInboxRepository.findAll();
        assertThat(clientInboxList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteClientInbox() throws Exception {
        // Initialize the database
        clientInboxRepository.saveAndFlush(clientInbox);

        int databaseSizeBeforeDelete = clientInboxRepository.findAll().size();

        // Delete the clientInbox
        restClientInboxMockMvc
            .perform(delete(ENTITY_API_URL_ID, clientInbox.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ClientInbox> clientInboxList = clientInboxRepository.findAll();
        assertThat(clientInboxList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
