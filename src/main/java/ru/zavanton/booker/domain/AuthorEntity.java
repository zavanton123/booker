package ru.zavanton.booker.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * A AuthorEntity.
 */
@Entity
@Table(name = "author")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AuthorEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "full_name")
    private String fullName;

    @Lob
    @Column(name = "biography")
    private String biography;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "death_date")
    private LocalDate deathDate;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "author")
    @JsonIgnoreProperties(value = { "book", "author" }, allowSetters = true)
    private Set<BookAuthorEntity> bookAuthors = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AuthorEntity id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public AuthorEntity firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public AuthorEntity lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return this.fullName;
    }

    public AuthorEntity fullName(String fullName) {
        this.setFullName(fullName);
        return this;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBiography() {
        return this.biography;
    }

    public AuthorEntity biography(String biography) {
        this.setBiography(biography);
        return this;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getPhotoUrl() {
        return this.photoUrl;
    }

    public AuthorEntity photoUrl(String photoUrl) {
        this.setPhotoUrl(photoUrl);
        return this;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public LocalDate getBirthDate() {
        return this.birthDate;
    }

    public AuthorEntity birthDate(LocalDate birthDate) {
        this.setBirthDate(birthDate);
        return this;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public LocalDate getDeathDate() {
        return this.deathDate;
    }

    public AuthorEntity deathDate(LocalDate deathDate) {
        this.setDeathDate(deathDate);
        return this;
    }

    public void setDeathDate(LocalDate deathDate) {
        this.deathDate = deathDate;
    }

    public String getNationality() {
        return this.nationality;
    }

    public AuthorEntity nationality(String nationality) {
        this.setNationality(nationality);
        return this;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public AuthorEntity createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public AuthorEntity updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<BookAuthorEntity> getBookAuthors() {
        return this.bookAuthors;
    }

    public void setBookAuthors(Set<BookAuthorEntity> bookAuthors) {
        if (this.bookAuthors != null) {
            this.bookAuthors.forEach(i -> i.setAuthor(null));
        }
        if (bookAuthors != null) {
            bookAuthors.forEach(i -> i.setAuthor(this));
        }
        this.bookAuthors = bookAuthors;
    }

    public AuthorEntity bookAuthors(Set<BookAuthorEntity> bookAuthors) {
        this.setBookAuthors(bookAuthors);
        return this;
    }

    public AuthorEntity addBookAuthor(BookAuthorEntity bookAuthor) {
        this.bookAuthors.add(bookAuthor);
        bookAuthor.setAuthor(this);
        return this;
    }

    public AuthorEntity removeBookAuthor(BookAuthorEntity bookAuthor) {
        this.bookAuthors.remove(bookAuthor);
        bookAuthor.setAuthor(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuthorEntity)) {
            return false;
        }
        return getId() != null && getId().equals(((AuthorEntity) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AuthorEntity{" +
            "id=" + getId() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", fullName='" + getFullName() + "'" +
            ", biography='" + getBiography() + "'" +
            ", photoUrl='" + getPhotoUrl() + "'" +
            ", birthDate='" + getBirthDate() + "'" +
            ", deathDate='" + getDeathDate() + "'" +
            ", nationality='" + getNationality() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
