package co.za.bankx.service;

import co.za.bankx.domain.ClientInfo;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link co.za.bankx.domain.ClientInfo}.
 */
public interface ClientInfoService {
    /**
     * Save a clientInfo.
     *
     * @param clientInfo the entity to save.
     * @return the persisted entity.
     */
    ClientInfo save(ClientInfo clientInfo);

    /**
     * Updates a clientInfo.
     *
     * @param clientInfo the entity to update.
     * @return the persisted entity.
     */
    ClientInfo update(ClientInfo clientInfo);

    /**
     * Partially updates a clientInfo.
     *
     * @param clientInfo the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ClientInfo> partialUpdate(ClientInfo clientInfo);

    /**
     * Get all the clientInfos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ClientInfo> findAll(Pageable pageable);

    /**
     * Get the "id" clientInfo.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ClientInfo> findOne(String id);

    /**
     * Delete the "id" clientInfo.
     *
     * @param id the id of the entity.
     */
    void delete(String id);
}
