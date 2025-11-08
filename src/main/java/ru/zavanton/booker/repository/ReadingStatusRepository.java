package ru.zavanton.booker.repository;

import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import ru.zavanton.booker.domain.ReadingStatusEntity;

/**
 * Spring Data JPA repository for the ReadingStatusEntity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ReadingStatusRepository extends JpaRepository<ReadingStatusEntity, Long>, JpaSpecificationExecutor<ReadingStatusEntity> {
    @Query("select readingStatus from ReadingStatusEntity readingStatus where readingStatus.user.login = ?#{authentication.name}")
    List<ReadingStatusEntity> findByUserIsCurrentUser();
}
