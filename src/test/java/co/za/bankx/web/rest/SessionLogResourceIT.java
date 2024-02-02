package co.za.bankx.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import co.za.bankx.IntegrationTest;
import co.za.bankx.domain.SessionLog;
import co.za.bankx.domain.enumeration.DebitCreditStatus;
import co.za.bankx.domain.enumeration.TransactionType;
import co.za.bankx.repository.SessionLogRepository;
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
 * Integration tests for the {@link SessionLogResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SessionLogResourceIT {

    private static final TransactionType DEFAULT_TRANSACTION_TYPE = TransactionType.DEBIT;
    private static final TransactionType UPDATED_TRANSACTION_TYPE = TransactionType.CREDIT;

    private static final DebitCreditStatus DEFAULT_STATUS = DebitCreditStatus.ACCEPTED;
    private static final DebitCreditStatus UPDATED_STATUS = DebitCreditStatus.INSUFFICIENT_FUNDS;

    private static final String ENTITY_API_URL = "/api/session-logs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SessionLogRepository sessionLogRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSessionLogMockMvc;

    private SessionLog sessionLog;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SessionLog createEntity(EntityManager em) {
        SessionLog sessionLog = new SessionLog().transactionType(DEFAULT_TRANSACTION_TYPE).status(DEFAULT_STATUS);
        return sessionLog;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SessionLog createUpdatedEntity(EntityManager em) {
        SessionLog sessionLog = new SessionLog().transactionType(UPDATED_TRANSACTION_TYPE).status(UPDATED_STATUS);
        return sessionLog;
    }

    @BeforeEach
    public void initTest() {
        sessionLog = createEntity(em);
    }

    @Test
    @Transactional
    void createSessionLog() throws Exception {
        int databaseSizeBeforeCreate = sessionLogRepository.findAll().size();
        // Create the SessionLog
        restSessionLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sessionLog)))
            .andExpect(status().isCreated());

        // Validate the SessionLog in the database
        List<SessionLog> sessionLogList = sessionLogRepository.findAll();
        assertThat(sessionLogList).hasSize(databaseSizeBeforeCreate + 1);
        SessionLog testSessionLog = sessionLogList.get(sessionLogList.size() - 1);
        assertThat(testSessionLog.getTransactionType()).isEqualTo(DEFAULT_TRANSACTION_TYPE);
        assertThat(testSessionLog.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createSessionLogWithExistingId() throws Exception {
        // Create the SessionLog with an existing ID
        sessionLog.setId(1L);

        int databaseSizeBeforeCreate = sessionLogRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSessionLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sessionLog)))
            .andExpect(status().isBadRequest());

        // Validate the SessionLog in the database
        List<SessionLog> sessionLogList = sessionLogRepository.findAll();
        assertThat(sessionLogList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTransactionTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = sessionLogRepository.findAll().size();
        // set the field null
        sessionLog.setTransactionType(null);

        // Create the SessionLog, which fails.

        restSessionLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sessionLog)))
            .andExpect(status().isBadRequest());

        List<SessionLog> sessionLogList = sessionLogRepository.findAll();
        assertThat(sessionLogList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = sessionLogRepository.findAll().size();
        // set the field null
        sessionLog.setStatus(null);

        // Create the SessionLog, which fails.

        restSessionLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sessionLog)))
            .andExpect(status().isBadRequest());

        List<SessionLog> sessionLogList = sessionLogRepository.findAll();
        assertThat(sessionLogList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSessionLogs() throws Exception {
        // Initialize the database
        sessionLogRepository.saveAndFlush(sessionLog);

        // Get all the sessionLogList
        restSessionLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sessionLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].transactionType").value(hasItem(DEFAULT_TRANSACTION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getSessionLog() throws Exception {
        // Initialize the database
        sessionLogRepository.saveAndFlush(sessionLog);

        // Get the sessionLog
        restSessionLogMockMvc
            .perform(get(ENTITY_API_URL_ID, sessionLog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sessionLog.getId().intValue()))
            .andExpect(jsonPath("$.transactionType").value(DEFAULT_TRANSACTION_TYPE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingSessionLog() throws Exception {
        // Get the sessionLog
        restSessionLogMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSessionLog() throws Exception {
        // Initialize the database
        sessionLogRepository.saveAndFlush(sessionLog);

        int databaseSizeBeforeUpdate = sessionLogRepository.findAll().size();

        // Update the sessionLog
        SessionLog updatedSessionLog = sessionLogRepository.findById(sessionLog.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSessionLog are not directly saved in db
        em.detach(updatedSessionLog);
        updatedSessionLog.transactionType(UPDATED_TRANSACTION_TYPE).status(UPDATED_STATUS);

        restSessionLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSessionLog.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSessionLog))
            )
            .andExpect(status().isOk());

        // Validate the SessionLog in the database
        List<SessionLog> sessionLogList = sessionLogRepository.findAll();
        assertThat(sessionLogList).hasSize(databaseSizeBeforeUpdate);
        SessionLog testSessionLog = sessionLogList.get(sessionLogList.size() - 1);
        assertThat(testSessionLog.getTransactionType()).isEqualTo(UPDATED_TRANSACTION_TYPE);
        assertThat(testSessionLog.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void putNonExistingSessionLog() throws Exception {
        int databaseSizeBeforeUpdate = sessionLogRepository.findAll().size();
        sessionLog.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSessionLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sessionLog.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(sessionLog))
            )
            .andExpect(status().isBadRequest());

        // Validate the SessionLog in the database
        List<SessionLog> sessionLogList = sessionLogRepository.findAll();
        assertThat(sessionLogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSessionLog() throws Exception {
        int databaseSizeBeforeUpdate = sessionLogRepository.findAll().size();
        sessionLog.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSessionLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(sessionLog))
            )
            .andExpect(status().isBadRequest());

        // Validate the SessionLog in the database
        List<SessionLog> sessionLogList = sessionLogRepository.findAll();
        assertThat(sessionLogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSessionLog() throws Exception {
        int databaseSizeBeforeUpdate = sessionLogRepository.findAll().size();
        sessionLog.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSessionLogMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sessionLog)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SessionLog in the database
        List<SessionLog> sessionLogList = sessionLogRepository.findAll();
        assertThat(sessionLogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSessionLogWithPatch() throws Exception {
        // Initialize the database
        sessionLogRepository.saveAndFlush(sessionLog);

        int databaseSizeBeforeUpdate = sessionLogRepository.findAll().size();

        // Update the sessionLog using partial update
        SessionLog partialUpdatedSessionLog = new SessionLog();
        partialUpdatedSessionLog.setId(sessionLog.getId());

        partialUpdatedSessionLog.status(UPDATED_STATUS);

        restSessionLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSessionLog.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSessionLog))
            )
            .andExpect(status().isOk());

        // Validate the SessionLog in the database
        List<SessionLog> sessionLogList = sessionLogRepository.findAll();
        assertThat(sessionLogList).hasSize(databaseSizeBeforeUpdate);
        SessionLog testSessionLog = sessionLogList.get(sessionLogList.size() - 1);
        assertThat(testSessionLog.getTransactionType()).isEqualTo(DEFAULT_TRANSACTION_TYPE);
        assertThat(testSessionLog.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateSessionLogWithPatch() throws Exception {
        // Initialize the database
        sessionLogRepository.saveAndFlush(sessionLog);

        int databaseSizeBeforeUpdate = sessionLogRepository.findAll().size();

        // Update the sessionLog using partial update
        SessionLog partialUpdatedSessionLog = new SessionLog();
        partialUpdatedSessionLog.setId(sessionLog.getId());

        partialUpdatedSessionLog.transactionType(UPDATED_TRANSACTION_TYPE).status(UPDATED_STATUS);

        restSessionLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSessionLog.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSessionLog))
            )
            .andExpect(status().isOk());

        // Validate the SessionLog in the database
        List<SessionLog> sessionLogList = sessionLogRepository.findAll();
        assertThat(sessionLogList).hasSize(databaseSizeBeforeUpdate);
        SessionLog testSessionLog = sessionLogList.get(sessionLogList.size() - 1);
        assertThat(testSessionLog.getTransactionType()).isEqualTo(UPDATED_TRANSACTION_TYPE);
        assertThat(testSessionLog.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingSessionLog() throws Exception {
        int databaseSizeBeforeUpdate = sessionLogRepository.findAll().size();
        sessionLog.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSessionLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, sessionLog.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(sessionLog))
            )
            .andExpect(status().isBadRequest());

        // Validate the SessionLog in the database
        List<SessionLog> sessionLogList = sessionLogRepository.findAll();
        assertThat(sessionLogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSessionLog() throws Exception {
        int databaseSizeBeforeUpdate = sessionLogRepository.findAll().size();
        sessionLog.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSessionLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(sessionLog))
            )
            .andExpect(status().isBadRequest());

        // Validate the SessionLog in the database
        List<SessionLog> sessionLogList = sessionLogRepository.findAll();
        assertThat(sessionLogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSessionLog() throws Exception {
        int databaseSizeBeforeUpdate = sessionLogRepository.findAll().size();
        sessionLog.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSessionLogMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(sessionLog))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SessionLog in the database
        List<SessionLog> sessionLogList = sessionLogRepository.findAll();
        assertThat(sessionLogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSessionLog() throws Exception {
        // Initialize the database
        sessionLogRepository.saveAndFlush(sessionLog);

        int databaseSizeBeforeDelete = sessionLogRepository.findAll().size();

        // Delete the sessionLog
        restSessionLogMockMvc
            .perform(delete(ENTITY_API_URL_ID, sessionLog.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SessionLog> sessionLogList = sessionLogRepository.findAll();
        assertThat(sessionLogList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
