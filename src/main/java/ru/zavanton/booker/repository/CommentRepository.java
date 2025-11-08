package ru.zavanton.booker.repository;

import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import ru.zavanton.booker.domain.CommentEntity;

/**
 * Spring Data JPA repository for the CommentEntity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long>, JpaSpecificationExecutor<CommentEntity> {
    @Query("select comment from CommentEntity comment where comment.user.login = ?#{authentication.name}")
    List<CommentEntity> findByUserIsCurrentUser();
}
