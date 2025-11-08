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
import ru.zavanton.booker.domain.CollectionEntity;
import ru.zavanton.booker.repository.CollectionRepository;
import ru.zavanton.booker.service.criteria.CollectionCriteria;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link CollectionEntity} entities in the database.
 * The main input is a {@link CollectionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CollectionEntity} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CollectionQueryService extends QueryService<CollectionEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(CollectionQueryService.class);

    private final CollectionRepository collectionRepository;

    public CollectionQueryService(CollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    /**
     * Return a {@link Page} of {@link CollectionEntity} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CollectionEntity> findByCriteria(CollectionCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<CollectionEntity> specification = createSpecification(criteria);
        return collectionRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CollectionCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<CollectionEntity> specification = createSpecification(criteria);
        return collectionRepository.count(specification);
    }

    /**
     * Function to convert {@link CollectionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<CollectionEntity> createSpecification(CollectionCriteria criteria) {
        Specification<CollectionEntity> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), CollectionEntity_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), CollectionEntity_.name));
            }
            if (criteria.getIsPublic() != null) {
                specification = specification.and(buildSpecification(criteria.getIsPublic(), CollectionEntity_.isPublic));
            }
            if (criteria.getBookCount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getBookCount(), CollectionEntity_.bookCount));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), CollectionEntity_.createdAt));
            }
            if (criteria.getUpdatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpdatedAt(), CollectionEntity_.updatedAt));
            }
            if (criteria.getBookCollectionId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getBookCollectionId(), root ->
                        root.join(CollectionEntity_.bookCollections, JoinType.LEFT).get(BookCollectionEntity_.id)
                    )
                );
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getUserId(), root -> root.join(CollectionEntity_.user, JoinType.LEFT).get(UserEntity_.id))
                );
            }
        }
        return specification;
    }
}
