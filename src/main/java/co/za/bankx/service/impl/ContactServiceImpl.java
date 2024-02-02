package co.za.bankx.service.impl;

import co.za.bankx.domain.Contact;
import co.za.bankx.repository.ContactRepository;
import co.za.bankx.service.ContactService;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link co.za.bankx.domain.Contact}.
 */
@Service
@Transactional
public class ContactServiceImpl implements ContactService {

    private final Logger log = LoggerFactory.getLogger(ContactServiceImpl.class);

    private final ContactRepository contactRepository;

    public ContactServiceImpl(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Override
    public Contact save(Contact contact) {
        log.debug("Request to save Contact : {}", contact);
        return contactRepository.save(contact);
    }

    @Override
    public Contact update(Contact contact) {
        log.debug("Request to update Contact : {}", contact);
        return contactRepository.save(contact);
    }

    @Override
    public Optional<Contact> partialUpdate(Contact contact) {
        log.debug("Request to partially update Contact : {}", contact);

        return contactRepository
            .findById(contact.getId())
            .map(existingContact -> {
                if (contact.getStreetAddress() != null) {
                    existingContact.setStreetAddress(contact.getStreetAddress());
                }
                if (contact.getPostalCode() != null) {
                    existingContact.setPostalCode(contact.getPostalCode());
                }
                if (contact.getCity() != null) {
                    existingContact.setCity(contact.getCity());
                }
                if (contact.getStateProvince() != null) {
                    existingContact.setStateProvince(contact.getStateProvince());
                }
                if (contact.getEmail() != null) {
                    existingContact.setEmail(contact.getEmail());
                }
                if (contact.getPhoneNumber() != null) {
                    existingContact.setPhoneNumber(contact.getPhoneNumber());
                }

                return existingContact;
            })
            .map(contactRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contact> findAll() {
        log.debug("Request to get all Contacts");
        return contactRepository.findAll();
    }

    /**
     *  Get all the contacts where ClientInfo is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Contact> findAllWhereClientInfoIsNull() {
        log.debug("Request to get all contacts where ClientInfo is null");
        return StreamSupport
            .stream(contactRepository.findAll().spliterator(), false)
            .filter(contact -> contact.getClientInfo() == null)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Contact> findOne(Long id) {
        log.debug("Request to get Contact : {}", id);
        return contactRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Contact : {}", id);
        contactRepository.deleteById(id);
    }
}
