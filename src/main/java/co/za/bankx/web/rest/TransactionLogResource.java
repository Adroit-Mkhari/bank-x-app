package co.za.bankx.web.rest;

import co.za.bankx.domain.ClientInbox;
import co.za.bankx.domain.SessionLog;
import co.za.bankx.domain.TransactionLog;
import co.za.bankx.domain.enumeration.*;
import co.za.bankx.repository.TransactionLogRepository;
import co.za.bankx.service.AccountInfoService;
import co.za.bankx.service.ClientInboxService;
import co.za.bankx.service.SessionLogService;
import co.za.bankx.service.TransactionLogService;
import co.za.bankx.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link co.za.bankx.domain.TransactionLog}.
 */
@RestController
@RequestMapping("/api/transaction-logs")
public class TransactionLogResource {

    private final Logger log = LoggerFactory.getLogger(TransactionLogResource.class);

    private static final String ENTITY_NAME = "transactionLog";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TransactionLogService transactionLogService;

    private final TransactionLogRepository transactionLogRepository;

    @Autowired
    private AccountInfoService accountInfoService;

    @Autowired
    private SessionLogService sessionLogService;

    @Autowired
    ClientInboxService clientInboxService;

    public TransactionLogResource(TransactionLogService transactionLogService, TransactionLogRepository transactionLogRepository) {
        this.transactionLogService = transactionLogService;
        this.transactionLogRepository = transactionLogRepository;
    }

    /**
     * {@code POST  /transaction-logs} : Create a new transactionLog.
     *
     * @param transactionLog the transactionLog to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new transactionLog, or with status {@code 400 (Bad Request)} if the transactionLog has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TransactionLog> createTransactionLog(@Valid @RequestBody TransactionLog transactionLog)
        throws URISyntaxException {
        log.debug("REST request to save Payment TransactionLog : {}", transactionLog);
        if (transactionLog.getUniqueTransactionId() != null) {
            throw new BadRequestAlertException("A new Payment transactionLog cannot already have an ID", ENTITY_NAME, "idexists");
        }

        TransactionLog result = processTransactionLog(transactionLog, false);

        return ResponseEntity
            .created(new URI("/api/transaction-logs/" + result.getUniqueTransactionId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getUniqueTransactionId().toString()))
            .body(result);
    }

    @PostMapping("batch")
    public ResponseEntity<ArrayList<TransactionLog>> batchTransactionLog(@Valid @RequestBody List<TransactionLog> transactionLogs)
        throws URISyntaxException {
        log.debug("REST request to save Payment TransactionLog : {}", transactionLogs);

        ArrayList<TransactionLog> results = new ArrayList<>();

        for (TransactionLog transactionLog : transactionLogs) {
            TransactionLog result = processTransactionLog(transactionLog, false);
            results.add(result);
        }

        return ResponseEntity
            .created(new URI("/api/transaction-logs"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, ""))
            .body(results);
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionLog> transferTransactionLog(@Valid @RequestBody TransactionLog transactionLog)
        throws URISyntaxException {
        log.debug("REST request to save Transfer TransactionLog : {}", transactionLog);
        if (transactionLog.getUniqueTransactionId() != null) {
            throw new BadRequestAlertException("A new  Transfer transactionLog cannot already have an ID", ENTITY_NAME, "idexists");
        }

        TransactionLog result = processTransactionLog(transactionLog, true);

        return ResponseEntity
            .created(new URI("/api/transaction-logs/" + result.getUniqueTransactionId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getUniqueTransactionId().toString()))
            .body(result);
    }

    private TransactionLog processTransactionLog(TransactionLog transactionLog, boolean isTransfer) {
        if (transactionLog.getTransactionTime() == null) {
            transactionLog.setTransactionTime(Instant.now());
        }

        SessionLog sessionLogDebit = new SessionLog();
        SessionLog sessionLogCredit = new SessionLog();

        sessionLogDebit.setTransactionType(TransactionType.DEBIT);
        sessionLogCredit.setTransactionType(TransactionType.CREDIT);

        // TODO: Link User Account Logic ???

        accountInfoService
            .findOneByAccountNumber(transactionLog.getCreditorAccount())
            .ifPresent(accountInfo -> {
                BigDecimal accountBalance = accountInfo.getAccountBalance();
                if (!isTransfer && accountInfo.getAccountType().equals(AccountType.SAVINGS)) {
                    transactionLog.setStatus(TransactionStatus.FAILED);
                    sessionLogDebit.setStatus(DebitCreditStatus.INVALID_ACCOUNT);
                } else {
                    if (accountBalance.compareTo(transactionLog.getAmount()) < 0) {
                        transactionLog.setStatus(TransactionStatus.FAILED);
                        sessionLogDebit.setStatus(DebitCreditStatus.INSUFFICIENT_FUNDS);
                    } else {
                        if (accountInfo.getAccountStatus().equals(AccountStatus.ACTIVE)) {
                            if (isTransfer) {
                                accountInfo.setAccountBalance(accountBalance.subtract(transactionLog.getAmount()));
                            } else {
                                // Check if the other account exists before debiting
                                accountInfoService
                                    .findOneByAccountNumber(transactionLog.getDebtorAccount())
                                    .ifPresent(accountTwo -> {
                                        if (
                                            accountInfo.getAccountStatus() != null &&
                                            accountTwo.getAccountStatus().equals(AccountStatus.ACTIVE)
                                        ) {
                                            BigDecimal depositPlusCharge = transactionLog
                                                .getAmount()
                                                .add(
                                                    transactionLog.getAmount().multiply(new BigDecimal("0.05")).divide(new BigDecimal(100))
                                                );

                                            accountInfo.setAccountBalance(accountBalance.subtract(depositPlusCharge));
                                        }
                                    });
                            }

                            transactionLog.setStatus(TransactionStatus.SUCCESSFUL);
                            sessionLogDebit.setStatus(DebitCreditStatus.ACCEPTED);
                            accountInfoService.update(accountInfo);
                        } else {
                            transactionLog.setStatus(TransactionStatus.FAILED);
                            sessionLogDebit.setStatus(DebitCreditStatus.INVALID_ACCOUNT_STATUS);
                        }
                    }
                }
            });

        if (transactionLog.getStatus() != null && transactionLog.getStatus().equals(TransactionStatus.SUCCESSFUL)) {
            accountInfoService
                .findOneByAccountNumber(transactionLog.getDebtorAccount())
                .ifPresent(accountInfo -> {
                    if (accountInfo.getAccountStatus().equals(AccountStatus.ACTIVE)) {
                        BigDecimal accountBalance = accountInfo.getAccountBalance();
                        accountInfo.setAccountBalance(accountBalance.add(transactionLog.getAmount()));

                        if (!isTransfer) {
                            accountBalance =
                                accountInfo
                                    .getAccountBalance()
                                    .add(accountBalance.multiply(new BigDecimal("0.5")).divide(new BigDecimal(100)));

                            accountInfo.setAccountBalance(accountBalance);
                        }

                        transactionLog.setStatus(TransactionStatus.SUCCESSFUL);
                        sessionLogCredit.setStatus(DebitCreditStatus.ACCEPTED);
                        accountInfoService.update(accountInfo);
                    } else {
                        transactionLog.setStatus(TransactionStatus.FAILED);
                        sessionLogCredit.setStatus(DebitCreditStatus.INVALID_ACCOUNT_STATUS);
                    }
                });
        } else {
            if (sessionLogDebit.getStatus() == null) {
                sessionLogDebit.setStatus(DebitCreditStatus.INVALID_ACCOUNT);
            }
        }

        TransactionLog result = transactionLogService.save(transactionLog);

        sessionLogDebit.setTransactionLog(result);
        sessionLogService.save(sessionLogDebit);

        if (!transactionLog.getStatus().equals(TransactionStatus.SUCCESSFUL)) {
            if (sessionLogCredit.getStatus() == null) {
                sessionLogCredit.setStatus(DebitCreditStatus.INVALID_ACCOUNT);
                if (sessionLogDebit.getStatus() != null && sessionLogDebit.getStatus().equals(DebitCreditStatus.ACCEPTED)) {
                    // TODO: Reverse Funds if debit was successful
                    // Not needed for now since I check first before
                }
            } else if (sessionLogCredit.getStatus().equals(DebitCreditStatus.INVALID_ACCOUNT_STATUS)) {
                if (sessionLogDebit.getStatus() != null && sessionLogDebit.getStatus().equals(DebitCreditStatus.ACCEPTED)) {
                    // TODO: Reverse Funds if debit was successful
                    // Not needed for now since I check first before
                }
            }
        }

        if (sessionLogDebit.getStatus() != null && sessionLogDebit.getStatus().equals(DebitCreditStatus.ACCEPTED)) {
            sessionLogCredit.setTransactionLog(result);
            sessionLogService.save(sessionLogCredit);
        }

        ClientInbox clientInbox = new ClientInbox();
        clientInbox.setMessage(
            transactionLog.getStatus() +
            ": Payment Of " +
            transactionLog.getAmount() +
            " Amount Was Made From Account " +
            transactionLog.getCreditorAccount() +
            " To " +
            transactionLog.getDebtorAccount()
        );
        clientInboxService.save(clientInbox);

        return result;
    }

    /**
     * {@code PUT  /transaction-logs/:uniqueTransactionId} : Updates an existing transactionLog.
     *
     * @param uniqueTransactionId the id of the transactionLog to save.
     * @param transactionLog the transactionLog to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transactionLog,
     * or with status {@code 400 (Bad Request)} if the transactionLog is not valid,
     * or with status {@code 500 (Internal Server Error)} if the transactionLog couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{uniqueTransactionId}")
    public ResponseEntity<TransactionLog> updateTransactionLog(
        @PathVariable(value = "uniqueTransactionId", required = false) final UUID uniqueTransactionId,
        @Valid @RequestBody TransactionLog transactionLog
    ) throws URISyntaxException {
        log.debug("REST request to update TransactionLog : {}, {}", uniqueTransactionId, transactionLog);
        if (transactionLog.getUniqueTransactionId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(uniqueTransactionId, transactionLog.getUniqueTransactionId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!transactionLogRepository.existsById(uniqueTransactionId)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TransactionLog result = transactionLogService.update(transactionLog);
        return ResponseEntity
            .ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, transactionLog.getUniqueTransactionId().toString())
            )
            .body(result);
    }

    /**
     * {@code PATCH  /transaction-logs/:uniqueTransactionId} : Partial updates given fields of an existing transactionLog, field will ignore if it is null
     *
     * @param uniqueTransactionId the id of the transactionLog to save.
     * @param transactionLog the transactionLog to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transactionLog,
     * or with status {@code 400 (Bad Request)} if the transactionLog is not valid,
     * or with status {@code 404 (Not Found)} if the transactionLog is not found,
     * or with status {@code 500 (Internal Server Error)} if the transactionLog couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{uniqueTransactionId}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TransactionLog> partialUpdateTransactionLog(
        @PathVariable(value = "uniqueTransactionId", required = false) final UUID uniqueTransactionId,
        @NotNull @RequestBody TransactionLog transactionLog
    ) throws URISyntaxException {
        log.debug("REST request to partial update TransactionLog partially : {}, {}", uniqueTransactionId, transactionLog);
        if (transactionLog.getUniqueTransactionId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(uniqueTransactionId, transactionLog.getUniqueTransactionId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!transactionLogRepository.existsById(uniqueTransactionId)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TransactionLog> result = transactionLogService.partialUpdate(transactionLog);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, transactionLog.getUniqueTransactionId().toString())
        );
    }

    /**
     * {@code GET  /transaction-logs} : get all the transactionLogs.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of transactionLogs in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TransactionLog>> getAllTransactionLogs(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of TransactionLogs");
        Page<TransactionLog> page = transactionLogService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /transaction-logs/:id} : get the "id" transactionLog.
     *
     * @param id the id of the transactionLog to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the transactionLog, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransactionLog> getTransactionLog(@PathVariable("id") UUID id) {
        log.debug("REST request to get TransactionLog : {}", id);
        Optional<TransactionLog> transactionLog = transactionLogService.findOne(id);
        return ResponseUtil.wrapOrNotFound(transactionLog);
    }

    /**
     * {@code DELETE  /transaction-logs/:id} : delete the "id" transactionLog.
     *
     * @param id the id of the transactionLog to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransactionLog(@PathVariable("id") UUID id) {
        log.debug("REST request to delete TransactionLog : {}", id);
        transactionLogService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
