package ru.zavanton.booker.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A TagEntity.
 */
@Entity
@Table(name = "tag")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TagEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @NotNull
    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "created_at")
    private Instant createdAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tag")
    @JsonIgnoreProperties(value = { "book", "tag" }, allowSetters = true)
    private Set<BookTagEntity> bookTags = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TagEntity id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public TagEntity name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return this.slug;
    }

    public TagEntity slug(String slug) {
        this.setSlug(slug);
        return this;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public TagEntity createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Set<BookTagEntity> getBookTags() {
        return this.bookTags;
    }

    public void setBookTags(Set<BookTagEntity> bookTags) {
        if (this.bookTags != null) {
            this.bookTags.forEach(i -> i.setTag(null));
        }
        if (bookTags != null) {
            bookTags.forEach(i -> i.setTag(this));
        }
        this.bookTags = bookTags;
    }

    public TagEntity bookTags(Set<BookTagEntity> bookTags) {
        this.setBookTags(bookTags);
        return this;
    }

    public TagEntity addBookTag(BookTagEntity bookTag) {
        this.bookTags.add(bookTag);
        bookTag.setTag(this);
        return this;
    }

    public TagEntity removeBookTag(BookTagEntity bookTag) {
        this.bookTags.remove(bookTag);
        bookTag.setTag(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TagEntity)) {
            return false;
        }
        return getId() != null && getId().equals(((TagEntity) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TagEntity{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", slug='" + getSlug() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
