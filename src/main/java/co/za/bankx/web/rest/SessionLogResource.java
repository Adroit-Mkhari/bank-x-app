package co.za.bankx.web.rest;

import co.za.bankx.domain.SessionLog;
import co.za.bankx.repository.SessionLogRepository;
import co.za.bankx.service.SessionLogService;
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
 * REST controller for managing {@link co.za.bankx.domain.SessionLog}.
 */
@RestController
@RequestMapping("/api/session-logs")
public class SessionLogResource {

    private final Logger log = LoggerFactory.getLogger(SessionLogResource.class);

    private static final String ENTITY_NAME = "sessionLog";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SessionLogService sessionLogService;

    private final SessionLogRepository sessionLogRepository;

    public SessionLogResource(SessionLogService sessionLogService, SessionLogRepository sessionLogRepository) {
        this.sessionLogService = sessionLogService;
        this.sessionLogRepository = sessionLogRepository;
    }

    /**
     * {@code POST  /session-logs} : Create a new sessionLog.
     *
     * @param sessionLog the sessionLog to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new sessionLog, or with status {@code 400 (Bad Request)} if the sessionLog has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SessionLog> createSessionLog(@Valid @RequestBody SessionLog sessionLog) throws URISyntaxException {
        log.debug("REST request to save SessionLog : {}", sessionLog);
        if (sessionLog.getId() != null) {
            throw new BadRequestAlertException("A new sessionLog cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SessionLog result = sessionLogService.save(sessionLog);
        return ResponseEntity
            .created(new URI("/api/session-logs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /session-logs/:id} : Updates an existing sessionLog.
     *
     * @param id the id of the sessionLog to save.
     * @param sessionLog the sessionLog to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sessionLog,
     * or with status {@code 400 (Bad Request)} if the sessionLog is not valid,
     * or with status {@code 500 (Internal Server Error)} if the sessionLog couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SessionLog> updateSessionLog(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SessionLog sessionLog
    ) throws URISyntaxException {
        log.debug("REST request to update SessionLog : {}, {}", id, sessionLog);
        if (sessionLog.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sessionLog.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sessionLogRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SessionLog result = sessionLogService.update(sessionLog);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, sessionLog.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /session-logs/:id} : Partial updates given fields of an existing sessionLog, field will ignore if it is null
     *
     * @param id the id of the sessionLog to save.
     * @param sessionLog the sessionLog to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sessionLog,
     * or with status {@code 400 (Bad Request)} if the sessionLog is not valid,
     * or with status {@code 404 (Not Found)} if the sessionLog is not found,
     * or with status {@code 500 (Internal Server Error)} if the sessionLog couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SessionLog> partialUpdateSessionLog(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SessionLog sessionLog
    ) throws URISyntaxException {
        log.debug("REST request to partial update SessionLog partially : {}, {}", id, sessionLog);
        if (sessionLog.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sessionLog.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sessionLogRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SessionLog> result = sessionLogService.partialUpdate(sessionLog);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, sessionLog.getId().toString())
        );
    }

    /**
     * {@code GET  /session-logs} : get all the sessionLogs.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sessionLogs in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SessionLog>> getAllSessionLogs(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of SessionLogs");
        Page<SessionLog> page = sessionLogService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /session-logs/:id} : get the "id" sessionLog.
     *
     * @param id the id of the sessionLog to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sessionLog, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SessionLog> getSessionLog(@PathVariable("id") Long id) {
        log.debug("REST request to get SessionLog : {}", id);
        Optional<SessionLog> sessionLog = sessionLogService.findOne(id);
        return ResponseUtil.wrapOrNotFound(sessionLog);
    }

    /**
     * {@code DELETE  /session-logs/:id} : delete the "id" sessionLog.
     *
     * @param id the id of the sessionLog to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSessionLog(@PathVariable("id") Long id) {
        log.debug("REST request to delete SessionLog : {}", id);
        sessionLogService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
