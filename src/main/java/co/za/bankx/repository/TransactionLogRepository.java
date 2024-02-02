package co.za.bankx.repository;

import co.za.bankx.domain.TransactionLog;
import java.util.UUID;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TransactionLog entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TransactionLogRepository extends JpaRepository<TransactionLog, UUID> {}
