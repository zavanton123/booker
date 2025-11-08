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
import ru.zavanton.booker.domain.ReviewEntity;
import ru.zavanton.booker.repository.ReviewRepository;
import ru.zavanton.booker.service.ReviewQueryService;
import ru.zavanton.booker.service.ReviewService;
import ru.zavanton.booker.service.criteria.ReviewCriteria;
import ru.zavanton.booker.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link ru.zavanton.booker.domain.ReviewEntity}.
 */
@RestController
@RequestMapping("/api/reviews")
public class ReviewResource {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewResource.class);

    private static final String ENTITY_NAME = "review";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ReviewService reviewService;

    private final ReviewRepository reviewRepository;

    private final ReviewQueryService reviewQueryService;

    public ReviewResource(ReviewService reviewService, ReviewRepository reviewRepository, ReviewQueryService reviewQueryService) {
        this.reviewService = reviewService;
        this.reviewRepository = reviewRepository;
        this.reviewQueryService = reviewQueryService;
    }

    /**
     * {@code POST  /reviews} : Create a new review.
     *
     * @param reviewEntity the reviewEntity to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new reviewEntity, or with status {@code 400 (Bad Request)} if the review has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ReviewEntity> createReview(@Valid @RequestBody ReviewEntity reviewEntity) throws URISyntaxException {
        LOG.debug("REST request to save Review : {}", reviewEntity);
        if (reviewEntity.getId() != null) {
            throw new BadRequestAlertException("A new review cannot already have an ID", ENTITY_NAME, "idexists");
        }
        reviewEntity = reviewService.save(reviewEntity);
        return ResponseEntity.created(new URI("/api/reviews/" + reviewEntity.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, reviewEntity.getId().toString()))
            .body(reviewEntity);
    }

    /**
     * {@code PUT  /reviews/:id} : Updates an existing review.
     *
     * @param id the id of the reviewEntity to save.
     * @param reviewEntity the reviewEntity to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated reviewEntity,
     * or with status {@code 400 (Bad Request)} if the reviewEntity is not valid,
     * or with status {@code 500 (Internal Server Error)} if the reviewEntity couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReviewEntity> updateReview(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ReviewEntity reviewEntity
    ) throws URISyntaxException {
        LOG.debug("REST request to update Review : {}, {}", id, reviewEntity);
        if (reviewEntity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, reviewEntity.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!reviewRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        reviewEntity = reviewService.update(reviewEntity);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, reviewEntity.getId().toString()))
            .body(reviewEntity);
    }

    /**
     * {@code PATCH  /reviews/:id} : Partial updates given fields of an existing review, field will ignore if it is null
     *
     * @param id the id of the reviewEntity to save.
     * @param reviewEntity the reviewEntity to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated reviewEntity,
     * or with status {@code 400 (Bad Request)} if the reviewEntity is not valid,
     * or with status {@code 404 (Not Found)} if the reviewEntity is not found,
     * or with status {@code 500 (Internal Server Error)} if the reviewEntity couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ReviewEntity> partialUpdateReview(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ReviewEntity reviewEntity
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Review partially : {}, {}", id, reviewEntity);
        if (reviewEntity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, reviewEntity.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!reviewRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ReviewEntity> result = reviewService.partialUpdate(reviewEntity);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, reviewEntity.getId().toString())
        );
    }

    /**
     * {@code GET  /reviews} : get all the reviews.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of reviews in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ReviewEntity>> getAllReviews(
        ReviewCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Reviews by criteria: {}", criteria);

        Page<ReviewEntity> page = reviewQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /reviews/count} : count all the reviews.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countReviews(ReviewCriteria criteria) {
        LOG.debug("REST request to count Reviews by criteria: {}", criteria);
        return ResponseEntity.ok().body(reviewQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /reviews/:id} : get the "id" review.
     *
     * @param id the id of the reviewEntity to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the reviewEntity, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReviewEntity> getReview(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Review : {}", id);
        Optional<ReviewEntity> reviewEntity = reviewService.findOne(id);
        return ResponseUtil.wrapOrNotFound(reviewEntity);
    }

    /**
     * {@code DELETE  /reviews/:id} : delete the "id" review.
     *
     * @param id the id of the reviewEntity to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Review : {}", id);
        reviewService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
