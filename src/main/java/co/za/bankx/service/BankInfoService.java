package co.za.bankx.service;

import co.za.bankx.domain.BankInfo;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link co.za.bankx.domain.BankInfo}.
 */
public interface BankInfoService {
    /**
     * Save a bankInfo.
     *
     * @param bankInfo the entity to save.
     * @return the persisted entity.
     */
    BankInfo save(BankInfo bankInfo);

    /**
     * Updates a bankInfo.
     *
     * @param bankInfo the entity to update.
     * @return the persisted entity.
     */
    BankInfo update(BankInfo bankInfo);

    /**
     * Partially updates a bankInfo.
     *
     * @param bankInfo the entity to update partially.
     * @return the persisted entity.
     */
    Optional<BankInfo> partialUpdate(BankInfo bankInfo);

    /**
     * Get all the bankInfos.
     *
     * @return the list of entities.
     */
    List<BankInfo> findAll();

    /**
     * Get the "id" bankInfo.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<BankInfo> findOne(Long id);

    /**
     * Delete the "id" bankInfo.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
