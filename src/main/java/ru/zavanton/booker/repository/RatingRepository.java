package ru.zavanton.booker.repository;

import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import ru.zavanton.booker.domain.RatingEntity;

/**
 * Spring Data JPA repository for the RatingEntity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RatingRepository extends JpaRepository<RatingEntity, Long>, JpaSpecificationExecutor<RatingEntity> {
    @Query("select rating from RatingEntity rating where rating.user.login = ?#{authentication.name}")
    List<RatingEntity> findByUserIsCurrentUser();
}
