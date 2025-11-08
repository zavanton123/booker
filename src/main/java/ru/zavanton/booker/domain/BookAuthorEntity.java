package ru.zavanton.booker.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * A BookAuthorEntity.
 */
@Entity
@Table(name = "book_author")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BookAuthorEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "is_primary")
    private Boolean isPrimary;

    @Column(name = "booker_order")
    private Integer order;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = { "reviews", "ratings", "readingStatuses", "bookAuthors", "bookGenres", "bookTags", "bookCollections", "publisher" },
        allowSetters = true
    )
    private BookEntity book;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "bookAuthors" }, allowSetters = true)
    private AuthorEntity author;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public BookAuthorEntity id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getIsPrimary() {
        return this.isPrimary;
    }

    public BookAuthorEntity isPrimary(Boolean isPrimary) {
        this.setIsPrimary(isPrimary);
        return this;
    }

    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public Integer getOrder() {
        return this.order;
    }

    public BookAuthorEntity order(Integer order) {
        this.setOrder(order);
        return this;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public BookEntity getBook() {
        return this.book;
    }

    public void setBook(BookEntity book) {
        this.book = book;
    }

    public BookAuthorEntity book(BookEntity book) {
        this.setBook(book);
        return this;
    }

    public AuthorEntity getAuthor() {
        return this.author;
    }

    public void setAuthor(AuthorEntity author) {
        this.author = author;
    }

    public BookAuthorEntity author(AuthorEntity author) {
        this.setAuthor(author);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BookAuthorEntity)) {
            return false;
        }
        return getId() != null && getId().equals(((BookAuthorEntity) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BookAuthorEntity{" +
            "id=" + getId() +
            ", isPrimary='" + getIsPrimary() + "'" +
            ", order=" + getOrder() +
            "}";
    }
}
