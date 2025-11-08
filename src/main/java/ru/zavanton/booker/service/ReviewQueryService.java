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
import ru.zavanton.booker.domain.ReviewEntity;
import ru.zavanton.booker.repository.ReviewRepository;
import ru.zavanton.booker.service.criteria.ReviewCriteria;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ReviewEntity} entities in the database.
 * The main input is a {@link ReviewCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ReviewEntity} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ReviewQueryService extends QueryService<ReviewEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewQueryService.class);

    private final ReviewRepository reviewRepository;

    public ReviewQueryService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    /**
     * Return a {@link Page} of {@link ReviewEntity} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ReviewEntity> findByCriteria(ReviewCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ReviewEntity> specification = createSpecification(criteria);
        return reviewRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ReviewCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ReviewEntity> specification = createSpecification(criteria);
        return reviewRepository.count(specification);
    }

    /**
     * Function to convert {@link ReviewCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ReviewEntity> createSpecification(ReviewCriteria criteria) {
        Specification<ReviewEntity> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), ReviewEntity_.id));
            }
            if (criteria.getRating() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getRating(), ReviewEntity_.rating));
            }
            if (criteria.getContainsSpoilers() != null) {
                specification = specification.and(buildSpecification(criteria.getContainsSpoilers(), ReviewEntity_.containsSpoilers));
            }
            if (criteria.getHelpfulCount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getHelpfulCount(), ReviewEntity_.helpfulCount));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), ReviewEntity_.createdAt));
            }
            if (criteria.getUpdatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpdatedAt(), ReviewEntity_.updatedAt));
            }
            if (criteria.getCommentId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getCommentId(), root ->
                        root.join(ReviewEntity_.comments, JoinType.LEFT).get(CommentEntity_.id)
                    )
                );
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getUserId(), root -> root.join(ReviewEntity_.user, JoinType.LEFT).get(UserEntity_.id))
                );
            }
            if (criteria.getBookId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getBookId(), root -> root.join(ReviewEntity_.book, JoinType.LEFT).get(BookEntity_.id))
                );
            }
        }
        return specification;
    }
}
