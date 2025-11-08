package ru.zavanton.booker.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import ru.zavanton.booker.domain.PublisherEntity;

/**
 * Spring Data JPA repository for the PublisherEntity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PublisherRepository extends JpaRepository<PublisherEntity, Long>, JpaSpecificationExecutor<PublisherEntity> {}
