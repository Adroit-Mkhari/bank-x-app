package co.za.bankx.web.rest.vm;

import co.za.bankx.service.dto.AdminUserDTO;
import jakarta.validation.constraints.Size;

/**
 * View Model extending the AdminUserDTO, which is meant to be used in the user management UI.
 */
public class ManagedUserVM extends AdminUserDTO {

    public static final int PASSWORD_MIN_LENGTH = 4;

    public static final int PASSWORD_MAX_LENGTH = 100;

    public static final int ID_NUMBER_MIN_LENGTH = 13;

    public static final int ID_NUMBER_MAX_LENGTH = 13;

    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    private String password;

    @Size(min = ID_NUMBER_MIN_LENGTH, max = ID_NUMBER_MAX_LENGTH)
    private String idNumber;

    private String firstName;

    private String lastName;

    private String streetAddress;

    private String postalCode;

    private String city;

    private String stateProvince;

    private String phoneNumber;

    public ManagedUserVM() {
        // Empty constructor needed for Jackson.
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStateProvince() {
        return stateProvince;
    }

    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return (
            "ManagedUserVM{" +
            "password='" +
            password +
            '\'' +
            ", idNumber='" +
            idNumber +
            '\'' +
            ", firstName='" +
            firstName +
            '\'' +
            ", lastName='" +
            lastName +
            '\'' +
            ", streetAddress='" +
            streetAddress +
            '\'' +
            ", postalCode='" +
            postalCode +
            '\'' +
            ", city='" +
            city +
            '\'' +
            ", stateProvince='" +
            stateProvince +
            '\'' +
            ", phoneNumber='" +
            phoneNumber +
            '\'' +
            '}'
        );
    }
}
