package co.za.bankx.web.rest;

import co.za.bankx.domain.ClientInbox;
import co.za.bankx.repository.ClientInboxRepository;
import co.za.bankx.service.ClientInboxService;
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
 * REST controller for managing {@link co.za.bankx.domain.ClientInbox}.
 */
@RestController
@RequestMapping("/api/client-inboxes")
public class ClientInboxResource {

    private final Logger log = LoggerFactory.getLogger(ClientInboxResource.class);

    private static final String ENTITY_NAME = "clientInbox";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClientInboxService clientInboxService;

    private final ClientInboxRepository clientInboxRepository;

    public ClientInboxResource(ClientInboxService clientInboxService, ClientInboxRepository clientInboxRepository) {
        this.clientInboxService = clientInboxService;
        this.clientInboxRepository = clientInboxRepository;
    }

    /**
     * {@code POST  /client-inboxes} : Create a new clientInbox.
     *
     * @param clientInbox the clientInbox to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new clientInbox, or with status {@code 400 (Bad Request)} if the clientInbox has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ClientInbox> createClientInbox(@Valid @RequestBody ClientInbox clientInbox) throws URISyntaxException {
        log.debug("REST request to save ClientInbox : {}", clientInbox);
        if (clientInbox.getId() != null) {
            throw new BadRequestAlertException("A new clientInbox cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ClientInbox result = clientInboxService.save(clientInbox);
        return ResponseEntity
            .created(new URI("/api/client-inboxes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /client-inboxes/:id} : Updates an existing clientInbox.
     *
     * @param id the id of the clientInbox to save.
     * @param clientInbox the clientInbox to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clientInbox,
     * or with status {@code 400 (Bad Request)} if the clientInbox is not valid,
     * or with status {@code 500 (Internal Server Error)} if the clientInbox couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClientInbox> updateClientInbox(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ClientInbox clientInbox
    ) throws URISyntaxException {
        log.debug("REST request to update ClientInbox : {}, {}", id, clientInbox);
        if (clientInbox.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clientInbox.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clientInboxRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ClientInbox result = clientInboxService.update(clientInbox);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, clientInbox.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /client-inboxes/:id} : Partial updates given fields of an existing clientInbox, field will ignore if it is null
     *
     * @param id the id of the clientInbox to save.
     * @param clientInbox the clientInbox to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clientInbox,
     * or with status {@code 400 (Bad Request)} if the clientInbox is not valid,
     * or with status {@code 404 (Not Found)} if the clientInbox is not found,
     * or with status {@code 500 (Internal Server Error)} if the clientInbox couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ClientInbox> partialUpdateClientInbox(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ClientInbox clientInbox
    ) throws URISyntaxException {
        log.debug("REST request to partial update ClientInbox partially : {}, {}", id, clientInbox);
        if (clientInbox.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clientInbox.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!clientInboxRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ClientInbox> result = clientInboxService.partialUpdate(clientInbox);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, clientInbox.getId().toString())
        );
    }

    /**
     * {@code GET  /client-inboxes} : get all the clientInboxes.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of clientInboxes in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ClientInbox>> getAllClientInboxes(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of ClientInboxes");
        Page<ClientInbox> page = clientInboxService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /client-inboxes/:id} : get the "id" clientInbox.
     *
     * @param id the id of the clientInbox to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the clientInbox, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClientInbox> getClientInbox(@PathVariable("id") Long id) {
        log.debug("REST request to get ClientInbox : {}", id);
        Optional<ClientInbox> clientInbox = clientInboxService.findOne(id);
        return ResponseUtil.wrapOrNotFound(clientInbox);
    }

    /**
     * {@code DELETE  /client-inboxes/:id} : delete the "id" clientInbox.
     *
     * @param id the id of the clientInbox to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClientInbox(@PathVariable("id") Long id) {
        log.debug("REST request to delete ClientInbox : {}", id);
        clientInboxService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
