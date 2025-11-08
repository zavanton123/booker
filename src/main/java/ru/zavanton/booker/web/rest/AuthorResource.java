package ru.zavanton.booker.web.rest;

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
import ru.zavanton.booker.domain.AuthorEntity;
import ru.zavanton.booker.repository.AuthorRepository;
import ru.zavanton.booker.service.AuthorQueryService;
import ru.zavanton.booker.service.AuthorService;
import ru.zavanton.booker.service.criteria.AuthorCriteria;
import ru.zavanton.booker.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link ru.zavanton.booker.domain.AuthorEntity}.
 */
@RestController
@RequestMapping("/api/authors")
public class AuthorResource {

    private static final Logger LOG = LoggerFactory.getLogger(AuthorResource.class);

    private static final String ENTITY_NAME = "author";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AuthorService authorService;

    private final AuthorRepository authorRepository;

    private final AuthorQueryService authorQueryService;

    public AuthorResource(AuthorService authorService, AuthorRepository authorRepository, AuthorQueryService authorQueryService) {
        this.authorService = authorService;
        this.authorRepository = authorRepository;
        this.authorQueryService = authorQueryService;
    }

    /**
     * {@code POST  /authors} : Create a new author.
     *
     * @param authorEntity the authorEntity to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new authorEntity, or with status {@code 400 (Bad Request)} if the author has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AuthorEntity> createAuthor(@RequestBody AuthorEntity authorEntity) throws URISyntaxException {
        LOG.debug("REST request to save Author : {}", authorEntity);
        if (authorEntity.getId() != null) {
            throw new BadRequestAlertException("A new author cannot already have an ID", ENTITY_NAME, "idexists");
        }
        authorEntity = authorService.save(authorEntity);
        return ResponseEntity.created(new URI("/api/authors/" + authorEntity.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, authorEntity.getId().toString()))
            .body(authorEntity);
    }

    /**
     * {@code PUT  /authors/:id} : Updates an existing author.
     *
     * @param id the id of the authorEntity to save.
     * @param authorEntity the authorEntity to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated authorEntity,
     * or with status {@code 400 (Bad Request)} if the authorEntity is not valid,
     * or with status {@code 500 (Internal Server Error)} if the authorEntity couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AuthorEntity> updateAuthor(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AuthorEntity authorEntity
    ) throws URISyntaxException {
        LOG.debug("REST request to update Author : {}, {}", id, authorEntity);
        if (authorEntity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, authorEntity.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!authorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        authorEntity = authorService.update(authorEntity);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, authorEntity.getId().toString()))
            .body(authorEntity);
    }

    /**
     * {@code PATCH  /authors/:id} : Partial updates given fields of an existing author, field will ignore if it is null
     *
     * @param id the id of the authorEntity to save.
     * @param authorEntity the authorEntity to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated authorEntity,
     * or with status {@code 400 (Bad Request)} if the authorEntity is not valid,
     * or with status {@code 404 (Not Found)} if the authorEntity is not found,
     * or with status {@code 500 (Internal Server Error)} if the authorEntity couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AuthorEntity> partialUpdateAuthor(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AuthorEntity authorEntity
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Author partially : {}, {}", id, authorEntity);
        if (authorEntity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, authorEntity.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!authorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AuthorEntity> result = authorService.partialUpdate(authorEntity);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, authorEntity.getId().toString())
        );
    }

    /**
     * {@code GET  /authors} : get all the authors.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of authors in body.
     */
    @GetMapping("")
    public ResponseEntity<List<AuthorEntity>> getAllAuthors(
        AuthorCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Authors by criteria: {}", criteria);

        Page<AuthorEntity> page = authorQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /authors/count} : count all the authors.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countAuthors(AuthorCriteria criteria) {
        LOG.debug("REST request to count Authors by criteria: {}", criteria);
        return ResponseEntity.ok().body(authorQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /authors/:id} : get the "id" author.
     *
     * @param id the id of the authorEntity to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the authorEntity, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AuthorEntity> getAuthor(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Author : {}", id);
        Optional<AuthorEntity> authorEntity = authorService.findOne(id);
        return ResponseUtil.wrapOrNotFound(authorEntity);
    }

    /**
     * {@code DELETE  /authors/:id} : delete the "id" author.
     *
     * @param id the id of the authorEntity to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Author : {}", id);
        authorService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
