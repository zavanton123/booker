package ru.zavanton.booker.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import ru.zavanton.booker.domain.BookGenreEntity;

/**
 * Spring Data JPA repository for the BookGenreEntity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BookGenreRepository extends JpaRepository<BookGenreEntity, Long>, JpaSpecificationExecutor<BookGenreEntity> {}
