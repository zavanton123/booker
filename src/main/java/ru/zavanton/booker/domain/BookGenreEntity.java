package ru.zavanton.booker.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * A BookGenreEntity.
 */
@Entity
@Table(name = "book_genre")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BookGenreEntity implements Serializable {

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
    @JsonIgnoreProperties(value = { "bookGenres" }, allowSetters = true)
    private GenreEntity genre;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public BookGenreEntity id(Long id) {
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

    public BookGenreEntity book(BookEntity book) {
        this.setBook(book);
        return this;
    }

    public GenreEntity getGenre() {
        return this.genre;
    }

    public void setGenre(GenreEntity genre) {
        this.genre = genre;
    }

    public BookGenreEntity genre(GenreEntity genre) {
        this.setGenre(genre);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BookGenreEntity)) {
            return false;
        }
        return getId() != null && getId().equals(((BookGenreEntity) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BookGenreEntity{" +
            "id=" + getId() +
            "}";
    }
}
