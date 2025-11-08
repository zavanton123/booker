package ru.zavanton.booker.web.rest;

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
import ru.zavanton.booker.domain.ReadingStatusEntity;
import ru.zavanton.booker.repository.ReadingStatusRepository;
import ru.zavanton.booker.service.ReadingStatusQueryService;
import ru.zavanton.booker.service.ReadingStatusService;
import ru.zavanton.booker.service.criteria.ReadingStatusCriteria;
import ru.zavanton.booker.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link ru.zavanton.booker.domain.ReadingStatusEntity}.
 */
@RestController
@RequestMapping("/api/reading-statuses")
public class ReadingStatusResource {

    private static final Logger LOG = LoggerFactory.getLogger(ReadingStatusResource.class);

    private static final String ENTITY_NAME = "readingStatus";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ReadingStatusService readingStatusService;

    private final ReadingStatusRepository readingStatusRepository;

    private final ReadingStatusQueryService readingStatusQueryService;

    public ReadingStatusResource(
        ReadingStatusService readingStatusService,
        ReadingStatusRepository readingStatusRepository,
        ReadingStatusQueryService readingStatusQueryService
    ) {
        this.readingStatusService = readingStatusService;
        this.readingStatusRepository = readingStatusRepository;
        this.readingStatusQueryService = readingStatusQueryService;
    }

    /**
     * {@code POST  /reading-statuses} : Create a new readingStatus.
     *
     * @param readingStatusEntity the readingStatusEntity to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new readingStatusEntity, or with status {@code 400 (Bad Request)} if the readingStatus has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ReadingStatusEntity> createReadingStatus(@Valid @RequestBody ReadingStatusEntity readingStatusEntity)
        throws URISyntaxException {
        LOG.debug("REST request to save ReadingStatus : {}", readingStatusEntity);
        if (readingStatusEntity.getId() != null) {
            throw new BadRequestAlertException("A new readingStatus cannot already have an ID", ENTITY_NAME, "idexists");
        }
        readingStatusEntity = readingStatusService.save(readingStatusEntity);
        return ResponseEntity.created(new URI("/api/reading-statuses/" + readingStatusEntity.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, readingStatusEntity.getId().toString()))
            .body(readingStatusEntity);
    }

    /**
     * {@code PUT  /reading-statuses/:id} : Updates an existing readingStatus.
     *
     * @param id the id of the readingStatusEntity to save.
     * @param readingStatusEntity the readingStatusEntity to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated readingStatusEntity,
     * or with status {@code 400 (Bad Request)} if the readingStatusEntity is not valid,
     * or with status {@code 500 (Internal Server Error)} if the readingStatusEntity couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReadingStatusEntity> updateReadingStatus(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ReadingStatusEntity readingStatusEntity
    ) throws URISyntaxException {
        LOG.debug("REST request to update ReadingStatus : {}, {}", id, readingStatusEntity);
        if (readingStatusEntity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, readingStatusEntity.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!readingStatusRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        readingStatusEntity = readingStatusService.update(readingStatusEntity);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, readingStatusEntity.getId().toString()))
            .body(readingStatusEntity);
    }

    /**
     * {@code PATCH  /reading-statuses/:id} : Partial updates given fields of an existing readingStatus, field will ignore if it is null
     *
     * @param id the id of the readingStatusEntity to save.
     * @param readingStatusEntity the readingStatusEntity to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated readingStatusEntity,
     * or with status {@code 400 (Bad Request)} if the readingStatusEntity is not valid,
     * or with status {@code 404 (Not Found)} if the readingStatusEntity is not found,
     * or with status {@code 500 (Internal Server Error)} if the readingStatusEntity couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ReadingStatusEntity> partialUpdateReadingStatus(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ReadingStatusEntity readingStatusEntity
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ReadingStatus partially : {}, {}", id, readingStatusEntity);
        if (readingStatusEntity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, readingStatusEntity.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!readingStatusRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ReadingStatusEntity> result = readingStatusService.partialUpdate(readingStatusEntity);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, readingStatusEntity.getId().toString())
        );
    }

    /**
     * {@code GET  /reading-statuses} : get all the readingStatuses.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of readingStatuses in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ReadingStatusEntity>> getAllReadingStatuses(
        ReadingStatusCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get ReadingStatuses by criteria: {}", criteria);

        Page<ReadingStatusEntity> page = readingStatusQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /reading-statuses/count} : count all the readingStatuses.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countReadingStatuses(ReadingStatusCriteria criteria) {
        LOG.debug("REST request to count ReadingStatuses by criteria: {}", criteria);
        return ResponseEntity.ok().body(readingStatusQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /reading-statuses/:id} : get the "id" readingStatus.
     *
     * @param id the id of the readingStatusEntity to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the readingStatusEntity, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReadingStatusEntity> getReadingStatus(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ReadingStatus : {}", id);
        Optional<ReadingStatusEntity> readingStatusEntity = readingStatusService.findOne(id);
        return ResponseUtil.wrapOrNotFound(readingStatusEntity);
    }

    /**
     * {@code DELETE  /reading-statuses/:id} : delete the "id" readingStatus.
     *
     * @param id the id of the readingStatusEntity to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReadingStatus(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ReadingStatus : {}", id);
        readingStatusService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
