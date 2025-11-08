package ru.zavanton.booker.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zavanton.booker.domain.BookGenreEntity;
import ru.zavanton.booker.repository.BookGenreRepository;

/**
 * Service Implementation for managing {@link ru.zavanton.booker.domain.BookGenreEntity}.
 */
@Service
@Transactional
public class BookGenreService {

    private static final Logger LOG = LoggerFactory.getLogger(BookGenreService.class);

    private final BookGenreRepository bookGenreRepository;

    public BookGenreService(BookGenreRepository bookGenreRepository) {
        this.bookGenreRepository = bookGenreRepository;
    }

    /**
     * Save a bookGenre.
     *
     * @param bookGenreEntity the entity to save.
     * @return the persisted entity.
     */
    public BookGenreEntity save(BookGenreEntity bookGenreEntity) {
        LOG.debug("Request to save BookGenre : {}", bookGenreEntity);
        return bookGenreRepository.save(bookGenreEntity);
    }

    /**
     * Update a bookGenre.
     *
     * @param bookGenreEntity the entity to save.
     * @return the persisted entity.
     */
    public BookGenreEntity update(BookGenreEntity bookGenreEntity) {
        LOG.debug("Request to update BookGenre : {}", bookGenreEntity);
        return bookGenreRepository.save(bookGenreEntity);
    }

    /**
     * Partially update a bookGenre.
     *
     * @param bookGenreEntity the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BookGenreEntity> partialUpdate(BookGenreEntity bookGenreEntity) {
        LOG.debug("Request to partially update BookGenre : {}", bookGenreEntity);

        return bookGenreRepository.findById(bookGenreEntity.getId()).map(bookGenreRepository::save);
    }

    /**
     * Get one bookGenre by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BookGenreEntity> findOne(Long id) {
        LOG.debug("Request to get BookGenre : {}", id);
        return bookGenreRepository.findById(id);
    }

    /**
     * Delete the bookGenre by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete BookGenre : {}", id);
        bookGenreRepository.deleteById(id);
    }
}
