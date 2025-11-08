package ru.zavanton.booker.repository;

import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import ru.zavanton.booker.domain.CollectionEntity;

/**
 * Spring Data JPA repository for the CollectionEntity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CollectionRepository extends JpaRepository<CollectionEntity, Long>, JpaSpecificationExecutor<CollectionEntity> {
    @Query("select collection from CollectionEntity collection where collection.user.login = ?#{authentication.name}")
    List<CollectionEntity> findByUserIsCurrentUser();
}
