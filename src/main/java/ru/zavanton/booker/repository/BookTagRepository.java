package ru.zavanton.booker.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import ru.zavanton.booker.domain.BookTagEntity;

/**
 * Spring Data JPA repository for the BookTagEntity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BookTagRepository extends JpaRepository<BookTagEntity, Long>, JpaSpecificationExecutor<BookTagEntity> {}
