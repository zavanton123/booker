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
import ru.zavanton.booker.domain.BookCollectionEntity;
import ru.zavanton.booker.repository.BookCollectionRepository;
import ru.zavanton.booker.service.BookCollectionQueryService;
import ru.zavanton.booker.service.BookCollectionService;
import ru.zavanton.booker.service.criteria.BookCollectionCriteria;
import ru.zavanton.booker.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link ru.zavanton.booker.domain.BookCollectionEntity}.
 */
@RestController
@RequestMapping("/api/book-collections")
public class BookCollectionResource {

    private static final Logger LOG = LoggerFactory.getLogger(BookCollectionResource.class);

    private static final String ENTITY_NAME = "bookCollection";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BookCollectionService bookCollectionService;

    private final BookCollectionRepository bookCollectionRepository;

    private final BookCollectionQueryService bookCollectionQueryService;

    public BookCollectionResource(
        BookCollectionService bookCollectionService,
        BookCollectionRepository bookCollectionRepository,
        BookCollectionQueryService bookCollectionQueryService
    ) {
        this.bookCollectionService = bookCollectionService;
        this.bookCollectionRepository = bookCollectionRepository;
        this.bookCollectionQueryService = bookCollectionQueryService;
    }

    /**
     * {@code POST  /book-collections} : Create a new bookCollection.
     *
     * @param bookCollectionEntity the bookCollectionEntity to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new bookCollectionEntity, or with status {@code 400 (Bad Request)} if the bookCollection has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<BookCollectionEntity> createBookCollection(@Valid @RequestBody BookCollectionEntity bookCollectionEntity)
        throws URISyntaxException {
        LOG.debug("REST request to save BookCollection : {}", bookCollectionEntity);
        if (bookCollectionEntity.getId() != null) {
            throw new BadRequestAlertException("A new bookCollection cannot already have an ID", ENTITY_NAME, "idexists");
        }
        bookCollectionEntity = bookCollectionService.save(bookCollectionEntity);
        return ResponseEntity.created(new URI("/api/book-collections/" + bookCollectionEntity.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, bookCollectionEntity.getId().toString()))
            .body(bookCollectionEntity);
    }

    /**
     * {@code PUT  /book-collections/:id} : Updates an existing bookCollection.
     *
     * @param id the id of the bookCollectionEntity to save.
     * @param bookCollectionEntity the bookCollectionEntity to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bookCollectionEntity,
     * or with status {@code 400 (Bad Request)} if the bookCollectionEntity is not valid,
     * or with status {@code 500 (Internal Server Error)} if the bookCollectionEntity couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BookCollectionEntity> updateBookCollection(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BookCollectionEntity bookCollectionEntity
    ) throws URISyntaxException {
        LOG.debug("REST request to update BookCollection : {}, {}", id, bookCollectionEntity);
        if (bookCollectionEntity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bookCollectionEntity.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bookCollectionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        bookCollectionEntity = bookCollectionService.update(bookCollectionEntity);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bookCollectionEntity.getId().toString()))
            .body(bookCollectionEntity);
    }

    /**
     * {@code PATCH  /book-collections/:id} : Partial updates given fields of an existing bookCollection, field will ignore if it is null
     *
     * @param id the id of the bookCollectionEntity to save.
     * @param bookCollectionEntity the bookCollectionEntity to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bookCollectionEntity,
     * or with status {@code 400 (Bad Request)} if the bookCollectionEntity is not valid,
     * or with status {@code 404 (Not Found)} if the bookCollectionEntity is not found,
     * or with status {@code 500 (Internal Server Error)} if the bookCollectionEntity couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BookCollectionEntity> partialUpdateBookCollection(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BookCollectionEntity bookCollectionEntity
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update BookCollection partially : {}, {}", id, bookCollectionEntity);
        if (bookCollectionEntity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bookCollectionEntity.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bookCollectionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BookCollectionEntity> result = bookCollectionService.partialUpdate(bookCollectionEntity);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bookCollectionEntity.getId().toString())
        );
    }

    /**
     * {@code GET  /book-collections} : get all the bookCollections.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bookCollections in body.
     */
    @GetMapping("")
    public ResponseEntity<List<BookCollectionEntity>> getAllBookCollections(
        BookCollectionCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get BookCollections by criteria: {}", criteria);

        Page<BookCollectionEntity> page = bookCollectionQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /book-collections/count} : count all the bookCollections.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countBookCollections(BookCollectionCriteria criteria) {
        LOG.debug("REST request to count BookCollections by criteria: {}", criteria);
        return ResponseEntity.ok().body(bookCollectionQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /book-collections/:id} : get the "id" bookCollection.
     *
     * @param id the id of the bookCollectionEntity to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the bookCollectionEntity, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookCollectionEntity> getBookCollection(@PathVariable("id") Long id) {
        LOG.debug("REST request to get BookCollection : {}", id);
        Optional<BookCollectionEntity> bookCollectionEntity = bookCollectionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(bookCollectionEntity);
    }

    /**
     * {@code DELETE  /book-collections/:id} : delete the "id" bookCollection.
     *
     * @param id the id of the bookCollectionEntity to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookCollection(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete BookCollection : {}", id);
        bookCollectionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
