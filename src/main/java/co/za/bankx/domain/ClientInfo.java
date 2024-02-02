package co.za.bankx.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.domain.Persistable;

/**
 * A ClientInfo.
 */
@Entity
@Table(name = "client_info")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties(value = { "new" })
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ClientInfo implements Serializable, Persistable<String> {

    private static final long serialVersionUID = 1L;

    @Pattern(regexp = "^\\d{13}$")
    @Id
    @Column(name = "id_number")
    private String idNumber;

    @NotNull
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotNull
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Transient
    private boolean isPersisted;

    @JsonIgnoreProperties(value = { "clientInfo" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Contact contact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "clientInfos", "accountInfos", "clientInboxes" }, allowSetters = true)
    private ProfileInfo profileInfo;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getIdNumber() {
        return this.idNumber;
    }

    public ClientInfo idNumber(String idNumber) {
        this.setIdNumber(idNumber);
        return this;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public ClientInfo firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public ClientInfo lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @PostLoad
    @PostPersist
    public void updateEntityState() {
        this.setIsPersisted();
    }

    @Override
    public String getId() {
        return this.idNumber;
    }

    @Transient
    @Override
    public boolean isNew() {
        return !this.isPersisted;
    }

    public ClientInfo setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public Contact getContact() {
        return this.contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public ClientInfo contact(Contact contact) {
        this.setContact(contact);
        return this;
    }

    public ProfileInfo getProfileInfo() {
        return this.profileInfo;
    }

    public void setProfileInfo(ProfileInfo profileInfo) {
        this.profileInfo = profileInfo;
    }

    public ClientInfo profileInfo(ProfileInfo profileInfo) {
        this.setProfileInfo(profileInfo);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClientInfo)) {
            return false;
        }
        return getIdNumber() != null && getIdNumber().equals(((ClientInfo) o).getIdNumber());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClientInfo{" +
            "idNumber=" + getIdNumber() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            "}";
    }
}
