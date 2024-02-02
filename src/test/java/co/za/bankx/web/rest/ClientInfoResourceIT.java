package co.za.bankx.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import co.za.bankx.IntegrationTest;
import co.za.bankx.domain.ClientInfo;
import co.za.bankx.repository.ClientInfoRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ClientInfoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ClientInfoResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/client-infos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{idNumber}";

    @Autowired
    private ClientInfoRepository clientInfoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClientInfoMockMvc;

    private ClientInfo clientInfo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClientInfo createEntity(EntityManager em) {
        ClientInfo clientInfo = new ClientInfo().firstName(DEFAULT_FIRST_NAME).lastName(DEFAULT_LAST_NAME);
        return clientInfo;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClientInfo createUpdatedEntity(EntityManager em) {
        ClientInfo clientInfo = new ClientInfo().firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME);
        return clientInfo;
    }

    @BeforeEach
    public void initTest() {
        clientInfo = createEntity(em);
    }

    @Test
    @Transactional
    void createClientInfo() throws Exception {
        int databaseSizeBeforeCreate = clientInfoRepository.findAll().size();
        // Create the ClientInfo
        restClientInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clientInfo)))
            .andExpect(status().isCreated());

        // Validate the ClientInfo in the database
        List<ClientInfo> clientInfoList = clientInfoRepository.findAll();
        assertThat(clientInfoList).hasSize(databaseSizeBeforeCreate + 1);
        ClientInfo testClientInfo = clientInfoList.get(clientInfoList.size() - 1);
        assertThat(testClientInfo.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testClientInfo.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
    }

    @Test
    @Transactional
    void createClientInfoWithExistingId() throws Exception {
        // Create the ClientInfo with an existing ID
        clientInfo.setIdNumber("existing_id");

        int databaseSizeBeforeCreate = clientInfoRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restClientInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clientInfo)))
            .andExpect(status().isBadRequest());

        // Validate the ClientInfo in the database
        List<ClientInfo> clientInfoList = clientInfoRepository.findAll();
        assertThat(clientInfoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFirstNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = clientInfoRepository.findAll().size();
        // set the field null
        clientInfo.setFirstName(null);

        // Create the ClientInfo, which fails.

        restClientInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clientInfo)))
            .andExpect(status().isBadRequest());

        List<ClientInfo> clientInfoList = clientInfoRepository.findAll();
        assertThat(clientInfoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLastNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = clientInfoRepository.findAll().size();
        // set the field null
        clientInfo.setLastName(null);

        // Create the ClientInfo, which fails.

        restClientInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clientInfo)))
            .andExpect(status().isBadRequest());

        List<ClientInfo> clientInfoList = clientInfoRepository.findAll();
        assertThat(clientInfoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllClientInfos() throws Exception {
        // Initialize the database
        clientInfo.setIdNumber(UUID.randomUUID().toString());
        clientInfoRepository.saveAndFlush(clientInfo);

        // Get all the clientInfoList
        restClientInfoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=idNumber,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].idNumber").value(hasItem(clientInfo.getIdNumber())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)));
    }

    @Test
    @Transactional
    void getClientInfo() throws Exception {
        // Initialize the database
        clientInfo.setIdNumber(UUID.randomUUID().toString());
        clientInfoRepository.saveAndFlush(clientInfo);

        // Get the clientInfo
        restClientInfoMockMvc
            .perform(get(ENTITY_API_URL_ID, clientInfo.getIdNumber()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.idNumber").value(clientInfo.getIdNumber()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME));
    }

    @Test
    @Transactional
    void getNonExistingClientInfo() throws Exception {
        // Get the clientInfo
        restClientInfoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingClientInfo() throws Exception {
        // Initialize the database
        clientInfo.setIdNumber(UUID.randomUUID().toString());
        clientInfoRepository.saveAndFlush(clientInfo);

        int databaseSizeBeforeUpdate = clientInfoRepository.findAll().size();

        // Update the clientInfo
        ClientInfo updatedClientInfo = clientInfoRepository.findById(clientInfo.getIdNumber()).orElseThrow();
        // Disconnect from session so that the updates on updatedClientInfo are not directly saved in db
        em.detach(updatedClientInfo);
        updatedClientInfo.firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME);

        restClientInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedClientInfo.getIdNumber())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedClientInfo))
            )
            .andExpect(status().isOk());

        // Validate the ClientInfo in the database
        List<ClientInfo> clientInfoList = clientInfoRepository.findAll();
        assertThat(clientInfoList).hasSize(databaseSizeBeforeUpdate);
        ClientInfo testClientInfo = clientInfoList.get(clientInfoList.size() - 1);
        assertThat(testClientInfo.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testClientInfo.getLastName()).isEqualTo(UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void putNonExistingClientInfo() throws Exception {
        int databaseSizeBeforeUpdate = clientInfoRepository.findAll().size();
        clientInfo.setIdNumber(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clientInfo.getIdNumber())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clientInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientInfo in the database
        List<ClientInfo> clientInfoList = clientInfoRepository.findAll();
        assertThat(clientInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchClientInfo() throws Exception {
        int databaseSizeBeforeUpdate = clientInfoRepository.findAll().size();
        clientInfo.setIdNumber(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clientInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientInfo in the database
        List<ClientInfo> clientInfoList = clientInfoRepository.findAll();
        assertThat(clientInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamClientInfo() throws Exception {
        int databaseSizeBeforeUpdate = clientInfoRepository.findAll().size();
        clientInfo.setIdNumber(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientInfoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(clientInfo)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClientInfo in the database
        List<ClientInfo> clientInfoList = clientInfoRepository.findAll();
        assertThat(clientInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateClientInfoWithPatch() throws Exception {
        // Initialize the database
        clientInfo.setIdNumber(UUID.randomUUID().toString());
        clientInfoRepository.saveAndFlush(clientInfo);

        int databaseSizeBeforeUpdate = clientInfoRepository.findAll().size();

        // Update the clientInfo using partial update
        ClientInfo partialUpdatedClientInfo = new ClientInfo();
        partialUpdatedClientInfo.setIdNumber(clientInfo.getIdNumber());

        restClientInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClientInfo.getIdNumber())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedClientInfo))
            )
            .andExpect(status().isOk());

        // Validate the ClientInfo in the database
        List<ClientInfo> clientInfoList = clientInfoRepository.findAll();
        assertThat(clientInfoList).hasSize(databaseSizeBeforeUpdate);
        ClientInfo testClientInfo = clientInfoList.get(clientInfoList.size() - 1);
        assertThat(testClientInfo.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testClientInfo.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
    }

    @Test
    @Transactional
    void fullUpdateClientInfoWithPatch() throws Exception {
        // Initialize the database
        clientInfo.setIdNumber(UUID.randomUUID().toString());
        clientInfoRepository.saveAndFlush(clientInfo);

        int databaseSizeBeforeUpdate = clientInfoRepository.findAll().size();

        // Update the clientInfo using partial update
        ClientInfo partialUpdatedClientInfo = new ClientInfo();
        partialUpdatedClientInfo.setIdNumber(clientInfo.getIdNumber());

        partialUpdatedClientInfo.firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME);

        restClientInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClientInfo.getIdNumber())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedClientInfo))
            )
            .andExpect(status().isOk());

        // Validate the ClientInfo in the database
        List<ClientInfo> clientInfoList = clientInfoRepository.findAll();
        assertThat(clientInfoList).hasSize(databaseSizeBeforeUpdate);
        ClientInfo testClientInfo = clientInfoList.get(clientInfoList.size() - 1);
        assertThat(testClientInfo.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testClientInfo.getLastName()).isEqualTo(UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingClientInfo() throws Exception {
        int databaseSizeBeforeUpdate = clientInfoRepository.findAll().size();
        clientInfo.setIdNumber(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, clientInfo.getIdNumber())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(clientInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientInfo in the database
        List<ClientInfo> clientInfoList = clientInfoRepository.findAll();
        assertThat(clientInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchClientInfo() throws Exception {
        int databaseSizeBeforeUpdate = clientInfoRepository.findAll().size();
        clientInfo.setIdNumber(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(clientInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClientInfo in the database
        List<ClientInfo> clientInfoList = clientInfoRepository.findAll();
        assertThat(clientInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamClientInfo() throws Exception {
        int databaseSizeBeforeUpdate = clientInfoRepository.findAll().size();
        clientInfo.setIdNumber(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientInfoMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(clientInfo))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClientInfo in the database
        List<ClientInfo> clientInfoList = clientInfoRepository.findAll();
        assertThat(clientInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteClientInfo() throws Exception {
        // Initialize the database
        clientInfo.setIdNumber(UUID.randomUUID().toString());
        clientInfoRepository.saveAndFlush(clientInfo);

        int databaseSizeBeforeDelete = clientInfoRepository.findAll().size();

        // Delete the clientInfo
        restClientInfoMockMvc
            .perform(delete(ENTITY_API_URL_ID, clientInfo.getIdNumber()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ClientInfo> clientInfoList = clientInfoRepository.findAll();
        assertThat(clientInfoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
