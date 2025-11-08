package ru.zavanton.booker.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A BookCollectionEntity.
 */
@Entity
@Table(name = "book_collection")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BookCollectionEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "position")
    private Integer position;

    @Column(name = "added_at")
    private Instant addedAt;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = { "reviews", "ratings", "readingStatuses", "bookAuthors", "bookGenres", "bookTags", "bookCollections", "publisher" },
        allowSetters = true
    )
    private BookEntity book;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "bookCollections", "user" }, allowSetters = true)
    private CollectionEntity collection;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public BookCollectionEntity id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPosition() {
        return this.position;
    }

    public BookCollectionEntity position(Integer position) {
        this.setPosition(position);
        return this;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Instant getAddedAt() {
        return this.addedAt;
    }

    public BookCollectionEntity addedAt(Instant addedAt) {
        this.setAddedAt(addedAt);
        return this;
    }

    public void setAddedAt(Instant addedAt) {
        this.addedAt = addedAt;
    }

    public BookEntity getBook() {
        return this.book;
    }

    public void setBook(BookEntity book) {
        this.book = book;
    }

    public BookCollectionEntity book(BookEntity book) {
        this.setBook(book);
        return this;
    }

    public CollectionEntity getCollection() {
        return this.collection;
    }

    public void setCollection(CollectionEntity collection) {
        this.collection = collection;
    }

    public BookCollectionEntity collection(CollectionEntity collection) {
        this.setCollection(collection);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BookCollectionEntity)) {
            return false;
        }
        return getId() != null && getId().equals(((BookCollectionEntity) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BookCollectionEntity{" +
            "id=" + getId() +
            ", position=" + getPosition() +
            ", addedAt='" + getAddedAt() + "'" +
            "}";
    }
}
