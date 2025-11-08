package ru.zavanton.booker.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A ReviewEntity.
 */
@Entity
@Table(name = "review")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReviewEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "contains_spoilers")
    private Boolean containsSpoilers;

    @Column(name = "helpful_count")
    private Integer helpfulCount;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "review")
    @JsonIgnoreProperties(value = { "user", "review" }, allowSetters = true)
    private Set<CommentEntity> comments = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    private UserEntity user;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = { "reviews", "ratings", "readingStatuses", "bookAuthors", "bookGenres", "bookTags", "bookCollections", "publisher" },
        allowSetters = true
    )
    private BookEntity book;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ReviewEntity id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return this.content;
    }

    public ReviewEntity content(String content) {
        this.setContent(content);
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getRating() {
        return this.rating;
    }

    public ReviewEntity rating(Integer rating) {
        this.setRating(rating);
        return this;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Boolean getContainsSpoilers() {
        return this.containsSpoilers;
    }

    public ReviewEntity containsSpoilers(Boolean containsSpoilers) {
        this.setContainsSpoilers(containsSpoilers);
        return this;
    }

    public void setContainsSpoilers(Boolean containsSpoilers) {
        this.containsSpoilers = containsSpoilers;
    }

    public Integer getHelpfulCount() {
        return this.helpfulCount;
    }

    public ReviewEntity helpfulCount(Integer helpfulCount) {
        this.setHelpfulCount(helpfulCount);
        return this;
    }

    public void setHelpfulCount(Integer helpfulCount) {
        this.helpfulCount = helpfulCount;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public ReviewEntity createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public ReviewEntity updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<CommentEntity> getComments() {
        return this.comments;
    }

    public void setComments(Set<CommentEntity> comments) {
        if (this.comments != null) {
            this.comments.forEach(i -> i.setReview(null));
        }
        if (comments != null) {
            comments.forEach(i -> i.setReview(this));
        }
        this.comments = comments;
    }

    public ReviewEntity comments(Set<CommentEntity> comments) {
        this.setComments(comments);
        return this;
    }

    public ReviewEntity addComment(CommentEntity comment) {
        this.comments.add(comment);
        comment.setReview(this);
        return this;
    }

    public ReviewEntity removeComment(CommentEntity comment) {
        this.comments.remove(comment);
        comment.setReview(null);
        return this;
    }

    public UserEntity getUser() {
        return this.user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public ReviewEntity user(UserEntity user) {
        this.setUser(user);
        return this;
    }

    public BookEntity getBook() {
        return this.book;
    }

    public void setBook(BookEntity book) {
        this.book = book;
    }

    public ReviewEntity book(BookEntity book) {
        this.setBook(book);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReviewEntity)) {
            return false;
        }
        return getId() != null && getId().equals(((ReviewEntity) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReviewEntity{" +
            "id=" + getId() +
            ", content='" + getContent() + "'" +
            ", rating=" + getRating() +
            ", containsSpoilers='" + getContainsSpoilers() + "'" +
            ", helpfulCount=" + getHelpfulCount() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
