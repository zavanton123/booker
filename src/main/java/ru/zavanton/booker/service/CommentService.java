package ru.zavanton.booker.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zavanton.booker.domain.CommentEntity;
import ru.zavanton.booker.repository.CommentRepository;

/**
 * Service Implementation for managing {@link ru.zavanton.booker.domain.CommentEntity}.
 */
@Service
@Transactional
public class CommentService {

    private static final Logger LOG = LoggerFactory.getLogger(CommentService.class);

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    /**
     * Save a comment.
     *
     * @param commentEntity the entity to save.
     * @return the persisted entity.
     */
    public CommentEntity save(CommentEntity commentEntity) {
        LOG.debug("Request to save Comment : {}", commentEntity);
        return commentRepository.save(commentEntity);
    }

    /**
     * Update a comment.
     *
     * @param commentEntity the entity to save.
     * @return the persisted entity.
     */
    public CommentEntity update(CommentEntity commentEntity) {
        LOG.debug("Request to update Comment : {}", commentEntity);
        return commentRepository.save(commentEntity);
    }

    /**
     * Partially update a comment.
     *
     * @param commentEntity the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CommentEntity> partialUpdate(CommentEntity commentEntity) {
        LOG.debug("Request to partially update Comment : {}", commentEntity);

        return commentRepository
            .findById(commentEntity.getId())
            .map(existingComment -> {
                if (commentEntity.getContent() != null) {
                    existingComment.setContent(commentEntity.getContent());
                }
                if (commentEntity.getCreatedAt() != null) {
                    existingComment.setCreatedAt(commentEntity.getCreatedAt());
                }
                if (commentEntity.getUpdatedAt() != null) {
                    existingComment.setUpdatedAt(commentEntity.getUpdatedAt());
                }

                return existingComment;
            })
            .map(commentRepository::save);
    }

    /**
     * Get one comment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CommentEntity> findOne(Long id) {
        LOG.debug("Request to get Comment : {}", id);
        return commentRepository.findById(id);
    }

    /**
     * Delete the comment by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Comment : {}", id);
        commentRepository.deleteById(id);
    }
}
