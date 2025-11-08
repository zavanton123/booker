package ru.zavanton.booker.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.zavanton.booker.domain.UserEntity;

/**
 * Spring Data JPA repository for the {@link UserEntity} entity.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findOneByActivationKey(String activationKey);
    List<UserEntity> findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant dateTime);
    Optional<UserEntity> findOneByResetKey(String resetKey);
    Optional<UserEntity> findOneByEmailIgnoreCase(String email);
    Optional<UserEntity> findOneByLogin(String login);

    @EntityGraph(attributePaths = "authorities")
    Optional<UserEntity> findOneWithAuthoritiesByLogin(String login);

    @EntityGraph(attributePaths = "authorities")
    Optional<UserEntity> findOneWithAuthoritiesByEmailIgnoreCase(String email);

    Page<UserEntity> findAllByIdNotNullAndActivatedIsTrue(Pageable pageable);
}
