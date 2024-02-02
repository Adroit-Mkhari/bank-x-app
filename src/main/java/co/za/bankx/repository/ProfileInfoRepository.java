package co.za.bankx.repository;

import co.za.bankx.domain.ProfileInfo;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProfileInfo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProfileInfoRepository extends JpaRepository<ProfileInfo, String> {}
