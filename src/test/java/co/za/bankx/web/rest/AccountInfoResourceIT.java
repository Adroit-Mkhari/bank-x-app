package co.za.bankx.web.rest;

import static co.za.bankx.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import co.za.bankx.IntegrationTest;
import co.za.bankx.domain.AccountInfo;
import co.za.bankx.domain.enumeration.AccountStatus;
import co.za.bankx.domain.enumeration.AccountType;
import co.za.bankx.repository.AccountInfoRepository;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link AccountInfoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AccountInfoResourceIT {

    private static final String DEFAULT_ACCOUNT_NUMBER = "9390856098";
    private static final String UPDATED_ACCOUNT_NUMBER = "62714262916228";

    private static final AccountType DEFAULT_ACCOUNT_TYPE = AccountType.SAVINGS;
    private static final AccountType UPDATED_ACCOUNT_TYPE = AccountType.CURRENT;

    private static final AccountStatus DEFAULT_ACCOUNT_STATUS = AccountStatus.ACTIVE;
    private static final AccountStatus UPDATED_ACCOUNT_STATUS = AccountStatus.CLOSED;

    private static final BigDecimal DEFAULT_ACCOUNT_BALANCE = new BigDecimal(1);
    private static final BigDecimal UPDATED_ACCOUNT_BALANCE = new BigDecimal(2);

    private static final String ENTITY_API_URL = "/api/account-infos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AccountInfoRepository accountInfoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAccountInfoMockMvc;

    private AccountInfo accountInfo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AccountInfo createEntity(EntityManager em) {
        AccountInfo accountInfo = new AccountInfo()
            .accountNumber(DEFAULT_ACCOUNT_NUMBER)
            .accountType(DEFAULT_ACCOUNT_TYPE)
            .accountStatus(DEFAULT_ACCOUNT_STATUS)
            .accountBalance(DEFAULT_ACCOUNT_BALANCE);
        return accountInfo;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AccountInfo createUpdatedEntity(EntityManager em) {
        AccountInfo accountInfo = new AccountInfo()
            .accountNumber(UPDATED_ACCOUNT_NUMBER)
            .accountType(UPDATED_ACCOUNT_TYPE)
            .accountStatus(UPDATED_ACCOUNT_STATUS)
            .accountBalance(UPDATED_ACCOUNT_BALANCE);
        return accountInfo;
    }

    @BeforeEach
    public void initTest() {
        accountInfo = createEntity(em);
    }

    @Test
    @Transactional
    void createAccountInfo() throws Exception {
        int databaseSizeBeforeCreate = accountInfoRepository.findAll().size();
        // Create the AccountInfo
        restAccountInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(accountInfo)))
            .andExpect(status().isCreated());

        // Validate the AccountInfo in the database
        List<AccountInfo> accountInfoList = accountInfoRepository.findAll();
        assertThat(accountInfoList).hasSize(databaseSizeBeforeCreate + 1);
        AccountInfo testAccountInfo = accountInfoList.get(accountInfoList.size() - 1);
        assertThat(testAccountInfo.getAccountNumber()).isEqualTo(DEFAULT_ACCOUNT_NUMBER);
        assertThat(testAccountInfo.getAccountType()).isEqualTo(DEFAULT_ACCOUNT_TYPE);
        assertThat(testAccountInfo.getAccountStatus()).isEqualTo(DEFAULT_ACCOUNT_STATUS);
        assertThat(testAccountInfo.getAccountBalance()).isEqualByComparingTo(DEFAULT_ACCOUNT_BALANCE);
    }

    @Test
    @Transactional
    void createAccountInfoWithExistingId() throws Exception {
        // Create the AccountInfo with an existing ID
        accountInfo.setId(1L);

        int databaseSizeBeforeCreate = accountInfoRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAccountInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(accountInfo)))
            .andExpect(status().isBadRequest());

        // Validate the AccountInfo in the database
        List<AccountInfo> accountInfoList = accountInfoRepository.findAll();
        assertThat(accountInfoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkAccountNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = accountInfoRepository.findAll().size();
        // set the field null
        accountInfo.setAccountNumber(null);

        // Create the AccountInfo, which fails.

        restAccountInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(accountInfo)))
            .andExpect(status().isBadRequest());

        List<AccountInfo> accountInfoList = accountInfoRepository.findAll();
        assertThat(accountInfoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAccountTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = accountInfoRepository.findAll().size();
        // set the field null
        accountInfo.setAccountType(null);

        // Create the AccountInfo, which fails.

        restAccountInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(accountInfo)))
            .andExpect(status().isBadRequest());

        List<AccountInfo> accountInfoList = accountInfoRepository.findAll();
        assertThat(accountInfoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAccountStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = accountInfoRepository.findAll().size();
        // set the field null
        accountInfo.setAccountStatus(null);

        // Create the AccountInfo, which fails.

        restAccountInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(accountInfo)))
            .andExpect(status().isBadRequest());

        List<AccountInfo> accountInfoList = accountInfoRepository.findAll();
        assertThat(accountInfoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAccountInfos() throws Exception {
        // Initialize the database
        accountInfoRepository.saveAndFlush(accountInfo);

        // Get all the accountInfoList
        restAccountInfoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(accountInfo.getId().intValue())))
            .andExpect(jsonPath("$.[*].accountNumber").value(hasItem(DEFAULT_ACCOUNT_NUMBER)))
            .andExpect(jsonPath("$.[*].accountType").value(hasItem(DEFAULT_ACCOUNT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].accountStatus").value(hasItem(DEFAULT_ACCOUNT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].accountBalance").value(hasItem(sameNumber(DEFAULT_ACCOUNT_BALANCE))));
    }

    @Test
    @Transactional
    void getAccountInfo() throws Exception {
        // Initialize the database
        accountInfoRepository.saveAndFlush(accountInfo);

        // Get the accountInfo
        restAccountInfoMockMvc
            .perform(get(ENTITY_API_URL_ID, accountInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(accountInfo.getId().intValue()))
            .andExpect(jsonPath("$.accountNumber").value(DEFAULT_ACCOUNT_NUMBER))
            .andExpect(jsonPath("$.accountType").value(DEFAULT_ACCOUNT_TYPE.toString()))
            .andExpect(jsonPath("$.accountStatus").value(DEFAULT_ACCOUNT_STATUS.toString()))
            .andExpect(jsonPath("$.accountBalance").value(sameNumber(DEFAULT_ACCOUNT_BALANCE)));
    }

    @Test
    @Transactional
    void getNonExistingAccountInfo() throws Exception {
        // Get the accountInfo
        restAccountInfoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAccountInfo() throws Exception {
        // Initialize the database
        accountInfoRepository.saveAndFlush(accountInfo);

        int databaseSizeBeforeUpdate = accountInfoRepository.findAll().size();

        // Update the accountInfo
        AccountInfo updatedAccountInfo = accountInfoRepository.findById(accountInfo.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAccountInfo are not directly saved in db
        em.detach(updatedAccountInfo);
        updatedAccountInfo
            .accountNumber(UPDATED_ACCOUNT_NUMBER)
            .accountType(UPDATED_ACCOUNT_TYPE)
            .accountStatus(UPDATED_ACCOUNT_STATUS)
            .accountBalance(UPDATED_ACCOUNT_BALANCE);

        restAccountInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAccountInfo.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAccountInfo))
            )
            .andExpect(status().isOk());

        // Validate the AccountInfo in the database
        List<AccountInfo> accountInfoList = accountInfoRepository.findAll();
        assertThat(accountInfoList).hasSize(databaseSizeBeforeUpdate);
        AccountInfo testAccountInfo = accountInfoList.get(accountInfoList.size() - 1);
        assertThat(testAccountInfo.getAccountNumber()).isEqualTo(UPDATED_ACCOUNT_NUMBER);
        assertThat(testAccountInfo.getAccountType()).isEqualTo(UPDATED_ACCOUNT_TYPE);
        assertThat(testAccountInfo.getAccountStatus()).isEqualTo(UPDATED_ACCOUNT_STATUS);
        assertThat(testAccountInfo.getAccountBalance()).isEqualByComparingTo(UPDATED_ACCOUNT_BALANCE);
    }

    @Test
    @Transactional
    void putNonExistingAccountInfo() throws Exception {
        int databaseSizeBeforeUpdate = accountInfoRepository.findAll().size();
        accountInfo.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAccountInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, accountInfo.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(accountInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the AccountInfo in the database
        List<AccountInfo> accountInfoList = accountInfoRepository.findAll();
        assertThat(accountInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAccountInfo() throws Exception {
        int databaseSizeBeforeUpdate = accountInfoRepository.findAll().size();
        accountInfo.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAccountInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(accountInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the AccountInfo in the database
        List<AccountInfo> accountInfoList = accountInfoRepository.findAll();
        assertThat(accountInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAccountInfo() throws Exception {
        int databaseSizeBeforeUpdate = accountInfoRepository.findAll().size();
        accountInfo.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAccountInfoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(accountInfo)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AccountInfo in the database
        List<AccountInfo> accountInfoList = accountInfoRepository.findAll();
        assertThat(accountInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAccountInfoWithPatch() throws Exception {
        // Initialize the database
        accountInfoRepository.saveAndFlush(accountInfo);

        int databaseSizeBeforeUpdate = accountInfoRepository.findAll().size();

        // Update the accountInfo using partial update
        AccountInfo partialUpdatedAccountInfo = new AccountInfo();
        partialUpdatedAccountInfo.setId(accountInfo.getId());

        partialUpdatedAccountInfo.accountNumber(UPDATED_ACCOUNT_NUMBER).accountStatus(UPDATED_ACCOUNT_STATUS);

        restAccountInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAccountInfo.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAccountInfo))
            )
            .andExpect(status().isOk());

        // Validate the AccountInfo in the database
        List<AccountInfo> accountInfoList = accountInfoRepository.findAll();
        assertThat(accountInfoList).hasSize(databaseSizeBeforeUpdate);
        AccountInfo testAccountInfo = accountInfoList.get(accountInfoList.size() - 1);
        assertThat(testAccountInfo.getAccountNumber()).isEqualTo(UPDATED_ACCOUNT_NUMBER);
        assertThat(testAccountInfo.getAccountType()).isEqualTo(DEFAULT_ACCOUNT_TYPE);
        assertThat(testAccountInfo.getAccountStatus()).isEqualTo(UPDATED_ACCOUNT_STATUS);
        assertThat(testAccountInfo.getAccountBalance()).isEqualByComparingTo(DEFAULT_ACCOUNT_BALANCE);
    }

    @Test
    @Transactional
    void fullUpdateAccountInfoWithPatch() throws Exception {
        // Initialize the database
        accountInfoRepository.saveAndFlush(accountInfo);

        int databaseSizeBeforeUpdate = accountInfoRepository.findAll().size();

        // Update the accountInfo using partial update
        AccountInfo partialUpdatedAccountInfo = new AccountInfo();
        partialUpdatedAccountInfo.setId(accountInfo.getId());

        partialUpdatedAccountInfo
            .accountNumber(UPDATED_ACCOUNT_NUMBER)
            .accountType(UPDATED_ACCOUNT_TYPE)
            .accountStatus(UPDATED_ACCOUNT_STATUS)
            .accountBalance(UPDATED_ACCOUNT_BALANCE);

        restAccountInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAccountInfo.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAccountInfo))
            )
            .andExpect(status().isOk());

        // Validate the AccountInfo in the database
        List<AccountInfo> accountInfoList = accountInfoRepository.findAll();
        assertThat(accountInfoList).hasSize(databaseSizeBeforeUpdate);
        AccountInfo testAccountInfo = accountInfoList.get(accountInfoList.size() - 1);
        assertThat(testAccountInfo.getAccountNumber()).isEqualTo(UPDATED_ACCOUNT_NUMBER);
        assertThat(testAccountInfo.getAccountType()).isEqualTo(UPDATED_ACCOUNT_TYPE);
        assertThat(testAccountInfo.getAccountStatus()).isEqualTo(UPDATED_ACCOUNT_STATUS);
        assertThat(testAccountInfo.getAccountBalance()).isEqualByComparingTo(UPDATED_ACCOUNT_BALANCE);
    }

    @Test
    @Transactional
    void patchNonExistingAccountInfo() throws Exception {
        int databaseSizeBeforeUpdate = accountInfoRepository.findAll().size();
        accountInfo.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAccountInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, accountInfo.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(accountInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the AccountInfo in the database
        List<AccountInfo> accountInfoList = accountInfoRepository.findAll();
        assertThat(accountInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAccountInfo() throws Exception {
        int databaseSizeBeforeUpdate = accountInfoRepository.findAll().size();
        accountInfo.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAccountInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(accountInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the AccountInfo in the database
        List<AccountInfo> accountInfoList = accountInfoRepository.findAll();
        assertThat(accountInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAccountInfo() throws Exception {
        int databaseSizeBeforeUpdate = accountInfoRepository.findAll().size();
        accountInfo.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAccountInfoMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(accountInfo))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AccountInfo in the database
        List<AccountInfo> accountInfoList = accountInfoRepository.findAll();
        assertThat(accountInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAccountInfo() throws Exception {
        // Initialize the database
        accountInfoRepository.saveAndFlush(accountInfo);

        int databaseSizeBeforeDelete = accountInfoRepository.findAll().size();

        // Delete the accountInfo
        restAccountInfoMockMvc
            .perform(delete(ENTITY_API_URL_ID, accountInfo.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<AccountInfo> accountInfoList = accountInfoRepository.findAll();
        assertThat(accountInfoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
