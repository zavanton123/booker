package ru.zavanton.booker.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * A BookTagEntity.
 */
@Entity
@Table(name = "book_tag")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BookTagEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = { "reviews", "ratings", "readingStatuses", "bookAuthors", "bookGenres", "bookTags", "bookCollections", "publisher" },
        allowSetters = true
    )
    private BookEntity book;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "bookTags" }, allowSetters = true)
    private TagEntity tag;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public BookTagEntity id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BookEntity getBook() {
        return this.book;
    }

    public void setBook(BookEntity book) {
        this.book = book;
    }

    public BookTagEntity book(BookEntity book) {
        this.setBook(book);
        return this;
    }

    public TagEntity getTag() {
        return this.tag;
    }

    public void setTag(TagEntity tag) {
        this.tag = tag;
    }

    public BookTagEntity tag(TagEntity tag) {
        this.setTag(tag);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BookTagEntity)) {
            return false;
        }
        return getId() != null && getId().equals(((BookTagEntity) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BookTagEntity{" +
            "id=" + getId() +
            "}";
    }
}
