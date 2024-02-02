package co.za.bankx.web.rest;

import co.za.bankx.domain.*;
import co.za.bankx.domain.enumeration.*;
import co.za.bankx.repository.UserRepository;
import co.za.bankx.security.SecurityUtils;
import co.za.bankx.service.*;
import co.za.bankx.service.dto.AdminUserDTO;
import co.za.bankx.service.dto.PasswordChangeDTO;
import co.za.bankx.web.rest.errors.*;
import co.za.bankx.web.rest.errors.EmailAlreadyUsedException;
import co.za.bankx.web.rest.errors.InvalidPasswordException;
import co.za.bankx.web.rest.vm.KeyAndPasswordVM;
import co.za.bankx.web.rest.vm.ManagedUserVM;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class AccountResource {

    private static class AccountResourceException extends RuntimeException {

        private AccountResourceException(String message) {
            super(message);
        }
    }

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    private final UserRepository userRepository;

    private final UserService userService;

    private final MailService mailService;

    @Autowired
    private ProfileInfoService profileInfoService;

    @Autowired
    private ClientInfoService clientInfoService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private AccountInfoService accountInfoService;

    @Autowired
    private TransactionLogService transactionLogService;

    @Autowired
    private SessionLogService sessionLogService;

    public AccountResource(UserRepository userRepository, UserService userService, MailService mailService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.mailService = mailService;
    }

    /**
     * {@code POST  /register} : register the user.
     *
     * @param managedUserVM the managed user View Model.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws LoginAlreadyUsedException {@code 400 (Bad Request)} if the login is already used.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerAccount(@Valid @RequestBody ManagedUserVM managedUserVM) {
        if (isPasswordLengthInvalid(managedUserVM.getPassword())) {
            throw new InvalidPasswordException();
        }
        User user = userService.registerUser(managedUserVM, managedUserVM.getPassword());
        mailService.sendActivationEmail(user);

        String login = user.getLogin();
        if (login != null && !user.isActivated()) {
            ProfileInfo profileInfo = new ProfileInfo();
            String profileNumber = generateRandomProfileNumber();

            while (profileInfoService.findOne(profileNumber).isPresent()) {
                profileNumber = generateRandomProfileNumber();
            }

            profileInfo.setProfileNumber(profileNumber); // TODO: Ensure that it matches pattern or regenerate
            profileInfo.setUserId(user.getId());
            profileInfo = profileInfoService.save(profileInfo);

            Contact contact = new Contact();
            contact.setEmail(managedUserVM.getEmail());
            contact.setPhoneNumber(managedUserVM.getPhoneNumber());
            contact.setStreetAddress(managedUserVM.getStreetAddress());
            contact.setCity(managedUserVM.getCity());
            contact.setStateProvince(managedUserVM.getStateProvince());
            contact.setPostalCode(managedUserVM.getPostalCode());
            contact = contactService.save(contact);

            ClientInfo clientInfo = new ClientInfo();
            clientInfo.setProfileInfo(profileInfo);
            clientInfo.setFirstName(managedUserVM.getFirstName());
            clientInfo.setLastName(managedUserVM.getLastName());
            clientInfo.setIdNumber(managedUserVM.getIdNumber()); // TODO: Check if ID number already exists in db
            clientInfo.setContact(contact);

            clientInfoService.save(clientInfo);

            AccountInfo savingsAccount = new AccountInfo();
            savingsAccount.setProfileInfo(profileInfo);
            savingsAccount.setAccountNumber(generateRandomAccountNumber(10));
            savingsAccount.setAccountType(AccountType.SAVINGS);
            savingsAccount.setAccountStatus(AccountStatus.ACTIVE);
            savingsAccount.setAccountBalance(BigDecimal.valueOf(500.00));
            savingsAccount = accountInfoService.save(savingsAccount);

            TransactionLog transactionLogSavings = new TransactionLog();
            transactionLogSavings.setDebtorAccount(savingsAccount.getAccountNumber());
            transactionLogSavings.setCreditorAccount("100000000000007");
            transactionLogSavings.setStatus(TransactionStatus.SUCCESSFUL);
            transactionLogSavings.setTransactionTime(Instant.now());
            transactionLogSavings.setAmount(BigDecimal.valueOf(500.00));
            transactionLogSavings = transactionLogService.save(transactionLogSavings);

            SessionLog sessionLogSavings = new SessionLog();
            sessionLogSavings.setStatus(DebitCreditStatus.ACCEPTED);
            sessionLogSavings.setTransactionLog(transactionLogSavings);
            sessionLogSavings.setTransactionType(TransactionType.CREDIT);
            sessionLogService.save(sessionLogSavings);

            AccountInfo currentAccount = new AccountInfo();
            currentAccount.setProfileInfo(profileInfo);
            currentAccount.setAccountNumber(generateRandomAccountNumber(12));
            currentAccount.setAccountType(AccountType.SAVINGS);
            currentAccount.setAccountStatus(AccountStatus.ACTIVE);
            currentAccount.setAccountBalance(BigDecimal.valueOf(00.00));
            currentAccount = accountInfoService.save(currentAccount);

            TransactionLog transactionLogCurrent = new TransactionLog();
            transactionLogCurrent.setDebtorAccount(currentAccount.getAccountNumber());
            transactionLogCurrent.setCreditorAccount("100000000000007");
            transactionLogCurrent.setStatus(TransactionStatus.SUCCESSFUL);
            transactionLogCurrent.setTransactionTime(Instant.now());
            transactionLogCurrent.setAmount(BigDecimal.valueOf(00.00));
            transactionLogCurrent = transactionLogService.save(transactionLogCurrent);

            SessionLog sessionLogCurrent = new SessionLog();
            sessionLogCurrent.setStatus(DebitCreditStatus.ACCEPTED);
            sessionLogCurrent.setTransactionLog(transactionLogCurrent);
            sessionLogCurrent.setTransactionType(TransactionType.CREDIT);
            sessionLogService.save(sessionLogCurrent);
        }
    }

    private static String generateRandomProfileNumber() {
        RandomStringGenerator randomStringGenerator = new RandomStringGenerator.Builder()
            .withinRange('A', 'Z')
            .filteredBy(CharacterPredicates.LETTERS)
            .build();

        RandomStringGenerator randomNumberStringGenerator = new RandomStringGenerator.Builder()
            .withinRange('0', '9')
            .filteredBy(CharacterPredicates.DIGITS)
            .build();

        String profileNumber = randomStringGenerator.generate(6) + " " + randomNumberStringGenerator.generate(3);
        return profileNumber;
    }

    private static String generateRandomAccountNumber(int size) {
        return new RandomStringGenerator.Builder().withinRange('0', '9').filteredBy(CharacterPredicates.DIGITS).build().generate(size);
    }

    /**
     * {@code GET  /activate} : activate the registered user.
     *
     * @param key the activation key.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be activated.
     */
    @GetMapping("/activate")
    public void activateAccount(@RequestParam(value = "key") String key) {
        Optional<User> user = userService.activateRegistration(key);
        if (!user.isPresent()) {
            throw new AccountResourceException("No user was found for this activation key");
        }
    }

    /**
     * {@code GET  /account} : get the current user.
     *
     * @return the current user.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be returned.
     */
    @GetMapping("/account")
    public AdminUserDTO getAccount() {
        return userService
            .getUserWithAuthorities()
            .map(AdminUserDTO::new)
            .orElseThrow(() -> new AccountResourceException("User could not be found"));
    }

    /**
     * {@code POST  /account} : update the current user information.
     *
     * @param userDTO the current user information.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user login wasn't found.
     */
    @PostMapping("/account")
    public void saveAccount(@Valid @RequestBody AdminUserDTO userDTO) {
        String userLogin = SecurityUtils
            .getCurrentUserLogin()
            .orElseThrow(() -> new AccountResourceException("Current user login not found"));
        Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.orElseThrow().getLogin().equalsIgnoreCase(userLogin))) {
            throw new EmailAlreadyUsedException();
        }
        Optional<User> user = userRepository.findOneByLogin(userLogin);
        if (!user.isPresent()) {
            throw new AccountResourceException("User could not be found");
        }
        userService.updateUser(
            userDTO.getFirstName(),
            userDTO.getLastName(),
            userDTO.getEmail(),
            userDTO.getLangKey(),
            userDTO.getImageUrl()
        );
    }

    /**
     * {@code POST  /account/change-password} : changes the current user's password.
     *
     * @param passwordChangeDto current and new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the new password is incorrect.
     */
    @PostMapping(path = "/account/change-password")
    public void changePassword(@RequestBody PasswordChangeDTO passwordChangeDto) {
        if (isPasswordLengthInvalid(passwordChangeDto.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        userService.changePassword(passwordChangeDto.getCurrentPassword(), passwordChangeDto.getNewPassword());
    }

    /**
     * {@code POST   /account/reset-password/init} : Send an email to reset the password of the user.
     *
     * @param mail the mail of the user.
     */
    @PostMapping(path = "/account/reset-password/init")
    public void requestPasswordReset(@RequestBody String mail) {
        Optional<User> user = userService.requestPasswordReset(mail);
        if (user.isPresent()) {
            mailService.sendPasswordResetMail(user.orElseThrow());
        } else {
            // Pretend the request has been successful to prevent checking which emails really exist
            // but log that an invalid attempt has been made
            log.warn("Password reset requested for non existing mail");
        }
    }

    /**
     * {@code POST   /account/reset-password/finish} : Finish to reset the password of the user.
     *
     * @param keyAndPassword the generated key and the new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the password could not be reset.
     */
    @PostMapping(path = "/account/reset-password/finish")
    public void finishPasswordReset(@RequestBody KeyAndPasswordVM keyAndPassword) {
        if (isPasswordLengthInvalid(keyAndPassword.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        Optional<User> user = userService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey());

        if (!user.isPresent()) {
            throw new AccountResourceException("No user was found for this reset key");
        }
    }

    private static boolean isPasswordLengthInvalid(String password) {
        return (
            StringUtils.isEmpty(password) ||
            password.length() < ManagedUserVM.PASSWORD_MIN_LENGTH ||
            password.length() > ManagedUserVM.PASSWORD_MAX_LENGTH
        );
    }
}
