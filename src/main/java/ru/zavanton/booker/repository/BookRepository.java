package ru.zavanton.booker.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import ru.zavanton.booker.domain.BookEntity;

/**
 * Spring Data JPA repository for the BookEntity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long>, JpaSpecificationExecutor<BookEntity> {}
