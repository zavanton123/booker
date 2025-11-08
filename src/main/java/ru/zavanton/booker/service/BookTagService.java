package ru.zavanton.booker.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zavanton.booker.domain.BookTagEntity;
import ru.zavanton.booker.repository.BookTagRepository;

/**
 * Service Implementation for managing {@link ru.zavanton.booker.domain.BookTagEntity}.
 */
@Service
@Transactional
public class BookTagService {

    private static final Logger LOG = LoggerFactory.getLogger(BookTagService.class);

    private final BookTagRepository bookTagRepository;

    public BookTagService(BookTagRepository bookTagRepository) {
        this.bookTagRepository = bookTagRepository;
    }

    /**
     * Save a bookTag.
     *
     * @param bookTagEntity the entity to save.
     * @return the persisted entity.
     */
    public BookTagEntity save(BookTagEntity bookTagEntity) {
        LOG.debug("Request to save BookTag : {}", bookTagEntity);
        return bookTagRepository.save(bookTagEntity);
    }

    /**
     * Update a bookTag.
     *
     * @param bookTagEntity the entity to save.
     * @return the persisted entity.
     */
    public BookTagEntity update(BookTagEntity bookTagEntity) {
        LOG.debug("Request to update BookTag : {}", bookTagEntity);
        return bookTagRepository.save(bookTagEntity);
    }

    /**
     * Partially update a bookTag.
     *
     * @param bookTagEntity the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BookTagEntity> partialUpdate(BookTagEntity bookTagEntity) {
        LOG.debug("Request to partially update BookTag : {}", bookTagEntity);

        return bookTagRepository.findById(bookTagEntity.getId()).map(bookTagRepository::save);
    }

    /**
     * Get one bookTag by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BookTagEntity> findOne(Long id) {
        LOG.debug("Request to get BookTag : {}", id);
        return bookTagRepository.findById(id);
    }

    /**
     * Delete the bookTag by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete BookTag : {}", id);
        bookTagRepository.deleteById(id);
    }
}
