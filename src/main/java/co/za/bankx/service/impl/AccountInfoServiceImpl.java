package co.za.bankx.service.impl;

import co.za.bankx.domain.AccountInfo;
import co.za.bankx.repository.AccountInfoRepository;
import co.za.bankx.service.AccountInfoService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link co.za.bankx.domain.AccountInfo}.
 */
@Service
@Transactional
public class AccountInfoServiceImpl implements AccountInfoService {

    private final Logger log = LoggerFactory.getLogger(AccountInfoServiceImpl.class);

    private final AccountInfoRepository accountInfoRepository;

    public AccountInfoServiceImpl(AccountInfoRepository accountInfoRepository) {
        this.accountInfoRepository = accountInfoRepository;
    }

    @Override
    public AccountInfo save(AccountInfo accountInfo) {
        log.debug("Request to save AccountInfo : {}", accountInfo);
        return accountInfoRepository.save(accountInfo);
    }

    @Override
    public AccountInfo update(AccountInfo accountInfo) {
        log.debug("Request to update AccountInfo : {}", accountInfo);
        return accountInfoRepository.save(accountInfo);
    }

    @Override
    public Optional<AccountInfo> partialUpdate(AccountInfo accountInfo) {
        log.debug("Request to partially update AccountInfo : {}", accountInfo);

        return accountInfoRepository
            .findById(accountInfo.getId())
            .map(existingAccountInfo -> {
                if (accountInfo.getAccountNumber() != null) {
                    existingAccountInfo.setAccountNumber(accountInfo.getAccountNumber());
                }
                if (accountInfo.getAccountType() != null) {
                    existingAccountInfo.setAccountType(accountInfo.getAccountType());
                }
                if (accountInfo.getAccountStatus() != null) {
                    existingAccountInfo.setAccountStatus(accountInfo.getAccountStatus());
                }
                if (accountInfo.getAccountBalance() != null) {
                    existingAccountInfo.setAccountBalance(accountInfo.getAccountBalance());
                }

                return existingAccountInfo;
            })
            .map(accountInfoRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AccountInfo> findAll(Pageable pageable) {
        log.debug("Request to get all AccountInfos");
        return accountInfoRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AccountInfo> findOne(Long id) {
        log.debug("Request to get AccountInfo : {}", id);
        return accountInfoRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete AccountInfo : {}", id);
        accountInfoRepository.deleteById(id);
    }
}
