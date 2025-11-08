package ru.zavanton.booker.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.zavanton.booker.domain.BookEntityAsserts.*;
import static ru.zavanton.booker.web.rest.TestUtil.createUpdateProxyForBean;
import static ru.zavanton.booker.web.rest.TestUtil.sameNumber;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.zavanton.booker.IntegrationTest;
import ru.zavanton.booker.domain.BookEntity;
import ru.zavanton.booker.domain.PublisherEntity;
import ru.zavanton.booker.repository.BookRepository;

/**
 * Integration tests for the {@link BookResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BookResourceIT {

    private static final String DEFAULT_ISBN = "AAAAAAAAAA";
    private static final String UPDATED_ISBN = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_COVER_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_COVER_IMAGE_URL = "BBBBBBBBBB";

    private static final Integer DEFAULT_PAGE_COUNT = 1;
    private static final Integer UPDATED_PAGE_COUNT = 2;
    private static final Integer SMALLER_PAGE_COUNT = 1 - 1;

    private static final LocalDate DEFAULT_PUBLICATION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_PUBLICATION_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_PUBLICATION_DATE = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_LANGUAGE = "AAAAAAAAAA";
    private static final String UPDATED_LANGUAGE = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_AVERAGE_RATING = new BigDecimal(1);
    private static final BigDecimal UPDATED_AVERAGE_RATING = new BigDecimal(2);
    private static final BigDecimal SMALLER_AVERAGE_RATING = new BigDecimal(1 - 1);

    private static final Integer DEFAULT_TOTAL_RATINGS = 1;
    private static final Integer UPDATED_TOTAL_RATINGS = 2;
    private static final Integer SMALLER_TOTAL_RATINGS = 1 - 1;

    private static final Integer DEFAULT_TOTAL_REVIEWS = 1;
    private static final Integer UPDATED_TOTAL_REVIEWS = 2;
    private static final Integer SMALLER_TOTAL_REVIEWS = 1 - 1;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/books";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBookMockMvc;

    private BookEntity bookEntity;

    private BookEntity insertedBookEntity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BookEntity createEntity() {
        return new BookEntity()
            .isbn(DEFAULT_ISBN)
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .coverImageUrl(DEFAULT_COVER_IMAGE_URL)
            .pageCount(DEFAULT_PAGE_COUNT)
            .publicationDate(DEFAULT_PUBLICATION_DATE)
            .language(DEFAULT_LANGUAGE)
            .averageRating(DEFAULT_AVERAGE_RATING)
            .totalRatings(DEFAULT_TOTAL_RATINGS)
            .totalReviews(DEFAULT_TOTAL_REVIEWS)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BookEntity createUpdatedEntity() {
        return new BookEntity()
            .isbn(UPDATED_ISBN)
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .coverImageUrl(UPDATED_COVER_IMAGE_URL)
            .pageCount(UPDATED_PAGE_COUNT)
            .publicationDate(UPDATED_PUBLICATION_DATE)
            .language(UPDATED_LANGUAGE)
            .averageRating(UPDATED_AVERAGE_RATING)
            .totalRatings(UPDATED_TOTAL_RATINGS)
            .totalReviews(UPDATED_TOTAL_REVIEWS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    public void initTest() {
        bookEntity = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedBookEntity != null) {
            bookRepository.delete(insertedBookEntity);
            insertedBookEntity = null;
        }
    }

    @Test
    @Transactional
    void createBook() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Book
        var returnedBookEntity = om.readValue(
            restBookMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookEntity)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            BookEntity.class
        );

        // Validate the Book in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertBookEntityUpdatableFieldsEquals(returnedBookEntity, getPersistedBookEntity(returnedBookEntity));

        insertedBookEntity = returnedBookEntity;
    }

    @Test
    @Transactional
    void createBookWithExistingId() throws Exception {
        // Create the Book with an existing ID
        bookEntity.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBookMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookEntity)))
            .andExpect(status().isBadRequest());

        // Validate the Book in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkIsbnIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        bookEntity.setIsbn(null);

        // Create the Book, which fails.

        restBookMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookEntity)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        bookEntity.setTitle(null);

        // Create the Book, which fails.

        restBookMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookEntity)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBooks() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList
        restBookMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].isbn").value(hasItem(DEFAULT_ISBN)))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].coverImageUrl").value(hasItem(DEFAULT_COVER_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].pageCount").value(hasItem(DEFAULT_PAGE_COUNT)))
            .andExpect(jsonPath("$.[*].publicationDate").value(hasItem(DEFAULT_PUBLICATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE)))
            .andExpect(jsonPath("$.[*].averageRating").value(hasItem(sameNumber(DEFAULT_AVERAGE_RATING))))
            .andExpect(jsonPath("$.[*].totalRatings").value(hasItem(DEFAULT_TOTAL_RATINGS)))
            .andExpect(jsonPath("$.[*].totalReviews").value(hasItem(DEFAULT_TOTAL_REVIEWS)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getBook() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get the book
        restBookMockMvc
            .perform(get(ENTITY_API_URL_ID, bookEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bookEntity.getId().intValue()))
            .andExpect(jsonPath("$.isbn").value(DEFAULT_ISBN))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.coverImageUrl").value(DEFAULT_COVER_IMAGE_URL))
            .andExpect(jsonPath("$.pageCount").value(DEFAULT_PAGE_COUNT))
            .andExpect(jsonPath("$.publicationDate").value(DEFAULT_PUBLICATION_DATE.toString()))
            .andExpect(jsonPath("$.language").value(DEFAULT_LANGUAGE))
            .andExpect(jsonPath("$.averageRating").value(sameNumber(DEFAULT_AVERAGE_RATING)))
            .andExpect(jsonPath("$.totalRatings").value(DEFAULT_TOTAL_RATINGS))
            .andExpect(jsonPath("$.totalReviews").value(DEFAULT_TOTAL_REVIEWS))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getBooksByIdFiltering() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        Long id = bookEntity.getId();

        defaultBookFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultBookFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultBookFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllBooksByIsbnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where isbn equals to
        defaultBookFiltering("isbn.equals=" + DEFAULT_ISBN, "isbn.equals=" + UPDATED_ISBN);
    }

    @Test
    @Transactional
    void getAllBooksByIsbnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where isbn in
        defaultBookFiltering("isbn.in=" + DEFAULT_ISBN + "," + UPDATED_ISBN, "isbn.in=" + UPDATED_ISBN);
    }

    @Test
    @Transactional
    void getAllBooksByIsbnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where isbn is not null
        defaultBookFiltering("isbn.specified=true", "isbn.specified=false");
    }

    @Test
    @Transactional
    void getAllBooksByIsbnContainsSomething() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where isbn contains
        defaultBookFiltering("isbn.contains=" + DEFAULT_ISBN, "isbn.contains=" + UPDATED_ISBN);
    }

    @Test
    @Transactional
    void getAllBooksByIsbnNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where isbn does not contain
        defaultBookFiltering("isbn.doesNotContain=" + UPDATED_ISBN, "isbn.doesNotContain=" + DEFAULT_ISBN);
    }

    @Test
    @Transactional
    void getAllBooksByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where title equals to
        defaultBookFiltering("title.equals=" + DEFAULT_TITLE, "title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllBooksByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where title in
        defaultBookFiltering("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE, "title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllBooksByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where title is not null
        defaultBookFiltering("title.specified=true", "title.specified=false");
    }

    @Test
    @Transactional
    void getAllBooksByTitleContainsSomething() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where title contains
        defaultBookFiltering("title.contains=" + DEFAULT_TITLE, "title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllBooksByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where title does not contain
        defaultBookFiltering("title.doesNotContain=" + UPDATED_TITLE, "title.doesNotContain=" + DEFAULT_TITLE);
    }

    @Test
    @Transactional
    void getAllBooksByCoverImageUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where coverImageUrl equals to
        defaultBookFiltering("coverImageUrl.equals=" + DEFAULT_COVER_IMAGE_URL, "coverImageUrl.equals=" + UPDATED_COVER_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllBooksByCoverImageUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where coverImageUrl in
        defaultBookFiltering(
            "coverImageUrl.in=" + DEFAULT_COVER_IMAGE_URL + "," + UPDATED_COVER_IMAGE_URL,
            "coverImageUrl.in=" + UPDATED_COVER_IMAGE_URL
        );
    }

    @Test
    @Transactional
    void getAllBooksByCoverImageUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where coverImageUrl is not null
        defaultBookFiltering("coverImageUrl.specified=true", "coverImageUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllBooksByCoverImageUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where coverImageUrl contains
        defaultBookFiltering("coverImageUrl.contains=" + DEFAULT_COVER_IMAGE_URL, "coverImageUrl.contains=" + UPDATED_COVER_IMAGE_URL);
    }

    @Test
    @Transactional
    void getAllBooksByCoverImageUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where coverImageUrl does not contain
        defaultBookFiltering(
            "coverImageUrl.doesNotContain=" + UPDATED_COVER_IMAGE_URL,
            "coverImageUrl.doesNotContain=" + DEFAULT_COVER_IMAGE_URL
        );
    }

    @Test
    @Transactional
    void getAllBooksByPageCountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where pageCount equals to
        defaultBookFiltering("pageCount.equals=" + DEFAULT_PAGE_COUNT, "pageCount.equals=" + UPDATED_PAGE_COUNT);
    }

    @Test
    @Transactional
    void getAllBooksByPageCountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where pageCount in
        defaultBookFiltering("pageCount.in=" + DEFAULT_PAGE_COUNT + "," + UPDATED_PAGE_COUNT, "pageCount.in=" + UPDATED_PAGE_COUNT);
    }

    @Test
    @Transactional
    void getAllBooksByPageCountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where pageCount is not null
        defaultBookFiltering("pageCount.specified=true", "pageCount.specified=false");
    }

    @Test
    @Transactional
    void getAllBooksByPageCountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where pageCount is greater than or equal to
        defaultBookFiltering("pageCount.greaterThanOrEqual=" + DEFAULT_PAGE_COUNT, "pageCount.greaterThanOrEqual=" + UPDATED_PAGE_COUNT);
    }

    @Test
    @Transactional
    void getAllBooksByPageCountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where pageCount is less than or equal to
        defaultBookFiltering("pageCount.lessThanOrEqual=" + DEFAULT_PAGE_COUNT, "pageCount.lessThanOrEqual=" + SMALLER_PAGE_COUNT);
    }

    @Test
    @Transactional
    void getAllBooksByPageCountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where pageCount is less than
        defaultBookFiltering("pageCount.lessThan=" + UPDATED_PAGE_COUNT, "pageCount.lessThan=" + DEFAULT_PAGE_COUNT);
    }

    @Test
    @Transactional
    void getAllBooksByPageCountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where pageCount is greater than
        defaultBookFiltering("pageCount.greaterThan=" + SMALLER_PAGE_COUNT, "pageCount.greaterThan=" + DEFAULT_PAGE_COUNT);
    }

    @Test
    @Transactional
    void getAllBooksByPublicationDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where publicationDate equals to
        defaultBookFiltering("publicationDate.equals=" + DEFAULT_PUBLICATION_DATE, "publicationDate.equals=" + UPDATED_PUBLICATION_DATE);
    }

    @Test
    @Transactional
    void getAllBooksByPublicationDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where publicationDate in
        defaultBookFiltering(
            "publicationDate.in=" + DEFAULT_PUBLICATION_DATE + "," + UPDATED_PUBLICATION_DATE,
            "publicationDate.in=" + UPDATED_PUBLICATION_DATE
        );
    }

    @Test
    @Transactional
    void getAllBooksByPublicationDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where publicationDate is not null
        defaultBookFiltering("publicationDate.specified=true", "publicationDate.specified=false");
    }

    @Test
    @Transactional
    void getAllBooksByPublicationDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where publicationDate is greater than or equal to
        defaultBookFiltering(
            "publicationDate.greaterThanOrEqual=" + DEFAULT_PUBLICATION_DATE,
            "publicationDate.greaterThanOrEqual=" + UPDATED_PUBLICATION_DATE
        );
    }

    @Test
    @Transactional
    void getAllBooksByPublicationDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where publicationDate is less than or equal to
        defaultBookFiltering(
            "publicationDate.lessThanOrEqual=" + DEFAULT_PUBLICATION_DATE,
            "publicationDate.lessThanOrEqual=" + SMALLER_PUBLICATION_DATE
        );
    }

    @Test
    @Transactional
    void getAllBooksByPublicationDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where publicationDate is less than
        defaultBookFiltering(
            "publicationDate.lessThan=" + UPDATED_PUBLICATION_DATE,
            "publicationDate.lessThan=" + DEFAULT_PUBLICATION_DATE
        );
    }

    @Test
    @Transactional
    void getAllBooksByPublicationDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where publicationDate is greater than
        defaultBookFiltering(
            "publicationDate.greaterThan=" + SMALLER_PUBLICATION_DATE,
            "publicationDate.greaterThan=" + DEFAULT_PUBLICATION_DATE
        );
    }

    @Test
    @Transactional
    void getAllBooksByLanguageIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where language equals to
        defaultBookFiltering("language.equals=" + DEFAULT_LANGUAGE, "language.equals=" + UPDATED_LANGUAGE);
    }

    @Test
    @Transactional
    void getAllBooksByLanguageIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where language in
        defaultBookFiltering("language.in=" + DEFAULT_LANGUAGE + "," + UPDATED_LANGUAGE, "language.in=" + UPDATED_LANGUAGE);
    }

    @Test
    @Transactional
    void getAllBooksByLanguageIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where language is not null
        defaultBookFiltering("language.specified=true", "language.specified=false");
    }

    @Test
    @Transactional
    void getAllBooksByLanguageContainsSomething() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where language contains
        defaultBookFiltering("language.contains=" + DEFAULT_LANGUAGE, "language.contains=" + UPDATED_LANGUAGE);
    }

    @Test
    @Transactional
    void getAllBooksByLanguageNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where language does not contain
        defaultBookFiltering("language.doesNotContain=" + UPDATED_LANGUAGE, "language.doesNotContain=" + DEFAULT_LANGUAGE);
    }

    @Test
    @Transactional
    void getAllBooksByAverageRatingIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where averageRating equals to
        defaultBookFiltering("averageRating.equals=" + DEFAULT_AVERAGE_RATING, "averageRating.equals=" + UPDATED_AVERAGE_RATING);
    }

    @Test
    @Transactional
    void getAllBooksByAverageRatingIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where averageRating in
        defaultBookFiltering(
            "averageRating.in=" + DEFAULT_AVERAGE_RATING + "," + UPDATED_AVERAGE_RATING,
            "averageRating.in=" + UPDATED_AVERAGE_RATING
        );
    }

    @Test
    @Transactional
    void getAllBooksByAverageRatingIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where averageRating is not null
        defaultBookFiltering("averageRating.specified=true", "averageRating.specified=false");
    }

    @Test
    @Transactional
    void getAllBooksByAverageRatingIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where averageRating is greater than or equal to
        defaultBookFiltering(
            "averageRating.greaterThanOrEqual=" + DEFAULT_AVERAGE_RATING,
            "averageRating.greaterThanOrEqual=" + UPDATED_AVERAGE_RATING
        );
    }

    @Test
    @Transactional
    void getAllBooksByAverageRatingIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where averageRating is less than or equal to
        defaultBookFiltering(
            "averageRating.lessThanOrEqual=" + DEFAULT_AVERAGE_RATING,
            "averageRating.lessThanOrEqual=" + SMALLER_AVERAGE_RATING
        );
    }

    @Test
    @Transactional
    void getAllBooksByAverageRatingIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where averageRating is less than
        defaultBookFiltering("averageRating.lessThan=" + UPDATED_AVERAGE_RATING, "averageRating.lessThan=" + DEFAULT_AVERAGE_RATING);
    }

    @Test
    @Transactional
    void getAllBooksByAverageRatingIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where averageRating is greater than
        defaultBookFiltering("averageRating.greaterThan=" + SMALLER_AVERAGE_RATING, "averageRating.greaterThan=" + DEFAULT_AVERAGE_RATING);
    }

    @Test
    @Transactional
    void getAllBooksByTotalRatingsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where totalRatings equals to
        defaultBookFiltering("totalRatings.equals=" + DEFAULT_TOTAL_RATINGS, "totalRatings.equals=" + UPDATED_TOTAL_RATINGS);
    }

    @Test
    @Transactional
    void getAllBooksByTotalRatingsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where totalRatings in
        defaultBookFiltering(
            "totalRatings.in=" + DEFAULT_TOTAL_RATINGS + "," + UPDATED_TOTAL_RATINGS,
            "totalRatings.in=" + UPDATED_TOTAL_RATINGS
        );
    }

    @Test
    @Transactional
    void getAllBooksByTotalRatingsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where totalRatings is not null
        defaultBookFiltering("totalRatings.specified=true", "totalRatings.specified=false");
    }

    @Test
    @Transactional
    void getAllBooksByTotalRatingsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where totalRatings is greater than or equal to
        defaultBookFiltering(
            "totalRatings.greaterThanOrEqual=" + DEFAULT_TOTAL_RATINGS,
            "totalRatings.greaterThanOrEqual=" + UPDATED_TOTAL_RATINGS
        );
    }

    @Test
    @Transactional
    void getAllBooksByTotalRatingsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where totalRatings is less than or equal to
        defaultBookFiltering(
            "totalRatings.lessThanOrEqual=" + DEFAULT_TOTAL_RATINGS,
            "totalRatings.lessThanOrEqual=" + SMALLER_TOTAL_RATINGS
        );
    }

    @Test
    @Transactional
    void getAllBooksByTotalRatingsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where totalRatings is less than
        defaultBookFiltering("totalRatings.lessThan=" + UPDATED_TOTAL_RATINGS, "totalRatings.lessThan=" + DEFAULT_TOTAL_RATINGS);
    }

    @Test
    @Transactional
    void getAllBooksByTotalRatingsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where totalRatings is greater than
        defaultBookFiltering("totalRatings.greaterThan=" + SMALLER_TOTAL_RATINGS, "totalRatings.greaterThan=" + DEFAULT_TOTAL_RATINGS);
    }

    @Test
    @Transactional
    void getAllBooksByTotalReviewsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where totalReviews equals to
        defaultBookFiltering("totalReviews.equals=" + DEFAULT_TOTAL_REVIEWS, "totalReviews.equals=" + UPDATED_TOTAL_REVIEWS);
    }

    @Test
    @Transactional
    void getAllBooksByTotalReviewsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where totalReviews in
        defaultBookFiltering(
            "totalReviews.in=" + DEFAULT_TOTAL_REVIEWS + "," + UPDATED_TOTAL_REVIEWS,
            "totalReviews.in=" + UPDATED_TOTAL_REVIEWS
        );
    }

    @Test
    @Transactional
    void getAllBooksByTotalReviewsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where totalReviews is not null
        defaultBookFiltering("totalReviews.specified=true", "totalReviews.specified=false");
    }

    @Test
    @Transactional
    void getAllBooksByTotalReviewsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where totalReviews is greater than or equal to
        defaultBookFiltering(
            "totalReviews.greaterThanOrEqual=" + DEFAULT_TOTAL_REVIEWS,
            "totalReviews.greaterThanOrEqual=" + UPDATED_TOTAL_REVIEWS
        );
    }

    @Test
    @Transactional
    void getAllBooksByTotalReviewsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where totalReviews is less than or equal to
        defaultBookFiltering(
            "totalReviews.lessThanOrEqual=" + DEFAULT_TOTAL_REVIEWS,
            "totalReviews.lessThanOrEqual=" + SMALLER_TOTAL_REVIEWS
        );
    }

    @Test
    @Transactional
    void getAllBooksByTotalReviewsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where totalReviews is less than
        defaultBookFiltering("totalReviews.lessThan=" + UPDATED_TOTAL_REVIEWS, "totalReviews.lessThan=" + DEFAULT_TOTAL_REVIEWS);
    }

    @Test
    @Transactional
    void getAllBooksByTotalReviewsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where totalReviews is greater than
        defaultBookFiltering("totalReviews.greaterThan=" + SMALLER_TOTAL_REVIEWS, "totalReviews.greaterThan=" + DEFAULT_TOTAL_REVIEWS);
    }

    @Test
    @Transactional
    void getAllBooksByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where createdAt equals to
        defaultBookFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllBooksByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where createdAt in
        defaultBookFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllBooksByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where createdAt is not null
        defaultBookFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllBooksByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where updatedAt equals to
        defaultBookFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllBooksByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where updatedAt in
        defaultBookFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllBooksByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        // Get all the bookList where updatedAt is not null
        defaultBookFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllBooksByPublisherIsEqualToSomething() throws Exception {
        PublisherEntity publisher;
        if (TestUtil.findAll(em, PublisherEntity.class).isEmpty()) {
            bookRepository.saveAndFlush(bookEntity);
            publisher = PublisherResourceIT.createEntity();
        } else {
            publisher = TestUtil.findAll(em, PublisherEntity.class).get(0);
        }
        em.persist(publisher);
        em.flush();
        bookEntity.setPublisher(publisher);
        bookRepository.saveAndFlush(bookEntity);
        Long publisherId = publisher.getId();
        // Get all the bookList where publisher equals to publisherId
        defaultBookShouldBeFound("publisherId.equals=" + publisherId);

        // Get all the bookList where publisher equals to (publisherId + 1)
        defaultBookShouldNotBeFound("publisherId.equals=" + (publisherId + 1));
    }

    private void defaultBookFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultBookShouldBeFound(shouldBeFound);
        defaultBookShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBookShouldBeFound(String filter) throws Exception {
        restBookMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].isbn").value(hasItem(DEFAULT_ISBN)))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].coverImageUrl").value(hasItem(DEFAULT_COVER_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].pageCount").value(hasItem(DEFAULT_PAGE_COUNT)))
            .andExpect(jsonPath("$.[*].publicationDate").value(hasItem(DEFAULT_PUBLICATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE)))
            .andExpect(jsonPath("$.[*].averageRating").value(hasItem(sameNumber(DEFAULT_AVERAGE_RATING))))
            .andExpect(jsonPath("$.[*].totalRatings").value(hasItem(DEFAULT_TOTAL_RATINGS)))
            .andExpect(jsonPath("$.[*].totalReviews").value(hasItem(DEFAULT_TOTAL_REVIEWS)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restBookMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBookShouldNotBeFound(String filter) throws Exception {
        restBookMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBookMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingBook() throws Exception {
        // Get the book
        restBookMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBook() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the book
        BookEntity updatedBookEntity = bookRepository.findById(bookEntity.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBookEntity are not directly saved in db
        em.detach(updatedBookEntity);
        updatedBookEntity
            .isbn(UPDATED_ISBN)
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .coverImageUrl(UPDATED_COVER_IMAGE_URL)
            .pageCount(UPDATED_PAGE_COUNT)
            .publicationDate(UPDATED_PUBLICATION_DATE)
            .language(UPDATED_LANGUAGE)
            .averageRating(UPDATED_AVERAGE_RATING)
            .totalRatings(UPDATED_TOTAL_RATINGS)
            .totalReviews(UPDATED_TOTAL_REVIEWS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restBookMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBookEntity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedBookEntity))
            )
            .andExpect(status().isOk());

        // Validate the Book in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBookEntityToMatchAllProperties(updatedBookEntity);
    }

    @Test
    @Transactional
    void putNonExistingBook() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookEntity.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bookEntity.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Book in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBook() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(bookEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Book in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBook() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookEntity)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Book in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBookWithPatch() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the book using partial update
        BookEntity partialUpdatedBookEntity = new BookEntity();
        partialUpdatedBookEntity.setId(bookEntity.getId());

        partialUpdatedBookEntity
            .description(UPDATED_DESCRIPTION)
            .coverImageUrl(UPDATED_COVER_IMAGE_URL)
            .pageCount(UPDATED_PAGE_COUNT)
            .publicationDate(UPDATED_PUBLICATION_DATE)
            .totalRatings(UPDATED_TOTAL_RATINGS)
            .totalReviews(UPDATED_TOTAL_REVIEWS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBookEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBookEntity))
            )
            .andExpect(status().isOk());

        // Validate the Book in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBookEntityUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedBookEntity, bookEntity),
            getPersistedBookEntity(bookEntity)
        );
    }

    @Test
    @Transactional
    void fullUpdateBookWithPatch() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the book using partial update
        BookEntity partialUpdatedBookEntity = new BookEntity();
        partialUpdatedBookEntity.setId(bookEntity.getId());

        partialUpdatedBookEntity
            .isbn(UPDATED_ISBN)
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .coverImageUrl(UPDATED_COVER_IMAGE_URL)
            .pageCount(UPDATED_PAGE_COUNT)
            .publicationDate(UPDATED_PUBLICATION_DATE)
            .language(UPDATED_LANGUAGE)
            .averageRating(UPDATED_AVERAGE_RATING)
            .totalRatings(UPDATED_TOTAL_RATINGS)
            .totalReviews(UPDATED_TOTAL_REVIEWS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBookEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBookEntity))
            )
            .andExpect(status().isOk());

        // Validate the Book in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBookEntityUpdatableFieldsEquals(partialUpdatedBookEntity, getPersistedBookEntity(partialUpdatedBookEntity));
    }

    @Test
    @Transactional
    void patchNonExistingBook() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookEntity.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, bookEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(bookEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Book in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBook() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(bookEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Book in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBook() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(bookEntity)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Book in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBook() throws Exception {
        // Initialize the database
        insertedBookEntity = bookRepository.saveAndFlush(bookEntity);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the book
        restBookMockMvc
            .perform(delete(ENTITY_API_URL_ID, bookEntity.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return bookRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected BookEntity getPersistedBookEntity(BookEntity book) {
        return bookRepository.findById(book.getId()).orElseThrow();
    }

    protected void assertPersistedBookEntityToMatchAllProperties(BookEntity expectedBookEntity) {
        assertBookEntityAllPropertiesEquals(expectedBookEntity, getPersistedBookEntity(expectedBookEntity));
    }

    protected void assertPersistedBookEntityToMatchUpdatableProperties(BookEntity expectedBookEntity) {
        assertBookEntityAllUpdatablePropertiesEquals(expectedBookEntity, getPersistedBookEntity(expectedBookEntity));
    }
}
