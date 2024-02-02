package co.za.bankx.web.rest;

import co.za.bankx.domain.ClientInfo;
import co.za.bankx.repository.ClientInfoRepository;
import co.za.bankx.service.ClientInfoService;
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
 * REST controller for managing {@link co.za.bankx.domain.ClientInfo}.
 */
@RestController
@RequestMapping("/api/client-infos")
public class ClientInfoResource {

    private final Logger log = LoggerFactory.getLogger(ClientInfoResource.class);

    private static final String ENTITY_NAME = "clientInfo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClientInfoService clientInfoService;

    private final ClientInfoRepository clientInfoRepository;

    public ClientInfoResource(ClientInfoService clientInfoService, ClientInfoRepository clientInfoRepository) {
        this.clientInfoService = clientInfoService;
        this.clientInfoRepository = clientInfoRepository;
    }

    /**
     * {@code POST  /client-infos} : Create a new clientInfo.
     *
     * @param clientInfo the clientInfo to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new clientInfo, or with status {@code 400 (Bad Request)} if the clientInfo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ClientInfo> createClientInfo(@Valid @RequestBody ClientInfo clientInfo) throws URISyntaxException {
        log.debug("REST request to save ClientInfo : {}", clientInfo);
        if (clientInfo.getIdNumber() != null) {
            throw new BadRequestAlertException("A new clientInfo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ClientInfo result = clientInfoService.save(clientInfo);
        return ResponseEntity
            .created(new URI("/api/client-infos/" + result.getIdNumber()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getIdNumber()))
            .body(result);
    }

    /**
     * {@code PUT  /client-infos/:idNumber} : Updates an existing clientInfo.
     *
     * @param idNumber the id of the clientInfo to save.
     * @param clientInfo the clientInfo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clientInfo,
     * or with status {@code 400 (Bad Request)} if the clientInfo is not valid,
     * or with status {@code 500 (Internal Server Error)} if the clientInfo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{idNumber}")
    public ResponseEntity<ClientInfo> updateClientInfo(
        @PathVariable(value = "idNumber", required = false) final String idNumber,
        @Valid @RequestBody ClientInfo clientInfo
    ) throws URISyntaxException {
        log.debug("REST request to update ClientInfo : {}, {}", idNumber, clientInfo);
        if (clientInfo.getIdNumber() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(idNumber, clientInfo.getIdNumber())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clientInfoRepository.existsById(idNumber)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ClientInfo result = clientInfoService.update(clientInfo);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, clientInfo.getIdNumber()))
            .body(result);
    }

    /**
     * {@code PATCH  /client-infos/:idNumber} : Partial updates given fields of an existing clientInfo, field will ignore if it is null
     *
     * @param idNumber the id of the clientInfo to save.
     * @param clientInfo the clientInfo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clientInfo,
     * or with status {@code 400 (Bad Request)} if the clientInfo is not valid,
     * or with status {@code 404 (Not Found)} if the clientInfo is not found,
     * or with status {@code 500 (Internal Server Error)} if the clientInfo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{idNumber}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ClientInfo> partialUpdateClientInfo(
        @PathVariable(value = "idNumber", required = false) final String idNumber,
        @NotNull @RequestBody ClientInfo clientInfo
    ) throws URISyntaxException {
        log.debug("REST request to partial update ClientInfo partially : {}, {}", idNumber, clientInfo);
        if (clientInfo.getIdNumber() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(idNumber, clientInfo.getIdNumber())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clientInfoRepository.existsById(idNumber)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ClientInfo> result = clientInfoService.partialUpdate(clientInfo);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, clientInfo.getIdNumber())
        );
    }

    /**
     * {@code GET  /client-infos} : get all the clientInfos.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of clientInfos in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ClientInfo>> getAllClientInfos(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of ClientInfos");
        Page<ClientInfo> page = clientInfoService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /client-infos/:id} : get the "id" clientInfo.
     *
     * @param id the id of the clientInfo to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the clientInfo, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClientInfo> getClientInfo(@PathVariable("id") String id) {
        log.debug("REST request to get ClientInfo : {}", id);
        Optional<ClientInfo> clientInfo = clientInfoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(clientInfo);
    }

    /**
     * {@code DELETE  /client-infos/:id} : delete the "id" clientInfo.
     *
     * @param id the id of the clientInfo to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClientInfo(@PathVariable("id") String id) {
        log.debug("REST request to delete ClientInfo : {}", id);
        clientInfoService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build();
    }
}
