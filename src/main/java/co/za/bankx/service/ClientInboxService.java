package co.za.bankx.service;

import co.za.bankx.domain.ClientInbox;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link co.za.bankx.domain.ClientInbox}.
 */
public interface ClientInboxService {
    /**
     * Save a clientInbox.
     *
     * @param clientInbox the entity to save.
     * @return the persisted entity.
     */
    ClientInbox save(ClientInbox clientInbox);

    /**
     * Updates a clientInbox.
     *
     * @param clientInbox the entity to update.
     * @return the persisted entity.
     */
    ClientInbox update(ClientInbox clientInbox);

    /**
     * Partially updates a clientInbox.
     *
     * @param clientInbox the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ClientInbox> partialUpdate(ClientInbox clientInbox);

    /**
     * Get all the clientInboxes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ClientInbox> findAll(Pageable pageable);

    /**
     * Get the "id" clientInbox.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ClientInbox> findOne(Long id);

    /**
     * Delete the "id" clientInbox.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
