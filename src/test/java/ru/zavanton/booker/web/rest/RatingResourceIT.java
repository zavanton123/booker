package ru.zavanton.booker.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.zavanton.booker.domain.RatingEntityAsserts.*;
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
import ru.zavanton.booker.domain.RatingEntity;
import ru.zavanton.booker.domain.UserEntity;
import ru.zavanton.booker.repository.RatingRepository;
import ru.zavanton.booker.repository.UserRepository;

/**
 * Integration tests for the {@link RatingResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RatingResourceIT {

    private static final Integer DEFAULT_RATING = 1;
    private static final Integer UPDATED_RATING = 2;
    private static final Integer SMALLER_RATING = 1 - 1;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/ratings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRatingMockMvc;

    private RatingEntity ratingEntity;

    private RatingEntity insertedRatingEntity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RatingEntity createEntity(EntityManager em) {
        RatingEntity ratingEntity = new RatingEntity().rating(DEFAULT_RATING).createdAt(DEFAULT_CREATED_AT).updatedAt(DEFAULT_UPDATED_AT);
        // Add required entity
        UserEntity user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        ratingEntity.setUser(user);
        // Add required entity
        BookEntity book;
        if (TestUtil.findAll(em, BookEntity.class).isEmpty()) {
            book = BookResourceIT.createEntity();
            em.persist(book);
            em.flush();
        } else {
            book = TestUtil.findAll(em, BookEntity.class).get(0);
        }
        ratingEntity.setBook(book);
        return ratingEntity;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RatingEntity createUpdatedEntity(EntityManager em) {
        RatingEntity updatedRatingEntity = new RatingEntity()
            .rating(UPDATED_RATING)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        // Add required entity
        UserEntity user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedRatingEntity.setUser(user);
        // Add required entity
        BookEntity book;
        if (TestUtil.findAll(em, BookEntity.class).isEmpty()) {
            book = BookResourceIT.createUpdatedEntity();
            em.persist(book);
            em.flush();
        } else {
            book = TestUtil.findAll(em, BookEntity.class).get(0);
        }
        updatedRatingEntity.setBook(book);
        return updatedRatingEntity;
    }

    @BeforeEach
    public void initTest() {
        ratingEntity = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedRatingEntity != null) {
            ratingRepository.delete(insertedRatingEntity);
            insertedRatingEntity = null;
        }
    }

    @Test
    @Transactional
    void createRating() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Rating
        var returnedRatingEntity = om.readValue(
            restRatingMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ratingEntity)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            RatingEntity.class
        );

        // Validate the Rating in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertRatingEntityUpdatableFieldsEquals(returnedRatingEntity, getPersistedRatingEntity(returnedRatingEntity));

        insertedRatingEntity = returnedRatingEntity;
    }

    @Test
    @Transactional
    void createRatingWithExistingId() throws Exception {
        // Create the Rating with an existing ID
        ratingEntity.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRatingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ratingEntity)))
            .andExpect(status().isBadRequest());

        // Validate the Rating in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkRatingIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ratingEntity.setRating(null);

        // Create the Rating, which fails.

        restRatingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ratingEntity)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllRatings() throws Exception {
        // Initialize the database
        insertedRatingEntity = ratingRepository.saveAndFlush(ratingEntity);

        // Get all the ratingList
        restRatingMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ratingEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].rating").value(hasItem(DEFAULT_RATING)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getRating() throws Exception {
        // Initialize the database
        insertedRatingEntity = ratingRepository.saveAndFlush(ratingEntity);

        // Get the rating
        restRatingMockMvc
            .perform(get(ENTITY_API_URL_ID, ratingEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ratingEntity.getId().intValue()))
            .andExpect(jsonPath("$.rating").value(DEFAULT_RATING))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getRatingsByIdFiltering() throws Exception {
        // Initialize the database
        insertedRatingEntity = ratingRepository.saveAndFlush(ratingEntity);

        Long id = ratingEntity.getId();

        defaultRatingFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultRatingFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultRatingFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllRatingsByRatingIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRatingEntity = ratingRepository.saveAndFlush(ratingEntity);

        // Get all the ratingList where rating equals to
        defaultRatingFiltering("rating.equals=" + DEFAULT_RATING, "rating.equals=" + UPDATED_RATING);
    }

    @Test
    @Transactional
    void getAllRatingsByRatingIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRatingEntity = ratingRepository.saveAndFlush(ratingEntity);

        // Get all the ratingList where rating in
        defaultRatingFiltering("rating.in=" + DEFAULT_RATING + "," + UPDATED_RATING, "rating.in=" + UPDATED_RATING);
    }

    @Test
    @Transactional
    void getAllRatingsByRatingIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRatingEntity = ratingRepository.saveAndFlush(ratingEntity);

        // Get all the ratingList where rating is not null
        defaultRatingFiltering("rating.specified=true", "rating.specified=false");
    }

    @Test
    @Transactional
    void getAllRatingsByRatingIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRatingEntity = ratingRepository.saveAndFlush(ratingEntity);

        // Get all the ratingList where rating is greater than or equal to
        defaultRatingFiltering("rating.greaterThanOrEqual=" + DEFAULT_RATING, "rating.greaterThanOrEqual=" + UPDATED_RATING);
    }

    @Test
    @Transactional
    void getAllRatingsByRatingIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRatingEntity = ratingRepository.saveAndFlush(ratingEntity);

        // Get all the ratingList where rating is less than or equal to
        defaultRatingFiltering("rating.lessThanOrEqual=" + DEFAULT_RATING, "rating.lessThanOrEqual=" + SMALLER_RATING);
    }

    @Test
    @Transactional
    void getAllRatingsByRatingIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedRatingEntity = ratingRepository.saveAndFlush(ratingEntity);

        // Get all the ratingList where rating is less than
        defaultRatingFiltering("rating.lessThan=" + UPDATED_RATING, "rating.lessThan=" + DEFAULT_RATING);
    }

    @Test
    @Transactional
    void getAllRatingsByRatingIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedRatingEntity = ratingRepository.saveAndFlush(ratingEntity);

        // Get all the ratingList where rating is greater than
        defaultRatingFiltering("rating.greaterThan=" + SMALLER_RATING, "rating.greaterThan=" + DEFAULT_RATING);
    }

    @Test
    @Transactional
    void getAllRatingsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRatingEntity = ratingRepository.saveAndFlush(ratingEntity);

        // Get all the ratingList where createdAt equals to
        defaultRatingFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllRatingsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRatingEntity = ratingRepository.saveAndFlush(ratingEntity);

        // Get all the ratingList where createdAt in
        defaultRatingFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllRatingsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRatingEntity = ratingRepository.saveAndFlush(ratingEntity);

        // Get all the ratingList where createdAt is not null
        defaultRatingFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllRatingsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRatingEntity = ratingRepository.saveAndFlush(ratingEntity);

        // Get all the ratingList where updatedAt equals to
        defaultRatingFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllRatingsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRatingEntity = ratingRepository.saveAndFlush(ratingEntity);

        // Get all the ratingList where updatedAt in
        defaultRatingFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllRatingsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRatingEntity = ratingRepository.saveAndFlush(ratingEntity);

        // Get all the ratingList where updatedAt is not null
        defaultRatingFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllRatingsByUserIsEqualToSomething() throws Exception {
        UserEntity user;
        if (TestUtil.findAll(em, UserEntity.class).isEmpty()) {
            ratingRepository.saveAndFlush(ratingEntity);
            user = UserResourceIT.createEntity();
        } else {
            user = TestUtil.findAll(em, UserEntity.class).get(0);
        }
        em.persist(user);
        em.flush();
        ratingEntity.setUser(user);
        ratingRepository.saveAndFlush(ratingEntity);
        Long userId = user.getId();
        // Get all the ratingList where user equals to userId
        defaultRatingShouldBeFound("userId.equals=" + userId);

        // Get all the ratingList where user equals to (userId + 1)
        defaultRatingShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    @Test
    @Transactional
    void getAllRatingsByBookIsEqualToSomething() throws Exception {
        BookEntity book;
        if (TestUtil.findAll(em, BookEntity.class).isEmpty()) {
            ratingRepository.saveAndFlush(ratingEntity);
            book = BookResourceIT.createEntity();
        } else {
            book = TestUtil.findAll(em, BookEntity.class).get(0);
        }
        em.persist(book);
        em.flush();
        ratingEntity.setBook(book);
        ratingRepository.saveAndFlush(ratingEntity);
        Long bookId = book.getId();
        // Get all the ratingList where book equals to bookId
        defaultRatingShouldBeFound("bookId.equals=" + bookId);

        // Get all the ratingList where book equals to (bookId + 1)
        defaultRatingShouldNotBeFound("bookId.equals=" + (bookId + 1));
    }

    private void defaultRatingFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultRatingShouldBeFound(shouldBeFound);
        defaultRatingShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRatingShouldBeFound(String filter) throws Exception {
        restRatingMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ratingEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].rating").value(hasItem(DEFAULT_RATING)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restRatingMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRatingShouldNotBeFound(String filter) throws Exception {
        restRatingMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRatingMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingRating() throws Exception {
        // Get the rating
        restRatingMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRating() throws Exception {
        // Initialize the database
        insertedRatingEntity = ratingRepository.saveAndFlush(ratingEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the rating
        RatingEntity updatedRatingEntity = ratingRepository.findById(ratingEntity.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedRatingEntity are not directly saved in db
        em.detach(updatedRatingEntity);
        updatedRatingEntity.rating(UPDATED_RATING).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);

        restRatingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedRatingEntity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedRatingEntity))
            )
            .andExpect(status().isOk());

        // Validate the Rating in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRatingEntityToMatchAllProperties(updatedRatingEntity);
    }

    @Test
    @Transactional
    void putNonExistingRating() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ratingEntity.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRatingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ratingEntity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ratingEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Rating in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRating() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ratingEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRatingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ratingEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Rating in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRating() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ratingEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRatingMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ratingEntity)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Rating in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRatingWithPatch() throws Exception {
        // Initialize the database
        insertedRatingEntity = ratingRepository.saveAndFlush(ratingEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the rating using partial update
        RatingEntity partialUpdatedRatingEntity = new RatingEntity();
        partialUpdatedRatingEntity.setId(ratingEntity.getId());

        partialUpdatedRatingEntity.createdAt(UPDATED_CREATED_AT);

        restRatingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRatingEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRatingEntity))
            )
            .andExpect(status().isOk());

        // Validate the Rating in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRatingEntityUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedRatingEntity, ratingEntity),
            getPersistedRatingEntity(ratingEntity)
        );
    }

    @Test
    @Transactional
    void fullUpdateRatingWithPatch() throws Exception {
        // Initialize the database
        insertedRatingEntity = ratingRepository.saveAndFlush(ratingEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the rating using partial update
        RatingEntity partialUpdatedRatingEntity = new RatingEntity();
        partialUpdatedRatingEntity.setId(ratingEntity.getId());

        partialUpdatedRatingEntity.rating(UPDATED_RATING).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);

        restRatingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRatingEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRatingEntity))
            )
            .andExpect(status().isOk());

        // Validate the Rating in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRatingEntityUpdatableFieldsEquals(partialUpdatedRatingEntity, getPersistedRatingEntity(partialUpdatedRatingEntity));
    }

    @Test
    @Transactional
    void patchNonExistingRating() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ratingEntity.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRatingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ratingEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ratingEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Rating in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRating() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ratingEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRatingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ratingEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Rating in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRating() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ratingEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRatingMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(ratingEntity)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Rating in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRating() throws Exception {
        // Initialize the database
        insertedRatingEntity = ratingRepository.saveAndFlush(ratingEntity);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the rating
        restRatingMockMvc
            .perform(delete(ENTITY_API_URL_ID, ratingEntity.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return ratingRepository.count();
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

    protected RatingEntity getPersistedRatingEntity(RatingEntity rating) {
        return ratingRepository.findById(rating.getId()).orElseThrow();
    }

    protected void assertPersistedRatingEntityToMatchAllProperties(RatingEntity expectedRatingEntity) {
        assertRatingEntityAllPropertiesEquals(expectedRatingEntity, getPersistedRatingEntity(expectedRatingEntity));
    }

    protected void assertPersistedRatingEntityToMatchUpdatableProperties(RatingEntity expectedRatingEntity) {
        assertRatingEntityAllUpdatablePropertiesEquals(expectedRatingEntity, getPersistedRatingEntity(expectedRatingEntity));
    }
}
