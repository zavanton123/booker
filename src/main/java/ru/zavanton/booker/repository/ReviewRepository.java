package ru.zavanton.booker.repository;

import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import ru.zavanton.booker.domain.ReviewEntity;

/**
 * Spring Data JPA repository for the ReviewEntity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long>, JpaSpecificationExecutor<ReviewEntity> {
    @Query("select review from ReviewEntity review where review.user.login = ?#{authentication.name}")
    List<ReviewEntity> findByUserIsCurrentUser();
}
