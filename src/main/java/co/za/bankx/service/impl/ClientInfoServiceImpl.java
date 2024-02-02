package co.za.bankx.service.impl;

import co.za.bankx.domain.ClientInfo;
import co.za.bankx.repository.ClientInfoRepository;
import co.za.bankx.service.ClientInfoService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link co.za.bankx.domain.ClientInfo}.
 */
@Service
@Transactional
public class ClientInfoServiceImpl implements ClientInfoService {

    private final Logger log = LoggerFactory.getLogger(ClientInfoServiceImpl.class);

    private final ClientInfoRepository clientInfoRepository;

    public ClientInfoServiceImpl(ClientInfoRepository clientInfoRepository) {
        this.clientInfoRepository = clientInfoRepository;
    }

    @Override
    public ClientInfo save(ClientInfo clientInfo) {
        log.debug("Request to save ClientInfo : {}", clientInfo);
        return clientInfoRepository.save(clientInfo);
    }

    @Override
    public ClientInfo update(ClientInfo clientInfo) {
        log.debug("Request to update ClientInfo : {}", clientInfo);
        clientInfo.setIsPersisted();
        return clientInfoRepository.save(clientInfo);
    }

    @Override
    public Optional<ClientInfo> partialUpdate(ClientInfo clientInfo) {
        log.debug("Request to partially update ClientInfo : {}", clientInfo);

        return clientInfoRepository
            .findById(clientInfo.getIdNumber())
            .map(existingClientInfo -> {
                if (clientInfo.getFirstName() != null) {
                    existingClientInfo.setFirstName(clientInfo.getFirstName());
                }
                if (clientInfo.getLastName() != null) {
                    existingClientInfo.setLastName(clientInfo.getLastName());
                }

                return existingClientInfo;
            })
            .map(clientInfoRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientInfo> findAll(Pageable pageable) {
        log.debug("Request to get all ClientInfos");
        return clientInfoRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClientInfo> findOne(String id) {
        log.debug("Request to get ClientInfo : {}", id);
        return clientInfoRepository.findById(id);
    }

    @Override
    public void delete(String id) {
        log.debug("Request to delete ClientInfo : {}", id);
        clientInfoRepository.deleteById(id);
    }
}
