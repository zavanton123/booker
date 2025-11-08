package ru.zavanton.booker.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zavanton.booker.domain.ReviewEntity;
import ru.zavanton.booker.repository.ReviewRepository;

/**
 * Service Implementation for managing {@link ru.zavanton.booker.domain.ReviewEntity}.
 */
@Service
@Transactional
public class ReviewService {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewService.class);

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    /**
     * Save a review.
     *
     * @param reviewEntity the entity to save.
     * @return the persisted entity.
     */
    public ReviewEntity save(ReviewEntity reviewEntity) {
        LOG.debug("Request to save Review : {}", reviewEntity);
        return reviewRepository.save(reviewEntity);
    }

    /**
     * Update a review.
     *
     * @param reviewEntity the entity to save.
     * @return the persisted entity.
     */
    public ReviewEntity update(ReviewEntity reviewEntity) {
        LOG.debug("Request to update Review : {}", reviewEntity);
        return reviewRepository.save(reviewEntity);
    }

    /**
     * Partially update a review.
     *
     * @param reviewEntity the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ReviewEntity> partialUpdate(ReviewEntity reviewEntity) {
        LOG.debug("Request to partially update Review : {}", reviewEntity);

        return reviewRepository
            .findById(reviewEntity.getId())
            .map(existingReview -> {
                if (reviewEntity.getContent() != null) {
                    existingReview.setContent(reviewEntity.getContent());
                }
                if (reviewEntity.getRating() != null) {
                    existingReview.setRating(reviewEntity.getRating());
                }
                if (reviewEntity.getContainsSpoilers() != null) {
                    existingReview.setContainsSpoilers(reviewEntity.getContainsSpoilers());
                }
                if (reviewEntity.getHelpfulCount() != null) {
                    existingReview.setHelpfulCount(reviewEntity.getHelpfulCount());
                }
                if (reviewEntity.getCreatedAt() != null) {
                    existingReview.setCreatedAt(reviewEntity.getCreatedAt());
                }
                if (reviewEntity.getUpdatedAt() != null) {
                    existingReview.setUpdatedAt(reviewEntity.getUpdatedAt());
                }

                return existingReview;
            })
            .map(reviewRepository::save);
    }

    /**
     * Get one review by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ReviewEntity> findOne(Long id) {
        LOG.debug("Request to get Review : {}", id);
        return reviewRepository.findById(id);
    }

    /**
     * Delete the review by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Review : {}", id);
        reviewRepository.deleteById(id);
    }
}
