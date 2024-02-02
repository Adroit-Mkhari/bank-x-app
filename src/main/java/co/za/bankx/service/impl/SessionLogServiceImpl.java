package co.za.bankx.service.impl;

import co.za.bankx.domain.SessionLog;
import co.za.bankx.repository.SessionLogRepository;
import co.za.bankx.service.SessionLogService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link co.za.bankx.domain.SessionLog}.
 */
@Service
@Transactional
public class SessionLogServiceImpl implements SessionLogService {

    private final Logger log = LoggerFactory.getLogger(SessionLogServiceImpl.class);

    private final SessionLogRepository sessionLogRepository;

    public SessionLogServiceImpl(SessionLogRepository sessionLogRepository) {
        this.sessionLogRepository = sessionLogRepository;
    }

    @Override
    public SessionLog save(SessionLog sessionLog) {
        log.debug("Request to save SessionLog : {}", sessionLog);
        return sessionLogRepository.save(sessionLog);
    }

    @Override
    public SessionLog update(SessionLog sessionLog) {
        log.debug("Request to update SessionLog : {}", sessionLog);
        return sessionLogRepository.save(sessionLog);
    }

    @Override
    public Optional<SessionLog> partialUpdate(SessionLog sessionLog) {
        log.debug("Request to partially update SessionLog : {}", sessionLog);

        return sessionLogRepository
            .findById(sessionLog.getId())
            .map(existingSessionLog -> {
                if (sessionLog.getTransactionType() != null) {
                    existingSessionLog.setTransactionType(sessionLog.getTransactionType());
                }
                if (sessionLog.getStatus() != null) {
                    existingSessionLog.setStatus(sessionLog.getStatus());
                }

                return existingSessionLog;
            })
            .map(sessionLogRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SessionLog> findAll(Pageable pageable) {
        log.debug("Request to get all SessionLogs");
        return sessionLogRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SessionLog> findOne(Long id) {
        log.debug("Request to get SessionLog : {}", id);
        return sessionLogRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete SessionLog : {}", id);
        sessionLogRepository.deleteById(id);
    }
}
