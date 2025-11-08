package ru.zavanton.booker.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zavanton.booker.domain.AuthorEntity;
import ru.zavanton.booker.repository.AuthorRepository;

/**
 * Service Implementation for managing {@link ru.zavanton.booker.domain.AuthorEntity}.
 */
@Service
@Transactional
public class AuthorService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthorService.class);

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    /**
     * Save a author.
     *
     * @param authorEntity the entity to save.
     * @return the persisted entity.
     */
    public AuthorEntity save(AuthorEntity authorEntity) {
        LOG.debug("Request to save Author : {}", authorEntity);
        return authorRepository.save(authorEntity);
    }

    /**
     * Update a author.
     *
     * @param authorEntity the entity to save.
     * @return the persisted entity.
     */
    public AuthorEntity update(AuthorEntity authorEntity) {
        LOG.debug("Request to update Author : {}", authorEntity);
        return authorRepository.save(authorEntity);
    }

    /**
     * Partially update a author.
     *
     * @param authorEntity the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AuthorEntity> partialUpdate(AuthorEntity authorEntity) {
        LOG.debug("Request to partially update Author : {}", authorEntity);

        return authorRepository
            .findById(authorEntity.getId())
            .map(existingAuthor -> {
                if (authorEntity.getFirstName() != null) {
                    existingAuthor.setFirstName(authorEntity.getFirstName());
                }
                if (authorEntity.getLastName() != null) {
                    existingAuthor.setLastName(authorEntity.getLastName());
                }
                if (authorEntity.getFullName() != null) {
                    existingAuthor.setFullName(authorEntity.getFullName());
                }
                if (authorEntity.getBiography() != null) {
                    existingAuthor.setBiography(authorEntity.getBiography());
                }
                if (authorEntity.getPhotoUrl() != null) {
                    existingAuthor.setPhotoUrl(authorEntity.getPhotoUrl());
                }
                if (authorEntity.getBirthDate() != null) {
                    existingAuthor.setBirthDate(authorEntity.getBirthDate());
                }
                if (authorEntity.getDeathDate() != null) {
                    existingAuthor.setDeathDate(authorEntity.getDeathDate());
                }
                if (authorEntity.getNationality() != null) {
                    existingAuthor.setNationality(authorEntity.getNationality());
                }
                if (authorEntity.getCreatedAt() != null) {
                    existingAuthor.setCreatedAt(authorEntity.getCreatedAt());
                }
                if (authorEntity.getUpdatedAt() != null) {
                    existingAuthor.setUpdatedAt(authorEntity.getUpdatedAt());
                }

                return existingAuthor;
            })
            .map(authorRepository::save);
    }

    /**
     * Get one author by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AuthorEntity> findOne(Long id) {
        LOG.debug("Request to get Author : {}", id);
        return authorRepository.findById(id);
    }

    /**
     * Delete the author by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Author : {}", id);
        authorRepository.deleteById(id);
    }
}
