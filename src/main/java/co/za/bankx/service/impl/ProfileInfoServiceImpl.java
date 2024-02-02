package co.za.bankx.service.impl;

import co.za.bankx.domain.ProfileInfo;
import co.za.bankx.repository.ProfileInfoRepository;
import co.za.bankx.service.ProfileInfoService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link co.za.bankx.domain.ProfileInfo}.
 */
@Service
@Transactional
public class ProfileInfoServiceImpl implements ProfileInfoService {

    private final Logger log = LoggerFactory.getLogger(ProfileInfoServiceImpl.class);

    private final ProfileInfoRepository profileInfoRepository;

    public ProfileInfoServiceImpl(ProfileInfoRepository profileInfoRepository) {
        this.profileInfoRepository = profileInfoRepository;
    }

    @Override
    public ProfileInfo save(ProfileInfo profileInfo) {
        log.debug("Request to save ProfileInfo : {}", profileInfo);
        return profileInfoRepository.save(profileInfo);
    }

    @Override
    public ProfileInfo update(ProfileInfo profileInfo) {
        log.debug("Request to update ProfileInfo : {}", profileInfo);
        profileInfo.setIsPersisted();
        return profileInfoRepository.save(profileInfo);
    }

    @Override
    public Optional<ProfileInfo> partialUpdate(ProfileInfo profileInfo) {
        log.debug("Request to partially update ProfileInfo : {}", profileInfo);

        return profileInfoRepository
            .findById(profileInfo.getProfileNumber())
            .map(existingProfileInfo -> {
                if (profileInfo.getUserId() != null) {
                    existingProfileInfo.setUserId(profileInfo.getUserId());
                }

                return existingProfileInfo;
            })
            .map(profileInfoRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProfileInfo> findAll(Pageable pageable) {
        log.debug("Request to get all ProfileInfos");
        return profileInfoRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProfileInfo> findOne(String id) {
        log.debug("Request to get ProfileInfo : {}", id);
        return profileInfoRepository.findById(id);
    }

    @Override
    public void delete(String id) {
        log.debug("Request to delete ProfileInfo : {}", id);
        profileInfoRepository.deleteById(id);
    }
}
