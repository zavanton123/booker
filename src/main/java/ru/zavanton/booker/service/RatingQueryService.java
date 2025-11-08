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
import ru.zavanton.booker.domain.RatingEntity;
import ru.zavanton.booker.repository.RatingRepository;
import ru.zavanton.booker.service.criteria.RatingCriteria;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link RatingEntity} entities in the database.
 * The main input is a {@link RatingCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link RatingEntity} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class RatingQueryService extends QueryService<RatingEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(RatingQueryService.class);

    private final RatingRepository ratingRepository;

    public RatingQueryService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    /**
     * Return a {@link Page} of {@link RatingEntity} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<RatingEntity> findByCriteria(RatingCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<RatingEntity> specification = createSpecification(criteria);
        return ratingRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(RatingCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<RatingEntity> specification = createSpecification(criteria);
        return ratingRepository.count(specification);
    }

    /**
     * Function to convert {@link RatingCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<RatingEntity> createSpecification(RatingCriteria criteria) {
        Specification<RatingEntity> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), RatingEntity_.id));
            }
            if (criteria.getRating() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getRating(), RatingEntity_.rating));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), RatingEntity_.createdAt));
            }
            if (criteria.getUpdatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpdatedAt(), RatingEntity_.updatedAt));
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getUserId(), root -> root.join(RatingEntity_.user, JoinType.LEFT).get(UserEntity_.id))
                );
            }
            if (criteria.getBookId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getBookId(), root -> root.join(RatingEntity_.book, JoinType.LEFT).get(BookEntity_.id))
                );
            }
        }
        return specification;
    }
}
