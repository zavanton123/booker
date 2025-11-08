package ru.zavanton.booker.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zavanton.booker.domain.TagEntity;
import ru.zavanton.booker.repository.TagRepository;

/**
 * Service Implementation for managing {@link ru.zavanton.booker.domain.TagEntity}.
 */
@Service
@Transactional
public class TagService {

    private static final Logger LOG = LoggerFactory.getLogger(TagService.class);

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    /**
     * Save a tag.
     *
     * @param tagEntity the entity to save.
     * @return the persisted entity.
     */
    public TagEntity save(TagEntity tagEntity) {
        LOG.debug("Request to save Tag : {}", tagEntity);
        return tagRepository.save(tagEntity);
    }

    /**
     * Update a tag.
     *
     * @param tagEntity the entity to save.
     * @return the persisted entity.
     */
    public TagEntity update(TagEntity tagEntity) {
        LOG.debug("Request to update Tag : {}", tagEntity);
        return tagRepository.save(tagEntity);
    }

    /**
     * Partially update a tag.
     *
     * @param tagEntity the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TagEntity> partialUpdate(TagEntity tagEntity) {
        LOG.debug("Request to partially update Tag : {}", tagEntity);

        return tagRepository
            .findById(tagEntity.getId())
            .map(existingTag -> {
                if (tagEntity.getName() != null) {
                    existingTag.setName(tagEntity.getName());
                }
                if (tagEntity.getSlug() != null) {
                    existingTag.setSlug(tagEntity.getSlug());
                }
                if (tagEntity.getCreatedAt() != null) {
                    existingTag.setCreatedAt(tagEntity.getCreatedAt());
                }

                return existingTag;
            })
            .map(tagRepository::save);
    }

    /**
     * Get one tag by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TagEntity> findOne(Long id) {
        LOG.debug("Request to get Tag : {}", id);
        return tagRepository.findById(id);
    }

    /**
     * Delete the tag by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Tag : {}", id);
        tagRepository.deleteById(id);
    }
}
