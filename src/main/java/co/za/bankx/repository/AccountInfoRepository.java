package co.za.bankx.repository;

import co.za.bankx.domain.AccountInfo;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AccountInfo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AccountInfoRepository extends JpaRepository<AccountInfo, Long> {}
