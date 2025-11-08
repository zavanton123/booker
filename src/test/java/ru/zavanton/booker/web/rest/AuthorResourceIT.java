package ru.zavanton.booker.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.zavanton.booker.domain.AuthorEntityAsserts.*;
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
import ru.zavanton.booker.domain.AuthorEntity;
import ru.zavanton.booker.repository.AuthorRepository;

/**
 * Integration tests for the {@link AuthorResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AuthorResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_FULL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FULL_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_BIOGRAPHY = "AAAAAAAAAA";
    private static final String UPDATED_BIOGRAPHY = "BBBBBBBBBB";

    private static final String DEFAULT_PHOTO_URL = "AAAAAAAAAA";
    private static final String UPDATED_PHOTO_URL = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_BIRTH_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_BIRTH_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_BIRTH_DATE = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_DEATH_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DEATH_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DEATH_DATE = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_NATIONALITY = "AAAAAAAAAA";
    private static final String UPDATED_NATIONALITY = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/authors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAuthorMockMvc;

    private AuthorEntity authorEntity;

    private AuthorEntity insertedAuthorEntity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AuthorEntity createEntity() {
        return new AuthorEntity()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .fullName(DEFAULT_FULL_NAME)
            .biography(DEFAULT_BIOGRAPHY)
            .photoUrl(DEFAULT_PHOTO_URL)
            .birthDate(DEFAULT_BIRTH_DATE)
            .deathDate(DEFAULT_DEATH_DATE)
            .nationality(DEFAULT_NATIONALITY)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AuthorEntity createUpdatedEntity() {
        return new AuthorEntity()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .fullName(UPDATED_FULL_NAME)
            .biography(UPDATED_BIOGRAPHY)
            .photoUrl(UPDATED_PHOTO_URL)
            .birthDate(UPDATED_BIRTH_DATE)
            .deathDate(UPDATED_DEATH_DATE)
            .nationality(UPDATED_NATIONALITY)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    public void initTest() {
        authorEntity = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedAuthorEntity != null) {
            authorRepository.delete(insertedAuthorEntity);
            insertedAuthorEntity = null;
        }
    }

    @Test
    @Transactional
    void createAuthor() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Author
        var returnedAuthorEntity = om.readValue(
            restAuthorMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(authorEntity)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AuthorEntity.class
        );

        // Validate the Author in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertAuthorEntityUpdatableFieldsEquals(returnedAuthorEntity, getPersistedAuthorEntity(returnedAuthorEntity));

        insertedAuthorEntity = returnedAuthorEntity;
    }

    @Test
    @Transactional
    void createAuthorWithExistingId() throws Exception {
        // Create the Author with an existing ID
        authorEntity.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAuthorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(authorEntity)))
            .andExpect(status().isBadRequest());

        // Validate the Author in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllAuthors() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList
        restAuthorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(authorEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].biography").value(hasItem(DEFAULT_BIOGRAPHY)))
            .andExpect(jsonPath("$.[*].photoUrl").value(hasItem(DEFAULT_PHOTO_URL)))
            .andExpect(jsonPath("$.[*].birthDate").value(hasItem(DEFAULT_BIRTH_DATE.toString())))
            .andExpect(jsonPath("$.[*].deathDate").value(hasItem(DEFAULT_DEATH_DATE.toString())))
            .andExpect(jsonPath("$.[*].nationality").value(hasItem(DEFAULT_NATIONALITY)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getAuthor() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get the author
        restAuthorMockMvc
            .perform(get(ENTITY_API_URL_ID, authorEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(authorEntity.getId().intValue()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.fullName").value(DEFAULT_FULL_NAME))
            .andExpect(jsonPath("$.biography").value(DEFAULT_BIOGRAPHY))
            .andExpect(jsonPath("$.photoUrl").value(DEFAULT_PHOTO_URL))
            .andExpect(jsonPath("$.birthDate").value(DEFAULT_BIRTH_DATE.toString()))
            .andExpect(jsonPath("$.deathDate").value(DEFAULT_DEATH_DATE.toString()))
            .andExpect(jsonPath("$.nationality").value(DEFAULT_NATIONALITY))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getAuthorsByIdFiltering() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        Long id = authorEntity.getId();

        defaultAuthorFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAuthorFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAuthorFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAuthorsByFirstNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where firstName equals to
        defaultAuthorFiltering("firstName.equals=" + DEFAULT_FIRST_NAME, "firstName.equals=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllAuthorsByFirstNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where firstName in
        defaultAuthorFiltering("firstName.in=" + DEFAULT_FIRST_NAME + "," + UPDATED_FIRST_NAME, "firstName.in=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllAuthorsByFirstNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where firstName is not null
        defaultAuthorFiltering("firstName.specified=true", "firstName.specified=false");
    }

    @Test
    @Transactional
    void getAllAuthorsByFirstNameContainsSomething() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where firstName contains
        defaultAuthorFiltering("firstName.contains=" + DEFAULT_FIRST_NAME, "firstName.contains=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllAuthorsByFirstNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where firstName does not contain
        defaultAuthorFiltering("firstName.doesNotContain=" + UPDATED_FIRST_NAME, "firstName.doesNotContain=" + DEFAULT_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllAuthorsByLastNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where lastName equals to
        defaultAuthorFiltering("lastName.equals=" + DEFAULT_LAST_NAME, "lastName.equals=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllAuthorsByLastNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where lastName in
        defaultAuthorFiltering("lastName.in=" + DEFAULT_LAST_NAME + "," + UPDATED_LAST_NAME, "lastName.in=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllAuthorsByLastNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where lastName is not null
        defaultAuthorFiltering("lastName.specified=true", "lastName.specified=false");
    }

    @Test
    @Transactional
    void getAllAuthorsByLastNameContainsSomething() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where lastName contains
        defaultAuthorFiltering("lastName.contains=" + DEFAULT_LAST_NAME, "lastName.contains=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllAuthorsByLastNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where lastName does not contain
        defaultAuthorFiltering("lastName.doesNotContain=" + UPDATED_LAST_NAME, "lastName.doesNotContain=" + DEFAULT_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllAuthorsByFullNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where fullName equals to
        defaultAuthorFiltering("fullName.equals=" + DEFAULT_FULL_NAME, "fullName.equals=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllAuthorsByFullNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where fullName in
        defaultAuthorFiltering("fullName.in=" + DEFAULT_FULL_NAME + "," + UPDATED_FULL_NAME, "fullName.in=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllAuthorsByFullNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where fullName is not null
        defaultAuthorFiltering("fullName.specified=true", "fullName.specified=false");
    }

    @Test
    @Transactional
    void getAllAuthorsByFullNameContainsSomething() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where fullName contains
        defaultAuthorFiltering("fullName.contains=" + DEFAULT_FULL_NAME, "fullName.contains=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllAuthorsByFullNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where fullName does not contain
        defaultAuthorFiltering("fullName.doesNotContain=" + UPDATED_FULL_NAME, "fullName.doesNotContain=" + DEFAULT_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllAuthorsByPhotoUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where photoUrl equals to
        defaultAuthorFiltering("photoUrl.equals=" + DEFAULT_PHOTO_URL, "photoUrl.equals=" + UPDATED_PHOTO_URL);
    }

    @Test
    @Transactional
    void getAllAuthorsByPhotoUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where photoUrl in
        defaultAuthorFiltering("photoUrl.in=" + DEFAULT_PHOTO_URL + "," + UPDATED_PHOTO_URL, "photoUrl.in=" + UPDATED_PHOTO_URL);
    }

    @Test
    @Transactional
    void getAllAuthorsByPhotoUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where photoUrl is not null
        defaultAuthorFiltering("photoUrl.specified=true", "photoUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllAuthorsByPhotoUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where photoUrl contains
        defaultAuthorFiltering("photoUrl.contains=" + DEFAULT_PHOTO_URL, "photoUrl.contains=" + UPDATED_PHOTO_URL);
    }

    @Test
    @Transactional
    void getAllAuthorsByPhotoUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where photoUrl does not contain
        defaultAuthorFiltering("photoUrl.doesNotContain=" + UPDATED_PHOTO_URL, "photoUrl.doesNotContain=" + DEFAULT_PHOTO_URL);
    }

    @Test
    @Transactional
    void getAllAuthorsByBirthDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where birthDate equals to
        defaultAuthorFiltering("birthDate.equals=" + DEFAULT_BIRTH_DATE, "birthDate.equals=" + UPDATED_BIRTH_DATE);
    }

    @Test
    @Transactional
    void getAllAuthorsByBirthDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where birthDate in
        defaultAuthorFiltering("birthDate.in=" + DEFAULT_BIRTH_DATE + "," + UPDATED_BIRTH_DATE, "birthDate.in=" + UPDATED_BIRTH_DATE);
    }

    @Test
    @Transactional
    void getAllAuthorsByBirthDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where birthDate is not null
        defaultAuthorFiltering("birthDate.specified=true", "birthDate.specified=false");
    }

    @Test
    @Transactional
    void getAllAuthorsByBirthDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where birthDate is greater than or equal to
        defaultAuthorFiltering("birthDate.greaterThanOrEqual=" + DEFAULT_BIRTH_DATE, "birthDate.greaterThanOrEqual=" + UPDATED_BIRTH_DATE);
    }

    @Test
    @Transactional
    void getAllAuthorsByBirthDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where birthDate is less than or equal to
        defaultAuthorFiltering("birthDate.lessThanOrEqual=" + DEFAULT_BIRTH_DATE, "birthDate.lessThanOrEqual=" + SMALLER_BIRTH_DATE);
    }

    @Test
    @Transactional
    void getAllAuthorsByBirthDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where birthDate is less than
        defaultAuthorFiltering("birthDate.lessThan=" + UPDATED_BIRTH_DATE, "birthDate.lessThan=" + DEFAULT_BIRTH_DATE);
    }

    @Test
    @Transactional
    void getAllAuthorsByBirthDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where birthDate is greater than
        defaultAuthorFiltering("birthDate.greaterThan=" + SMALLER_BIRTH_DATE, "birthDate.greaterThan=" + DEFAULT_BIRTH_DATE);
    }

    @Test
    @Transactional
    void getAllAuthorsByDeathDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where deathDate equals to
        defaultAuthorFiltering("deathDate.equals=" + DEFAULT_DEATH_DATE, "deathDate.equals=" + UPDATED_DEATH_DATE);
    }

    @Test
    @Transactional
    void getAllAuthorsByDeathDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where deathDate in
        defaultAuthorFiltering("deathDate.in=" + DEFAULT_DEATH_DATE + "," + UPDATED_DEATH_DATE, "deathDate.in=" + UPDATED_DEATH_DATE);
    }

    @Test
    @Transactional
    void getAllAuthorsByDeathDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where deathDate is not null
        defaultAuthorFiltering("deathDate.specified=true", "deathDate.specified=false");
    }

    @Test
    @Transactional
    void getAllAuthorsByDeathDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where deathDate is greater than or equal to
        defaultAuthorFiltering("deathDate.greaterThanOrEqual=" + DEFAULT_DEATH_DATE, "deathDate.greaterThanOrEqual=" + UPDATED_DEATH_DATE);
    }

    @Test
    @Transactional
    void getAllAuthorsByDeathDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where deathDate is less than or equal to
        defaultAuthorFiltering("deathDate.lessThanOrEqual=" + DEFAULT_DEATH_DATE, "deathDate.lessThanOrEqual=" + SMALLER_DEATH_DATE);
    }

    @Test
    @Transactional
    void getAllAuthorsByDeathDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where deathDate is less than
        defaultAuthorFiltering("deathDate.lessThan=" + UPDATED_DEATH_DATE, "deathDate.lessThan=" + DEFAULT_DEATH_DATE);
    }

    @Test
    @Transactional
    void getAllAuthorsByDeathDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where deathDate is greater than
        defaultAuthorFiltering("deathDate.greaterThan=" + SMALLER_DEATH_DATE, "deathDate.greaterThan=" + DEFAULT_DEATH_DATE);
    }

    @Test
    @Transactional
    void getAllAuthorsByNationalityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where nationality equals to
        defaultAuthorFiltering("nationality.equals=" + DEFAULT_NATIONALITY, "nationality.equals=" + UPDATED_NATIONALITY);
    }

    @Test
    @Transactional
    void getAllAuthorsByNationalityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where nationality in
        defaultAuthorFiltering(
            "nationality.in=" + DEFAULT_NATIONALITY + "," + UPDATED_NATIONALITY,
            "nationality.in=" + UPDATED_NATIONALITY
        );
    }

    @Test
    @Transactional
    void getAllAuthorsByNationalityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where nationality is not null
        defaultAuthorFiltering("nationality.specified=true", "nationality.specified=false");
    }

    @Test
    @Transactional
    void getAllAuthorsByNationalityContainsSomething() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where nationality contains
        defaultAuthorFiltering("nationality.contains=" + DEFAULT_NATIONALITY, "nationality.contains=" + UPDATED_NATIONALITY);
    }

    @Test
    @Transactional
    void getAllAuthorsByNationalityNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where nationality does not contain
        defaultAuthorFiltering("nationality.doesNotContain=" + UPDATED_NATIONALITY, "nationality.doesNotContain=" + DEFAULT_NATIONALITY);
    }

    @Test
    @Transactional
    void getAllAuthorsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where createdAt equals to
        defaultAuthorFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllAuthorsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where createdAt in
        defaultAuthorFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllAuthorsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where createdAt is not null
        defaultAuthorFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllAuthorsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where updatedAt equals to
        defaultAuthorFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllAuthorsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where updatedAt in
        defaultAuthorFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllAuthorsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        // Get all the authorList where updatedAt is not null
        defaultAuthorFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    private void defaultAuthorFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultAuthorShouldBeFound(shouldBeFound);
        defaultAuthorShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAuthorShouldBeFound(String filter) throws Exception {
        restAuthorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(authorEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].biography").value(hasItem(DEFAULT_BIOGRAPHY)))
            .andExpect(jsonPath("$.[*].photoUrl").value(hasItem(DEFAULT_PHOTO_URL)))
            .andExpect(jsonPath("$.[*].birthDate").value(hasItem(DEFAULT_BIRTH_DATE.toString())))
            .andExpect(jsonPath("$.[*].deathDate").value(hasItem(DEFAULT_DEATH_DATE.toString())))
            .andExpect(jsonPath("$.[*].nationality").value(hasItem(DEFAULT_NATIONALITY)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restAuthorMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAuthorShouldNotBeFound(String filter) throws Exception {
        restAuthorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAuthorMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAuthor() throws Exception {
        // Get the author
        restAuthorMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAuthor() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the author
        AuthorEntity updatedAuthorEntity = authorRepository.findById(authorEntity.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAuthorEntity are not directly saved in db
        em.detach(updatedAuthorEntity);
        updatedAuthorEntity
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .fullName(UPDATED_FULL_NAME)
            .biography(UPDATED_BIOGRAPHY)
            .photoUrl(UPDATED_PHOTO_URL)
            .birthDate(UPDATED_BIRTH_DATE)
            .deathDate(UPDATED_DEATH_DATE)
            .nationality(UPDATED_NATIONALITY)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restAuthorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAuthorEntity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedAuthorEntity))
            )
            .andExpect(status().isOk());

        // Validate the Author in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAuthorEntityToMatchAllProperties(updatedAuthorEntity);
    }

    @Test
    @Transactional
    void putNonExistingAuthor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        authorEntity.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAuthorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, authorEntity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(authorEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Author in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAuthor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        authorEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuthorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(authorEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Author in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAuthor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        authorEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuthorMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(authorEntity)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Author in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAuthorWithPatch() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the author using partial update
        AuthorEntity partialUpdatedAuthorEntity = new AuthorEntity();
        partialUpdatedAuthorEntity.setId(authorEntity.getId());

        partialUpdatedAuthorEntity.firstName(UPDATED_FIRST_NAME).createdAt(UPDATED_CREATED_AT);

        restAuthorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAuthorEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAuthorEntity))
            )
            .andExpect(status().isOk());

        // Validate the Author in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAuthorEntityUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAuthorEntity, authorEntity),
            getPersistedAuthorEntity(authorEntity)
        );
    }

    @Test
    @Transactional
    void fullUpdateAuthorWithPatch() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the author using partial update
        AuthorEntity partialUpdatedAuthorEntity = new AuthorEntity();
        partialUpdatedAuthorEntity.setId(authorEntity.getId());

        partialUpdatedAuthorEntity
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .fullName(UPDATED_FULL_NAME)
            .biography(UPDATED_BIOGRAPHY)
            .photoUrl(UPDATED_PHOTO_URL)
            .birthDate(UPDATED_BIRTH_DATE)
            .deathDate(UPDATED_DEATH_DATE)
            .nationality(UPDATED_NATIONALITY)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restAuthorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAuthorEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAuthorEntity))
            )
            .andExpect(status().isOk());

        // Validate the Author in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAuthorEntityUpdatableFieldsEquals(partialUpdatedAuthorEntity, getPersistedAuthorEntity(partialUpdatedAuthorEntity));
    }

    @Test
    @Transactional
    void patchNonExistingAuthor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        authorEntity.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAuthorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, authorEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(authorEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Author in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAuthor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        authorEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuthorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(authorEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Author in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAuthor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        authorEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuthorMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(authorEntity)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Author in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAuthor() throws Exception {
        // Initialize the database
        insertedAuthorEntity = authorRepository.saveAndFlush(authorEntity);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the author
        restAuthorMockMvc
            .perform(delete(ENTITY_API_URL_ID, authorEntity.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return authorRepository.count();
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

    protected AuthorEntity getPersistedAuthorEntity(AuthorEntity author) {
        return authorRepository.findById(author.getId()).orElseThrow();
    }

    protected void assertPersistedAuthorEntityToMatchAllProperties(AuthorEntity expectedAuthorEntity) {
        assertAuthorEntityAllPropertiesEquals(expectedAuthorEntity, getPersistedAuthorEntity(expectedAuthorEntity));
    }

    protected void assertPersistedAuthorEntityToMatchUpdatableProperties(AuthorEntity expectedAuthorEntity) {
        assertAuthorEntityAllUpdatablePropertiesEquals(expectedAuthorEntity, getPersistedAuthorEntity(expectedAuthorEntity));
    }
}
