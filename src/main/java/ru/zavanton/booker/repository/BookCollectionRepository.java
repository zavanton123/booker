package ru.zavanton.booker.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import ru.zavanton.booker.domain.BookCollectionEntity;

/**
 * Spring Data JPA repository for the BookCollectionEntity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BookCollectionRepository
    extends JpaRepository<BookCollectionEntity, Long>, JpaSpecificationExecutor<BookCollectionEntity> {}
