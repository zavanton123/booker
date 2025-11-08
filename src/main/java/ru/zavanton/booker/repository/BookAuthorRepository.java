package ru.zavanton.booker.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import ru.zavanton.booker.domain.BookAuthorEntity;

/**
 * Spring Data JPA repository for the BookAuthorEntity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BookAuthorRepository extends JpaRepository<BookAuthorEntity, Long>, JpaSpecificationExecutor<BookAuthorEntity> {}
