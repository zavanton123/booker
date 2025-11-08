package ru.zavanton.booker.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * A BookEntity.
 */
@Entity
@Table(name = "book")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BookEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "isbn", nullable = false, unique = true)
    private String isbn;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    @Column(name = "page_count")
    private Integer pageCount;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    @Column(name = "language")
    private String language;

    @Column(name = "average_rating", precision = 21, scale = 2)
    private BigDecimal averageRating;

    @Column(name = "total_ratings")
    private Integer totalRatings;

    @Column(name = "total_reviews")
    private Integer totalReviews;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "book")
    @JsonIgnoreProperties(value = { "comments", "user", "book" }, allowSetters = true)
    private Set<ReviewEntity> reviews = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "book")
    @JsonIgnoreProperties(value = { "user", "book" }, allowSetters = true)
    private Set<RatingEntity> ratings = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "book")
    @JsonIgnoreProperties(value = { "user", "book" }, allowSetters = true)
    private Set<ReadingStatusEntity> readingStatuses = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "book")
    @JsonIgnoreProperties(value = { "book", "author" }, allowSetters = true)
    private Set<BookAuthorEntity> bookAuthors = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "book")
    @JsonIgnoreProperties(value = { "book", "genre" }, allowSetters = true)
    private Set<BookGenreEntity> bookGenres = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "book")
    @JsonIgnoreProperties(value = { "book", "tag" }, allowSetters = true)
    private Set<BookTagEntity> bookTags = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "book")
    @JsonIgnoreProperties(value = { "book", "collection" }, allowSetters = true)
    private Set<BookCollectionEntity> bookCollections = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private PublisherEntity publisher;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public BookEntity id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsbn() {
        return this.isbn;
    }

    public BookEntity isbn(String isbn) {
        this.setIsbn(isbn);
        return this;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return this.title;
    }

    public BookEntity title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public BookEntity description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverImageUrl() {
        return this.coverImageUrl;
    }

    public BookEntity coverImageUrl(String coverImageUrl) {
        this.setCoverImageUrl(coverImageUrl);
        return this;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public Integer getPageCount() {
        return this.pageCount;
    }

    public BookEntity pageCount(Integer pageCount) {
        this.setPageCount(pageCount);
        return this;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public LocalDate getPublicationDate() {
        return this.publicationDate;
    }

    public BookEntity publicationDate(LocalDate publicationDate) {
        this.setPublicationDate(publicationDate);
        return this;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getLanguage() {
        return this.language;
    }

    public BookEntity language(String language) {
        this.setLanguage(language);
        return this;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public BigDecimal getAverageRating() {
        return this.averageRating;
    }

    public BookEntity averageRating(BigDecimal averageRating) {
        this.setAverageRating(averageRating);
        return this;
    }

    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getTotalRatings() {
        return this.totalRatings;
    }

    public BookEntity totalRatings(Integer totalRatings) {
        this.setTotalRatings(totalRatings);
        return this;
    }

    public void setTotalRatings(Integer totalRatings) {
        this.totalRatings = totalRatings;
    }

    public Integer getTotalReviews() {
        return this.totalReviews;
    }

    public BookEntity totalReviews(Integer totalReviews) {
        this.setTotalReviews(totalReviews);
        return this;
    }

    public void setTotalReviews(Integer totalReviews) {
        this.totalReviews = totalReviews;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public BookEntity createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public BookEntity updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<ReviewEntity> getReviews() {
        return this.reviews;
    }

    public void setReviews(Set<ReviewEntity> reviews) {
        if (this.reviews != null) {
            this.reviews.forEach(i -> i.setBook(null));
        }
        if (reviews != null) {
            reviews.forEach(i -> i.setBook(this));
        }
        this.reviews = reviews;
    }

    public BookEntity reviews(Set<ReviewEntity> reviews) {
        this.setReviews(reviews);
        return this;
    }

    public BookEntity addReview(ReviewEntity review) {
        this.reviews.add(review);
        review.setBook(this);
        return this;
    }

    public BookEntity removeReview(ReviewEntity review) {
        this.reviews.remove(review);
        review.setBook(null);
        return this;
    }

    public Set<RatingEntity> getRatings() {
        return this.ratings;
    }

    public void setRatings(Set<RatingEntity> ratings) {
        if (this.ratings != null) {
            this.ratings.forEach(i -> i.setBook(null));
        }
        if (ratings != null) {
            ratings.forEach(i -> i.setBook(this));
        }
        this.ratings = ratings;
    }

    public BookEntity ratings(Set<RatingEntity> ratings) {
        this.setRatings(ratings);
        return this;
    }

    public BookEntity addRating(RatingEntity rating) {
        this.ratings.add(rating);
        rating.setBook(this);
        return this;
    }

    public BookEntity removeRating(RatingEntity rating) {
        this.ratings.remove(rating);
        rating.setBook(null);
        return this;
    }

    public Set<ReadingStatusEntity> getReadingStatuses() {
        return this.readingStatuses;
    }

    public void setReadingStatuses(Set<ReadingStatusEntity> readingStatuses) {
        if (this.readingStatuses != null) {
            this.readingStatuses.forEach(i -> i.setBook(null));
        }
        if (readingStatuses != null) {
            readingStatuses.forEach(i -> i.setBook(this));
        }
        this.readingStatuses = readingStatuses;
    }

    public BookEntity readingStatuses(Set<ReadingStatusEntity> readingStatuses) {
        this.setReadingStatuses(readingStatuses);
        return this;
    }

    public BookEntity addReadingStatus(ReadingStatusEntity readingStatus) {
        this.readingStatuses.add(readingStatus);
        readingStatus.setBook(this);
        return this;
    }

    public BookEntity removeReadingStatus(ReadingStatusEntity readingStatus) {
        this.readingStatuses.remove(readingStatus);
        readingStatus.setBook(null);
        return this;
    }

    public Set<BookAuthorEntity> getBookAuthors() {
        return this.bookAuthors;
    }

    public void setBookAuthors(Set<BookAuthorEntity> bookAuthors) {
        if (this.bookAuthors != null) {
            this.bookAuthors.forEach(i -> i.setBook(null));
        }
        if (bookAuthors != null) {
            bookAuthors.forEach(i -> i.setBook(this));
        }
        this.bookAuthors = bookAuthors;
    }

    public BookEntity bookAuthors(Set<BookAuthorEntity> bookAuthors) {
        this.setBookAuthors(bookAuthors);
        return this;
    }

    public BookEntity addBookAuthor(BookAuthorEntity bookAuthor) {
        this.bookAuthors.add(bookAuthor);
        bookAuthor.setBook(this);
        return this;
    }

    public BookEntity removeBookAuthor(BookAuthorEntity bookAuthor) {
        this.bookAuthors.remove(bookAuthor);
        bookAuthor.setBook(null);
        return this;
    }

    public Set<BookGenreEntity> getBookGenres() {
        return this.bookGenres;
    }

    public void setBookGenres(Set<BookGenreEntity> bookGenres) {
        if (this.bookGenres != null) {
            this.bookGenres.forEach(i -> i.setBook(null));
        }
        if (bookGenres != null) {
            bookGenres.forEach(i -> i.setBook(this));
        }
        this.bookGenres = bookGenres;
    }

    public BookEntity bookGenres(Set<BookGenreEntity> bookGenres) {
        this.setBookGenres(bookGenres);
        return this;
    }

    public BookEntity addBookGenre(BookGenreEntity bookGenre) {
        this.bookGenres.add(bookGenre);
        bookGenre.setBook(this);
        return this;
    }

    public BookEntity removeBookGenre(BookGenreEntity bookGenre) {
        this.bookGenres.remove(bookGenre);
        bookGenre.setBook(null);
        return this;
    }

    public Set<BookTagEntity> getBookTags() {
        return this.bookTags;
    }

    public void setBookTags(Set<BookTagEntity> bookTags) {
        if (this.bookTags != null) {
            this.bookTags.forEach(i -> i.setBook(null));
        }
        if (bookTags != null) {
            bookTags.forEach(i -> i.setBook(this));
        }
        this.bookTags = bookTags;
    }

    public BookEntity bookTags(Set<BookTagEntity> bookTags) {
        this.setBookTags(bookTags);
        return this;
    }

    public BookEntity addBookTag(BookTagEntity bookTag) {
        this.bookTags.add(bookTag);
        bookTag.setBook(this);
        return this;
    }

    public BookEntity removeBookTag(BookTagEntity bookTag) {
        this.bookTags.remove(bookTag);
        bookTag.setBook(null);
        return this;
    }

    public Set<BookCollectionEntity> getBookCollections() {
        return this.bookCollections;
    }

    public void setBookCollections(Set<BookCollectionEntity> bookCollections) {
        if (this.bookCollections != null) {
            this.bookCollections.forEach(i -> i.setBook(null));
        }
        if (bookCollections != null) {
            bookCollections.forEach(i -> i.setBook(this));
        }
        this.bookCollections = bookCollections;
    }

    public BookEntity bookCollections(Set<BookCollectionEntity> bookCollections) {
        this.setBookCollections(bookCollections);
        return this;
    }

    public BookEntity addBookCollection(BookCollectionEntity bookCollection) {
        this.bookCollections.add(bookCollection);
        bookCollection.setBook(this);
        return this;
    }

    public BookEntity removeBookCollection(BookCollectionEntity bookCollection) {
        this.bookCollections.remove(bookCollection);
        bookCollection.setBook(null);
        return this;
    }

    public PublisherEntity getPublisher() {
        return this.publisher;
    }

    public void setPublisher(PublisherEntity publisher) {
        this.publisher = publisher;
    }

    public BookEntity publisher(PublisherEntity publisher) {
        this.setPublisher(publisher);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BookEntity)) {
            return false;
        }
        return getId() != null && getId().equals(((BookEntity) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BookEntity{" +
            "id=" + getId() +
            ", isbn='" + getIsbn() + "'" +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", coverImageUrl='" + getCoverImageUrl() + "'" +
            ", pageCount=" + getPageCount() +
            ", publicationDate='" + getPublicationDate() + "'" +
            ", language='" + getLanguage() + "'" +
            ", averageRating=" + getAverageRating() +
            ", totalRatings=" + getTotalRatings() +
            ", totalReviews=" + getTotalReviews() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
