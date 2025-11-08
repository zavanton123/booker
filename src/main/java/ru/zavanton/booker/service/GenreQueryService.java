package ru.zavanton.booker.service;

import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zavanton.booker.domain.*; // for static metamodels
import ru.zavanton.booker.domain.GenreEntity;
import ru.zavanton.booker.repository.GenreRepository;
import ru.zavanton.booker.service.criteria.GenreCriteria;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link GenreEntity} entities in the database.
 * The main input is a {@link GenreCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link GenreEntity} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class GenreQueryService extends QueryService<GenreEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(GenreQueryService.class);

    private final GenreRepository genreRepository;

    public GenreQueryService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    /**
     * Return a {@link Page} of {@link GenreEntity} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<GenreEntity> findByCriteria(GenreCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<GenreEntity> specification = createSpecification(criteria);
        return genreRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(GenreCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<GenreEntity> specification = createSpecification(criteria);
        return genreRepository.count(specification);
    }

    /**
     * Function to convert {@link GenreCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<GenreEntity> createSpecification(GenreCriteria criteria) {
        Specification<GenreEntity> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), GenreEntity_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), GenreEntity_.name));
            }
            if (criteria.getSlug() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSlug(), GenreEntity_.slug));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), GenreEntity_.createdAt));
            }
            if (criteria.getUpdatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpdatedAt(), GenreEntity_.updatedAt));
            }
            if (criteria.getBookGenreId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getBookGenreId(), root ->
                        root.join(GenreEntity_.bookGenres, JoinType.LEFT).get(BookGenreEntity_.id)
                    )
                );
            }
        }
        return specification;
    }
}
