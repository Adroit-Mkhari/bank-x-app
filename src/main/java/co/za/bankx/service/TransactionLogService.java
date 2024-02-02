package co.za.bankx.service;

import co.za.bankx.domain.TransactionLog;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link co.za.bankx.domain.TransactionLog}.
 */
public interface TransactionLogService {
    /**
     * Save a transactionLog.
     *
     * @param transactionLog the entity to save.
     * @return the persisted entity.
     */
    TransactionLog save(TransactionLog transactionLog);

    /**
     * Updates a transactionLog.
     *
     * @param transactionLog the entity to update.
     * @return the persisted entity.
     */
    TransactionLog update(TransactionLog transactionLog);

    /**
     * Partially updates a transactionLog.
     *
     * @param transactionLog the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TransactionLog> partialUpdate(TransactionLog transactionLog);

    /**
     * Get all the transactionLogs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TransactionLog> findAll(Pageable pageable);

    /**
     * Get the "id" transactionLog.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TransactionLog> findOne(UUID id);

    /**
     * Delete the "id" transactionLog.
     *
     * @param id the id of the entity.
     */
    void delete(UUID id);
}
