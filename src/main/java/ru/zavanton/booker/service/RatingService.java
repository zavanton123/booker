package ru.zavanton.booker.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zavanton.booker.domain.RatingEntity;
import ru.zavanton.booker.repository.RatingRepository;

/**
 * Service Implementation for managing {@link ru.zavanton.booker.domain.RatingEntity}.
 */
@Service
@Transactional
public class RatingService {

    private static final Logger LOG = LoggerFactory.getLogger(RatingService.class);

    private final RatingRepository ratingRepository;

    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    /**
     * Save a rating.
     *
     * @param ratingEntity the entity to save.
     * @return the persisted entity.
     */
    public RatingEntity save(RatingEntity ratingEntity) {
        LOG.debug("Request to save Rating : {}", ratingEntity);
        return ratingRepository.save(ratingEntity);
    }

    /**
     * Update a rating.
     *
     * @param ratingEntity the entity to save.
     * @return the persisted entity.
     */
    public RatingEntity update(RatingEntity ratingEntity) {
        LOG.debug("Request to update Rating : {}", ratingEntity);
        return ratingRepository.save(ratingEntity);
    }

    /**
     * Partially update a rating.
     *
     * @param ratingEntity the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<RatingEntity> partialUpdate(RatingEntity ratingEntity) {
        LOG.debug("Request to partially update Rating : {}", ratingEntity);

        return ratingRepository
            .findById(ratingEntity.getId())
            .map(existingRating -> {
                if (ratingEntity.getRating() != null) {
                    existingRating.setRating(ratingEntity.getRating());
                }
                if (ratingEntity.getCreatedAt() != null) {
                    existingRating.setCreatedAt(ratingEntity.getCreatedAt());
                }
                if (ratingEntity.getUpdatedAt() != null) {
                    existingRating.setUpdatedAt(ratingEntity.getUpdatedAt());
                }

                return existingRating;
            })
            .map(ratingRepository::save);
    }

    /**
     * Get one rating by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<RatingEntity> findOne(Long id) {
        LOG.debug("Request to get Rating : {}", id);
        return ratingRepository.findById(id);
    }

    /**
     * Delete the rating by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Rating : {}", id);
        ratingRepository.deleteById(id);
    }
}
