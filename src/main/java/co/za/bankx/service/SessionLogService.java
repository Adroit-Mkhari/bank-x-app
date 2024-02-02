package co.za.bankx.service;

import co.za.bankx.domain.SessionLog;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link co.za.bankx.domain.SessionLog}.
 */
public interface SessionLogService {
    /**
     * Save a sessionLog.
     *
     * @param sessionLog the entity to save.
     * @return the persisted entity.
     */
    SessionLog save(SessionLog sessionLog);

    /**
     * Updates a sessionLog.
     *
     * @param sessionLog the entity to update.
     * @return the persisted entity.
     */
    SessionLog update(SessionLog sessionLog);

    /**
     * Partially updates a sessionLog.
     *
     * @param sessionLog the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SessionLog> partialUpdate(SessionLog sessionLog);

    /**
     * Get all the sessionLogs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SessionLog> findAll(Pageable pageable);

    /**
     * Get the "id" sessionLog.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SessionLog> findOne(Long id);

    /**
     * Delete the "id" sessionLog.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
