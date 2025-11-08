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
import ru.zavanton.booker.domain.BookEntity;
import ru.zavanton.booker.repository.BookRepository;
import ru.zavanton.booker.service.BookQueryService;
import ru.zavanton.booker.service.BookService;
import ru.zavanton.booker.service.criteria.BookCriteria;
import ru.zavanton.booker.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link ru.zavanton.booker.domain.BookEntity}.
 */
@RestController
@RequestMapping("/api/books")
public class BookResource {

    private static final Logger LOG = LoggerFactory.getLogger(BookResource.class);

    private static final String ENTITY_NAME = "book";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BookService bookService;

    private final BookRepository bookRepository;

    private final BookQueryService bookQueryService;

    public BookResource(BookService bookService, BookRepository bookRepository, BookQueryService bookQueryService) {
        this.bookService = bookService;
        this.bookRepository = bookRepository;
        this.bookQueryService = bookQueryService;
    }

    /**
     * {@code POST  /books} : Create a new book.
     *
     * @param bookEntity the bookEntity to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new bookEntity, or with status {@code 400 (Bad Request)} if the book has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<BookEntity> createBook(@Valid @RequestBody BookEntity bookEntity) throws URISyntaxException {
        LOG.debug("REST request to save Book : {}", bookEntity);
        if (bookEntity.getId() != null) {
            throw new BadRequestAlertException("A new book cannot already have an ID", ENTITY_NAME, "idexists");
        }
        bookEntity = bookService.save(bookEntity);
        return ResponseEntity.created(new URI("/api/books/" + bookEntity.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, bookEntity.getId().toString()))
            .body(bookEntity);
    }

    /**
     * {@code PUT  /books/:id} : Updates an existing book.
     *
     * @param id the id of the bookEntity to save.
     * @param bookEntity the bookEntity to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bookEntity,
     * or with status {@code 400 (Bad Request)} if the bookEntity is not valid,
     * or with status {@code 500 (Internal Server Error)} if the bookEntity couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BookEntity> updateBook(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BookEntity bookEntity
    ) throws URISyntaxException {
        LOG.debug("REST request to update Book : {}, {}", id, bookEntity);
        if (bookEntity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bookEntity.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bookRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        bookEntity = bookService.update(bookEntity);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bookEntity.getId().toString()))
            .body(bookEntity);
    }

    /**
     * {@code PATCH  /books/:id} : Partial updates given fields of an existing book, field will ignore if it is null
     *
     * @param id the id of the bookEntity to save.
     * @param bookEntity the bookEntity to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bookEntity,
     * or with status {@code 400 (Bad Request)} if the bookEntity is not valid,
     * or with status {@code 404 (Not Found)} if the bookEntity is not found,
     * or with status {@code 500 (Internal Server Error)} if the bookEntity couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BookEntity> partialUpdateBook(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BookEntity bookEntity
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Book partially : {}, {}", id, bookEntity);
        if (bookEntity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bookEntity.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bookRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BookEntity> result = bookService.partialUpdate(bookEntity);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bookEntity.getId().toString())
        );
    }

    /**
     * {@code GET  /books} : get all the books.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of books in body.
     */
    @GetMapping("")
    public ResponseEntity<List<BookEntity>> getAllBooks(
        BookCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Books by criteria: {}", criteria);

        Page<BookEntity> page = bookQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /books/count} : count all the books.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countBooks(BookCriteria criteria) {
        LOG.debug("REST request to count Books by criteria: {}", criteria);
        return ResponseEntity.ok().body(bookQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /books/:id} : get the "id" book.
     *
     * @param id the id of the bookEntity to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the bookEntity, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookEntity> getBook(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Book : {}", id);
        Optional<BookEntity> bookEntity = bookService.findOne(id);
        return ResponseUtil.wrapOrNotFound(bookEntity);
    }

    /**
     * {@code DELETE  /books/:id} : delete the "id" book.
     *
     * @param id the id of the bookEntity to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Book : {}", id);
        bookService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
