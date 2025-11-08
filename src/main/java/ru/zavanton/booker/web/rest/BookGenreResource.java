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
import ru.zavanton.booker.domain.BookGenreEntity;
import ru.zavanton.booker.repository.BookGenreRepository;
import ru.zavanton.booker.service.BookGenreQueryService;
import ru.zavanton.booker.service.BookGenreService;
import ru.zavanton.booker.service.criteria.BookGenreCriteria;
import ru.zavanton.booker.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link ru.zavanton.booker.domain.BookGenreEntity}.
 */
@RestController
@RequestMapping("/api/book-genres")
public class BookGenreResource {

    private static final Logger LOG = LoggerFactory.getLogger(BookGenreResource.class);

    private static final String ENTITY_NAME = "bookGenre";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BookGenreService bookGenreService;

    private final BookGenreRepository bookGenreRepository;

    private final BookGenreQueryService bookGenreQueryService;

    public BookGenreResource(
        BookGenreService bookGenreService,
        BookGenreRepository bookGenreRepository,
        BookGenreQueryService bookGenreQueryService
    ) {
        this.bookGenreService = bookGenreService;
        this.bookGenreRepository = bookGenreRepository;
        this.bookGenreQueryService = bookGenreQueryService;
    }

    /**
     * {@code POST  /book-genres} : Create a new bookGenre.
     *
     * @param bookGenreEntity the bookGenreEntity to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new bookGenreEntity, or with status {@code 400 (Bad Request)} if the bookGenre has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<BookGenreEntity> createBookGenre(@Valid @RequestBody BookGenreEntity bookGenreEntity) throws URISyntaxException {
        LOG.debug("REST request to save BookGenre : {}", bookGenreEntity);
        if (bookGenreEntity.getId() != null) {
            throw new BadRequestAlertException("A new bookGenre cannot already have an ID", ENTITY_NAME, "idexists");
        }
        bookGenreEntity = bookGenreService.save(bookGenreEntity);
        return ResponseEntity.created(new URI("/api/book-genres/" + bookGenreEntity.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, bookGenreEntity.getId().toString()))
            .body(bookGenreEntity);
    }

    /**
     * {@code PUT  /book-genres/:id} : Updates an existing bookGenre.
     *
     * @param id the id of the bookGenreEntity to save.
     * @param bookGenreEntity the bookGenreEntity to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bookGenreEntity,
     * or with status {@code 400 (Bad Request)} if the bookGenreEntity is not valid,
     * or with status {@code 500 (Internal Server Error)} if the bookGenreEntity couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BookGenreEntity> updateBookGenre(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BookGenreEntity bookGenreEntity
    ) throws URISyntaxException {
        LOG.debug("REST request to update BookGenre : {}, {}", id, bookGenreEntity);
        if (bookGenreEntity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bookGenreEntity.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bookGenreRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        bookGenreEntity = bookGenreService.update(bookGenreEntity);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bookGenreEntity.getId().toString()))
            .body(bookGenreEntity);
    }

    /**
     * {@code PATCH  /book-genres/:id} : Partial updates given fields of an existing bookGenre, field will ignore if it is null
     *
     * @param id the id of the bookGenreEntity to save.
     * @param bookGenreEntity the bookGenreEntity to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bookGenreEntity,
     * or with status {@code 400 (Bad Request)} if the bookGenreEntity is not valid,
     * or with status {@code 404 (Not Found)} if the bookGenreEntity is not found,
     * or with status {@code 500 (Internal Server Error)} if the bookGenreEntity couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BookGenreEntity> partialUpdateBookGenre(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BookGenreEntity bookGenreEntity
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update BookGenre partially : {}, {}", id, bookGenreEntity);
        if (bookGenreEntity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bookGenreEntity.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bookGenreRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BookGenreEntity> result = bookGenreService.partialUpdate(bookGenreEntity);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bookGenreEntity.getId().toString())
        );
    }

    /**
     * {@code GET  /book-genres} : get all the bookGenres.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bookGenres in body.
     */
    @GetMapping("")
    public ResponseEntity<List<BookGenreEntity>> getAllBookGenres(
        BookGenreCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get BookGenres by criteria: {}", criteria);

        Page<BookGenreEntity> page = bookGenreQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /book-genres/count} : count all the bookGenres.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countBookGenres(BookGenreCriteria criteria) {
        LOG.debug("REST request to count BookGenres by criteria: {}", criteria);
        return ResponseEntity.ok().body(bookGenreQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /book-genres/:id} : get the "id" bookGenre.
     *
     * @param id the id of the bookGenreEntity to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the bookGenreEntity, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookGenreEntity> getBookGenre(@PathVariable("id") Long id) {
        LOG.debug("REST request to get BookGenre : {}", id);
        Optional<BookGenreEntity> bookGenreEntity = bookGenreService.findOne(id);
        return ResponseUtil.wrapOrNotFound(bookGenreEntity);
    }

    /**
     * {@code DELETE  /book-genres/:id} : delete the "id" bookGenre.
     *
     * @param id the id of the bookGenreEntity to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookGenre(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete BookGenre : {}", id);
        bookGenreService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
