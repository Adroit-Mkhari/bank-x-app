package co.za.bankx.web.rest;

import static co.za.bankx.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import co.za.bankx.IntegrationTest;
import co.za.bankx.domain.TransactionLog;
import co.za.bankx.domain.enumeration.TransactionStatus;
import co.za.bankx.repository.TransactionLogRepository;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link TransactionLogResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TransactionLogResourceIT {

    private static final Instant DEFAULT_TRANSACTION_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TRANSACTION_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_DEBTOR_ACCOUNT = "15592521600622";
    private static final String UPDATED_DEBTOR_ACCOUNT = "498940983809855";

    private static final String DEFAULT_CREDITOR_ACCOUNT = "830446432066";
    private static final String UPDATED_CREDITOR_ACCOUNT = "852728587460027";

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);

    private static final TransactionStatus DEFAULT_STATUS = TransactionStatus.PENDING;
    private static final TransactionStatus UPDATED_STATUS = TransactionStatus.SUCCESSFUL;

    private static final String ENTITY_API_URL = "/api/transaction-logs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{uniqueTransactionId}";

    @Autowired
    private TransactionLogRepository transactionLogRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTransactionLogMockMvc;

    private TransactionLog transactionLog;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TransactionLog createEntity(EntityManager em) {
        TransactionLog transactionLog = new TransactionLog()
            .transactionTime(DEFAULT_TRANSACTION_TIME)
            .debtorAccount(DEFAULT_DEBTOR_ACCOUNT)
            .creditorAccount(DEFAULT_CREDITOR_ACCOUNT)
            .amount(DEFAULT_AMOUNT)
            .status(DEFAULT_STATUS);
        return transactionLog;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TransactionLog createUpdatedEntity(EntityManager em) {
        TransactionLog transactionLog = new TransactionLog()
            .transactionTime(UPDATED_TRANSACTION_TIME)
            .debtorAccount(UPDATED_DEBTOR_ACCOUNT)
            .creditorAccount(UPDATED_CREDITOR_ACCOUNT)
            .amount(UPDATED_AMOUNT)
            .status(UPDATED_STATUS);
        return transactionLog;
    }

    @BeforeEach
    public void initTest() {
        transactionLog = createEntity(em);
    }

    @Test
    @Transactional
    void createTransactionLog() throws Exception {
        int databaseSizeBeforeCreate = transactionLogRepository.findAll().size();
        // Create the TransactionLog
        restTransactionLogMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionLog))
            )
            .andExpect(status().isCreated());

        // Validate the TransactionLog in the database
        List<TransactionLog> transactionLogList = transactionLogRepository.findAll();
        assertThat(transactionLogList).hasSize(databaseSizeBeforeCreate + 1);
        TransactionLog testTransactionLog = transactionLogList.get(transactionLogList.size() - 1);
        assertThat(testTransactionLog.getTransactionTime()).isEqualTo(DEFAULT_TRANSACTION_TIME);
        assertThat(testTransactionLog.getDebtorAccount()).isEqualTo(DEFAULT_DEBTOR_ACCOUNT);
        assertThat(testTransactionLog.getCreditorAccount()).isEqualTo(DEFAULT_CREDITOR_ACCOUNT);
        assertThat(testTransactionLog.getAmount()).isEqualByComparingTo(DEFAULT_AMOUNT);
        assertThat(testTransactionLog.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createTransactionLogWithExistingId() throws Exception {
        // Create the TransactionLog with an existing ID
        transactionLogRepository.saveAndFlush(transactionLog);

        int databaseSizeBeforeCreate = transactionLogRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTransactionLogMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionLog))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionLog in the database
        List<TransactionLog> transactionLogList = transactionLogRepository.findAll();
        assertThat(transactionLogList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTransactionTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionLogRepository.findAll().size();
        // set the field null
        transactionLog.setTransactionTime(null);

        // Create the TransactionLog, which fails.

        restTransactionLogMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionLog))
            )
            .andExpect(status().isBadRequest());

        List<TransactionLog> transactionLogList = transactionLogRepository.findAll();
        assertThat(transactionLogList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDebtorAccountIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionLogRepository.findAll().size();
        // set the field null
        transactionLog.setDebtorAccount(null);

        // Create the TransactionLog, which fails.

        restTransactionLogMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionLog))
            )
            .andExpect(status().isBadRequest());

        List<TransactionLog> transactionLogList = transactionLogRepository.findAll();
        assertThat(transactionLogList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreditorAccountIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionLogRepository.findAll().size();
        // set the field null
        transactionLog.setCreditorAccount(null);

        // Create the TransactionLog, which fails.

        restTransactionLogMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionLog))
            )
            .andExpect(status().isBadRequest());

        List<TransactionLog> transactionLogList = transactionLogRepository.findAll();
        assertThat(transactionLogList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionLogRepository.findAll().size();
        // set the field null
        transactionLog.setAmount(null);

        // Create the TransactionLog, which fails.

        restTransactionLogMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionLog))
            )
            .andExpect(status().isBadRequest());

        List<TransactionLog> transactionLogList = transactionLogRepository.findAll();
        assertThat(transactionLogList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionLogRepository.findAll().size();
        // set the field null
        transactionLog.setStatus(null);

        // Create the TransactionLog, which fails.

        restTransactionLogMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionLog))
            )
            .andExpect(status().isBadRequest());

        List<TransactionLog> transactionLogList = transactionLogRepository.findAll();
        assertThat(transactionLogList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTransactionLogs() throws Exception {
        // Initialize the database
        transactionLogRepository.saveAndFlush(transactionLog);

        // Get all the transactionLogList
        restTransactionLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=uniqueTransactionId,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].uniqueTransactionId").value(hasItem(transactionLog.getUniqueTransactionId().toString())))
            .andExpect(jsonPath("$.[*].transactionTime").value(hasItem(DEFAULT_TRANSACTION_TIME.toString())))
            .andExpect(jsonPath("$.[*].debtorAccount").value(hasItem(DEFAULT_DEBTOR_ACCOUNT)))
            .andExpect(jsonPath("$.[*].creditorAccount").value(hasItem(DEFAULT_CREDITOR_ACCOUNT)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getTransactionLog() throws Exception {
        // Initialize the database
        transactionLogRepository.saveAndFlush(transactionLog);

        // Get the transactionLog
        restTransactionLogMockMvc
            .perform(get(ENTITY_API_URL_ID, transactionLog.getUniqueTransactionId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.uniqueTransactionId").value(transactionLog.getUniqueTransactionId().toString()))
            .andExpect(jsonPath("$.transactionTime").value(DEFAULT_TRANSACTION_TIME.toString()))
            .andExpect(jsonPath("$.debtorAccount").value(DEFAULT_DEBTOR_ACCOUNT))
            .andExpect(jsonPath("$.creditorAccount").value(DEFAULT_CREDITOR_ACCOUNT))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingTransactionLog() throws Exception {
        // Get the transactionLog
        restTransactionLogMockMvc.perform(get(ENTITY_API_URL_ID, UUID.randomUUID().toString())).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTransactionLog() throws Exception {
        // Initialize the database
        transactionLogRepository.saveAndFlush(transactionLog);

        int databaseSizeBeforeUpdate = transactionLogRepository.findAll().size();

        // Update the transactionLog
        TransactionLog updatedTransactionLog = transactionLogRepository.findById(transactionLog.getUniqueTransactionId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTransactionLog are not directly saved in db
        em.detach(updatedTransactionLog);
        updatedTransactionLog
            .transactionTime(UPDATED_TRANSACTION_TIME)
            .debtorAccount(UPDATED_DEBTOR_ACCOUNT)
            .creditorAccount(UPDATED_CREDITOR_ACCOUNT)
            .amount(UPDATED_AMOUNT)
            .status(UPDATED_STATUS);

        restTransactionLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTransactionLog.getUniqueTransactionId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTransactionLog))
            )
            .andExpect(status().isOk());

        // Validate the TransactionLog in the database
        List<TransactionLog> transactionLogList = transactionLogRepository.findAll();
        assertThat(transactionLogList).hasSize(databaseSizeBeforeUpdate);
        TransactionLog testTransactionLog = transactionLogList.get(transactionLogList.size() - 1);
        assertThat(testTransactionLog.getTransactionTime()).isEqualTo(UPDATED_TRANSACTION_TIME);
        assertThat(testTransactionLog.getDebtorAccount()).isEqualTo(UPDATED_DEBTOR_ACCOUNT);
        assertThat(testTransactionLog.getCreditorAccount()).isEqualTo(UPDATED_CREDITOR_ACCOUNT);
        assertThat(testTransactionLog.getAmount()).isEqualByComparingTo(UPDATED_AMOUNT);
        assertThat(testTransactionLog.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void putNonExistingTransactionLog() throws Exception {
        int databaseSizeBeforeUpdate = transactionLogRepository.findAll().size();
        transactionLog.setUniqueTransactionId(UUID.randomUUID());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transactionLog.getUniqueTransactionId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionLog))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionLog in the database
        List<TransactionLog> transactionLogList = transactionLogRepository.findAll();
        assertThat(transactionLogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTransactionLog() throws Exception {
        int databaseSizeBeforeUpdate = transactionLogRepository.findAll().size();
        transactionLog.setUniqueTransactionId(UUID.randomUUID());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionLog))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionLog in the database
        List<TransactionLog> transactionLogList = transactionLogRepository.findAll();
        assertThat(transactionLogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTransactionLog() throws Exception {
        int databaseSizeBeforeUpdate = transactionLogRepository.findAll().size();
        transactionLog.setUniqueTransactionId(UUID.randomUUID());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionLogMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionLog)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TransactionLog in the database
        List<TransactionLog> transactionLogList = transactionLogRepository.findAll();
        assertThat(transactionLogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTransactionLogWithPatch() throws Exception {
        // Initialize the database
        transactionLogRepository.saveAndFlush(transactionLog);

        int databaseSizeBeforeUpdate = transactionLogRepository.findAll().size();

        // Update the transactionLog using partial update
        TransactionLog partialUpdatedTransactionLog = new TransactionLog();
        partialUpdatedTransactionLog.setUniqueTransactionId(transactionLog.getUniqueTransactionId());

        partialUpdatedTransactionLog
            .transactionTime(UPDATED_TRANSACTION_TIME)
            .creditorAccount(UPDATED_CREDITOR_ACCOUNT)
            .status(UPDATED_STATUS);

        restTransactionLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransactionLog.getUniqueTransactionId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransactionLog))
            )
            .andExpect(status().isOk());

        // Validate the TransactionLog in the database
        List<TransactionLog> transactionLogList = transactionLogRepository.findAll();
        assertThat(transactionLogList).hasSize(databaseSizeBeforeUpdate);
        TransactionLog testTransactionLog = transactionLogList.get(transactionLogList.size() - 1);
        assertThat(testTransactionLog.getTransactionTime()).isEqualTo(UPDATED_TRANSACTION_TIME);
        assertThat(testTransactionLog.getDebtorAccount()).isEqualTo(DEFAULT_DEBTOR_ACCOUNT);
        assertThat(testTransactionLog.getCreditorAccount()).isEqualTo(UPDATED_CREDITOR_ACCOUNT);
        assertThat(testTransactionLog.getAmount()).isEqualByComparingTo(DEFAULT_AMOUNT);
        assertThat(testTransactionLog.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateTransactionLogWithPatch() throws Exception {
        // Initialize the database
        transactionLogRepository.saveAndFlush(transactionLog);

        int databaseSizeBeforeUpdate = transactionLogRepository.findAll().size();

        // Update the transactionLog using partial update
        TransactionLog partialUpdatedTransactionLog = new TransactionLog();
        partialUpdatedTransactionLog.setUniqueTransactionId(transactionLog.getUniqueTransactionId());

        partialUpdatedTransactionLog
            .transactionTime(UPDATED_TRANSACTION_TIME)
            .debtorAccount(UPDATED_DEBTOR_ACCOUNT)
            .creditorAccount(UPDATED_CREDITOR_ACCOUNT)
            .amount(UPDATED_AMOUNT)
            .status(UPDATED_STATUS);

        restTransactionLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransactionLog.getUniqueTransactionId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransactionLog))
            )
            .andExpect(status().isOk());

        // Validate the TransactionLog in the database
        List<TransactionLog> transactionLogList = transactionLogRepository.findAll();
        assertThat(transactionLogList).hasSize(databaseSizeBeforeUpdate);
        TransactionLog testTransactionLog = transactionLogList.get(transactionLogList.size() - 1);
        assertThat(testTransactionLog.getTransactionTime()).isEqualTo(UPDATED_TRANSACTION_TIME);
        assertThat(testTransactionLog.getDebtorAccount()).isEqualTo(UPDATED_DEBTOR_ACCOUNT);
        assertThat(testTransactionLog.getCreditorAccount()).isEqualTo(UPDATED_CREDITOR_ACCOUNT);
        assertThat(testTransactionLog.getAmount()).isEqualByComparingTo(UPDATED_AMOUNT);
        assertThat(testTransactionLog.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingTransactionLog() throws Exception {
        int databaseSizeBeforeUpdate = transactionLogRepository.findAll().size();
        transactionLog.setUniqueTransactionId(UUID.randomUUID());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, transactionLog.getUniqueTransactionId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transactionLog))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionLog in the database
        List<TransactionLog> transactionLogList = transactionLogRepository.findAll();
        assertThat(transactionLogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTransactionLog() throws Exception {
        int databaseSizeBeforeUpdate = transactionLogRepository.findAll().size();
        transactionLog.setUniqueTransactionId(UUID.randomUUID());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transactionLog))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionLog in the database
        List<TransactionLog> transactionLogList = transactionLogRepository.findAll();
        assertThat(transactionLogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTransactionLog() throws Exception {
        int databaseSizeBeforeUpdate = transactionLogRepository.findAll().size();
        transactionLog.setUniqueTransactionId(UUID.randomUUID());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionLogMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(transactionLog))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TransactionLog in the database
        List<TransactionLog> transactionLogList = transactionLogRepository.findAll();
        assertThat(transactionLogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTransactionLog() throws Exception {
        // Initialize the database
        transactionLogRepository.saveAndFlush(transactionLog);

        int databaseSizeBeforeDelete = transactionLogRepository.findAll().size();

        // Delete the transactionLog
        restTransactionLogMockMvc
            .perform(delete(ENTITY_API_URL_ID, transactionLog.getUniqueTransactionId().toString()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TransactionLog> transactionLogList = transactionLogRepository.findAll();
        assertThat(transactionLogList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
