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
import ru.zavanton.booker.domain.BookAuthorEntity;
import ru.zavanton.booker.repository.BookAuthorRepository;
import ru.zavanton.booker.service.BookAuthorQueryService;
import ru.zavanton.booker.service.BookAuthorService;
import ru.zavanton.booker.service.criteria.BookAuthorCriteria;
import ru.zavanton.booker.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link ru.zavanton.booker.domain.BookAuthorEntity}.
 */
@RestController
@RequestMapping("/api/book-authors")
public class BookAuthorResource {

    private static final Logger LOG = LoggerFactory.getLogger(BookAuthorResource.class);

    private static final String ENTITY_NAME = "bookAuthor";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BookAuthorService bookAuthorService;

    private final BookAuthorRepository bookAuthorRepository;

    private final BookAuthorQueryService bookAuthorQueryService;

    public BookAuthorResource(
        BookAuthorService bookAuthorService,
        BookAuthorRepository bookAuthorRepository,
        BookAuthorQueryService bookAuthorQueryService
    ) {
        this.bookAuthorService = bookAuthorService;
        this.bookAuthorRepository = bookAuthorRepository;
        this.bookAuthorQueryService = bookAuthorQueryService;
    }

    /**
     * {@code POST  /book-authors} : Create a new bookAuthor.
     *
     * @param bookAuthorEntity the bookAuthorEntity to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new bookAuthorEntity, or with status {@code 400 (Bad Request)} if the bookAuthor has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<BookAuthorEntity> createBookAuthor(@Valid @RequestBody BookAuthorEntity bookAuthorEntity)
        throws URISyntaxException {
        LOG.debug("REST request to save BookAuthor : {}", bookAuthorEntity);
        if (bookAuthorEntity.getId() != null) {
            throw new BadRequestAlertException("A new bookAuthor cannot already have an ID", ENTITY_NAME, "idexists");
        }
        bookAuthorEntity = bookAuthorService.save(bookAuthorEntity);
        return ResponseEntity.created(new URI("/api/book-authors/" + bookAuthorEntity.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, bookAuthorEntity.getId().toString()))
            .body(bookAuthorEntity);
    }

    /**
     * {@code PUT  /book-authors/:id} : Updates an existing bookAuthor.
     *
     * @param id the id of the bookAuthorEntity to save.
     * @param bookAuthorEntity the bookAuthorEntity to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bookAuthorEntity,
     * or with status {@code 400 (Bad Request)} if the bookAuthorEntity is not valid,
     * or with status {@code 500 (Internal Server Error)} if the bookAuthorEntity couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BookAuthorEntity> updateBookAuthor(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BookAuthorEntity bookAuthorEntity
    ) throws URISyntaxException {
        LOG.debug("REST request to update BookAuthor : {}, {}", id, bookAuthorEntity);
        if (bookAuthorEntity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bookAuthorEntity.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bookAuthorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        bookAuthorEntity = bookAuthorService.update(bookAuthorEntity);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bookAuthorEntity.getId().toString()))
            .body(bookAuthorEntity);
    }

    /**
     * {@code PATCH  /book-authors/:id} : Partial updates given fields of an existing bookAuthor, field will ignore if it is null
     *
     * @param id the id of the bookAuthorEntity to save.
     * @param bookAuthorEntity the bookAuthorEntity to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bookAuthorEntity,
     * or with status {@code 400 (Bad Request)} if the bookAuthorEntity is not valid,
     * or with status {@code 404 (Not Found)} if the bookAuthorEntity is not found,
     * or with status {@code 500 (Internal Server Error)} if the bookAuthorEntity couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BookAuthorEntity> partialUpdateBookAuthor(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BookAuthorEntity bookAuthorEntity
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update BookAuthor partially : {}, {}", id, bookAuthorEntity);
        if (bookAuthorEntity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bookAuthorEntity.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bookAuthorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BookAuthorEntity> result = bookAuthorService.partialUpdate(bookAuthorEntity);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bookAuthorEntity.getId().toString())
        );
    }

    /**
     * {@code GET  /book-authors} : get all the bookAuthors.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bookAuthors in body.
     */
    @GetMapping("")
    public ResponseEntity<List<BookAuthorEntity>> getAllBookAuthors(
        BookAuthorCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get BookAuthors by criteria: {}", criteria);

        Page<BookAuthorEntity> page = bookAuthorQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /book-authors/count} : count all the bookAuthors.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countBookAuthors(BookAuthorCriteria criteria) {
        LOG.debug("REST request to count BookAuthors by criteria: {}", criteria);
        return ResponseEntity.ok().body(bookAuthorQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /book-authors/:id} : get the "id" bookAuthor.
     *
     * @param id the id of the bookAuthorEntity to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the bookAuthorEntity, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookAuthorEntity> getBookAuthor(@PathVariable("id") Long id) {
        LOG.debug("REST request to get BookAuthor : {}", id);
        Optional<BookAuthorEntity> bookAuthorEntity = bookAuthorService.findOne(id);
        return ResponseUtil.wrapOrNotFound(bookAuthorEntity);
    }

    /**
     * {@code DELETE  /book-authors/:id} : delete the "id" bookAuthor.
     *
     * @param id the id of the bookAuthorEntity to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookAuthor(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete BookAuthor : {}", id);
        bookAuthorService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
