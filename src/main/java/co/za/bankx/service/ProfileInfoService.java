package co.za.bankx.service;

import co.za.bankx.domain.ProfileInfo;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link co.za.bankx.domain.ProfileInfo}.
 */
public interface ProfileInfoService {
    /**
     * Save a profileInfo.
     *
     * @param profileInfo the entity to save.
     * @return the persisted entity.
     */
    ProfileInfo save(ProfileInfo profileInfo);

    /**
     * Updates a profileInfo.
     *
     * @param profileInfo the entity to update.
     * @return the persisted entity.
     */
    ProfileInfo update(ProfileInfo profileInfo);

    /**
     * Partially updates a profileInfo.
     *
     * @param profileInfo the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ProfileInfo> partialUpdate(ProfileInfo profileInfo);

    /**
     * Get all the profileInfos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProfileInfo> findAll(Pageable pageable);

    /**
     * Get the "id" profileInfo.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ProfileInfo> findOne(String id);

    /**
     * Delete the "id" profileInfo.
     *
     * @param id the id of the entity.
     */
    void delete(String id);
}
