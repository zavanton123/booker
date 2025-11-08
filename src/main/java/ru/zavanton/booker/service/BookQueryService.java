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
import ru.zavanton.booker.domain.BookEntity;
import ru.zavanton.booker.repository.BookRepository;
import ru.zavanton.booker.service.criteria.BookCriteria;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link BookEntity} entities in the database.
 * The main input is a {@link BookCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link BookEntity} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BookQueryService extends QueryService<BookEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(BookQueryService.class);

    private final BookRepository bookRepository;

    public BookQueryService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Return a {@link Page} of {@link BookEntity} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BookEntity> findByCriteria(BookCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<BookEntity> specification = createSpecification(criteria);
        return bookRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BookCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<BookEntity> specification = createSpecification(criteria);
        return bookRepository.count(specification);
    }

    /**
     * Function to convert {@link BookCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<BookEntity> createSpecification(BookCriteria criteria) {
        Specification<BookEntity> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), BookEntity_.id));
            }
            if (criteria.getIsbn() != null) {
                specification = specification.and(buildStringSpecification(criteria.getIsbn(), BookEntity_.isbn));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), BookEntity_.title));
            }
            if (criteria.getCoverImageUrl() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCoverImageUrl(), BookEntity_.coverImageUrl));
            }
            if (criteria.getPageCount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPageCount(), BookEntity_.pageCount));
            }
            if (criteria.getPublicationDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPublicationDate(), BookEntity_.publicationDate));
            }
            if (criteria.getLanguage() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLanguage(), BookEntity_.language));
            }
            if (criteria.getAverageRating() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAverageRating(), BookEntity_.averageRating));
            }
            if (criteria.getTotalRatings() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTotalRatings(), BookEntity_.totalRatings));
            }
            if (criteria.getTotalReviews() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTotalReviews(), BookEntity_.totalReviews));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), BookEntity_.createdAt));
            }
            if (criteria.getUpdatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpdatedAt(), BookEntity_.updatedAt));
            }
            if (criteria.getReviewId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getReviewId(), root -> root.join(BookEntity_.reviews, JoinType.LEFT).get(ReviewEntity_.id))
                );
            }
            if (criteria.getRatingId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getRatingId(), root -> root.join(BookEntity_.ratings, JoinType.LEFT).get(RatingEntity_.id))
                );
            }
            if (criteria.getReadingStatusId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getReadingStatusId(), root ->
                        root.join(BookEntity_.readingStatuses, JoinType.LEFT).get(ReadingStatusEntity_.id)
                    )
                );
            }
            if (criteria.getBookAuthorId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getBookAuthorId(), root ->
                        root.join(BookEntity_.bookAuthors, JoinType.LEFT).get(BookAuthorEntity_.id)
                    )
                );
            }
            if (criteria.getBookGenreId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getBookGenreId(), root ->
                        root.join(BookEntity_.bookGenres, JoinType.LEFT).get(BookGenreEntity_.id)
                    )
                );
            }
            if (criteria.getBookTagId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getBookTagId(), root ->
                        root.join(BookEntity_.bookTags, JoinType.LEFT).get(BookTagEntity_.id)
                    )
                );
            }
            if (criteria.getBookCollectionId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getBookCollectionId(), root ->
                        root.join(BookEntity_.bookCollections, JoinType.LEFT).get(BookCollectionEntity_.id)
                    )
                );
            }
            if (criteria.getPublisherId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getPublisherId(), root ->
                        root.join(BookEntity_.publisher, JoinType.LEFT).get(PublisherEntity_.id)
                    )
                );
            }
        }
        return specification;
    }
}
