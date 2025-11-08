package ru.zavanton.booker.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zavanton.booker.domain.GenreEntity;
import ru.zavanton.booker.repository.GenreRepository;

/**
 * Service Implementation for managing {@link ru.zavanton.booker.domain.GenreEntity}.
 */
@Service
@Transactional
public class GenreService {

    private static final Logger LOG = LoggerFactory.getLogger(GenreService.class);

    private final GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    /**
     * Save a genre.
     *
     * @param genreEntity the entity to save.
     * @return the persisted entity.
     */
    public GenreEntity save(GenreEntity genreEntity) {
        LOG.debug("Request to save Genre : {}", genreEntity);
        return genreRepository.save(genreEntity);
    }

    /**
     * Update a genre.
     *
     * @param genreEntity the entity to save.
     * @return the persisted entity.
     */
    public GenreEntity update(GenreEntity genreEntity) {
        LOG.debug("Request to update Genre : {}", genreEntity);
        return genreRepository.save(genreEntity);
    }

    /**
     * Partially update a genre.
     *
     * @param genreEntity the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<GenreEntity> partialUpdate(GenreEntity genreEntity) {
        LOG.debug("Request to partially update Genre : {}", genreEntity);

        return genreRepository
            .findById(genreEntity.getId())
            .map(existingGenre -> {
                if (genreEntity.getName() != null) {
                    existingGenre.setName(genreEntity.getName());
                }
                if (genreEntity.getSlug() != null) {
                    existingGenre.setSlug(genreEntity.getSlug());
                }
                if (genreEntity.getDescription() != null) {
                    existingGenre.setDescription(genreEntity.getDescription());
                }
                if (genreEntity.getCreatedAt() != null) {
                    existingGenre.setCreatedAt(genreEntity.getCreatedAt());
                }
                if (genreEntity.getUpdatedAt() != null) {
                    existingGenre.setUpdatedAt(genreEntity.getUpdatedAt());
                }

                return existingGenre;
            })
            .map(genreRepository::save);
    }

    /**
     * Get one genre by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<GenreEntity> findOne(Long id) {
        LOG.debug("Request to get Genre : {}", id);
        return genreRepository.findById(id);
    }

    /**
     * Delete the genre by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Genre : {}", id);
        genreRepository.deleteById(id);
    }
}
