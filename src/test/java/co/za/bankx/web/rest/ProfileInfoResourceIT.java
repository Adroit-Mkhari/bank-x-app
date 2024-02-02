package co.za.bankx.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import co.za.bankx.IntegrationTest;
import co.za.bankx.domain.ProfileInfo;
import co.za.bankx.repository.ProfileInfoRepository;
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
 * Integration tests for the {@link ProfileInfoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProfileInfoResourceIT {

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    private static final String ENTITY_API_URL = "/api/profile-infos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{profileNumber}";

    @Autowired
    private ProfileInfoRepository profileInfoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProfileInfoMockMvc;

    private ProfileInfo profileInfo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProfileInfo createEntity(EntityManager em) {
        ProfileInfo profileInfo = new ProfileInfo().userId(DEFAULT_USER_ID);
        return profileInfo;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProfileInfo createUpdatedEntity(EntityManager em) {
        ProfileInfo profileInfo = new ProfileInfo().userId(UPDATED_USER_ID);
        return profileInfo;
    }

    @BeforeEach
    public void initTest() {
        profileInfo = createEntity(em);
    }

    @Test
    @Transactional
    void createProfileInfo() throws Exception {
        int databaseSizeBeforeCreate = profileInfoRepository.findAll().size();
        // Create the ProfileInfo
        restProfileInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(profileInfo)))
            .andExpect(status().isCreated());

        // Validate the ProfileInfo in the database
        List<ProfileInfo> profileInfoList = profileInfoRepository.findAll();
        assertThat(profileInfoList).hasSize(databaseSizeBeforeCreate + 1);
        ProfileInfo testProfileInfo = profileInfoList.get(profileInfoList.size() - 1);
        assertThat(testProfileInfo.getUserId()).isEqualTo(DEFAULT_USER_ID);
    }

    @Test
    @Transactional
    void createProfileInfoWithExistingId() throws Exception {
        // Create the ProfileInfo with an existing ID
        profileInfo.setProfileNumber("existing_id");

        int databaseSizeBeforeCreate = profileInfoRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProfileInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(profileInfo)))
            .andExpect(status().isBadRequest());

        // Validate the ProfileInfo in the database
        List<ProfileInfo> profileInfoList = profileInfoRepository.findAll();
        assertThat(profileInfoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkUserIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = profileInfoRepository.findAll().size();
        // set the field null
        profileInfo.setUserId(null);

        // Create the ProfileInfo, which fails.

        restProfileInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(profileInfo)))
            .andExpect(status().isBadRequest());

        List<ProfileInfo> profileInfoList = profileInfoRepository.findAll();
        assertThat(profileInfoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProfileInfos() throws Exception {
        // Initialize the database
        profileInfo.setProfileNumber(UUID.randomUUID().toString());
        profileInfoRepository.saveAndFlush(profileInfo);

        // Get all the profileInfoList
        restProfileInfoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=profileNumber,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].profileNumber").value(hasItem(profileInfo.getProfileNumber())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())));
    }

    @Test
    @Transactional
    void getProfileInfo() throws Exception {
        // Initialize the database
        profileInfo.setProfileNumber(UUID.randomUUID().toString());
        profileInfoRepository.saveAndFlush(profileInfo);

        // Get the profileInfo
        restProfileInfoMockMvc
            .perform(get(ENTITY_API_URL_ID, profileInfo.getProfileNumber()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.profileNumber").value(profileInfo.getProfileNumber()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.intValue()));
    }

    @Test
    @Transactional
    void getNonExistingProfileInfo() throws Exception {
        // Get the profileInfo
        restProfileInfoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProfileInfo() throws Exception {
        // Initialize the database
        profileInfo.setProfileNumber(UUID.randomUUID().toString());
        profileInfoRepository.saveAndFlush(profileInfo);

        int databaseSizeBeforeUpdate = profileInfoRepository.findAll().size();

        // Update the profileInfo
        ProfileInfo updatedProfileInfo = profileInfoRepository.findById(profileInfo.getProfileNumber()).orElseThrow();
        // Disconnect from session so that the updates on updatedProfileInfo are not directly saved in db
        em.detach(updatedProfileInfo);
        updatedProfileInfo.userId(UPDATED_USER_ID);

        restProfileInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedProfileInfo.getProfileNumber())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedProfileInfo))
            )
            .andExpect(status().isOk());

        // Validate the ProfileInfo in the database
        List<ProfileInfo> profileInfoList = profileInfoRepository.findAll();
        assertThat(profileInfoList).hasSize(databaseSizeBeforeUpdate);
        ProfileInfo testProfileInfo = profileInfoList.get(profileInfoList.size() - 1);
        assertThat(testProfileInfo.getUserId()).isEqualTo(UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void putNonExistingProfileInfo() throws Exception {
        int databaseSizeBeforeUpdate = profileInfoRepository.findAll().size();
        profileInfo.setProfileNumber(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProfileInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, profileInfo.getProfileNumber())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(profileInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProfileInfo in the database
        List<ProfileInfo> profileInfoList = profileInfoRepository.findAll();
        assertThat(profileInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProfileInfo() throws Exception {
        int databaseSizeBeforeUpdate = profileInfoRepository.findAll().size();
        profileInfo.setProfileNumber(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfileInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(profileInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProfileInfo in the database
        List<ProfileInfo> profileInfoList = profileInfoRepository.findAll();
        assertThat(profileInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProfileInfo() throws Exception {
        int databaseSizeBeforeUpdate = profileInfoRepository.findAll().size();
        profileInfo.setProfileNumber(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfileInfoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(profileInfo)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProfileInfo in the database
        List<ProfileInfo> profileInfoList = profileInfoRepository.findAll();
        assertThat(profileInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProfileInfoWithPatch() throws Exception {
        // Initialize the database
        profileInfo.setProfileNumber(UUID.randomUUID().toString());
        profileInfoRepository.saveAndFlush(profileInfo);

        int databaseSizeBeforeUpdate = profileInfoRepository.findAll().size();

        // Update the profileInfo using partial update
        ProfileInfo partialUpdatedProfileInfo = new ProfileInfo();
        partialUpdatedProfileInfo.setProfileNumber(profileInfo.getProfileNumber());

        partialUpdatedProfileInfo.userId(UPDATED_USER_ID);

        restProfileInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProfileInfo.getProfileNumber())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProfileInfo))
            )
            .andExpect(status().isOk());

        // Validate the ProfileInfo in the database
        List<ProfileInfo> profileInfoList = profileInfoRepository.findAll();
        assertThat(profileInfoList).hasSize(databaseSizeBeforeUpdate);
        ProfileInfo testProfileInfo = profileInfoList.get(profileInfoList.size() - 1);
        assertThat(testProfileInfo.getUserId()).isEqualTo(UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void fullUpdateProfileInfoWithPatch() throws Exception {
        // Initialize the database
        profileInfo.setProfileNumber(UUID.randomUUID().toString());
        profileInfoRepository.saveAndFlush(profileInfo);

        int databaseSizeBeforeUpdate = profileInfoRepository.findAll().size();

        // Update the profileInfo using partial update
        ProfileInfo partialUpdatedProfileInfo = new ProfileInfo();
        partialUpdatedProfileInfo.setProfileNumber(profileInfo.getProfileNumber());

        partialUpdatedProfileInfo.userId(UPDATED_USER_ID);

        restProfileInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProfileInfo.getProfileNumber())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProfileInfo))
            )
            .andExpect(status().isOk());

        // Validate the ProfileInfo in the database
        List<ProfileInfo> profileInfoList = profileInfoRepository.findAll();
        assertThat(profileInfoList).hasSize(databaseSizeBeforeUpdate);
        ProfileInfo testProfileInfo = profileInfoList.get(profileInfoList.size() - 1);
        assertThat(testProfileInfo.getUserId()).isEqualTo(UPDATED_USER_ID);
    }

    @Test
    @Transactional
    void patchNonExistingProfileInfo() throws Exception {
        int databaseSizeBeforeUpdate = profileInfoRepository.findAll().size();
        profileInfo.setProfileNumber(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProfileInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, profileInfo.getProfileNumber())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(profileInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProfileInfo in the database
        List<ProfileInfo> profileInfoList = profileInfoRepository.findAll();
        assertThat(profileInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProfileInfo() throws Exception {
        int databaseSizeBeforeUpdate = profileInfoRepository.findAll().size();
        profileInfo.setProfileNumber(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfileInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(profileInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProfileInfo in the database
        List<ProfileInfo> profileInfoList = profileInfoRepository.findAll();
        assertThat(profileInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProfileInfo() throws Exception {
        int databaseSizeBeforeUpdate = profileInfoRepository.findAll().size();
        profileInfo.setProfileNumber(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfileInfoMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(profileInfo))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProfileInfo in the database
        List<ProfileInfo> profileInfoList = profileInfoRepository.findAll();
        assertThat(profileInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProfileInfo() throws Exception {
        // Initialize the database
        profileInfo.setProfileNumber(UUID.randomUUID().toString());
        profileInfoRepository.saveAndFlush(profileInfo);

        int databaseSizeBeforeDelete = profileInfoRepository.findAll().size();

        // Delete the profileInfo
        restProfileInfoMockMvc
            .perform(delete(ENTITY_API_URL_ID, profileInfo.getProfileNumber()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ProfileInfo> profileInfoList = profileInfoRepository.findAll();
        assertThat(profileInfoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
