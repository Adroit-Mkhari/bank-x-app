package co.za.bankx.repository;

import co.za.bankx.domain.ClientInfo;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ClientInfo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClientInfoRepository extends JpaRepository<ClientInfo, String> {}
