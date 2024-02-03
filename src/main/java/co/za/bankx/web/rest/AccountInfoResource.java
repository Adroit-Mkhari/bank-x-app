package co.za.bankx.web.rest;

import co.za.bankx.domain.AccountInfo;
import co.za.bankx.repository.AccountInfoRepository;
import co.za.bankx.service.AccountInfoService;
import co.za.bankx.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * REST controller for managing {@link co.za.bankx.domain.AccountInfo}.
 */
@RestController
@RequestMapping("/api/account-infos")
public class AccountInfoResource {

    private final Logger log = LoggerFactory.getLogger(AccountInfoResource.class);

    private static final String ENTITY_NAME = "accountInfo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AccountInfoService accountInfoService;

    private final AccountInfoRepository accountInfoRepository;

    public AccountInfoResource(AccountInfoService accountInfoService, AccountInfoRepository accountInfoRepository) {
        this.accountInfoService = accountInfoService;
        this.accountInfoRepository = accountInfoRepository;
    }

    /**
     * {@code POST  /account-infos} : Create a new accountInfo.
     *
     * @param accountInfo the accountInfo to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new accountInfo, or with status {@code 400 (Bad Request)} if the accountInfo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AccountInfo> createAccountInfo(@Valid @RequestBody AccountInfo accountInfo) throws URISyntaxException {
        log.debug("REST request to save AccountInfo : {}", accountInfo);
        if (accountInfo.getId() != null) {
            throw new BadRequestAlertException("A new accountInfo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AccountInfo result = accountInfoService.save(accountInfo);
        return ResponseEntity
            .created(new URI("/api/account-infos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /account-infos/:id} : Updates an existing accountInfo.
     *
     * @param id the id of the accountInfo to save.
     * @param accountInfo the accountInfo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated accountInfo,
     * or with status {@code 400 (Bad Request)} if the accountInfo is not valid,
     * or with status {@code 500 (Internal Server Error)} if the accountInfo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AccountInfo> updateAccountInfo(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AccountInfo accountInfo
    ) throws URISyntaxException {
        log.debug("REST request to update AccountInfo : {}, {}", id, accountInfo);
        if (accountInfo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, accountInfo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!accountInfoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AccountInfo result = accountInfoService.update(accountInfo);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, accountInfo.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /account-infos/:id} : Partial updates given fields of an existing accountInfo, field will ignore if it is null
     *
     * @param id the id of the accountInfo to save.
     * @param accountInfo the accountInfo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated accountInfo,
     * or with status {@code 400 (Bad Request)} if the accountInfo is not valid,
     * or with status {@code 404 (Not Found)} if the accountInfo is not found,
     * or with status {@code 500 (Internal Server Error)} if the accountInfo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AccountInfo> partialUpdateAccountInfo(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AccountInfo accountInfo
    ) throws URISyntaxException {
        log.debug("REST request to partial update AccountInfo partially : {}, {}", id, accountInfo);
        if (accountInfo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, accountInfo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!accountInfoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AccountInfo> result = accountInfoService.partialUpdate(accountInfo);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, accountInfo.getId().toString())
        );
    }

    /**
     * {@code GET  /account-infos} : get all the accountInfos.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of accountInfos in body.
     */
    @GetMapping("")
    public ResponseEntity<List<AccountInfo>> getAllAccountInfos(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of AccountInfos");
        Page<AccountInfo> page = accountInfoService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /account-infos/:id} : get the "id" accountInfo.
     *
     * @param id the id of the accountInfo to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the accountInfo, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AccountInfo> getAccountInfo(@PathVariable("id") Long id) {
        log.debug("REST request to get AccountInfo : {}", id);
        Optional<AccountInfo> accountInfo = accountInfoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(accountInfo);
    }

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<AccountInfo> getAccountInfoByAccountNumber(@PathVariable("accountNumber") String accountNumber) {
        log.debug("REST request to get AccountInfo : {}", accountNumber);
        Optional<AccountInfo> accountInfo = accountInfoService.findOneByAccountNumber(accountNumber);
        return ResponseUtil.wrapOrNotFound(accountInfo);
    }

    /**
     * {@code DELETE  /account-infos/:id} : delete the "id" accountInfo.
     *
     * @param id the id of the accountInfo to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccountInfo(@PathVariable("id") Long id) {
        log.debug("REST request to delete AccountInfo : {}", id);
        accountInfoService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
