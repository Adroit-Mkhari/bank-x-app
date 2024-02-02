package co.za.bankx.service;

import co.za.bankx.domain.AccountInfo;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link co.za.bankx.domain.AccountInfo}.
 */
public interface AccountInfoService {
    /**
     * Save a accountInfo.
     *
     * @param accountInfo the entity to save.
     * @return the persisted entity.
     */
    AccountInfo save(AccountInfo accountInfo);

    /**
     * Updates a accountInfo.
     *
     * @param accountInfo the entity to update.
     * @return the persisted entity.
     */
    AccountInfo update(AccountInfo accountInfo);

    /**
     * Partially updates a accountInfo.
     *
     * @param accountInfo the entity to update partially.
     * @return the persisted entity.
     */
    Optional<AccountInfo> partialUpdate(AccountInfo accountInfo);

    /**
     * Get all the accountInfos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AccountInfo> findAll(Pageable pageable);

    /**
     * Get the "id" accountInfo.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AccountInfo> findOne(Long id);

    /**
     * Delete the "id" accountInfo.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
