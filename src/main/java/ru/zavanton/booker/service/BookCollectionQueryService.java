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
import ru.zavanton.booker.domain.BookCollectionEntity;
import ru.zavanton.booker.repository.BookCollectionRepository;
import ru.zavanton.booker.service.criteria.BookCollectionCriteria;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link BookCollectionEntity} entities in the database.
 * The main input is a {@link BookCollectionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link BookCollectionEntity} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BookCollectionQueryService extends QueryService<BookCollectionEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(BookCollectionQueryService.class);

    private final BookCollectionRepository bookCollectionRepository;

    public BookCollectionQueryService(BookCollectionRepository bookCollectionRepository) {
        this.bookCollectionRepository = bookCollectionRepository;
    }

    /**
     * Return a {@link Page} of {@link BookCollectionEntity} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BookCollectionEntity> findByCriteria(BookCollectionCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<BookCollectionEntity> specification = createSpecification(criteria);
        return bookCollectionRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BookCollectionCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<BookCollectionEntity> specification = createSpecification(criteria);
        return bookCollectionRepository.count(specification);
    }

    /**
     * Function to convert {@link BookCollectionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<BookCollectionEntity> createSpecification(BookCollectionCriteria criteria) {
        Specification<BookCollectionEntity> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), BookCollectionEntity_.id));
            }
            if (criteria.getPosition() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPosition(), BookCollectionEntity_.position));
            }
            if (criteria.getAddedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAddedAt(), BookCollectionEntity_.addedAt));
            }
            if (criteria.getBookId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getBookId(), root ->
                        root.join(BookCollectionEntity_.book, JoinType.LEFT).get(BookEntity_.id)
                    )
                );
            }
            if (criteria.getCollectionId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getCollectionId(), root ->
                        root.join(BookCollectionEntity_.collection, JoinType.LEFT).get(CollectionEntity_.id)
                    )
                );
            }
        }
        return specification;
    }
}
