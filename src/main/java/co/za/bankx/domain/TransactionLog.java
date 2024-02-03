package co.za.bankx.domain;

import co.za.bankx.domain.enumeration.TransactionStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TransactionLog.
 */
@Entity
@Table(name = "transaction_log")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransactionLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "transaction_time", nullable = false)
    private Instant transactionTime;

    @Id
    @GeneratedValue
    @Column(name = "unique_transaction_id")
    private UUID uniqueTransactionId;

    @NotNull
    @Pattern(regexp = "^\\d{10,15}$")
    @Column(name = "debtor_account", nullable = false)
    private String debtorAccount;

    @NotNull
    @Pattern(regexp = "^\\d{10,15}$")
    @Column(name = "creditor_account", nullable = false)
    private String creditorAccount;

    @NotNull
    @Column(name = "amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal amount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransactionStatus status;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "transactionLog")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "transactionLog" }, allowSetters = true)
    private Set<SessionLog> sessionLogs = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Instant getTransactionTime() {
        return this.transactionTime;
    }

    public TransactionLog transactionTime(Instant transactionTime) {
        this.setTransactionTime(transactionTime);
        return this;
    }

    public void setTransactionTime(Instant transactionTime) {
        this.transactionTime = transactionTime;
    }

    public UUID getUniqueTransactionId() {
        return this.uniqueTransactionId;
    }

    public TransactionLog uniqueTransactionId(UUID uniqueTransactionId) {
        this.setUniqueTransactionId(uniqueTransactionId);
        return this;
    }

    public void setUniqueTransactionId(UUID uniqueTransactionId) {
        this.uniqueTransactionId = uniqueTransactionId;
    }

    public String getDebtorAccount() {
        return this.debtorAccount;
    }

    public TransactionLog debtorAccount(String debtorAccount) {
        this.setDebtorAccount(debtorAccount);
        return this;
    }

    public void setDebtorAccount(String debtorAccount) {
        this.debtorAccount = debtorAccount;
    }

    public String getCreditorAccount() {
        return this.creditorAccount;
    }

    public TransactionLog creditorAccount(String creditorAccount) {
        this.setCreditorAccount(creditorAccount);
        return this;
    }

    public void setCreditorAccount(String creditorAccount) {
        this.creditorAccount = creditorAccount;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public TransactionLog amount(BigDecimal amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionStatus getStatus() {
        return this.status;
    }

    public TransactionLog status(TransactionStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public Set<SessionLog> getSessionLogs() {
        return this.sessionLogs;
    }

    public void setSessionLogs(Set<SessionLog> sessionLogs) {
        if (this.sessionLogs != null) {
            this.sessionLogs.forEach(i -> i.setTransactionLog(null));
        }
        if (sessionLogs != null) {
            sessionLogs.forEach(i -> i.setTransactionLog(this));
        }
        this.sessionLogs = sessionLogs;
    }

    public TransactionLog sessionLogs(Set<SessionLog> sessionLogs) {
        this.setSessionLogs(sessionLogs);
        return this;
    }

    public TransactionLog addSessionLog(SessionLog sessionLog) {
        this.sessionLogs.add(sessionLog);
        sessionLog.setTransactionLog(this);
        return this;
    }

    public TransactionLog removeSessionLog(SessionLog sessionLog) {
        this.sessionLogs.remove(sessionLog);
        sessionLog.setTransactionLog(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransactionLog)) {
            return false;
        }
        return getUniqueTransactionId() != null && getUniqueTransactionId().equals(((TransactionLog) o).getUniqueTransactionId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionLog{" +
            "uniqueTransactionId=" + getUniqueTransactionId() +
            ", transactionTime='" + getTransactionTime() + "'" +
            ", debtorAccount='" + getDebtorAccount() + "'" +
            ", creditorAccount='" + getCreditorAccount() + "'" +
            ", amount=" + getAmount() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
