package co.za.bankx.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import co.za.bankx.IntegrationTest;
import co.za.bankx.domain.BankInfo;
import co.za.bankx.repository.BankInfoRepository;
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
 * Integration tests for the {@link BankInfoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BankInfoResourceIT {

    private static final String DEFAULT_SWIFT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_SWIFT_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_BANK_NAME = "AAAAAAAAAA";
    private static final String UPDATED_BANK_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/bank-infos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BankInfoRepository bankInfoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBankInfoMockMvc;

    private BankInfo bankInfo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BankInfo createEntity(EntityManager em) {
        BankInfo bankInfo = new BankInfo().swiftCode(DEFAULT_SWIFT_CODE).bankName(DEFAULT_BANK_NAME);
        return bankInfo;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BankInfo createUpdatedEntity(EntityManager em) {
        BankInfo bankInfo = new BankInfo().swiftCode(UPDATED_SWIFT_CODE).bankName(UPDATED_BANK_NAME);
        return bankInfo;
    }

    @BeforeEach
    public void initTest() {
        bankInfo = createEntity(em);
    }

    @Test
    @Transactional
    void createBankInfo() throws Exception {
        int databaseSizeBeforeCreate = bankInfoRepository.findAll().size();
        // Create the BankInfo
        restBankInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bankInfo)))
            .andExpect(status().isCreated());

        // Validate the BankInfo in the database
        List<BankInfo> bankInfoList = bankInfoRepository.findAll();
        assertThat(bankInfoList).hasSize(databaseSizeBeforeCreate + 1);
        BankInfo testBankInfo = bankInfoList.get(bankInfoList.size() - 1);
        assertThat(testBankInfo.getSwiftCode()).isEqualTo(DEFAULT_SWIFT_CODE);
        assertThat(testBankInfo.getBankName()).isEqualTo(DEFAULT_BANK_NAME);
    }

    @Test
    @Transactional
    void createBankInfoWithExistingId() throws Exception {
        // Create the BankInfo with an existing ID
        bankInfo.setId(1L);

        int databaseSizeBeforeCreate = bankInfoRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBankInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bankInfo)))
            .andExpect(status().isBadRequest());

        // Validate the BankInfo in the database
        List<BankInfo> bankInfoList = bankInfoRepository.findAll();
        assertThat(bankInfoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkSwiftCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = bankInfoRepository.findAll().size();
        // set the field null
        bankInfo.setSwiftCode(null);

        // Create the BankInfo, which fails.

        restBankInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bankInfo)))
            .andExpect(status().isBadRequest());

        List<BankInfo> bankInfoList = bankInfoRepository.findAll();
        assertThat(bankInfoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkBankNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = bankInfoRepository.findAll().size();
        // set the field null
        bankInfo.setBankName(null);

        // Create the BankInfo, which fails.

        restBankInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bankInfo)))
            .andExpect(status().isBadRequest());

        List<BankInfo> bankInfoList = bankInfoRepository.findAll();
        assertThat(bankInfoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBankInfos() throws Exception {
        // Initialize the database
        bankInfoRepository.saveAndFlush(bankInfo);

        // Get all the bankInfoList
        restBankInfoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bankInfo.getId().intValue())))
            .andExpect(jsonPath("$.[*].swiftCode").value(hasItem(DEFAULT_SWIFT_CODE)))
            .andExpect(jsonPath("$.[*].bankName").value(hasItem(DEFAULT_BANK_NAME)));
    }

    @Test
    @Transactional
    void getBankInfo() throws Exception {
        // Initialize the database
        bankInfoRepository.saveAndFlush(bankInfo);

        // Get the bankInfo
        restBankInfoMockMvc
            .perform(get(ENTITY_API_URL_ID, bankInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bankInfo.getId().intValue()))
            .andExpect(jsonPath("$.swiftCode").value(DEFAULT_SWIFT_CODE))
            .andExpect(jsonPath("$.bankName").value(DEFAULT_BANK_NAME));
    }

    @Test
    @Transactional
    void getNonExistingBankInfo() throws Exception {
        // Get the bankInfo
        restBankInfoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBankInfo() throws Exception {
        // Initialize the database
        bankInfoRepository.saveAndFlush(bankInfo);

        int databaseSizeBeforeUpdate = bankInfoRepository.findAll().size();

        // Update the bankInfo
        BankInfo updatedBankInfo = bankInfoRepository.findById(bankInfo.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBankInfo are not directly saved in db
        em.detach(updatedBankInfo);
        updatedBankInfo.swiftCode(UPDATED_SWIFT_CODE).bankName(UPDATED_BANK_NAME);

        restBankInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBankInfo.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedBankInfo))
            )
            .andExpect(status().isOk());

        // Validate the BankInfo in the database
        List<BankInfo> bankInfoList = bankInfoRepository.findAll();
        assertThat(bankInfoList).hasSize(databaseSizeBeforeUpdate);
        BankInfo testBankInfo = bankInfoList.get(bankInfoList.size() - 1);
        assertThat(testBankInfo.getSwiftCode()).isEqualTo(UPDATED_SWIFT_CODE);
        assertThat(testBankInfo.getBankName()).isEqualTo(UPDATED_BANK_NAME);
    }

    @Test
    @Transactional
    void putNonExistingBankInfo() throws Exception {
        int databaseSizeBeforeUpdate = bankInfoRepository.findAll().size();
        bankInfo.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBankInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bankInfo.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bankInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the BankInfo in the database
        List<BankInfo> bankInfoList = bankInfoRepository.findAll();
        assertThat(bankInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBankInfo() throws Exception {
        int databaseSizeBeforeUpdate = bankInfoRepository.findAll().size();
        bankInfo.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBankInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bankInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the BankInfo in the database
        List<BankInfo> bankInfoList = bankInfoRepository.findAll();
        assertThat(bankInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBankInfo() throws Exception {
        int databaseSizeBeforeUpdate = bankInfoRepository.findAll().size();
        bankInfo.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBankInfoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bankInfo)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BankInfo in the database
        List<BankInfo> bankInfoList = bankInfoRepository.findAll();
        assertThat(bankInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBankInfoWithPatch() throws Exception {
        // Initialize the database
        bankInfoRepository.saveAndFlush(bankInfo);

        int databaseSizeBeforeUpdate = bankInfoRepository.findAll().size();

        // Update the bankInfo using partial update
        BankInfo partialUpdatedBankInfo = new BankInfo();
        partialUpdatedBankInfo.setId(bankInfo.getId());

        partialUpdatedBankInfo.swiftCode(UPDATED_SWIFT_CODE).bankName(UPDATED_BANK_NAME);

        restBankInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBankInfo.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBankInfo))
            )
            .andExpect(status().isOk());

        // Validate the BankInfo in the database
        List<BankInfo> bankInfoList = bankInfoRepository.findAll();
        assertThat(bankInfoList).hasSize(databaseSizeBeforeUpdate);
        BankInfo testBankInfo = bankInfoList.get(bankInfoList.size() - 1);
        assertThat(testBankInfo.getSwiftCode()).isEqualTo(UPDATED_SWIFT_CODE);
        assertThat(testBankInfo.getBankName()).isEqualTo(UPDATED_BANK_NAME);
    }

    @Test
    @Transactional
    void fullUpdateBankInfoWithPatch() throws Exception {
        // Initialize the database
        bankInfoRepository.saveAndFlush(bankInfo);

        int databaseSizeBeforeUpdate = bankInfoRepository.findAll().size();

        // Update the bankInfo using partial update
        BankInfo partialUpdatedBankInfo = new BankInfo();
        partialUpdatedBankInfo.setId(bankInfo.getId());

        partialUpdatedBankInfo.swiftCode(UPDATED_SWIFT_CODE).bankName(UPDATED_BANK_NAME);

        restBankInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBankInfo.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBankInfo))
            )
            .andExpect(status().isOk());

        // Validate the BankInfo in the database
        List<BankInfo> bankInfoList = bankInfoRepository.findAll();
        assertThat(bankInfoList).hasSize(databaseSizeBeforeUpdate);
        BankInfo testBankInfo = bankInfoList.get(bankInfoList.size() - 1);
        assertThat(testBankInfo.getSwiftCode()).isEqualTo(UPDATED_SWIFT_CODE);
        assertThat(testBankInfo.getBankName()).isEqualTo(UPDATED_BANK_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingBankInfo() throws Exception {
        int databaseSizeBeforeUpdate = bankInfoRepository.findAll().size();
        bankInfo.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBankInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, bankInfo.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bankInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the BankInfo in the database
        List<BankInfo> bankInfoList = bankInfoRepository.findAll();
        assertThat(bankInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBankInfo() throws Exception {
        int databaseSizeBeforeUpdate = bankInfoRepository.findAll().size();
        bankInfo.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBankInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bankInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the BankInfo in the database
        List<BankInfo> bankInfoList = bankInfoRepository.findAll();
        assertThat(bankInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBankInfo() throws Exception {
        int databaseSizeBeforeUpdate = bankInfoRepository.findAll().size();
        bankInfo.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBankInfoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(bankInfo)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BankInfo in the database
        List<BankInfo> bankInfoList = bankInfoRepository.findAll();
        assertThat(bankInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBankInfo() throws Exception {
        // Initialize the database
        bankInfoRepository.saveAndFlush(bankInfo);

        int databaseSizeBeforeDelete = bankInfoRepository.findAll().size();

        // Delete the bankInfo
        restBankInfoMockMvc
            .perform(delete(ENTITY_API_URL_ID, bankInfo.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BankInfo> bankInfoList = bankInfoRepository.findAll();
        assertThat(bankInfoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
