package ru.zavanton.booker.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zavanton.booker.domain.ReadingStatusEntity;
import ru.zavanton.booker.repository.ReadingStatusRepository;

/**
 * Service Implementation for managing {@link ru.zavanton.booker.domain.ReadingStatusEntity}.
 */
@Service
@Transactional
public class ReadingStatusService {

    private static final Logger LOG = LoggerFactory.getLogger(ReadingStatusService.class);

    private final ReadingStatusRepository readingStatusRepository;

    public ReadingStatusService(ReadingStatusRepository readingStatusRepository) {
        this.readingStatusRepository = readingStatusRepository;
    }

    /**
     * Save a readingStatus.
     *
     * @param readingStatusEntity the entity to save.
     * @return the persisted entity.
     */
    public ReadingStatusEntity save(ReadingStatusEntity readingStatusEntity) {
        LOG.debug("Request to save ReadingStatus : {}", readingStatusEntity);
        return readingStatusRepository.save(readingStatusEntity);
    }

    /**
     * Update a readingStatus.
     *
     * @param readingStatusEntity the entity to save.
     * @return the persisted entity.
     */
    public ReadingStatusEntity update(ReadingStatusEntity readingStatusEntity) {
        LOG.debug("Request to update ReadingStatus : {}", readingStatusEntity);
        return readingStatusRepository.save(readingStatusEntity);
    }

    /**
     * Partially update a readingStatus.
     *
     * @param readingStatusEntity the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ReadingStatusEntity> partialUpdate(ReadingStatusEntity readingStatusEntity) {
        LOG.debug("Request to partially update ReadingStatus : {}", readingStatusEntity);

        return readingStatusRepository
            .findById(readingStatusEntity.getId())
            .map(existingReadingStatus -> {
                if (readingStatusEntity.getStatus() != null) {
                    existingReadingStatus.setStatus(readingStatusEntity.getStatus());
                }
                if (readingStatusEntity.getStartedDate() != null) {
                    existingReadingStatus.setStartedDate(readingStatusEntity.getStartedDate());
                }
                if (readingStatusEntity.getFinishedDate() != null) {
                    existingReadingStatus.setFinishedDate(readingStatusEntity.getFinishedDate());
                }
                if (readingStatusEntity.getCurrentPage() != null) {
                    existingReadingStatus.setCurrentPage(readingStatusEntity.getCurrentPage());
                }
                if (readingStatusEntity.getCreatedAt() != null) {
                    existingReadingStatus.setCreatedAt(readingStatusEntity.getCreatedAt());
                }
                if (readingStatusEntity.getUpdatedAt() != null) {
                    existingReadingStatus.setUpdatedAt(readingStatusEntity.getUpdatedAt());
                }

                return existingReadingStatus;
            })
            .map(readingStatusRepository::save);
    }

    /**
     * Get one readingStatus by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ReadingStatusEntity> findOne(Long id) {
        LOG.debug("Request to get ReadingStatus : {}", id);
        return readingStatusRepository.findById(id);
    }

    /**
     * Delete the readingStatus by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ReadingStatus : {}", id);
        readingStatusRepository.deleteById(id);
    }
}
