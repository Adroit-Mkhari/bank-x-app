package co.za.bankx.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.domain.Persistable;

/**
 * A ProfileInfo.
 */
@Entity
@Table(name = "profile_info")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties(value = { "new" })
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProfileInfo implements Serializable, Persistable<String> {

    private static final long serialVersionUID = 1L;

    @Pattern(regexp = "^[A-Z]{6}\\s\\d{3}$")
    @Id
    @Column(name = "profile_number")
    private String profileNumber;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Transient
    private boolean isPersisted;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "profileInfo")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "contact", "profileInfo" }, allowSetters = true)
    private Set<ClientInfo> clientInfos = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "profileInfo")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "profileInfo" }, allowSetters = true)
    private Set<AccountInfo> accountInfos = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "profileInfo")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "profileInfo" }, allowSetters = true)
    private Set<ClientInbox> clientInboxes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getProfileNumber() {
        return this.profileNumber;
    }

    public ProfileInfo profileNumber(String profileNumber) {
        this.setProfileNumber(profileNumber);
        return this;
    }

    public void setProfileNumber(String profileNumber) {
        this.profileNumber = profileNumber;
    }

    public Long getUserId() {
        return this.userId;
    }

    public ProfileInfo userId(Long userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @PostLoad
    @PostPersist
    public void updateEntityState() {
        this.setIsPersisted();
    }

    @Override
    public String getId() {
        return this.profileNumber;
    }

    @Transient
    @Override
    public boolean isNew() {
        return !this.isPersisted;
    }

    public ProfileInfo setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public Set<ClientInfo> getClientInfos() {
        return this.clientInfos;
    }

    public void setClientInfos(Set<ClientInfo> clientInfos) {
        if (this.clientInfos != null) {
            this.clientInfos.forEach(i -> i.setProfileInfo(null));
        }
        if (clientInfos != null) {
            clientInfos.forEach(i -> i.setProfileInfo(this));
        }
        this.clientInfos = clientInfos;
    }

    public ProfileInfo clientInfos(Set<ClientInfo> clientInfos) {
        this.setClientInfos(clientInfos);
        return this;
    }

    public ProfileInfo addClientInfo(ClientInfo clientInfo) {
        this.clientInfos.add(clientInfo);
        clientInfo.setProfileInfo(this);
        return this;
    }

    public ProfileInfo removeClientInfo(ClientInfo clientInfo) {
        this.clientInfos.remove(clientInfo);
        clientInfo.setProfileInfo(null);
        return this;
    }

    public Set<AccountInfo> getAccountInfos() {
        return this.accountInfos;
    }

    public void setAccountInfos(Set<AccountInfo> accountInfos) {
        if (this.accountInfos != null) {
            this.accountInfos.forEach(i -> i.setProfileInfo(null));
        }
        if (accountInfos != null) {
            accountInfos.forEach(i -> i.setProfileInfo(this));
        }
        this.accountInfos = accountInfos;
    }

    public ProfileInfo accountInfos(Set<AccountInfo> accountInfos) {
        this.setAccountInfos(accountInfos);
        return this;
    }

    public ProfileInfo addAccountInfo(AccountInfo accountInfo) {
        this.accountInfos.add(accountInfo);
        accountInfo.setProfileInfo(this);
        return this;
    }

    public ProfileInfo removeAccountInfo(AccountInfo accountInfo) {
        this.accountInfos.remove(accountInfo);
        accountInfo.setProfileInfo(null);
        return this;
    }

    public Set<ClientInbox> getClientInboxes() {
        return this.clientInboxes;
    }

    public void setClientInboxes(Set<ClientInbox> clientInboxes) {
        if (this.clientInboxes != null) {
            this.clientInboxes.forEach(i -> i.setProfileInfo(null));
        }
        if (clientInboxes != null) {
            clientInboxes.forEach(i -> i.setProfileInfo(this));
        }
        this.clientInboxes = clientInboxes;
    }

    public ProfileInfo clientInboxes(Set<ClientInbox> clientInboxes) {
        this.setClientInboxes(clientInboxes);
        return this;
    }

    public ProfileInfo addClientInbox(ClientInbox clientInbox) {
        this.clientInboxes.add(clientInbox);
        clientInbox.setProfileInfo(this);
        return this;
    }

    public ProfileInfo removeClientInbox(ClientInbox clientInbox) {
        this.clientInboxes.remove(clientInbox);
        clientInbox.setProfileInfo(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProfileInfo)) {
            return false;
        }
        return getProfileNumber() != null && getProfileNumber().equals(((ProfileInfo) o).getProfileNumber());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProfileInfo{" +
            "profileNumber=" + getProfileNumber() +
            ", userId=" + getUserId() +
            "}";
    }
}
