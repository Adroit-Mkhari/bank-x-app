package co.za.bankx.repository;

import co.za.bankx.domain.SessionLog;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SessionLog entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SessionLogRepository extends JpaRepository<SessionLog, Long> {}
