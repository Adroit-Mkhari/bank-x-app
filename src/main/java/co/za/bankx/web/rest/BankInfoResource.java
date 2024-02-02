package co.za.bankx.web.rest;

import co.za.bankx.domain.BankInfo;
import co.za.bankx.repository.BankInfoRepository;
import co.za.bankx.service.BankInfoService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link co.za.bankx.domain.BankInfo}.
 */
@RestController
@RequestMapping("/api/bank-infos")
public class BankInfoResource {

    private final Logger log = LoggerFactory.getLogger(BankInfoResource.class);

    private static final String ENTITY_NAME = "bankInfo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BankInfoService bankInfoService;

    private final BankInfoRepository bankInfoRepository;

    public BankInfoResource(BankInfoService bankInfoService, BankInfoRepository bankInfoRepository) {
        this.bankInfoService = bankInfoService;
        this.bankInfoRepository = bankInfoRepository;
    }

    /**
     * {@code POST  /bank-infos} : Create a new bankInfo.
     *
     * @param bankInfo the bankInfo to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new bankInfo, or with status {@code 400 (Bad Request)} if the bankInfo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<BankInfo> createBankInfo(@Valid @RequestBody BankInfo bankInfo) throws URISyntaxException {
        log.debug("REST request to save BankInfo : {}", bankInfo);
        if (bankInfo.getId() != null) {
            throw new BadRequestAlertException("A new bankInfo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BankInfo result = bankInfoService.save(bankInfo);
        return ResponseEntity
            .created(new URI("/api/bank-infos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /bank-infos/:id} : Updates an existing bankInfo.
     *
     * @param id the id of the bankInfo to save.
     * @param bankInfo the bankInfo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bankInfo,
     * or with status {@code 400 (Bad Request)} if the bankInfo is not valid,
     * or with status {@code 500 (Internal Server Error)} if the bankInfo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BankInfo> updateBankInfo(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BankInfo bankInfo
    ) throws URISyntaxException {
        log.debug("REST request to update BankInfo : {}, {}", id, bankInfo);
        if (bankInfo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bankInfo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bankInfoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        BankInfo result = bankInfoService.update(bankInfo);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, bankInfo.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /bank-infos/:id} : Partial updates given fields of an existing bankInfo, field will ignore if it is null
     *
     * @param id the id of the bankInfo to save.
     * @param bankInfo the bankInfo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bankInfo,
     * or with status {@code 400 (Bad Request)} if the bankInfo is not valid,
     * or with status {@code 404 (Not Found)} if the bankInfo is not found,
     * or with status {@code 500 (Internal Server Error)} if the bankInfo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BankInfo> partialUpdateBankInfo(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BankInfo bankInfo
    ) throws URISyntaxException {
        log.debug("REST request to partial update BankInfo partially : {}, {}", id, bankInfo);
        if (bankInfo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bankInfo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bankInfoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BankInfo> result = bankInfoService.partialUpdate(bankInfo);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, bankInfo.getId().toString())
        );
    }

    /**
     * {@code GET  /bank-infos} : get all the bankInfos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bankInfos in body.
     */
    @GetMapping("")
    public List<BankInfo> getAllBankInfos() {
        log.debug("REST request to get all BankInfos");
        return bankInfoService.findAll();
    }

    /**
     * {@code GET  /bank-infos/:id} : get the "id" bankInfo.
     *
     * @param id the id of the bankInfo to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the bankInfo, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BankInfo> getBankInfo(@PathVariable("id") Long id) {
        log.debug("REST request to get BankInfo : {}", id);
        Optional<BankInfo> bankInfo = bankInfoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(bankInfo);
    }

    /**
     * {@code DELETE  /bank-infos/:id} : delete the "id" bankInfo.
     *
     * @param id the id of the bankInfo to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBankInfo(@PathVariable("id") Long id) {
        log.debug("REST request to delete BankInfo : {}", id);
        bankInfoService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
