package co.za.bankx.repository;

import co.za.bankx.domain.ClientInbox;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ClientInbox entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClientInboxRepository extends JpaRepository<ClientInbox, Long> {}
