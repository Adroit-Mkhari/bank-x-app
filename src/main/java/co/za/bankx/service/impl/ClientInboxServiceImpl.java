package co.za.bankx.service.impl;

import co.za.bankx.domain.ClientInbox;
import co.za.bankx.repository.ClientInboxRepository;
import co.za.bankx.service.ClientInboxService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link co.za.bankx.domain.ClientInbox}.
 */
@Service
@Transactional
public class ClientInboxServiceImpl implements ClientInboxService {

    private final Logger log = LoggerFactory.getLogger(ClientInboxServiceImpl.class);

    private final ClientInboxRepository clientInboxRepository;

    public ClientInboxServiceImpl(ClientInboxRepository clientInboxRepository) {
        this.clientInboxRepository = clientInboxRepository;
    }

    @Override
    public ClientInbox save(ClientInbox clientInbox) {
        log.debug("Request to save ClientInbox : {}", clientInbox);
        return clientInboxRepository.save(clientInbox);
    }

    @Override
    public ClientInbox update(ClientInbox clientInbox) {
        log.debug("Request to update ClientInbox : {}", clientInbox);
        return clientInboxRepository.save(clientInbox);
    }

    @Override
    public Optional<ClientInbox> partialUpdate(ClientInbox clientInbox) {
        log.debug("Request to partially update ClientInbox : {}", clientInbox);

        return clientInboxRepository
            .findById(clientInbox.getId())
            .map(existingClientInbox -> {
                if (clientInbox.getMessage() != null) {
                    existingClientInbox.setMessage(clientInbox.getMessage());
                }

                return existingClientInbox;
            })
            .map(clientInboxRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientInbox> findAll(Pageable pageable) {
        log.debug("Request to get all ClientInboxes");
        return clientInboxRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClientInbox> findOne(Long id) {
        log.debug("Request to get ClientInbox : {}", id);
        return clientInboxRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete ClientInbox : {}", id);
        clientInboxRepository.deleteById(id);
    }
}
