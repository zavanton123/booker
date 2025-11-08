package ru.zavanton.booker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zavanton.booker.domain.*; // for static metamodels
import ru.zavanton.booker.domain.PublisherEntity;
import ru.zavanton.booker.repository.PublisherRepository;
import ru.zavanton.booker.service.criteria.PublisherCriteria;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link PublisherEntity} entities in the database.
 * The main input is a {@link PublisherCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link PublisherEntity} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PublisherQueryService extends QueryService<PublisherEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(PublisherQueryService.class);

    private final PublisherRepository publisherRepository;

    public PublisherQueryService(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    /**
     * Return a {@link Page} of {@link PublisherEntity} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PublisherEntity> findByCriteria(PublisherCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<PublisherEntity> specification = createSpecification(criteria);
        return publisherRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PublisherCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<PublisherEntity> specification = createSpecification(criteria);
        return publisherRepository.count(specification);
    }

    /**
     * Function to convert {@link PublisherCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<PublisherEntity> createSpecification(PublisherCriteria criteria) {
        Specification<PublisherEntity> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), PublisherEntity_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), PublisherEntity_.name));
            }
            if (criteria.getWebsiteUrl() != null) {
                specification = specification.and(buildStringSpecification(criteria.getWebsiteUrl(), PublisherEntity_.websiteUrl));
            }
            if (criteria.getLogoUrl() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLogoUrl(), PublisherEntity_.logoUrl));
            }
            if (criteria.getFoundedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getFoundedDate(), PublisherEntity_.foundedDate));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), PublisherEntity_.createdAt));
            }
            if (criteria.getUpdatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpdatedAt(), PublisherEntity_.updatedAt));
            }
        }
        return specification;
    }
}
