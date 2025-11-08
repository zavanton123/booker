package ru.zavanton.booker.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import ru.zavanton.booker.domain.TagEntity;

/**
 * Spring Data JPA repository for the TagEntity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TagRepository extends JpaRepository<TagEntity, Long>, JpaSpecificationExecutor<TagEntity> {}
