package ru.zavanton.booker.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import ru.zavanton.booker.domain.GenreEntity;

/**
 * Spring Data JPA repository for the GenreEntity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GenreRepository extends JpaRepository<GenreEntity, Long>, JpaSpecificationExecutor<GenreEntity> {}
