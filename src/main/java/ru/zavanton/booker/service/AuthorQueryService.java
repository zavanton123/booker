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
import ru.zavanton.booker.domain.AuthorEntity;
import ru.zavanton.booker.repository.AuthorRepository;
import ru.zavanton.booker.service.criteria.AuthorCriteria;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link AuthorEntity} entities in the database.
 * The main input is a {@link AuthorCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link AuthorEntity} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AuthorQueryService extends QueryService<AuthorEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(AuthorQueryService.class);

    private final AuthorRepository authorRepository;

    public AuthorQueryService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    /**
     * Return a {@link Page} of {@link AuthorEntity} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<AuthorEntity> findByCriteria(AuthorCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<AuthorEntity> specification = createSpecification(criteria);
        return authorRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AuthorCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<AuthorEntity> specification = createSpecification(criteria);
        return authorRepository.count(specification);
    }

    /**
     * Function to convert {@link AuthorCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<AuthorEntity> createSpecification(AuthorCriteria criteria) {
        Specification<AuthorEntity> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), AuthorEntity_.id));
            }
            if (criteria.getFirstName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFirstName(), AuthorEntity_.firstName));
            }
            if (criteria.getLastName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastName(), AuthorEntity_.lastName));
            }
            if (criteria.getFullName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFullName(), AuthorEntity_.fullName));
            }
            if (criteria.getPhotoUrl() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPhotoUrl(), AuthorEntity_.photoUrl));
            }
            if (criteria.getBirthDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getBirthDate(), AuthorEntity_.birthDate));
            }
            if (criteria.getDeathDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDeathDate(), AuthorEntity_.deathDate));
            }
            if (criteria.getNationality() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNationality(), AuthorEntity_.nationality));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), AuthorEntity_.createdAt));
            }
            if (criteria.getUpdatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpdatedAt(), AuthorEntity_.updatedAt));
            }
            if (criteria.getBookAuthorId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getBookAuthorId(), root ->
                        root.join(AuthorEntity_.bookAuthors, JoinType.LEFT).get(BookAuthorEntity_.id)
                    )
                );
            }
        }
        return specification;
    }
}
