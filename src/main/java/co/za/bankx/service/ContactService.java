package co.za.bankx.service;

import co.za.bankx.domain.Contact;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link co.za.bankx.domain.Contact}.
 */
public interface ContactService {
    /**
     * Save a contact.
     *
     * @param contact the entity to save.
     * @return the persisted entity.
     */
    Contact save(Contact contact);

    /**
     * Updates a contact.
     *
     * @param contact the entity to update.
     * @return the persisted entity.
     */
    Contact update(Contact contact);

    /**
     * Partially updates a contact.
     *
     * @param contact the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Contact> partialUpdate(Contact contact);

    /**
     * Get all the contacts.
     *
     * @return the list of entities.
     */
    List<Contact> findAll();

    /**
     * Get all the Contact where ClientInfo is {@code null}.
     *
     * @return the {@link List} of entities.
     */
    List<Contact> findAllWhereClientInfoIsNull();

    /**
     * Get the "id" contact.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Contact> findOne(Long id);

    /**
     * Delete the "id" contact.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
