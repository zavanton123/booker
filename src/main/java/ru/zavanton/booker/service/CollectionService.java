package ru.zavanton.booker.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zavanton.booker.domain.CollectionEntity;
import ru.zavanton.booker.repository.CollectionRepository;

/**
 * Service Implementation for managing {@link ru.zavanton.booker.domain.CollectionEntity}.
 */
@Service
@Transactional
public class CollectionService {

    private static final Logger LOG = LoggerFactory.getLogger(CollectionService.class);

    private final CollectionRepository collectionRepository;

    public CollectionService(CollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    /**
     * Save a collection.
     *
     * @param collectionEntity the entity to save.
     * @return the persisted entity.
     */
    public CollectionEntity save(CollectionEntity collectionEntity) {
        LOG.debug("Request to save Collection : {}", collectionEntity);
        return collectionRepository.save(collectionEntity);
    }

    /**
     * Update a collection.
     *
     * @param collectionEntity the entity to save.
     * @return the persisted entity.
     */
    public CollectionEntity update(CollectionEntity collectionEntity) {
        LOG.debug("Request to update Collection : {}", collectionEntity);
        return collectionRepository.save(collectionEntity);
    }

    /**
     * Partially update a collection.
     *
     * @param collectionEntity the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CollectionEntity> partialUpdate(CollectionEntity collectionEntity) {
        LOG.debug("Request to partially update Collection : {}", collectionEntity);

        return collectionRepository
            .findById(collectionEntity.getId())
            .map(existingCollection -> {
                if (collectionEntity.getName() != null) {
                    existingCollection.setName(collectionEntity.getName());
                }
                if (collectionEntity.getDescription() != null) {
                    existingCollection.setDescription(collectionEntity.getDescription());
                }
                if (collectionEntity.getIsPublic() != null) {
                    existingCollection.setIsPublic(collectionEntity.getIsPublic());
                }
                if (collectionEntity.getBookCount() != null) {
                    existingCollection.setBookCount(collectionEntity.getBookCount());
                }
                if (collectionEntity.getCreatedAt() != null) {
                    existingCollection.setCreatedAt(collectionEntity.getCreatedAt());
                }
                if (collectionEntity.getUpdatedAt() != null) {
                    existingCollection.setUpdatedAt(collectionEntity.getUpdatedAt());
                }

                return existingCollection;
            })
            .map(collectionRepository::save);
    }

    /**
     * Get one collection by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CollectionEntity> findOne(Long id) {
        LOG.debug("Request to get Collection : {}", id);
        return collectionRepository.findById(id);
    }

    /**
     * Delete the collection by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Collection : {}", id);
        collectionRepository.deleteById(id);
    }
}
