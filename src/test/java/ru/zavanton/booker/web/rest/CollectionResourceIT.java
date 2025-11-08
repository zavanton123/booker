package ru.zavanton.booker.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.zavanton.booker.domain.CollectionEntityAsserts.*;
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
import ru.zavanton.booker.domain.CollectionEntity;
import ru.zavanton.booker.domain.UserEntity;
import ru.zavanton.booker.repository.CollectionRepository;
import ru.zavanton.booker.repository.UserRepository;

/**
 * Integration tests for the {@link CollectionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CollectionResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_PUBLIC = false;
    private static final Boolean UPDATED_IS_PUBLIC = true;

    private static final Integer DEFAULT_BOOK_COUNT = 1;
    private static final Integer UPDATED_BOOK_COUNT = 2;
    private static final Integer SMALLER_BOOK_COUNT = 1 - 1;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/collections";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCollectionMockMvc;

    private CollectionEntity collectionEntity;

    private CollectionEntity insertedCollectionEntity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CollectionEntity createEntity(EntityManager em) {
        CollectionEntity collectionEntity = new CollectionEntity()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .isPublic(DEFAULT_IS_PUBLIC)
            .bookCount(DEFAULT_BOOK_COUNT)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
        // Add required entity
        UserEntity user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        collectionEntity.setUser(user);
        return collectionEntity;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CollectionEntity createUpdatedEntity(EntityManager em) {
        CollectionEntity updatedCollectionEntity = new CollectionEntity()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .isPublic(UPDATED_IS_PUBLIC)
            .bookCount(UPDATED_BOOK_COUNT)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        // Add required entity
        UserEntity user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedCollectionEntity.setUser(user);
        return updatedCollectionEntity;
    }

    @BeforeEach
    public void initTest() {
        collectionEntity = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedCollectionEntity != null) {
            collectionRepository.delete(insertedCollectionEntity);
            insertedCollectionEntity = null;
        }
    }

    @Test
    @Transactional
    void createCollection() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Collection
        var returnedCollectionEntity = om.readValue(
            restCollectionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(collectionEntity)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CollectionEntity.class
        );

        // Validate the Collection in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertCollectionEntityUpdatableFieldsEquals(returnedCollectionEntity, getPersistedCollectionEntity(returnedCollectionEntity));

        insertedCollectionEntity = returnedCollectionEntity;
    }

    @Test
    @Transactional
    void createCollectionWithExistingId() throws Exception {
        // Create the Collection with an existing ID
        collectionEntity.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCollectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(collectionEntity)))
            .andExpect(status().isBadRequest());

        // Validate the Collection in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        collectionEntity.setName(null);

        // Create the Collection, which fails.

        restCollectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(collectionEntity)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCollections() throws Exception {
        // Initialize the database
        insertedCollectionEntity = collectionRepository.saveAndFlush(collectionEntity);

        // Get all the collectionList
        restCollectionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(collectionEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].isPublic").value(hasItem(DEFAULT_IS_PUBLIC)))
            .andExpect(jsonPath("$.[*].bookCount").value(hasItem(DEFAULT_BOOK_COUNT)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getCollection() throws Exception {
        // Initialize the database
        insertedCollectionEntity = collectionRepository.saveAndFlush(collectionEntity);

        // Get the collection
        restCollectionMockMvc
            .perform(get(ENTITY_API_URL_ID, collectionEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(collectionEntity.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.isPublic").value(DEFAULT_IS_PUBLIC))
            .andExpect(jsonPath("$.bookCount").value(DEFAULT_BOOK_COUNT))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getCollectionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedCollectionEntity = collectionRepository.saveAndFlush(collectionEntity);

        Long id = collectionEntity.getId();

        defaultCollectionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCollectionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCollectionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCollectionsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCollectionEntity = collectionRepository.saveAndFlush(collectionEntity);

        // Get all the collectionList where name equals to
        defaultCollectionFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCollectionsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCollectionEntity = collectionRepository.saveAndFlush(collectionEntity);

        // Get all the collectionList where name in
        defaultCollectionFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCollectionsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCollectionEntity = collectionRepository.saveAndFlush(collectionEntity);

        // Get all the collectionList where name is not null
        defaultCollectionFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllCollectionsByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedCollectionEntity = collectionRepository.saveAndFlush(collectionEntity);

        // Get all the collectionList where name contains
        defaultCollectionFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllCollectionsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCollectionEntity = collectionRepository.saveAndFlush(collectionEntity);

        // Get all the collectionList where name does not contain
        defaultCollectionFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllCollectionsByIsPublicIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCollectionEntity = collectionRepository.saveAndFlush(collectionEntity);

        // Get all the collectionList where isPublic equals to
        defaultCollectionFiltering("isPublic.equals=" + DEFAULT_IS_PUBLIC, "isPublic.equals=" + UPDATED_IS_PUBLIC);
    }

    @Test
    @Transactional
    void getAllCollectionsByIsPublicIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCollectionEntity = collectionRepository.saveAndFlush(collectionEntity);

        // Get all the collectionList where isPublic in
        defaultCollectionFiltering("isPublic.in=" + DEFAULT_IS_PUBLIC + "," + UPDATED_IS_PUBLIC, "isPublic.in=" + UPDATED_IS_PUBLIC);
    }

    @Test
    @Transactional
    void getAllCollectionsByIsPublicIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCollectionEntity = collectionRepository.saveAndFlush(collectionEntity);

        // Get all the collectionList where isPublic is not null
        defaultCollectionFiltering("isPublic.specified=true", "isPublic.specified=false");
    }

    @Test
    @Transactional
    void getAllCollectionsByBookCountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCollectionEntity = collectionRepository.saveAndFlush(collectionEntity);

        // Get all the collectionList where bookCount equals to
        defaultCollectionFiltering("bookCount.equals=" + DEFAULT_BOOK_COUNT, "bookCount.equals=" + UPDATED_BOOK_COUNT);
    }

    @Test
    @Transactional
    void getAllCollectionsByBookCountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCollectionEntity = collectionRepository.saveAndFlush(collectionEntity);

        // Get all the collectionList where bookCount in
        defaultCollectionFiltering("bookCount.in=" + DEFAULT_BOOK_COUNT + "," + UPDATED_BOOK_COUNT, "bookCount.in=" + UPDATED_BOOK_COUNT);
    }

    @Test
    @Transactional
    void getAllCollectionsByBookCountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCollectionEntity = collectionRepository.saveAndFlush(collectionEntity);

        // Get all the collectionList where bookCount is not null
        defaultCollectionFiltering("bookCount.specified=true", "bookCount.specified=false");
    }

    @Test
    @Transactional
    void getAllCollectionsByBookCountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCollectionEntity = collectionRepository.saveAndFlush(collectionEntity);

        // Get all the collectionList where bookCount is greater than or equal to
        defaultCollectionFiltering(
            "bookCount.greaterThanOrEqual=" + DEFAULT_BOOK_COUNT,
            "bookCount.greaterThanOrEqual=" + UPDATED_BOOK_COUNT
        );
    }

    @Test
    @Transactional
    void getAllCollectionsByBookCountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCollectionEntity = collectionRepository.saveAndFlush(collectionEntity);

        // Get all the collectionList where bookCount is less than or equal to
        defaultCollectionFiltering("bookCount.lessThanOrEqual=" + DEFAULT_BOOK_COUNT, "bookCount.lessThanOrEqual=" + SMALLER_BOOK_COUNT);
    }

    @Test
    @Transactional
    void getAllCollectionsByBookCountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCollectionEntity = collectionRepository.saveAndFlush(collectionEntity);

        // Get all the collectionList where bookCount is less than
        defaultCollectionFiltering("bookCount.lessThan=" + UPDATED_BOOK_COUNT, "bookCount.lessThan=" + DEFAULT_BOOK_COUNT);
    }

    @Test
    @Transactional
    void getAllCollectionsByBookCountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCollectionEntity = collectionRepository.saveAndFlush(collectionEntity);

        // Get all the collectionList where bookCount is greater than
        defaultCollectionFiltering("bookCount.greaterThan=" + SMALLER_BOOK_COUNT, "bookCount.greaterThan=" + DEFAULT_BOOK_COUNT);
    }

    @Test
    @Transactional
    void getAllCollectionsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCollectionEntity = collectionRepository.saveAndFlush(collectionEntity);

        // Get all the collectionList where createdAt equals to
        defaultCollectionFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllCollectionsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCollectionEntity = collectionRepository.saveAndFlush(collectionEntity);

        // Get all the collectionList where createdAt in
        defaultCollectionFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllCollectionsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCollectionEntity = collectionRepository.saveAndFlush(collectionEntity);

        // Get all the collectionList where createdAt is not null
        defaultCollectionFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllCollectionsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCollectionEntity = collectionRepository.saveAndFlush(collectionEntity);

        // Get all the collectionList where updatedAt equals to
        defaultCollectionFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllCollectionsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCollectionEntity = collectionRepository.saveAndFlush(collectionEntity);

        // Get all the collectionList where updatedAt in
        defaultCollectionFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllCollectionsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCollectionEntity = collectionRepository.saveAndFlush(collectionEntity);

        // Get all the collectionList where updatedAt is not null
        defaultCollectionFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllCollectionsByUserIsEqualToSomething() throws Exception {
        UserEntity user;
        if (TestUtil.findAll(em, UserEntity.class).isEmpty()) {
            collectionRepository.saveAndFlush(collectionEntity);
            user = UserResourceIT.createEntity();
        } else {
            user = TestUtil.findAll(em, UserEntity.class).get(0);
        }
        em.persist(user);
        em.flush();
        collectionEntity.setUser(user);
        collectionRepository.saveAndFlush(collectionEntity);
        Long userId = user.getId();
        // Get all the collectionList where user equals to userId
        defaultCollectionShouldBeFound("userId.equals=" + userId);

        // Get all the collectionList where user equals to (userId + 1)
        defaultCollectionShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    private void defaultCollectionFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCollectionShouldBeFound(shouldBeFound);
        defaultCollectionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCollectionShouldBeFound(String filter) throws Exception {
        restCollectionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(collectionEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].isPublic").value(hasItem(DEFAULT_IS_PUBLIC)))
            .andExpect(jsonPath("$.[*].bookCount").value(hasItem(DEFAULT_BOOK_COUNT)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restCollectionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCollectionShouldNotBeFound(String filter) throws Exception {
        restCollectionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCollectionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCollection() throws Exception {
        // Get the collection
        restCollectionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCollection() throws Exception {
        // Initialize the database
        insertedCollectionEntity = collectionRepository.saveAndFlush(collectionEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the collection
        CollectionEntity updatedCollectionEntity = collectionRepository.findById(collectionEntity.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCollectionEntity are not directly saved in db
        em.detach(updatedCollectionEntity);
        updatedCollectionEntity
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .isPublic(UPDATED_IS_PUBLIC)
            .bookCount(UPDATED_BOOK_COUNT)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restCollectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCollectionEntity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedCollectionEntity))
            )
            .andExpect(status().isOk());

        // Validate the Collection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCollectionEntityToMatchAllProperties(updatedCollectionEntity);
    }

    @Test
    @Transactional
    void putNonExistingCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        collectionEntity.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCollectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, collectionEntity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(collectionEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Collection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        collectionEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCollectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(collectionEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Collection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        collectionEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCollectionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(collectionEntity)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Collection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCollectionWithPatch() throws Exception {
        // Initialize the database
        insertedCollectionEntity = collectionRepository.saveAndFlush(collectionEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the collection using partial update
        CollectionEntity partialUpdatedCollectionEntity = new CollectionEntity();
        partialUpdatedCollectionEntity.setId(collectionEntity.getId());

        partialUpdatedCollectionEntity.name(UPDATED_NAME).createdAt(UPDATED_CREATED_AT);

        restCollectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCollectionEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCollectionEntity))
            )
            .andExpect(status().isOk());

        // Validate the Collection in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCollectionEntityUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCollectionEntity, collectionEntity),
            getPersistedCollectionEntity(collectionEntity)
        );
    }

    @Test
    @Transactional
    void fullUpdateCollectionWithPatch() throws Exception {
        // Initialize the database
        insertedCollectionEntity = collectionRepository.saveAndFlush(collectionEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the collection using partial update
        CollectionEntity partialUpdatedCollectionEntity = new CollectionEntity();
        partialUpdatedCollectionEntity.setId(collectionEntity.getId());

        partialUpdatedCollectionEntity
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .isPublic(UPDATED_IS_PUBLIC)
            .bookCount(UPDATED_BOOK_COUNT)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restCollectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCollectionEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCollectionEntity))
            )
            .andExpect(status().isOk());

        // Validate the Collection in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCollectionEntityUpdatableFieldsEquals(
            partialUpdatedCollectionEntity,
            getPersistedCollectionEntity(partialUpdatedCollectionEntity)
        );
    }

    @Test
    @Transactional
    void patchNonExistingCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        collectionEntity.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCollectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, collectionEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(collectionEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Collection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        collectionEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCollectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(collectionEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Collection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        collectionEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCollectionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(collectionEntity)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Collection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCollection() throws Exception {
        // Initialize the database
        insertedCollectionEntity = collectionRepository.saveAndFlush(collectionEntity);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the collection
        restCollectionMockMvc
            .perform(delete(ENTITY_API_URL_ID, collectionEntity.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return collectionRepository.count();
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

    protected CollectionEntity getPersistedCollectionEntity(CollectionEntity collection) {
        return collectionRepository.findById(collection.getId()).orElseThrow();
    }

    protected void assertPersistedCollectionEntityToMatchAllProperties(CollectionEntity expectedCollectionEntity) {
        assertCollectionEntityAllPropertiesEquals(expectedCollectionEntity, getPersistedCollectionEntity(expectedCollectionEntity));
    }

    protected void assertPersistedCollectionEntityToMatchUpdatableProperties(CollectionEntity expectedCollectionEntity) {
        assertCollectionEntityAllUpdatablePropertiesEquals(
            expectedCollectionEntity,
            getPersistedCollectionEntity(expectedCollectionEntity)
        );
    }
}
