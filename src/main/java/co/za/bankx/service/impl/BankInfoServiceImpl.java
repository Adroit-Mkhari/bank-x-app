package co.za.bankx.service.impl;

import co.za.bankx.domain.BankInfo;
import co.za.bankx.repository.BankInfoRepository;
import co.za.bankx.service.BankInfoService;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link co.za.bankx.domain.BankInfo}.
 */
@Service
@Transactional
public class BankInfoServiceImpl implements BankInfoService {

    private final Logger log = LoggerFactory.getLogger(BankInfoServiceImpl.class);

    private final BankInfoRepository bankInfoRepository;

    public BankInfoServiceImpl(BankInfoRepository bankInfoRepository) {
        this.bankInfoRepository = bankInfoRepository;
    }

    @Override
    public BankInfo save(BankInfo bankInfo) {
        log.debug("Request to save BankInfo : {}", bankInfo);
        return bankInfoRepository.save(bankInfo);
    }

    @Override
    public BankInfo update(BankInfo bankInfo) {
        log.debug("Request to update BankInfo : {}", bankInfo);
        return bankInfoRepository.save(bankInfo);
    }

    @Override
    public Optional<BankInfo> partialUpdate(BankInfo bankInfo) {
        log.debug("Request to partially update BankInfo : {}", bankInfo);

        return bankInfoRepository
            .findById(bankInfo.getId())
            .map(existingBankInfo -> {
                if (bankInfo.getSwiftCode() != null) {
                    existingBankInfo.setSwiftCode(bankInfo.getSwiftCode());
                }
                if (bankInfo.getBankName() != null) {
                    existingBankInfo.setBankName(bankInfo.getBankName());
                }

                return existingBankInfo;
            })
            .map(bankInfoRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BankInfo> findAll() {
        log.debug("Request to get all BankInfos");
        return bankInfoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BankInfo> findOne(Long id) {
        log.debug("Request to get BankInfo : {}", id);
        return bankInfoRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete BankInfo : {}", id);
        bankInfoRepository.deleteById(id);
    }
}
