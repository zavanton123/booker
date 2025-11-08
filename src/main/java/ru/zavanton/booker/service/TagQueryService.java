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
import ru.zavanton.booker.domain.TagEntity;
import ru.zavanton.booker.repository.TagRepository;
import ru.zavanton.booker.service.criteria.TagCriteria;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link TagEntity} entities in the database.
 * The main input is a {@link TagCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link TagEntity} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TagQueryService extends QueryService<TagEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(TagQueryService.class);

    private final TagRepository tagRepository;

    public TagQueryService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    /**
     * Return a {@link Page} of {@link TagEntity} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TagEntity> findByCriteria(TagCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TagEntity> specification = createSpecification(criteria);
        return tagRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TagCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<TagEntity> specification = createSpecification(criteria);
        return tagRepository.count(specification);
    }

    /**
     * Function to convert {@link TagCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TagEntity> createSpecification(TagCriteria criteria) {
        Specification<TagEntity> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), TagEntity_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), TagEntity_.name));
            }
            if (criteria.getSlug() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSlug(), TagEntity_.slug));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), TagEntity_.createdAt));
            }
            if (criteria.getBookTagId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getBookTagId(), root -> root.join(TagEntity_.bookTags, JoinType.LEFT).get(BookTagEntity_.id)
                    )
                );
            }
        }
        return specification;
    }
}
