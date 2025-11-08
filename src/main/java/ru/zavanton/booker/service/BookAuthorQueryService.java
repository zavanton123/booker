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
import ru.zavanton.booker.domain.BookAuthorEntity;
import ru.zavanton.booker.repository.BookAuthorRepository;
import ru.zavanton.booker.service.criteria.BookAuthorCriteria;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link BookAuthorEntity} entities in the database.
 * The main input is a {@link BookAuthorCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link BookAuthorEntity} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BookAuthorQueryService extends QueryService<BookAuthorEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(BookAuthorQueryService.class);

    private final BookAuthorRepository bookAuthorRepository;

    public BookAuthorQueryService(BookAuthorRepository bookAuthorRepository) {
        this.bookAuthorRepository = bookAuthorRepository;
    }

    /**
     * Return a {@link Page} of {@link BookAuthorEntity} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BookAuthorEntity> findByCriteria(BookAuthorCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<BookAuthorEntity> specification = createSpecification(criteria);
        return bookAuthorRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BookAuthorCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<BookAuthorEntity> specification = createSpecification(criteria);
        return bookAuthorRepository.count(specification);
    }

    /**
     * Function to convert {@link BookAuthorCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<BookAuthorEntity> createSpecification(BookAuthorCriteria criteria) {
        Specification<BookAuthorEntity> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), BookAuthorEntity_.id));
            }
            if (criteria.getIsPrimary() != null) {
                specification = specification.and(buildSpecification(criteria.getIsPrimary(), BookAuthorEntity_.isPrimary));
            }
            if (criteria.getOrder() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getOrder(), BookAuthorEntity_.order));
            }
            if (criteria.getBookId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getBookId(), root -> root.join(BookAuthorEntity_.book, JoinType.LEFT).get(BookEntity_.id))
                );
            }
            if (criteria.getAuthorId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getAuthorId(), root ->
                        root.join(BookAuthorEntity_.author, JoinType.LEFT).get(AuthorEntity_.id)
                    )
                );
            }
        }
        return specification;
    }
}
