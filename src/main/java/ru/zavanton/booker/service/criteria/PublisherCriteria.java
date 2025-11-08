package ru.zavanton.booker.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link ru.zavanton.booker.domain.PublisherEntity} entity. This class is used
 * in {@link ru.zavanton.booker.web.rest.PublisherResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /publishers?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PublisherCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter websiteUrl;

    private StringFilter logoUrl;

    private LocalDateFilter foundedDate;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private Boolean distinct;

    public PublisherCriteria() {}

    public PublisherCriteria(PublisherCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.websiteUrl = other.optionalWebsiteUrl().map(StringFilter::copy).orElse(null);
        this.logoUrl = other.optionalLogoUrl().map(StringFilter::copy).orElse(null);
        this.foundedDate = other.optionalFoundedDate().map(LocalDateFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public PublisherCriteria copy() {
        return new PublisherCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public Optional<StringFilter> optionalName() {
        return Optional.ofNullable(name);
    }

    public StringFilter name() {
        if (name == null) {
            setName(new StringFilter());
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getWebsiteUrl() {
        return websiteUrl;
    }

    public Optional<StringFilter> optionalWebsiteUrl() {
        return Optional.ofNullable(websiteUrl);
    }

    public StringFilter websiteUrl() {
        if (websiteUrl == null) {
            setWebsiteUrl(new StringFilter());
        }
        return websiteUrl;
    }

    public void setWebsiteUrl(StringFilter websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public StringFilter getLogoUrl() {
        return logoUrl;
    }

    public Optional<StringFilter> optionalLogoUrl() {
        return Optional.ofNullable(logoUrl);
    }

    public StringFilter logoUrl() {
        if (logoUrl == null) {
            setLogoUrl(new StringFilter());
        }
        return logoUrl;
    }

    public void setLogoUrl(StringFilter logoUrl) {
        this.logoUrl = logoUrl;
    }

    public LocalDateFilter getFoundedDate() {
        return foundedDate;
    }

    public Optional<LocalDateFilter> optionalFoundedDate() {
        return Optional.ofNullable(foundedDate);
    }

    public LocalDateFilter foundedDate() {
        if (foundedDate == null) {
            setFoundedDate(new LocalDateFilter());
        }
        return foundedDate;
    }

    public void setFoundedDate(LocalDateFilter foundedDate) {
        this.foundedDate = foundedDate;
    }

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public Optional<InstantFilter> optionalCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            setCreatedAt(new InstantFilter());
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public InstantFilter getUpdatedAt() {
        return updatedAt;
    }

    public Optional<InstantFilter> optionalUpdatedAt() {
        return Optional.ofNullable(updatedAt);
    }

    public InstantFilter updatedAt() {
        if (updatedAt == null) {
            setUpdatedAt(new InstantFilter());
        }
        return updatedAt;
    }

    public void setUpdatedAt(InstantFilter updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PublisherCriteria that = (PublisherCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(websiteUrl, that.websiteUrl) &&
            Objects.equals(logoUrl, that.logoUrl) &&
            Objects.equals(foundedDate, that.foundedDate) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, websiteUrl, logoUrl, foundedDate, createdAt, updatedAt, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PublisherCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalWebsiteUrl().map(f -> "websiteUrl=" + f + ", ").orElse("") +
            optionalLogoUrl().map(f -> "logoUrl=" + f + ", ").orElse("") +
            optionalFoundedDate().map(f -> "foundedDate=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
