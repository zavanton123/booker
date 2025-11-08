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
import ru.zavanton.booker.domain.BookTagEntity;
import ru.zavanton.booker.repository.BookTagRepository;
import ru.zavanton.booker.service.criteria.BookTagCriteria;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link BookTagEntity} entities in the database.
 * The main input is a {@link BookTagCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link BookTagEntity} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BookTagQueryService extends QueryService<BookTagEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(BookTagQueryService.class);

    private final BookTagRepository bookTagRepository;

    public BookTagQueryService(BookTagRepository bookTagRepository) {
        this.bookTagRepository = bookTagRepository;
    }

    /**
     * Return a {@link Page} of {@link BookTagEntity} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BookTagEntity> findByCriteria(BookTagCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<BookTagEntity> specification = createSpecification(criteria);
        return bookTagRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BookTagCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<BookTagEntity> specification = createSpecification(criteria);
        return bookTagRepository.count(specification);
    }

    /**
     * Function to convert {@link BookTagCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<BookTagEntity> createSpecification(BookTagCriteria criteria) {
        Specification<BookTagEntity> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), BookTagEntity_.id));
            }
            if (criteria.getBookId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getBookId(), root -> root.join(BookTagEntity_.book, JoinType.LEFT).get(BookEntity_.id))
                );
            }
            if (criteria.getTagId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getTagId(), root -> root.join(BookTagEntity_.tag, JoinType.LEFT).get(TagEntity_.id))
                );
            }
        }
        return specification;
    }
}
