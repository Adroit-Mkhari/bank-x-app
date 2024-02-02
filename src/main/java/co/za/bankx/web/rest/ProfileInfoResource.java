package co.za.bankx.web.rest;

import co.za.bankx.domain.ProfileInfo;
import co.za.bankx.repository.ProfileInfoRepository;
import co.za.bankx.service.ProfileInfoService;
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
 * REST controller for managing {@link co.za.bankx.domain.ProfileInfo}.
 */
@RestController
@RequestMapping("/api/profile-infos")
public class ProfileInfoResource {

    private final Logger log = LoggerFactory.getLogger(ProfileInfoResource.class);

    private static final String ENTITY_NAME = "profileInfo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProfileInfoService profileInfoService;

    private final ProfileInfoRepository profileInfoRepository;

    public ProfileInfoResource(ProfileInfoService profileInfoService, ProfileInfoRepository profileInfoRepository) {
        this.profileInfoService = profileInfoService;
        this.profileInfoRepository = profileInfoRepository;
    }

    /**
     * {@code POST  /profile-infos} : Create a new profileInfo.
     *
     * @param profileInfo the profileInfo to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new profileInfo, or with status {@code 400 (Bad Request)} if the profileInfo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ProfileInfo> createProfileInfo(@Valid @RequestBody ProfileInfo profileInfo) throws URISyntaxException {
        log.debug("REST request to save ProfileInfo : {}", profileInfo);
        if (profileInfo.getProfileNumber() != null) {
            throw new BadRequestAlertException("A new profileInfo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ProfileInfo result = profileInfoService.save(profileInfo);
        return ResponseEntity
            .created(new URI("/api/profile-infos/" + result.getProfileNumber()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getProfileNumber()))
            .body(result);
    }

    /**
     * {@code PUT  /profile-infos/:profileNumber} : Updates an existing profileInfo.
     *
     * @param profileNumber the id of the profileInfo to save.
     * @param profileInfo the profileInfo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated profileInfo,
     * or with status {@code 400 (Bad Request)} if the profileInfo is not valid,
     * or with status {@code 500 (Internal Server Error)} if the profileInfo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{profileNumber}")
    public ResponseEntity<ProfileInfo> updateProfileInfo(
        @PathVariable(value = "profileNumber", required = false) final String profileNumber,
        @Valid @RequestBody ProfileInfo profileInfo
    ) throws URISyntaxException {
        log.debug("REST request to update ProfileInfo : {}, {}", profileNumber, profileInfo);
        if (profileInfo.getProfileNumber() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(profileNumber, profileInfo.getProfileNumber())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!profileInfoRepository.existsById(profileNumber)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ProfileInfo result = profileInfoService.update(profileInfo);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, profileInfo.getProfileNumber()))
            .body(result);
    }

    /**
     * {@code PATCH  /profile-infos/:profileNumber} : Partial updates given fields of an existing profileInfo, field will ignore if it is null
     *
     * @param profileNumber the id of the profileInfo to save.
     * @param profileInfo the profileInfo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated profileInfo,
     * or with status {@code 400 (Bad Request)} if the profileInfo is not valid,
     * or with status {@code 404 (Not Found)} if the profileInfo is not found,
     * or with status {@code 500 (Internal Server Error)} if the profileInfo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{profileNumber}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProfileInfo> partialUpdateProfileInfo(
        @PathVariable(value = "profileNumber", required = false) final String profileNumber,
        @NotNull @RequestBody ProfileInfo profileInfo
    ) throws URISyntaxException {
        log.debug("REST request to partial update ProfileInfo partially : {}, {}", profileNumber, profileInfo);
        if (profileInfo.getProfileNumber() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(profileNumber, profileInfo.getProfileNumber())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!profileInfoRepository.existsById(profileNumber)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProfileInfo> result = profileInfoService.partialUpdate(profileInfo);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, profileInfo.getProfileNumber())
        );
    }

    /**
     * {@code GET  /profile-infos} : get all the profileInfos.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of profileInfos in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ProfileInfo>> getAllProfileInfos(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of ProfileInfos");
        Page<ProfileInfo> page = profileInfoService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /profile-infos/:id} : get the "id" profileInfo.
     *
     * @param id the id of the profileInfo to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the profileInfo, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProfileInfo> getProfileInfo(@PathVariable("id") String id) {
        log.debug("REST request to get ProfileInfo : {}", id);
        Optional<ProfileInfo> profileInfo = profileInfoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(profileInfo);
    }

    /**
     * {@code DELETE  /profile-infos/:id} : delete the "id" profileInfo.
     *
     * @param id the id of the profileInfo to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfileInfo(@PathVariable("id") String id) {
        log.debug("REST request to delete ProfileInfo : {}", id);
        profileInfoService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build();
    }
}
