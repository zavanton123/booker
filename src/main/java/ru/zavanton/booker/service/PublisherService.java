package ru.zavanton.booker.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zavanton.booker.domain.PublisherEntity;
import ru.zavanton.booker.repository.PublisherRepository;

/**
 * Service Implementation for managing {@link ru.zavanton.booker.domain.PublisherEntity}.
 */
@Service
@Transactional
public class PublisherService {

    private static final Logger LOG = LoggerFactory.getLogger(PublisherService.class);

    private final PublisherRepository publisherRepository;

    public PublisherService(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    /**
     * Save a publisher.
     *
     * @param publisherEntity the entity to save.
     * @return the persisted entity.
     */
    public PublisherEntity save(PublisherEntity publisherEntity) {
        LOG.debug("Request to save Publisher : {}", publisherEntity);
        return publisherRepository.save(publisherEntity);
    }

    /**
     * Update a publisher.
     *
     * @param publisherEntity the entity to save.
     * @return the persisted entity.
     */
    public PublisherEntity update(PublisherEntity publisherEntity) {
        LOG.debug("Request to update Publisher : {}", publisherEntity);
        return publisherRepository.save(publisherEntity);
    }

    /**
     * Partially update a publisher.
     *
     * @param publisherEntity the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PublisherEntity> partialUpdate(PublisherEntity publisherEntity) {
        LOG.debug("Request to partially update Publisher : {}", publisherEntity);

        return publisherRepository
            .findById(publisherEntity.getId())
            .map(existingPublisher -> {
                if (publisherEntity.getName() != null) {
                    existingPublisher.setName(publisherEntity.getName());
                }
                if (publisherEntity.getWebsiteUrl() != null) {
                    existingPublisher.setWebsiteUrl(publisherEntity.getWebsiteUrl());
                }
                if (publisherEntity.getLogoUrl() != null) {
                    existingPublisher.setLogoUrl(publisherEntity.getLogoUrl());
                }
                if (publisherEntity.getFoundedDate() != null) {
                    existingPublisher.setFoundedDate(publisherEntity.getFoundedDate());
                }
                if (publisherEntity.getCreatedAt() != null) {
                    existingPublisher.setCreatedAt(publisherEntity.getCreatedAt());
                }
                if (publisherEntity.getUpdatedAt() != null) {
                    existingPublisher.setUpdatedAt(publisherEntity.getUpdatedAt());
                }

                return existingPublisher;
            })
            .map(publisherRepository::save);
    }

    /**
     * Get one publisher by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PublisherEntity> findOne(Long id) {
        LOG.debug("Request to get Publisher : {}", id);
        return publisherRepository.findById(id);
    }

    /**
     * Delete the publisher by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Publisher : {}", id);
        publisherRepository.deleteById(id);
    }
}
