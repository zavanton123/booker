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
import ru.zavanton.booker.domain.BookTagEntity;
import ru.zavanton.booker.repository.BookTagRepository;
import ru.zavanton.booker.service.BookTagQueryService;
import ru.zavanton.booker.service.BookTagService;
import ru.zavanton.booker.service.criteria.BookTagCriteria;
import ru.zavanton.booker.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link ru.zavanton.booker.domain.BookTagEntity}.
 */
@RestController
@RequestMapping("/api/book-tags")
public class BookTagResource {

    private static final Logger LOG = LoggerFactory.getLogger(BookTagResource.class);

    private static final String ENTITY_NAME = "bookTag";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BookTagService bookTagService;

    private final BookTagRepository bookTagRepository;

    private final BookTagQueryService bookTagQueryService;

    public BookTagResource(BookTagService bookTagService, BookTagRepository bookTagRepository, BookTagQueryService bookTagQueryService) {
        this.bookTagService = bookTagService;
        this.bookTagRepository = bookTagRepository;
        this.bookTagQueryService = bookTagQueryService;
    }

    /**
     * {@code POST  /book-tags} : Create a new bookTag.
     *
     * @param bookTagEntity the bookTagEntity to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new bookTagEntity, or with status {@code 400 (Bad Request)} if the bookTag has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<BookTagEntity> createBookTag(@Valid @RequestBody BookTagEntity bookTagEntity) throws URISyntaxException {
        LOG.debug("REST request to save BookTag : {}", bookTagEntity);
        if (bookTagEntity.getId() != null) {
            throw new BadRequestAlertException("A new bookTag cannot already have an ID", ENTITY_NAME, "idexists");
        }
        bookTagEntity = bookTagService.save(bookTagEntity);
        return ResponseEntity.created(new URI("/api/book-tags/" + bookTagEntity.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, bookTagEntity.getId().toString()))
            .body(bookTagEntity);
    }

    /**
     * {@code PUT  /book-tags/:id} : Updates an existing bookTag.
     *
     * @param id the id of the bookTagEntity to save.
     * @param bookTagEntity the bookTagEntity to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bookTagEntity,
     * or with status {@code 400 (Bad Request)} if the bookTagEntity is not valid,
     * or with status {@code 500 (Internal Server Error)} if the bookTagEntity couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BookTagEntity> updateBookTag(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BookTagEntity bookTagEntity
    ) throws URISyntaxException {
        LOG.debug("REST request to update BookTag : {}, {}", id, bookTagEntity);
        if (bookTagEntity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bookTagEntity.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bookTagRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        bookTagEntity = bookTagService.update(bookTagEntity);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bookTagEntity.getId().toString()))
            .body(bookTagEntity);
    }

    /**
     * {@code PATCH  /book-tags/:id} : Partial updates given fields of an existing bookTag, field will ignore if it is null
     *
     * @param id the id of the bookTagEntity to save.
     * @param bookTagEntity the bookTagEntity to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bookTagEntity,
     * or with status {@code 400 (Bad Request)} if the bookTagEntity is not valid,
     * or with status {@code 404 (Not Found)} if the bookTagEntity is not found,
     * or with status {@code 500 (Internal Server Error)} if the bookTagEntity couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BookTagEntity> partialUpdateBookTag(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BookTagEntity bookTagEntity
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update BookTag partially : {}, {}", id, bookTagEntity);
        if (bookTagEntity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bookTagEntity.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bookTagRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BookTagEntity> result = bookTagService.partialUpdate(bookTagEntity);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bookTagEntity.getId().toString())
        );
    }

    /**
     * {@code GET  /book-tags} : get all the bookTags.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bookTags in body.
     */
    @GetMapping("")
    public ResponseEntity<List<BookTagEntity>> getAllBookTags(
        BookTagCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get BookTags by criteria: {}", criteria);

        Page<BookTagEntity> page = bookTagQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /book-tags/count} : count all the bookTags.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countBookTags(BookTagCriteria criteria) {
        LOG.debug("REST request to count BookTags by criteria: {}", criteria);
        return ResponseEntity.ok().body(bookTagQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /book-tags/:id} : get the "id" bookTag.
     *
     * @param id the id of the bookTagEntity to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the bookTagEntity, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookTagEntity> getBookTag(@PathVariable("id") Long id) {
        LOG.debug("REST request to get BookTag : {}", id);
        Optional<BookTagEntity> bookTagEntity = bookTagService.findOne(id);
        return ResponseUtil.wrapOrNotFound(bookTagEntity);
    }

    /**
     * {@code DELETE  /book-tags/:id} : delete the "id" bookTag.
     *
     * @param id the id of the bookTagEntity to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookTag(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete BookTag : {}", id);
        bookTagService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
