package ru.zavanton.booker.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.zavanton.booker.domain.ReviewEntityAsserts.*;
import static ru.zavanton.booker.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
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
import ru.zavanton.booker.domain.ReviewEntity;
import ru.zavanton.booker.domain.UserEntity;
import ru.zavanton.booker.repository.ReviewRepository;
import ru.zavanton.booker.repository.UserRepository;

/**
 * Integration tests for the {@link ReviewResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ReviewResourceIT {

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final Integer DEFAULT_RATING = 1;
    private static final Integer UPDATED_RATING = 2;
    private static final Integer SMALLER_RATING = 1 - 1;

    private static final Boolean DEFAULT_CONTAINS_SPOILERS = false;
    private static final Boolean UPDATED_CONTAINS_SPOILERS = true;

    private static final Integer DEFAULT_HELPFUL_COUNT = 1;
    private static final Integer UPDATED_HELPFUL_COUNT = 2;
    private static final Integer SMALLER_HELPFUL_COUNT = 1 - 1;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/reviews";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReviewMockMvc;

    private ReviewEntity reviewEntity;

    private ReviewEntity insertedReviewEntity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReviewEntity createEntity(EntityManager em) {
        ReviewEntity reviewEntity = new ReviewEntity()
            .content(DEFAULT_CONTENT)
            .rating(DEFAULT_RATING)
            .containsSpoilers(DEFAULT_CONTAINS_SPOILERS)
            .helpfulCount(DEFAULT_HELPFUL_COUNT)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
        // Add required entity
        UserEntity user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        reviewEntity.setUser(user);
        // Add required entity
        BookEntity book;
        if (TestUtil.findAll(em, BookEntity.class).isEmpty()) {
            book = BookResourceIT.createEntity();
            em.persist(book);
            em.flush();
        } else {
            book = TestUtil.findAll(em, BookEntity.class).get(0);
        }
        reviewEntity.setBook(book);
        return reviewEntity;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReviewEntity createUpdatedEntity(EntityManager em) {
        ReviewEntity updatedReviewEntity = new ReviewEntity()
            .content(UPDATED_CONTENT)
            .rating(UPDATED_RATING)
            .containsSpoilers(UPDATED_CONTAINS_SPOILERS)
            .helpfulCount(UPDATED_HELPFUL_COUNT)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        // Add required entity
        UserEntity user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedReviewEntity.setUser(user);
        // Add required entity
        BookEntity book;
        if (TestUtil.findAll(em, BookEntity.class).isEmpty()) {
            book = BookResourceIT.createUpdatedEntity();
            em.persist(book);
            em.flush();
        } else {
            book = TestUtil.findAll(em, BookEntity.class).get(0);
        }
        updatedReviewEntity.setBook(book);
        return updatedReviewEntity;
    }

    @BeforeEach
    public void initTest() {
        reviewEntity = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedReviewEntity != null) {
            reviewRepository.delete(insertedReviewEntity);
            insertedReviewEntity = null;
        }
    }

    @Test
    @Transactional
    void createReview() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Review
        var returnedReviewEntity = om.readValue(
            restReviewMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reviewEntity)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ReviewEntity.class
        );

        // Validate the Review in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertReviewEntityUpdatableFieldsEquals(returnedReviewEntity, getPersistedReviewEntity(returnedReviewEntity));

        insertedReviewEntity = returnedReviewEntity;
    }

    @Test
    @Transactional
    void createReviewWithExistingId() throws Exception {
        // Create the Review with an existing ID
        reviewEntity.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restReviewMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reviewEntity)))
            .andExpect(status().isBadRequest());

        // Validate the Review in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllReviews() throws Exception {
        // Initialize the database
        insertedReviewEntity = reviewRepository.saveAndFlush(reviewEntity);

        // Get all the reviewList
        restReviewMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(reviewEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].rating").value(hasItem(DEFAULT_RATING)))
            .andExpect(jsonPath("$.[*].containsSpoilers").value(hasItem(DEFAULT_CONTAINS_SPOILERS)))
            .andExpect(jsonPath("$.[*].helpfulCount").value(hasItem(DEFAULT_HELPFUL_COUNT)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getReview() throws Exception {
        // Initialize the database
        insertedReviewEntity = reviewRepository.saveAndFlush(reviewEntity);

        // Get the review
        restReviewMockMvc
            .perform(get(ENTITY_API_URL_ID, reviewEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(reviewEntity.getId().intValue()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
            .andExpect(jsonPath("$.rating").value(DEFAULT_RATING))
            .andExpect(jsonPath("$.containsSpoilers").value(DEFAULT_CONTAINS_SPOILERS))
            .andExpect(jsonPath("$.helpfulCount").value(DEFAULT_HELPFUL_COUNT))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getReviewsByIdFiltering() throws Exception {
        // Initialize the database
        insertedReviewEntity = reviewRepository.saveAndFlush(reviewEntity);

        Long id = reviewEntity.getId();

        defaultReviewFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultReviewFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultReviewFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllReviewsByRatingIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReviewEntity = reviewRepository.saveAndFlush(reviewEntity);

        // Get all the reviewList where rating equals to
        defaultReviewFiltering("rating.equals=" + DEFAULT_RATING, "rating.equals=" + UPDATED_RATING);
    }

    @Test
    @Transactional
    void getAllReviewsByRatingIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReviewEntity = reviewRepository.saveAndFlush(reviewEntity);

        // Get all the reviewList where rating in
        defaultReviewFiltering("rating.in=" + DEFAULT_RATING + "," + UPDATED_RATING, "rating.in=" + UPDATED_RATING);
    }

    @Test
    @Transactional
    void getAllReviewsByRatingIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReviewEntity = reviewRepository.saveAndFlush(reviewEntity);

        // Get all the reviewList where rating is not null
        defaultReviewFiltering("rating.specified=true", "rating.specified=false");
    }

    @Test
    @Transactional
    void getAllReviewsByRatingIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedReviewEntity = reviewRepository.saveAndFlush(reviewEntity);

        // Get all the reviewList where rating is greater than or equal to
        defaultReviewFiltering("rating.greaterThanOrEqual=" + DEFAULT_RATING, "rating.greaterThanOrEqual=" + UPDATED_RATING);
    }

    @Test
    @Transactional
    void getAllReviewsByRatingIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedReviewEntity = reviewRepository.saveAndFlush(reviewEntity);

        // Get all the reviewList where rating is less than or equal to
        defaultReviewFiltering("rating.lessThanOrEqual=" + DEFAULT_RATING, "rating.lessThanOrEqual=" + SMALLER_RATING);
    }

    @Test
    @Transactional
    void getAllReviewsByRatingIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedReviewEntity = reviewRepository.saveAndFlush(reviewEntity);

        // Get all the reviewList where rating is less than
        defaultReviewFiltering("rating.lessThan=" + UPDATED_RATING, "rating.lessThan=" + DEFAULT_RATING);
    }

    @Test
    @Transactional
    void getAllReviewsByRatingIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedReviewEntity = reviewRepository.saveAndFlush(reviewEntity);

        // Get all the reviewList where rating is greater than
        defaultReviewFiltering("rating.greaterThan=" + SMALLER_RATING, "rating.greaterThan=" + DEFAULT_RATING);
    }

    @Test
    @Transactional
    void getAllReviewsByContainsSpoilersIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReviewEntity = reviewRepository.saveAndFlush(reviewEntity);

        // Get all the reviewList where containsSpoilers equals to
        defaultReviewFiltering(
            "containsSpoilers.equals=" + DEFAULT_CONTAINS_SPOILERS,
            "containsSpoilers.equals=" + UPDATED_CONTAINS_SPOILERS
        );
    }

    @Test
    @Transactional
    void getAllReviewsByContainsSpoilersIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReviewEntity = reviewRepository.saveAndFlush(reviewEntity);

        // Get all the reviewList where containsSpoilers in
        defaultReviewFiltering(
            "containsSpoilers.in=" + DEFAULT_CONTAINS_SPOILERS + "," + UPDATED_CONTAINS_SPOILERS,
            "containsSpoilers.in=" + UPDATED_CONTAINS_SPOILERS
        );
    }

    @Test
    @Transactional
    void getAllReviewsByContainsSpoilersIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReviewEntity = reviewRepository.saveAndFlush(reviewEntity);

        // Get all the reviewList where containsSpoilers is not null
        defaultReviewFiltering("containsSpoilers.specified=true", "containsSpoilers.specified=false");
    }

    @Test
    @Transactional
    void getAllReviewsByHelpfulCountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReviewEntity = reviewRepository.saveAndFlush(reviewEntity);

        // Get all the reviewList where helpfulCount equals to
        defaultReviewFiltering("helpfulCount.equals=" + DEFAULT_HELPFUL_COUNT, "helpfulCount.equals=" + UPDATED_HELPFUL_COUNT);
    }

    @Test
    @Transactional
    void getAllReviewsByHelpfulCountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReviewEntity = reviewRepository.saveAndFlush(reviewEntity);

        // Get all the reviewList where helpfulCount in
        defaultReviewFiltering(
            "helpfulCount.in=" + DEFAULT_HELPFUL_COUNT + "," + UPDATED_HELPFUL_COUNT,
            "helpfulCount.in=" + UPDATED_HELPFUL_COUNT
        );
    }

    @Test
    @Transactional
    void getAllReviewsByHelpfulCountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReviewEntity = reviewRepository.saveAndFlush(reviewEntity);

        // Get all the reviewList where helpfulCount is not null
        defaultReviewFiltering("helpfulCount.specified=true", "helpfulCount.specified=false");
    }

    @Test
    @Transactional
    void getAllReviewsByHelpfulCountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedReviewEntity = reviewRepository.saveAndFlush(reviewEntity);

        // Get all the reviewList where helpfulCount is greater than or equal to
        defaultReviewFiltering(
            "helpfulCount.greaterThanOrEqual=" + DEFAULT_HELPFUL_COUNT,
            "helpfulCount.greaterThanOrEqual=" + UPDATED_HELPFUL_COUNT
        );
    }

    @Test
    @Transactional
    void getAllReviewsByHelpfulCountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedReviewEntity = reviewRepository.saveAndFlush(reviewEntity);

        // Get all the reviewList where helpfulCount is less than or equal to
        defaultReviewFiltering(
            "helpfulCount.lessThanOrEqual=" + DEFAULT_HELPFUL_COUNT,
            "helpfulCount.lessThanOrEqual=" + SMALLER_HELPFUL_COUNT
        );
    }

    @Test
    @Transactional
    void getAllReviewsByHelpfulCountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedReviewEntity = reviewRepository.saveAndFlush(reviewEntity);

        // Get all the reviewList where helpfulCount is less than
        defaultReviewFiltering("helpfulCount.lessThan=" + UPDATED_HELPFUL_COUNT, "helpfulCount.lessThan=" + DEFAULT_HELPFUL_COUNT);
    }

    @Test
    @Transactional
    void getAllReviewsByHelpfulCountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedReviewEntity = reviewRepository.saveAndFlush(reviewEntity);

        // Get all the reviewList where helpfulCount is greater than
        defaultReviewFiltering("helpfulCount.greaterThan=" + SMALLER_HELPFUL_COUNT, "helpfulCount.greaterThan=" + DEFAULT_HELPFUL_COUNT);
    }

    @Test
    @Transactional
    void getAllReviewsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReviewEntity = reviewRepository.saveAndFlush(reviewEntity);

        // Get all the reviewList where createdAt equals to
        defaultReviewFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllReviewsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReviewEntity = reviewRepository.saveAndFlush(reviewEntity);

        // Get all the reviewList where createdAt in
        defaultReviewFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllReviewsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReviewEntity = reviewRepository.saveAndFlush(reviewEntity);

        // Get all the reviewList where createdAt is not null
        defaultReviewFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllReviewsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReviewEntity = reviewRepository.saveAndFlush(reviewEntity);

        // Get all the reviewList where updatedAt equals to
        defaultReviewFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllReviewsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReviewEntity = reviewRepository.saveAndFlush(reviewEntity);

        // Get all the reviewList where updatedAt in
        defaultReviewFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllReviewsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReviewEntity = reviewRepository.saveAndFlush(reviewEntity);

        // Get all the reviewList where updatedAt is not null
        defaultReviewFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllReviewsByUserIsEqualToSomething() throws Exception {
        UserEntity user;
        if (TestUtil.findAll(em, UserEntity.class).isEmpty()) {
            reviewRepository.saveAndFlush(reviewEntity);
            user = UserResourceIT.createEntity();
        } else {
            user = TestUtil.findAll(em, UserEntity.class).get(0);
        }
        em.persist(user);
        em.flush();
        reviewEntity.setUser(user);
        reviewRepository.saveAndFlush(reviewEntity);
        Long userId = user.getId();
        // Get all the reviewList where user equals to userId
        defaultReviewShouldBeFound("userId.equals=" + userId);

        // Get all the reviewList where user equals to (userId + 1)
        defaultReviewShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    @Test
    @Transactional
    void getAllReviewsByBookIsEqualToSomething() throws Exception {
        BookEntity book;
        if (TestUtil.findAll(em, BookEntity.class).isEmpty()) {
            reviewRepository.saveAndFlush(reviewEntity);
            book = BookResourceIT.createEntity();
        } else {
            book = TestUtil.findAll(em, BookEntity.class).get(0);
        }
        em.persist(book);
        em.flush();
        reviewEntity.setBook(book);
        reviewRepository.saveAndFlush(reviewEntity);
        Long bookId = book.getId();
        // Get all the reviewList where book equals to bookId
        defaultReviewShouldBeFound("bookId.equals=" + bookId);

        // Get all the reviewList where book equals to (bookId + 1)
        defaultReviewShouldNotBeFound("bookId.equals=" + (bookId + 1));
    }

    private void defaultReviewFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultReviewShouldBeFound(shouldBeFound);
        defaultReviewShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultReviewShouldBeFound(String filter) throws Exception {
        restReviewMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(reviewEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].rating").value(hasItem(DEFAULT_RATING)))
            .andExpect(jsonPath("$.[*].containsSpoilers").value(hasItem(DEFAULT_CONTAINS_SPOILERS)))
            .andExpect(jsonPath("$.[*].helpfulCount").value(hasItem(DEFAULT_HELPFUL_COUNT)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restReviewMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultReviewShouldNotBeFound(String filter) throws Exception {
        restReviewMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restReviewMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingReview() throws Exception {
        // Get the review
        restReviewMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingReview() throws Exception {
        // Initialize the database
        insertedReviewEntity = reviewRepository.saveAndFlush(reviewEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the review
        ReviewEntity updatedReviewEntity = reviewRepository.findById(reviewEntity.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedReviewEntity are not directly saved in db
        em.detach(updatedReviewEntity);
        updatedReviewEntity
            .content(UPDATED_CONTENT)
            .rating(UPDATED_RATING)
            .containsSpoilers(UPDATED_CONTAINS_SPOILERS)
            .helpfulCount(UPDATED_HELPFUL_COUNT)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restReviewMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedReviewEntity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedReviewEntity))
            )
            .andExpect(status().isOk());

        // Validate the Review in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedReviewEntityToMatchAllProperties(updatedReviewEntity);
    }

    @Test
    @Transactional
    void putNonExistingReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reviewEntity.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReviewMockMvc
            .perform(
                put(ENTITY_API_URL_ID, reviewEntity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(reviewEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Review in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reviewEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReviewMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(reviewEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Review in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reviewEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReviewMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reviewEntity)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Review in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateReviewWithPatch() throws Exception {
        // Initialize the database
        insertedReviewEntity = reviewRepository.saveAndFlush(reviewEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the review using partial update
        ReviewEntity partialUpdatedReviewEntity = new ReviewEntity();
        partialUpdatedReviewEntity.setId(reviewEntity.getId());

        partialUpdatedReviewEntity
            .content(UPDATED_CONTENT)
            .rating(UPDATED_RATING)
            .containsSpoilers(UPDATED_CONTAINS_SPOILERS)
            .createdAt(UPDATED_CREATED_AT);

        restReviewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReviewEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReviewEntity))
            )
            .andExpect(status().isOk());

        // Validate the Review in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReviewEntityUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedReviewEntity, reviewEntity),
            getPersistedReviewEntity(reviewEntity)
        );
    }

    @Test
    @Transactional
    void fullUpdateReviewWithPatch() throws Exception {
        // Initialize the database
        insertedReviewEntity = reviewRepository.saveAndFlush(reviewEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the review using partial update
        ReviewEntity partialUpdatedReviewEntity = new ReviewEntity();
        partialUpdatedReviewEntity.setId(reviewEntity.getId());

        partialUpdatedReviewEntity
            .content(UPDATED_CONTENT)
            .rating(UPDATED_RATING)
            .containsSpoilers(UPDATED_CONTAINS_SPOILERS)
            .helpfulCount(UPDATED_HELPFUL_COUNT)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restReviewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReviewEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReviewEntity))
            )
            .andExpect(status().isOk());

        // Validate the Review in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReviewEntityUpdatableFieldsEquals(partialUpdatedReviewEntity, getPersistedReviewEntity(partialUpdatedReviewEntity));
    }

    @Test
    @Transactional
    void patchNonExistingReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reviewEntity.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReviewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, reviewEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(reviewEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Review in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reviewEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReviewMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(reviewEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Review in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamReview() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        reviewEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReviewMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(reviewEntity)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Review in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteReview() throws Exception {
        // Initialize the database
        insertedReviewEntity = reviewRepository.saveAndFlush(reviewEntity);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the review
        restReviewMockMvc
            .perform(delete(ENTITY_API_URL_ID, reviewEntity.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return reviewRepository.count();
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

    protected ReviewEntity getPersistedReviewEntity(ReviewEntity review) {
        return reviewRepository.findById(review.getId()).orElseThrow();
    }

    protected void assertPersistedReviewEntityToMatchAllProperties(ReviewEntity expectedReviewEntity) {
        assertReviewEntityAllPropertiesEquals(expectedReviewEntity, getPersistedReviewEntity(expectedReviewEntity));
    }

    protected void assertPersistedReviewEntityToMatchUpdatableProperties(ReviewEntity expectedReviewEntity) {
        assertReviewEntityAllUpdatablePropertiesEquals(expectedReviewEntity, getPersistedReviewEntity(expectedReviewEntity));
    }
}
