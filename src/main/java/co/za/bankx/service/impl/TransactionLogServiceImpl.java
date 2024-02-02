package co.za.bankx.service.impl;

import co.za.bankx.domain.TransactionLog;
import co.za.bankx.repository.TransactionLogRepository;
import co.za.bankx.service.TransactionLogService;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link co.za.bankx.domain.TransactionLog}.
 */
@Service
@Transactional
public class TransactionLogServiceImpl implements TransactionLogService {

    private final Logger log = LoggerFactory.getLogger(TransactionLogServiceImpl.class);

    private final TransactionLogRepository transactionLogRepository;

    public TransactionLogServiceImpl(TransactionLogRepository transactionLogRepository) {
        this.transactionLogRepository = transactionLogRepository;
    }

    @Override
    public TransactionLog save(TransactionLog transactionLog) {
        log.debug("Request to save TransactionLog : {}", transactionLog);
        return transactionLogRepository.save(transactionLog);
    }

    @Override
    public TransactionLog update(TransactionLog transactionLog) {
        log.debug("Request to update TransactionLog : {}", transactionLog);
        return transactionLogRepository.save(transactionLog);
    }

    @Override
    public Optional<TransactionLog> partialUpdate(TransactionLog transactionLog) {
        log.debug("Request to partially update TransactionLog : {}", transactionLog);

        return transactionLogRepository
            .findById(transactionLog.getUniqueTransactionId())
            .map(existingTransactionLog -> {
                if (transactionLog.getTransactionTime() != null) {
                    existingTransactionLog.setTransactionTime(transactionLog.getTransactionTime());
                }
                if (transactionLog.getDebtorAccount() != null) {
                    existingTransactionLog.setDebtorAccount(transactionLog.getDebtorAccount());
                }
                if (transactionLog.getCreditorAccount() != null) {
                    existingTransactionLog.setCreditorAccount(transactionLog.getCreditorAccount());
                }
                if (transactionLog.getAmount() != null) {
                    existingTransactionLog.setAmount(transactionLog.getAmount());
                }
                if (transactionLog.getStatus() != null) {
                    existingTransactionLog.setStatus(transactionLog.getStatus());
                }

                return existingTransactionLog;
            })
            .map(transactionLogRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionLog> findAll(Pageable pageable) {
        log.debug("Request to get all TransactionLogs");
        return transactionLogRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TransactionLog> findOne(UUID id) {
        log.debug("Request to get TransactionLog : {}", id);
        return transactionLogRepository.findById(id);
    }

    @Override
    public void delete(UUID id) {
        log.debug("Request to delete TransactionLog : {}", id);
        transactionLogRepository.deleteById(id);
    }
}
