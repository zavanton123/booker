package ru.zavanton.booker.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A CollectionEntity.
 */
@Entity
@Table(name = "collection")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CollectionEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "is_public")
    private Boolean isPublic;

    @Column(name = "book_count")
    private Integer bookCount;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "collection")
    @JsonIgnoreProperties(value = { "book", "collection" }, allowSetters = true)
    private Set<BookCollectionEntity> bookCollections = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    private UserEntity user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CollectionEntity id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public CollectionEntity name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public CollectionEntity description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsPublic() {
        return this.isPublic;
    }

    public CollectionEntity isPublic(Boolean isPublic) {
        this.setIsPublic(isPublic);
        return this;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Integer getBookCount() {
        return this.bookCount;
    }

    public CollectionEntity bookCount(Integer bookCount) {
        this.setBookCount(bookCount);
        return this;
    }

    public void setBookCount(Integer bookCount) {
        this.bookCount = bookCount;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public CollectionEntity createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public CollectionEntity updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<BookCollectionEntity> getBookCollections() {
        return this.bookCollections;
    }

    public void setBookCollections(Set<BookCollectionEntity> bookCollections) {
        if (this.bookCollections != null) {
            this.bookCollections.forEach(i -> i.setCollection(null));
        }
        if (bookCollections != null) {
            bookCollections.forEach(i -> i.setCollection(this));
        }
        this.bookCollections = bookCollections;
    }

    public CollectionEntity bookCollections(Set<BookCollectionEntity> bookCollections) {
        this.setBookCollections(bookCollections);
        return this;
    }

    public CollectionEntity addBookCollection(BookCollectionEntity bookCollection) {
        this.bookCollections.add(bookCollection);
        bookCollection.setCollection(this);
        return this;
    }

    public CollectionEntity removeBookCollection(BookCollectionEntity bookCollection) {
        this.bookCollections.remove(bookCollection);
        bookCollection.setCollection(null);
        return this;
    }

    public UserEntity getUser() {
        return this.user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public CollectionEntity user(UserEntity user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CollectionEntity)) {
            return false;
        }
        return getId() != null && getId().equals(((CollectionEntity) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CollectionEntity{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", isPublic='" + getIsPublic() + "'" +
            ", bookCount=" + getBookCount() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
