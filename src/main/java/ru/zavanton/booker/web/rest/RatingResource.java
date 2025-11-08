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
import ru.zavanton.booker.domain.RatingEntity;
import ru.zavanton.booker.repository.RatingRepository;
import ru.zavanton.booker.service.RatingQueryService;
import ru.zavanton.booker.service.RatingService;
import ru.zavanton.booker.service.criteria.RatingCriteria;
import ru.zavanton.booker.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link ru.zavanton.booker.domain.RatingEntity}.
 */
@RestController
@RequestMapping("/api/ratings")
public class RatingResource {

    private static final Logger LOG = LoggerFactory.getLogger(RatingResource.class);

    private static final String ENTITY_NAME = "rating";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RatingService ratingService;

    private final RatingRepository ratingRepository;

    private final RatingQueryService ratingQueryService;

    public RatingResource(RatingService ratingService, RatingRepository ratingRepository, RatingQueryService ratingQueryService) {
        this.ratingService = ratingService;
        this.ratingRepository = ratingRepository;
        this.ratingQueryService = ratingQueryService;
    }

    /**
     * {@code POST  /ratings} : Create a new rating.
     *
     * @param ratingEntity the ratingEntity to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ratingEntity, or with status {@code 400 (Bad Request)} if the rating has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<RatingEntity> createRating(@Valid @RequestBody RatingEntity ratingEntity) throws URISyntaxException {
        LOG.debug("REST request to save Rating : {}", ratingEntity);
        if (ratingEntity.getId() != null) {
            throw new BadRequestAlertException("A new rating cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ratingEntity = ratingService.save(ratingEntity);
        return ResponseEntity.created(new URI("/api/ratings/" + ratingEntity.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, ratingEntity.getId().toString()))
            .body(ratingEntity);
    }

    /**
     * {@code PUT  /ratings/:id} : Updates an existing rating.
     *
     * @param id the id of the ratingEntity to save.
     * @param ratingEntity the ratingEntity to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ratingEntity,
     * or with status {@code 400 (Bad Request)} if the ratingEntity is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ratingEntity couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RatingEntity> updateRating(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RatingEntity ratingEntity
    ) throws URISyntaxException {
        LOG.debug("REST request to update Rating : {}, {}", id, ratingEntity);
        if (ratingEntity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ratingEntity.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ratingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ratingEntity = ratingService.update(ratingEntity);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ratingEntity.getId().toString()))
            .body(ratingEntity);
    }

    /**
     * {@code PATCH  /ratings/:id} : Partial updates given fields of an existing rating, field will ignore if it is null
     *
     * @param id the id of the ratingEntity to save.
     * @param ratingEntity the ratingEntity to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ratingEntity,
     * or with status {@code 400 (Bad Request)} if the ratingEntity is not valid,
     * or with status {@code 404 (Not Found)} if the ratingEntity is not found,
     * or with status {@code 500 (Internal Server Error)} if the ratingEntity couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<RatingEntity> partialUpdateRating(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RatingEntity ratingEntity
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Rating partially : {}, {}", id, ratingEntity);
        if (ratingEntity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ratingEntity.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ratingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RatingEntity> result = ratingService.partialUpdate(ratingEntity);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ratingEntity.getId().toString())
        );
    }

    /**
     * {@code GET  /ratings} : get all the ratings.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of ratings in body.
     */
    @GetMapping("")
    public ResponseEntity<List<RatingEntity>> getAllRatings(
        RatingCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Ratings by criteria: {}", criteria);

        Page<RatingEntity> page = ratingQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /ratings/count} : count all the ratings.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countRatings(RatingCriteria criteria) {
        LOG.debug("REST request to count Ratings by criteria: {}", criteria);
        return ResponseEntity.ok().body(ratingQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /ratings/:id} : get the "id" rating.
     *
     * @param id the id of the ratingEntity to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ratingEntity, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RatingEntity> getRating(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Rating : {}", id);
        Optional<RatingEntity> ratingEntity = ratingService.findOne(id);
        return ResponseUtil.wrapOrNotFound(ratingEntity);
    }

    /**
     * {@code DELETE  /ratings/:id} : delete the "id" rating.
     *
     * @param id the id of the ratingEntity to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRating(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Rating : {}", id);
        ratingService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
