package ru.zavanton.booker.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zavanton.booker.domain.BookCollectionEntity;
import ru.zavanton.booker.repository.BookCollectionRepository;

/**
 * Service Implementation for managing {@link ru.zavanton.booker.domain.BookCollectionEntity}.
 */
@Service
@Transactional
public class BookCollectionService {

    private static final Logger LOG = LoggerFactory.getLogger(BookCollectionService.class);

    private final BookCollectionRepository bookCollectionRepository;

    public BookCollectionService(BookCollectionRepository bookCollectionRepository) {
        this.bookCollectionRepository = bookCollectionRepository;
    }

    /**
     * Save a bookCollection.
     *
     * @param bookCollectionEntity the entity to save.
     * @return the persisted entity.
     */
    public BookCollectionEntity save(BookCollectionEntity bookCollectionEntity) {
        LOG.debug("Request to save BookCollection : {}", bookCollectionEntity);
        return bookCollectionRepository.save(bookCollectionEntity);
    }

    /**
     * Update a bookCollection.
     *
     * @param bookCollectionEntity the entity to save.
     * @return the persisted entity.
     */
    public BookCollectionEntity update(BookCollectionEntity bookCollectionEntity) {
        LOG.debug("Request to update BookCollection : {}", bookCollectionEntity);
        return bookCollectionRepository.save(bookCollectionEntity);
    }

    /**
     * Partially update a bookCollection.
     *
     * @param bookCollectionEntity the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BookCollectionEntity> partialUpdate(BookCollectionEntity bookCollectionEntity) {
        LOG.debug("Request to partially update BookCollection : {}", bookCollectionEntity);

        return bookCollectionRepository
            .findById(bookCollectionEntity.getId())
            .map(existingBookCollection -> {
                if (bookCollectionEntity.getPosition() != null) {
                    existingBookCollection.setPosition(bookCollectionEntity.getPosition());
                }
                if (bookCollectionEntity.getAddedAt() != null) {
                    existingBookCollection.setAddedAt(bookCollectionEntity.getAddedAt());
                }

                return existingBookCollection;
            })
            .map(bookCollectionRepository::save);
    }

    /**
     * Get one bookCollection by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BookCollectionEntity> findOne(Long id) {
        LOG.debug("Request to get BookCollection : {}", id);
        return bookCollectionRepository.findById(id);
    }

    /**
     * Delete the bookCollection by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete BookCollection : {}", id);
        bookCollectionRepository.deleteById(id);
    }
}
