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
import ru.zavanton.booker.domain.BookGenreEntity;
import ru.zavanton.booker.repository.BookGenreRepository;
import ru.zavanton.booker.service.criteria.BookGenreCriteria;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link BookGenreEntity} entities in the database.
 * The main input is a {@link BookGenreCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link BookGenreEntity} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BookGenreQueryService extends QueryService<BookGenreEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(BookGenreQueryService.class);

    private final BookGenreRepository bookGenreRepository;

    public BookGenreQueryService(BookGenreRepository bookGenreRepository) {
        this.bookGenreRepository = bookGenreRepository;
    }

    /**
     * Return a {@link Page} of {@link BookGenreEntity} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BookGenreEntity> findByCriteria(BookGenreCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<BookGenreEntity> specification = createSpecification(criteria);
        return bookGenreRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BookGenreCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<BookGenreEntity> specification = createSpecification(criteria);
        return bookGenreRepository.count(specification);
    }

    /**
     * Function to convert {@link BookGenreCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<BookGenreEntity> createSpecification(BookGenreCriteria criteria) {
        Specification<BookGenreEntity> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), BookGenreEntity_.id));
            }
            if (criteria.getBookId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getBookId(), root -> root.join(BookGenreEntity_.book, JoinType.LEFT).get(BookEntity_.id))
                );
            }
            if (criteria.getGenreId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getGenreId(), root -> root.join(BookGenreEntity_.genre, JoinType.LEFT).get(GenreEntity_.id))
                );
            }
        }
        return specification;
    }
}
