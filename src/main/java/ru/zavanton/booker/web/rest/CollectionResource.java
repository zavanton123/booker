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
import ru.zavanton.booker.domain.CollectionEntity;
import ru.zavanton.booker.repository.CollectionRepository;
import ru.zavanton.booker.service.CollectionQueryService;
import ru.zavanton.booker.service.CollectionService;
import ru.zavanton.booker.service.criteria.CollectionCriteria;
import ru.zavanton.booker.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link ru.zavanton.booker.domain.CollectionEntity}.
 */
@RestController
@RequestMapping("/api/collections")
public class CollectionResource {

    private static final Logger LOG = LoggerFactory.getLogger(CollectionResource.class);

    private static final String ENTITY_NAME = "collection";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CollectionService collectionService;

    private final CollectionRepository collectionRepository;

    private final CollectionQueryService collectionQueryService;

    public CollectionResource(
        CollectionService collectionService,
        CollectionRepository collectionRepository,
        CollectionQueryService collectionQueryService
    ) {
        this.collectionService = collectionService;
        this.collectionRepository = collectionRepository;
        this.collectionQueryService = collectionQueryService;
    }

    /**
     * {@code POST  /collections} : Create a new collection.
     *
     * @param collectionEntity the collectionEntity to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new collectionEntity, or with status {@code 400 (Bad Request)} if the collection has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CollectionEntity> createCollection(@Valid @RequestBody CollectionEntity collectionEntity)
        throws URISyntaxException {
        LOG.debug("REST request to save Collection : {}", collectionEntity);
        if (collectionEntity.getId() != null) {
            throw new BadRequestAlertException("A new collection cannot already have an ID", ENTITY_NAME, "idexists");
        }
        collectionEntity = collectionService.save(collectionEntity);
        return ResponseEntity.created(new URI("/api/collections/" + collectionEntity.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, collectionEntity.getId().toString()))
            .body(collectionEntity);
    }

    /**
     * {@code PUT  /collections/:id} : Updates an existing collection.
     *
     * @param id the id of the collectionEntity to save.
     * @param collectionEntity the collectionEntity to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated collectionEntity,
     * or with status {@code 400 (Bad Request)} if the collectionEntity is not valid,
     * or with status {@code 500 (Internal Server Error)} if the collectionEntity couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CollectionEntity> updateCollection(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CollectionEntity collectionEntity
    ) throws URISyntaxException {
        LOG.debug("REST request to update Collection : {}, {}", id, collectionEntity);
        if (collectionEntity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, collectionEntity.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!collectionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        collectionEntity = collectionService.update(collectionEntity);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, collectionEntity.getId().toString()))
            .body(collectionEntity);
    }

    /**
     * {@code PATCH  /collections/:id} : Partial updates given fields of an existing collection, field will ignore if it is null
     *
     * @param id the id of the collectionEntity to save.
     * @param collectionEntity the collectionEntity to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated collectionEntity,
     * or with status {@code 400 (Bad Request)} if the collectionEntity is not valid,
     * or with status {@code 404 (Not Found)} if the collectionEntity is not found,
     * or with status {@code 500 (Internal Server Error)} if the collectionEntity couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CollectionEntity> partialUpdateCollection(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CollectionEntity collectionEntity
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Collection partially : {}, {}", id, collectionEntity);
        if (collectionEntity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, collectionEntity.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!collectionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CollectionEntity> result = collectionService.partialUpdate(collectionEntity);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, collectionEntity.getId().toString())
        );
    }

    /**
     * {@code GET  /collections} : get all the collections.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of collections in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CollectionEntity>> getAllCollections(
        CollectionCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Collections by criteria: {}", criteria);

        Page<CollectionEntity> page = collectionQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /collections/count} : count all the collections.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countCollections(CollectionCriteria criteria) {
        LOG.debug("REST request to count Collections by criteria: {}", criteria);
        return ResponseEntity.ok().body(collectionQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /collections/:id} : get the "id" collection.
     *
     * @param id the id of the collectionEntity to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the collectionEntity, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CollectionEntity> getCollection(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Collection : {}", id);
        Optional<CollectionEntity> collectionEntity = collectionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(collectionEntity);
    }

    /**
     * {@code DELETE  /collections/:id} : delete the "id" collection.
     *
     * @param id the id of the collectionEntity to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCollection(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Collection : {}", id);
        collectionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
