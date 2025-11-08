package ru.zavanton.booker.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zavanton.booker.domain.BookAuthorEntity;
import ru.zavanton.booker.repository.BookAuthorRepository;

/**
 * Service Implementation for managing {@link ru.zavanton.booker.domain.BookAuthorEntity}.
 */
@Service
@Transactional
public class BookAuthorService {

    private static final Logger LOG = LoggerFactory.getLogger(BookAuthorService.class);

    private final BookAuthorRepository bookAuthorRepository;

    public BookAuthorService(BookAuthorRepository bookAuthorRepository) {
        this.bookAuthorRepository = bookAuthorRepository;
    }

    /**
     * Save a bookAuthor.
     *
     * @param bookAuthorEntity the entity to save.
     * @return the persisted entity.
     */
    public BookAuthorEntity save(BookAuthorEntity bookAuthorEntity) {
        LOG.debug("Request to save BookAuthor : {}", bookAuthorEntity);
        return bookAuthorRepository.save(bookAuthorEntity);
    }

    /**
     * Update a bookAuthor.
     *
     * @param bookAuthorEntity the entity to save.
     * @return the persisted entity.
     */
    public BookAuthorEntity update(BookAuthorEntity bookAuthorEntity) {
        LOG.debug("Request to update BookAuthor : {}", bookAuthorEntity);
        return bookAuthorRepository.save(bookAuthorEntity);
    }

    /**
     * Partially update a bookAuthor.
     *
     * @param bookAuthorEntity the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BookAuthorEntity> partialUpdate(BookAuthorEntity bookAuthorEntity) {
        LOG.debug("Request to partially update BookAuthor : {}", bookAuthorEntity);

        return bookAuthorRepository
            .findById(bookAuthorEntity.getId())
            .map(existingBookAuthor -> {
                if (bookAuthorEntity.getIsPrimary() != null) {
                    existingBookAuthor.setIsPrimary(bookAuthorEntity.getIsPrimary());
                }
                if (bookAuthorEntity.getOrder() != null) {
                    existingBookAuthor.setOrder(bookAuthorEntity.getOrder());
                }

                return existingBookAuthor;
            })
            .map(bookAuthorRepository::save);
    }

    /**
     * Get one bookAuthor by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BookAuthorEntity> findOne(Long id) {
        LOG.debug("Request to get BookAuthor : {}", id);
        return bookAuthorRepository.findById(id);
    }

    /**
     * Delete the bookAuthor by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete BookAuthor : {}", id);
        bookAuthorRepository.deleteById(id);
    }
}
