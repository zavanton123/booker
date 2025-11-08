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
import ru.zavanton.booker.domain.GenreEntity;
import ru.zavanton.booker.repository.GenreRepository;
import ru.zavanton.booker.service.GenreQueryService;
import ru.zavanton.booker.service.GenreService;
import ru.zavanton.booker.service.criteria.GenreCriteria;
import ru.zavanton.booker.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link ru.zavanton.booker.domain.GenreEntity}.
 */
@RestController
@RequestMapping("/api/genres")
public class GenreResource {

    private static final Logger LOG = LoggerFactory.getLogger(GenreResource.class);

    private static final String ENTITY_NAME = "genre";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GenreService genreService;

    private final GenreRepository genreRepository;

    private final GenreQueryService genreQueryService;

    public GenreResource(GenreService genreService, GenreRepository genreRepository, GenreQueryService genreQueryService) {
        this.genreService = genreService;
        this.genreRepository = genreRepository;
        this.genreQueryService = genreQueryService;
    }

    /**
     * {@code POST  /genres} : Create a new genre.
     *
     * @param genreEntity the genreEntity to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new genreEntity, or with status {@code 400 (Bad Request)} if the genre has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<GenreEntity> createGenre(@Valid @RequestBody GenreEntity genreEntity) throws URISyntaxException {
        LOG.debug("REST request to save Genre : {}", genreEntity);
        if (genreEntity.getId() != null) {
            throw new BadRequestAlertException("A new genre cannot already have an ID", ENTITY_NAME, "idexists");
        }
        genreEntity = genreService.save(genreEntity);
        return ResponseEntity.created(new URI("/api/genres/" + genreEntity.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, genreEntity.getId().toString()))
            .body(genreEntity);
    }

    /**
     * {@code PUT  /genres/:id} : Updates an existing genre.
     *
     * @param id the id of the genreEntity to save.
     * @param genreEntity the genreEntity to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated genreEntity,
     * or with status {@code 400 (Bad Request)} if the genreEntity is not valid,
     * or with status {@code 500 (Internal Server Error)} if the genreEntity couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<GenreEntity> updateGenre(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody GenreEntity genreEntity
    ) throws URISyntaxException {
        LOG.debug("REST request to update Genre : {}, {}", id, genreEntity);
        if (genreEntity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, genreEntity.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!genreRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        genreEntity = genreService.update(genreEntity);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, genreEntity.getId().toString()))
            .body(genreEntity);
    }

    /**
     * {@code PATCH  /genres/:id} : Partial updates given fields of an existing genre, field will ignore if it is null
     *
     * @param id the id of the genreEntity to save.
     * @param genreEntity the genreEntity to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated genreEntity,
     * or with status {@code 400 (Bad Request)} if the genreEntity is not valid,
     * or with status {@code 404 (Not Found)} if the genreEntity is not found,
     * or with status {@code 500 (Internal Server Error)} if the genreEntity couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<GenreEntity> partialUpdateGenre(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody GenreEntity genreEntity
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Genre partially : {}, {}", id, genreEntity);
        if (genreEntity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, genreEntity.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!genreRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<GenreEntity> result = genreService.partialUpdate(genreEntity);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, genreEntity.getId().toString())
        );
    }

    /**
     * {@code GET  /genres} : get all the genres.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of genres in body.
     */
    @GetMapping("")
    public ResponseEntity<List<GenreEntity>> getAllGenres(
        GenreCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Genres by criteria: {}", criteria);

        Page<GenreEntity> page = genreQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /genres/count} : count all the genres.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countGenres(GenreCriteria criteria) {
        LOG.debug("REST request to count Genres by criteria: {}", criteria);
        return ResponseEntity.ok().body(genreQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /genres/:id} : get the "id" genre.
     *
     * @param id the id of the genreEntity to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the genreEntity, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<GenreEntity> getGenre(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Genre : {}", id);
        Optional<GenreEntity> genreEntity = genreService.findOne(id);
        return ResponseUtil.wrapOrNotFound(genreEntity);
    }

    /**
     * {@code DELETE  /genres/:id} : delete the "id" genre.
     *
     * @param id the id of the genreEntity to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenre(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Genre : {}", id);
        genreService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
