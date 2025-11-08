package ru.zavanton.booker.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.zavanton.booker.domain.PublisherEntityAsserts.*;
import static ru.zavanton.booker.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
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
import ru.zavanton.booker.domain.PublisherEntity;
import ru.zavanton.booker.repository.PublisherRepository;

/**
 * Integration tests for the {@link PublisherResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PublisherResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_WEBSITE_URL = "AAAAAAAAAA";
    private static final String UPDATED_WEBSITE_URL = "BBBBBBBBBB";

    private static final String DEFAULT_LOGO_URL = "AAAAAAAAAA";
    private static final String UPDATED_LOGO_URL = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_FOUNDED_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_FOUNDED_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_FOUNDED_DATE = LocalDate.ofEpochDay(-1L);

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/publishers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPublisherMockMvc;

    private PublisherEntity publisherEntity;

    private PublisherEntity insertedPublisherEntity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PublisherEntity createEntity() {
        return new PublisherEntity()
            .name(DEFAULT_NAME)
            .websiteUrl(DEFAULT_WEBSITE_URL)
            .logoUrl(DEFAULT_LOGO_URL)
            .foundedDate(DEFAULT_FOUNDED_DATE)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PublisherEntity createUpdatedEntity() {
        return new PublisherEntity()
            .name(UPDATED_NAME)
            .websiteUrl(UPDATED_WEBSITE_URL)
            .logoUrl(UPDATED_LOGO_URL)
            .foundedDate(UPDATED_FOUNDED_DATE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    public void initTest() {
        publisherEntity = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedPublisherEntity != null) {
            publisherRepository.delete(insertedPublisherEntity);
            insertedPublisherEntity = null;
        }
    }

    @Test
    @Transactional
    void createPublisher() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Publisher
        var returnedPublisherEntity = om.readValue(
            restPublisherMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(publisherEntity)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PublisherEntity.class
        );

        // Validate the Publisher in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertPublisherEntityUpdatableFieldsEquals(returnedPublisherEntity, getPersistedPublisherEntity(returnedPublisherEntity));

        insertedPublisherEntity = returnedPublisherEntity;
    }

    @Test
    @Transactional
    void createPublisherWithExistingId() throws Exception {
        // Create the Publisher with an existing ID
        publisherEntity.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPublisherMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(publisherEntity)))
            .andExpect(status().isBadRequest());

        // Validate the Publisher in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        publisherEntity.setName(null);

        // Create the Publisher, which fails.

        restPublisherMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(publisherEntity)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPublishers() throws Exception {
        // Initialize the database
        insertedPublisherEntity = publisherRepository.saveAndFlush(publisherEntity);

        // Get all the publisherList
        restPublisherMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(publisherEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].websiteUrl").value(hasItem(DEFAULT_WEBSITE_URL)))
            .andExpect(jsonPath("$.[*].logoUrl").value(hasItem(DEFAULT_LOGO_URL)))
            .andExpect(jsonPath("$.[*].foundedDate").value(hasItem(DEFAULT_FOUNDED_DATE.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getPublisher() throws Exception {
        // Initialize the database
        insertedPublisherEntity = publisherRepository.saveAndFlush(publisherEntity);

        // Get the publisher
        restPublisherMockMvc
            .perform(get(ENTITY_API_URL_ID, publisherEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(publisherEntity.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.websiteUrl").value(DEFAULT_WEBSITE_URL))
            .andExpect(jsonPath("$.logoUrl").value(DEFAULT_LOGO_URL))
            .andExpect(jsonPath("$.foundedDate").value(DEFAULT_FOUNDED_DATE.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getPublishersByIdFiltering() throws Exception {
        // Initialize the database
        insertedPublisherEntity = publisherRepository.saveAndFlush(publisherEntity);

        Long id = publisherEntity.getId();

        defaultPublisherFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultPublisherFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultPublisherFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPublishersByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPublisherEntity = publisherRepository.saveAndFlush(publisherEntity);

        // Get all the publisherList where name equals to
        defaultPublisherFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPublishersByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPublisherEntity = publisherRepository.saveAndFlush(publisherEntity);

        // Get all the publisherList where name in
        defaultPublisherFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPublishersByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPublisherEntity = publisherRepository.saveAndFlush(publisherEntity);

        // Get all the publisherList where name is not null
        defaultPublisherFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllPublishersByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedPublisherEntity = publisherRepository.saveAndFlush(publisherEntity);

        // Get all the publisherList where name contains
        defaultPublisherFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPublishersByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPublisherEntity = publisherRepository.saveAndFlush(publisherEntity);

        // Get all the publisherList where name does not contain
        defaultPublisherFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllPublishersByWebsiteUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPublisherEntity = publisherRepository.saveAndFlush(publisherEntity);

        // Get all the publisherList where websiteUrl equals to
        defaultPublisherFiltering("websiteUrl.equals=" + DEFAULT_WEBSITE_URL, "websiteUrl.equals=" + UPDATED_WEBSITE_URL);
    }

    @Test
    @Transactional
    void getAllPublishersByWebsiteUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPublisherEntity = publisherRepository.saveAndFlush(publisherEntity);

        // Get all the publisherList where websiteUrl in
        defaultPublisherFiltering(
            "websiteUrl.in=" + DEFAULT_WEBSITE_URL + "," + UPDATED_WEBSITE_URL,
            "websiteUrl.in=" + UPDATED_WEBSITE_URL
        );
    }

    @Test
    @Transactional
    void getAllPublishersByWebsiteUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPublisherEntity = publisherRepository.saveAndFlush(publisherEntity);

        // Get all the publisherList where websiteUrl is not null
        defaultPublisherFiltering("websiteUrl.specified=true", "websiteUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllPublishersByWebsiteUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedPublisherEntity = publisherRepository.saveAndFlush(publisherEntity);

        // Get all the publisherList where websiteUrl contains
        defaultPublisherFiltering("websiteUrl.contains=" + DEFAULT_WEBSITE_URL, "websiteUrl.contains=" + UPDATED_WEBSITE_URL);
    }

    @Test
    @Transactional
    void getAllPublishersByWebsiteUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPublisherEntity = publisherRepository.saveAndFlush(publisherEntity);

        // Get all the publisherList where websiteUrl does not contain
        defaultPublisherFiltering("websiteUrl.doesNotContain=" + UPDATED_WEBSITE_URL, "websiteUrl.doesNotContain=" + DEFAULT_WEBSITE_URL);
    }

    @Test
    @Transactional
    void getAllPublishersByLogoUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPublisherEntity = publisherRepository.saveAndFlush(publisherEntity);

        // Get all the publisherList where logoUrl equals to
        defaultPublisherFiltering("logoUrl.equals=" + DEFAULT_LOGO_URL, "logoUrl.equals=" + UPDATED_LOGO_URL);
    }

    @Test
    @Transactional
    void getAllPublishersByLogoUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPublisherEntity = publisherRepository.saveAndFlush(publisherEntity);

        // Get all the publisherList where logoUrl in
        defaultPublisherFiltering("logoUrl.in=" + DEFAULT_LOGO_URL + "," + UPDATED_LOGO_URL, "logoUrl.in=" + UPDATED_LOGO_URL);
    }

    @Test
    @Transactional
    void getAllPublishersByLogoUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPublisherEntity = publisherRepository.saveAndFlush(publisherEntity);

        // Get all the publisherList where logoUrl is not null
        defaultPublisherFiltering("logoUrl.specified=true", "logoUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllPublishersByLogoUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedPublisherEntity = publisherRepository.saveAndFlush(publisherEntity);

        // Get all the publisherList where logoUrl contains
        defaultPublisherFiltering("logoUrl.contains=" + DEFAULT_LOGO_URL, "logoUrl.contains=" + UPDATED_LOGO_URL);
    }

    @Test
    @Transactional
    void getAllPublishersByLogoUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPublisherEntity = publisherRepository.saveAndFlush(publisherEntity);

        // Get all the publisherList where logoUrl does not contain
        defaultPublisherFiltering("logoUrl.doesNotContain=" + UPDATED_LOGO_URL, "logoUrl.doesNotContain=" + DEFAULT_LOGO_URL);
    }

    @Test
    @Transactional
    void getAllPublishersByFoundedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPublisherEntity = publisherRepository.saveAndFlush(publisherEntity);

        // Get all the publisherList where foundedDate equals to
        defaultPublisherFiltering("foundedDate.equals=" + DEFAULT_FOUNDED_DATE, "foundedDate.equals=" + UPDATED_FOUNDED_DATE);
    }

    @Test
    @Transactional
    void getAllPublishersByFoundedDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPublisherEntity = publisherRepository.saveAndFlush(publisherEntity);

        // Get all the publisherList where foundedDate in
        defaultPublisherFiltering(
            "foundedDate.in=" + DEFAULT_FOUNDED_DATE + "," + UPDATED_FOUNDED_DATE,
            "foundedDate.in=" + UPDATED_FOUNDED_DATE
        );
    }

    @Test
    @Transactional
    void getAllPublishersByFoundedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPublisherEntity = publisherRepository.saveAndFlush(publisherEntity);

        // Get all the publisherList where foundedDate is not null
        defaultPublisherFiltering("foundedDate.specified=true", "foundedDate.specified=false");
    }

    @Test
    @Transactional
    void getAllPublishersByFoundedDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPublisherEntity = publisherRepository.saveAndFlush(publisherEntity);

        // Get all the publisherList where foundedDate is greater than or equal to
        defaultPublisherFiltering(
            "foundedDate.greaterThanOrEqual=" + DEFAULT_FOUNDED_DATE,
            "foundedDate.greaterThanOrEqual=" + UPDATED_FOUNDED_DATE
        );
    }

    @Test
    @Transactional
    void getAllPublishersByFoundedDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPublisherEntity = publisherRepository.saveAndFlush(publisherEntity);

        // Get all the publisherList where foundedDate is less than or equal to
        defaultPublisherFiltering(
            "foundedDate.lessThanOrEqual=" + DEFAULT_FOUNDED_DATE,
            "foundedDate.lessThanOrEqual=" + SMALLER_FOUNDED_DATE
        );
    }

    @Test
    @Transactional
    void getAllPublishersByFoundedDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPublisherEntity = publisherRepository.saveAndFlush(publisherEntity);

        // Get all the publisherList where foundedDate is less than
        defaultPublisherFiltering("foundedDate.lessThan=" + UPDATED_FOUNDED_DATE, "foundedDate.lessThan=" + DEFAULT_FOUNDED_DATE);
    }

    @Test
    @Transactional
    void getAllPublishersByFoundedDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPublisherEntity = publisherRepository.saveAndFlush(publisherEntity);

        // Get all the publisherList where foundedDate is greater than
        defaultPublisherFiltering("foundedDate.greaterThan=" + SMALLER_FOUNDED_DATE, "foundedDate.greaterThan=" + DEFAULT_FOUNDED_DATE);
    }

    @Test
    @Transactional
    void getAllPublishersByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPublisherEntity = publisherRepository.saveAndFlush(publisherEntity);

        // Get all the publisherList where createdAt equals to
        defaultPublisherFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllPublishersByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPublisherEntity = publisherRepository.saveAndFlush(publisherEntity);

        // Get all the publisherList where createdAt in
        defaultPublisherFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllPublishersByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPublisherEntity = publisherRepository.saveAndFlush(publisherEntity);

        // Get all the publisherList where createdAt is not null
        defaultPublisherFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllPublishersByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPublisherEntity = publisherRepository.saveAndFlush(publisherEntity);

        // Get all the publisherList where updatedAt equals to
        defaultPublisherFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllPublishersByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPublisherEntity = publisherRepository.saveAndFlush(publisherEntity);

        // Get all the publisherList where updatedAt in
        defaultPublisherFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllPublishersByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPublisherEntity = publisherRepository.saveAndFlush(publisherEntity);

        // Get all the publisherList where updatedAt is not null
        defaultPublisherFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    private void defaultPublisherFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultPublisherShouldBeFound(shouldBeFound);
        defaultPublisherShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPublisherShouldBeFound(String filter) throws Exception {
        restPublisherMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(publisherEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].websiteUrl").value(hasItem(DEFAULT_WEBSITE_URL)))
            .andExpect(jsonPath("$.[*].logoUrl").value(hasItem(DEFAULT_LOGO_URL)))
            .andExpect(jsonPath("$.[*].foundedDate").value(hasItem(DEFAULT_FOUNDED_DATE.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restPublisherMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPublisherShouldNotBeFound(String filter) throws Exception {
        restPublisherMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPublisherMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPublisher() throws Exception {
        // Get the publisher
        restPublisherMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPublisher() throws Exception {
        // Initialize the database
        insertedPublisherEntity = publisherRepository.saveAndFlush(publisherEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the publisher
        PublisherEntity updatedPublisherEntity = publisherRepository.findById(publisherEntity.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPublisherEntity are not directly saved in db
        em.detach(updatedPublisherEntity);
        updatedPublisherEntity
            .name(UPDATED_NAME)
            .websiteUrl(UPDATED_WEBSITE_URL)
            .logoUrl(UPDATED_LOGO_URL)
            .foundedDate(UPDATED_FOUNDED_DATE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restPublisherMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPublisherEntity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedPublisherEntity))
            )
            .andExpect(status().isOk());

        // Validate the Publisher in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPublisherEntityToMatchAllProperties(updatedPublisherEntity);
    }

    @Test
    @Transactional
    void putNonExistingPublisher() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        publisherEntity.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPublisherMockMvc
            .perform(
                put(ENTITY_API_URL_ID, publisherEntity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(publisherEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Publisher in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPublisher() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        publisherEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPublisherMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(publisherEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Publisher in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPublisher() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        publisherEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPublisherMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(publisherEntity)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Publisher in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePublisherWithPatch() throws Exception {
        // Initialize the database
        insertedPublisherEntity = publisherRepository.saveAndFlush(publisherEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the publisher using partial update
        PublisherEntity partialUpdatedPublisherEntity = new PublisherEntity();
        partialUpdatedPublisherEntity.setId(publisherEntity.getId());

        partialUpdatedPublisherEntity.logoUrl(UPDATED_LOGO_URL);

        restPublisherMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPublisherEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPublisherEntity))
            )
            .andExpect(status().isOk());

        // Validate the Publisher in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPublisherEntityUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPublisherEntity, publisherEntity),
            getPersistedPublisherEntity(publisherEntity)
        );
    }

    @Test
    @Transactional
    void fullUpdatePublisherWithPatch() throws Exception {
        // Initialize the database
        insertedPublisherEntity = publisherRepository.saveAndFlush(publisherEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the publisher using partial update
        PublisherEntity partialUpdatedPublisherEntity = new PublisherEntity();
        partialUpdatedPublisherEntity.setId(publisherEntity.getId());

        partialUpdatedPublisherEntity
            .name(UPDATED_NAME)
            .websiteUrl(UPDATED_WEBSITE_URL)
            .logoUrl(UPDATED_LOGO_URL)
            .foundedDate(UPDATED_FOUNDED_DATE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restPublisherMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPublisherEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPublisherEntity))
            )
            .andExpect(status().isOk());

        // Validate the Publisher in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPublisherEntityUpdatableFieldsEquals(
            partialUpdatedPublisherEntity,
            getPersistedPublisherEntity(partialUpdatedPublisherEntity)
        );
    }

    @Test
    @Transactional
    void patchNonExistingPublisher() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        publisherEntity.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPublisherMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, publisherEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(publisherEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Publisher in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPublisher() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        publisherEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPublisherMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(publisherEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Publisher in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPublisher() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        publisherEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPublisherMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(publisherEntity)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Publisher in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePublisher() throws Exception {
        // Initialize the database
        insertedPublisherEntity = publisherRepository.saveAndFlush(publisherEntity);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the publisher
        restPublisherMockMvc
            .perform(delete(ENTITY_API_URL_ID, publisherEntity.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return publisherRepository.count();
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

    protected PublisherEntity getPersistedPublisherEntity(PublisherEntity publisher) {
        return publisherRepository.findById(publisher.getId()).orElseThrow();
    }

    protected void assertPersistedPublisherEntityToMatchAllProperties(PublisherEntity expectedPublisherEntity) {
        assertPublisherEntityAllPropertiesEquals(expectedPublisherEntity, getPersistedPublisherEntity(expectedPublisherEntity));
    }

    protected void assertPersistedPublisherEntityToMatchUpdatableProperties(PublisherEntity expectedPublisherEntity) {
        assertPublisherEntityAllUpdatablePropertiesEquals(expectedPublisherEntity, getPersistedPublisherEntity(expectedPublisherEntity));
    }
}
