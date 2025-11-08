package ru.zavanton.booker.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.zavanton.booker.domain.ReadingStatusEntityAsserts.*;
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
import ru.zavanton.booker.domain.BookEntity;
import ru.zavanton.booker.domain.ReadingStatusEntity;
import ru.zavanton.booker.domain.UserEntity;
import ru.zavanton.booker.repository.ReadingStatusRepository;
import ru.zavanton.booker.repository.UserRepository;

/**
 * Integration tests for the {@link ReadingStatusResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ReadingStatusResourceIT {

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_STARTED_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_STARTED_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_STARTED_DATE = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_FINISHED_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_FINISHED_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_FINISHED_DATE = LocalDate.ofEpochDay(-1L);

    private static final Integer DEFAULT_CURRENT_PAGE = 1;
    private static final Integer UPDATED_CURRENT_PAGE = 2;
    private static final Integer SMALLER_CURRENT_PAGE = 1 - 1;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/reading-statuses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ReadingStatusRepository readingStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReadingStatusMockMvc;

    private ReadingStatusEntity readingStatusEntity;

    private ReadingStatusEntity insertedReadingStatusEntity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReadingStatusEntity createEntity(EntityManager em) {
        ReadingStatusEntity readingStatusEntity = new ReadingStatusEntity()
            .status(DEFAULT_STATUS)
            .startedDate(DEFAULT_STARTED_DATE)
            .finishedDate(DEFAULT_FINISHED_DATE)
            .currentPage(DEFAULT_CURRENT_PAGE)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
        // Add required entity
        UserEntity user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        readingStatusEntity.setUser(user);
        // Add required entity
        BookEntity book;
        if (TestUtil.findAll(em, BookEntity.class).isEmpty()) {
            book = BookResourceIT.createEntity();
            em.persist(book);
            em.flush();
        } else {
            book = TestUtil.findAll(em, BookEntity.class).get(0);
        }
        readingStatusEntity.setBook(book);
        return readingStatusEntity;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReadingStatusEntity createUpdatedEntity(EntityManager em) {
        ReadingStatusEntity updatedReadingStatusEntity = new ReadingStatusEntity()
            .status(UPDATED_STATUS)
            .startedDate(UPDATED_STARTED_DATE)
            .finishedDate(UPDATED_FINISHED_DATE)
            .currentPage(UPDATED_CURRENT_PAGE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        // Add required entity
        UserEntity user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedReadingStatusEntity.setUser(user);
        // Add required entity
        BookEntity book;
        if (TestUtil.findAll(em, BookEntity.class).isEmpty()) {
            book = BookResourceIT.createUpdatedEntity();
            em.persist(book);
            em.flush();
        } else {
            book = TestUtil.findAll(em, BookEntity.class).get(0);
        }
        updatedReadingStatusEntity.setBook(book);
        return updatedReadingStatusEntity;
    }

    @BeforeEach
    public void initTest() {
        readingStatusEntity = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedReadingStatusEntity != null) {
            readingStatusRepository.delete(insertedReadingStatusEntity);
            insertedReadingStatusEntity = null;
        }
    }

    @Test
    @Transactional
    void createReadingStatus() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ReadingStatus
        var returnedReadingStatusEntity = om.readValue(
            restReadingStatusMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(readingStatusEntity)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ReadingStatusEntity.class
        );

        // Validate the ReadingStatus in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertReadingStatusEntityUpdatableFieldsEquals(
            returnedReadingStatusEntity,
            getPersistedReadingStatusEntity(returnedReadingStatusEntity)
        );

        insertedReadingStatusEntity = returnedReadingStatusEntity;
    }

    @Test
    @Transactional
    void createReadingStatusWithExistingId() throws Exception {
        // Create the ReadingStatus with an existing ID
        readingStatusEntity.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restReadingStatusMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(readingStatusEntity)))
            .andExpect(status().isBadRequest());

        // Validate the ReadingStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        readingStatusEntity.setStatus(null);

        // Create the ReadingStatus, which fails.

        restReadingStatusMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(readingStatusEntity)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllReadingStatuses() throws Exception {
        // Initialize the database
        insertedReadingStatusEntity = readingStatusRepository.saveAndFlush(readingStatusEntity);

        // Get all the readingStatusList
        restReadingStatusMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(readingStatusEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].startedDate").value(hasItem(DEFAULT_STARTED_DATE.toString())))
            .andExpect(jsonPath("$.[*].finishedDate").value(hasItem(DEFAULT_FINISHED_DATE.toString())))
            .andExpect(jsonPath("$.[*].currentPage").value(hasItem(DEFAULT_CURRENT_PAGE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getReadingStatus() throws Exception {
        // Initialize the database
        insertedReadingStatusEntity = readingStatusRepository.saveAndFlush(readingStatusEntity);

        // Get the readingStatus
        restReadingStatusMockMvc
            .perform(get(ENTITY_API_URL_ID, readingStatusEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(readingStatusEntity.getId().intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.startedDate").value(DEFAULT_STARTED_DATE.toString()))
            .andExpect(jsonPath("$.finishedDate").value(DEFAULT_FINISHED_DATE.toString()))
            .andExpect(jsonPath("$.currentPage").value(DEFAULT_CURRENT_PAGE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getReadingStatusesByIdFiltering() throws Exception {
        // Initialize the database
        insertedReadingStatusEntity = readingStatusRepository.saveAndFlush(readingStatusEntity);

        Long id = readingStatusEntity.getId();

        defaultReadingStatusFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultReadingStatusFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultReadingStatusFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllReadingStatusesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReadingStatusEntity = readingStatusRepository.saveAndFlush(readingStatusEntity);

        // Get all the readingStatusList where status equals to
        defaultReadingStatusFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllReadingStatusesByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReadingStatusEntity = readingStatusRepository.saveAndFlush(readingStatusEntity);

        // Get all the readingStatusList where status in
        defaultReadingStatusFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllReadingStatusesByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReadingStatusEntity = readingStatusRepository.saveAndFlush(readingStatusEntity);

        // Get all the readingStatusList where status is not null
        defaultReadingStatusFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllReadingStatusesByStatusContainsSomething() throws Exception {
        // Initialize the database
        insertedReadingStatusEntity = readingStatusRepository.saveAndFlush(readingStatusEntity);

        // Get all the readingStatusList where status contains
        defaultReadingStatusFiltering("status.contains=" + DEFAULT_STATUS, "status.contains=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllReadingStatusesByStatusNotContainsSomething() throws Exception {
        // Initialize the database
        insertedReadingStatusEntity = readingStatusRepository.saveAndFlush(readingStatusEntity);

        // Get all the readingStatusList where status does not contain
        defaultReadingStatusFiltering("status.doesNotContain=" + UPDATED_STATUS, "status.doesNotContain=" + DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void getAllReadingStatusesByStartedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReadingStatusEntity = readingStatusRepository.saveAndFlush(readingStatusEntity);

        // Get all the readingStatusList where startedDate equals to
        defaultReadingStatusFiltering("startedDate.equals=" + DEFAULT_STARTED_DATE, "startedDate.equals=" + UPDATED_STARTED_DATE);
    }

    @Test
    @Transactional
    void getAllReadingStatusesByStartedDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReadingStatusEntity = readingStatusRepository.saveAndFlush(readingStatusEntity);

        // Get all the readingStatusList where startedDate in
        defaultReadingStatusFiltering(
            "startedDate.in=" + DEFAULT_STARTED_DATE + "," + UPDATED_STARTED_DATE,
            "startedDate.in=" + UPDATED_STARTED_DATE
        );
    }

    @Test
    @Transactional
    void getAllReadingStatusesByStartedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReadingStatusEntity = readingStatusRepository.saveAndFlush(readingStatusEntity);

        // Get all the readingStatusList where startedDate is not null
        defaultReadingStatusFiltering("startedDate.specified=true", "startedDate.specified=false");
    }

    @Test
    @Transactional
    void getAllReadingStatusesByStartedDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedReadingStatusEntity = readingStatusRepository.saveAndFlush(readingStatusEntity);

        // Get all the readingStatusList where startedDate is greater than or equal to
        defaultReadingStatusFiltering(
            "startedDate.greaterThanOrEqual=" + DEFAULT_STARTED_DATE,
            "startedDate.greaterThanOrEqual=" + UPDATED_STARTED_DATE
        );
    }

    @Test
    @Transactional
    void getAllReadingStatusesByStartedDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedReadingStatusEntity = readingStatusRepository.saveAndFlush(readingStatusEntity);

        // Get all the readingStatusList where startedDate is less than or equal to
        defaultReadingStatusFiltering(
            "startedDate.lessThanOrEqual=" + DEFAULT_STARTED_DATE,
            "startedDate.lessThanOrEqual=" + SMALLER_STARTED_DATE
        );
    }

    @Test
    @Transactional
    void getAllReadingStatusesByStartedDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedReadingStatusEntity = readingStatusRepository.saveAndFlush(readingStatusEntity);

        // Get all the readingStatusList where startedDate is less than
        defaultReadingStatusFiltering("startedDate.lessThan=" + UPDATED_STARTED_DATE, "startedDate.lessThan=" + DEFAULT_STARTED_DATE);
    }

    @Test
    @Transactional
    void getAllReadingStatusesByStartedDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedReadingStatusEntity = readingStatusRepository.saveAndFlush(readingStatusEntity);

        // Get all the readingStatusList where startedDate is greater than
        defaultReadingStatusFiltering("startedDate.greaterThan=" + SMALLER_STARTED_DATE, "startedDate.greaterThan=" + DEFAULT_STARTED_DATE);
    }

    @Test
    @Transactional
    void getAllReadingStatusesByFinishedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReadingStatusEntity = readingStatusRepository.saveAndFlush(readingStatusEntity);

        // Get all the readingStatusList where finishedDate equals to
        defaultReadingStatusFiltering("finishedDate.equals=" + DEFAULT_FINISHED_DATE, "finishedDate.equals=" + UPDATED_FINISHED_DATE);
    }

    @Test
    @Transactional
    void getAllReadingStatusesByFinishedDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReadingStatusEntity = readingStatusRepository.saveAndFlush(readingStatusEntity);

        // Get all the readingStatusList where finishedDate in
        defaultReadingStatusFiltering(
            "finishedDate.in=" + DEFAULT_FINISHED_DATE + "," + UPDATED_FINISHED_DATE,
            "finishedDate.in=" + UPDATED_FINISHED_DATE
        );
    }

    @Test
    @Transactional
    void getAllReadingStatusesByFinishedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReadingStatusEntity = readingStatusRepository.saveAndFlush(readingStatusEntity);

        // Get all the readingStatusList where finishedDate is not null
        defaultReadingStatusFiltering("finishedDate.specified=true", "finishedDate.specified=false");
    }

    @Test
    @Transactional
    void getAllReadingStatusesByFinishedDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedReadingStatusEntity = readingStatusRepository.saveAndFlush(readingStatusEntity);

        // Get all the readingStatusList where finishedDate is greater than or equal to
        defaultReadingStatusFiltering(
            "finishedDate.greaterThanOrEqual=" + DEFAULT_FINISHED_DATE,
            "finishedDate.greaterThanOrEqual=" + UPDATED_FINISHED_DATE
        );
    }

    @Test
    @Transactional
    void getAllReadingStatusesByFinishedDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedReadingStatusEntity = readingStatusRepository.saveAndFlush(readingStatusEntity);

        // Get all the readingStatusList where finishedDate is less than or equal to
        defaultReadingStatusFiltering(
            "finishedDate.lessThanOrEqual=" + DEFAULT_FINISHED_DATE,
            "finishedDate.lessThanOrEqual=" + SMALLER_FINISHED_DATE
        );
    }

    @Test
    @Transactional
    void getAllReadingStatusesByFinishedDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedReadingStatusEntity = readingStatusRepository.saveAndFlush(readingStatusEntity);

        // Get all the readingStatusList where finishedDate is less than
        defaultReadingStatusFiltering("finishedDate.lessThan=" + UPDATED_FINISHED_DATE, "finishedDate.lessThan=" + DEFAULT_FINISHED_DATE);
    }

    @Test
    @Transactional
    void getAllReadingStatusesByFinishedDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedReadingStatusEntity = readingStatusRepository.saveAndFlush(readingStatusEntity);

        // Get all the readingStatusList where finishedDate is greater than
        defaultReadingStatusFiltering(
            "finishedDate.greaterThan=" + SMALLER_FINISHED_DATE,
            "finishedDate.greaterThan=" + DEFAULT_FINISHED_DATE
        );
    }

    @Test
    @Transactional
    void getAllReadingStatusesByCurrentPageIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReadingStatusEntity = readingStatusRepository.saveAndFlush(readingStatusEntity);

        // Get all the readingStatusList where currentPage equals to
        defaultReadingStatusFiltering("currentPage.equals=" + DEFAULT_CURRENT_PAGE, "currentPage.equals=" + UPDATED_CURRENT_PAGE);
    }

    @Test
    @Transactional
    void getAllReadingStatusesByCurrentPageIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReadingStatusEntity = readingStatusRepository.saveAndFlush(readingStatusEntity);

        // Get all the readingStatusList where currentPage in
        defaultReadingStatusFiltering(
            "currentPage.in=" + DEFAULT_CURRENT_PAGE + "," + UPDATED_CURRENT_PAGE,
            "currentPage.in=" + UPDATED_CURRENT_PAGE
        );
    }

    @Test
    @Transactional
    void getAllReadingStatusesByCurrentPageIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReadingStatusEntity = readingStatusRepository.saveAndFlush(readingStatusEntity);

        // Get all the readingStatusList where currentPage is not null
        defaultReadingStatusFiltering("currentPage.specified=true", "currentPage.specified=false");
    }

    @Test
    @Transactional
    void getAllReadingStatusesByCurrentPageIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedReadingStatusEntity = readingStatusRepository.saveAndFlush(readingStatusEntity);

        // Get all the readingStatusList where currentPage is greater than or equal to
        defaultReadingStatusFiltering(
            "currentPage.greaterThanOrEqual=" + DEFAULT_CURRENT_PAGE,
            "currentPage.greaterThanOrEqual=" + UPDATED_CURRENT_PAGE
        );
    }

    @Test
    @Transactional
    void getAllReadingStatusesByCurrentPageIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedReadingStatusEntity = readingStatusRepository.saveAndFlush(readingStatusEntity);

        // Get all the readingStatusList where currentPage is less than or equal to
        defaultReadingStatusFiltering(
            "currentPage.lessThanOrEqual=" + DEFAULT_CURRENT_PAGE,
            "currentPage.lessThanOrEqual=" + SMALLER_CURRENT_PAGE
        );
    }

    @Test
    @Transactional
    void getAllReadingStatusesByCurrentPageIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedReadingStatusEntity = readingStatusRepository.saveAndFlush(readingStatusEntity);

        // Get all the readingStatusList where currentPage is less than
        defaultReadingStatusFiltering("currentPage.lessThan=" + UPDATED_CURRENT_PAGE, "currentPage.lessThan=" + DEFAULT_CURRENT_PAGE);
    }

    @Test
    @Transactional
    void getAllReadingStatusesByCurrentPageIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedReadingStatusEntity = readingStatusRepository.saveAndFlush(readingStatusEntity);

        // Get all the readingStatusList where currentPage is greater than
        defaultReadingStatusFiltering("currentPage.greaterThan=" + SMALLER_CURRENT_PAGE, "currentPage.greaterThan=" + DEFAULT_CURRENT_PAGE);
    }

    @Test
    @Transactional
    void getAllReadingStatusesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReadingStatusEntity = readingStatusRepository.saveAndFlush(readingStatusEntity);

        // Get all the readingStatusList where createdAt equals to
        defaultReadingStatusFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllReadingStatusesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReadingStatusEntity = readingStatusRepository.saveAndFlush(readingStatusEntity);

        // Get all the readingStatusList where createdAt in
        defaultReadingStatusFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllReadingStatusesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReadingStatusEntity = readingStatusRepository.saveAndFlush(readingStatusEntity);

        // Get all the readingStatusList where createdAt is not null
        defaultReadingStatusFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllReadingStatusesByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReadingStatusEntity = readingStatusRepository.saveAndFlush(readingStatusEntity);

        // Get all the readingStatusList where updatedAt equals to
        defaultReadingStatusFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllReadingStatusesByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReadingStatusEntity = readingStatusRepository.saveAndFlush(readingStatusEntity);

        // Get all the readingStatusList where updatedAt in
        defaultReadingStatusFiltering(
            "updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT,
            "updatedAt.in=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllReadingStatusesByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReadingStatusEntity = readingStatusRepository.saveAndFlush(readingStatusEntity);

        // Get all the readingStatusList where updatedAt is not null
        defaultReadingStatusFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllReadingStatusesByUserIsEqualToSomething() throws Exception {
        UserEntity user;
        if (TestUtil.findAll(em, UserEntity.class).isEmpty()) {
            readingStatusRepository.saveAndFlush(readingStatusEntity);
            user = UserResourceIT.createEntity();
        } else {
            user = TestUtil.findAll(em, UserEntity.class).get(0);
        }
        em.persist(user);
        em.flush();
        readingStatusEntity.setUser(user);
        readingStatusRepository.saveAndFlush(readingStatusEntity);
        Long userId = user.getId();
        // Get all the readingStatusList where user equals to userId
        defaultReadingStatusShouldBeFound("userId.equals=" + userId);

        // Get all the readingStatusList where user equals to (userId + 1)
        defaultReadingStatusShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    @Test
    @Transactional
    void getAllReadingStatusesByBookIsEqualToSomething() throws Exception {
        BookEntity book;
        if (TestUtil.findAll(em, BookEntity.class).isEmpty()) {
            readingStatusRepository.saveAndFlush(readingStatusEntity);
            book = BookResourceIT.createEntity();
        } else {
            book = TestUtil.findAll(em, BookEntity.class).get(0);
        }
        em.persist(book);
        em.flush();
        readingStatusEntity.setBook(book);
        readingStatusRepository.saveAndFlush(readingStatusEntity);
        Long bookId = book.getId();
        // Get all the readingStatusList where book equals to bookId
        defaultReadingStatusShouldBeFound("bookId.equals=" + bookId);

        // Get all the readingStatusList where book equals to (bookId + 1)
        defaultReadingStatusShouldNotBeFound("bookId.equals=" + (bookId + 1));
    }

    private void defaultReadingStatusFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultReadingStatusShouldBeFound(shouldBeFound);
        defaultReadingStatusShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultReadingStatusShouldBeFound(String filter) throws Exception {
        restReadingStatusMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(readingStatusEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].startedDate").value(hasItem(DEFAULT_STARTED_DATE.toString())))
            .andExpect(jsonPath("$.[*].finishedDate").value(hasItem(DEFAULT_FINISHED_DATE.toString())))
            .andExpect(jsonPath("$.[*].currentPage").value(hasItem(DEFAULT_CURRENT_PAGE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restReadingStatusMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultReadingStatusShouldNotBeFound(String filter) throws Exception {
        restReadingStatusMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restReadingStatusMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingReadingStatus() throws Exception {
        // Get the readingStatus
        restReadingStatusMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingReadingStatus() throws Exception {
        // Initialize the database
        insertedReadingStatusEntity = readingStatusRepository.saveAndFlush(readingStatusEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the readingStatus
        ReadingStatusEntity updatedReadingStatusEntity = readingStatusRepository.findById(readingStatusEntity.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedReadingStatusEntity are not directly saved in db
        em.detach(updatedReadingStatusEntity);
        updatedReadingStatusEntity
            .status(UPDATED_STATUS)
            .startedDate(UPDATED_STARTED_DATE)
            .finishedDate(UPDATED_FINISHED_DATE)
            .currentPage(UPDATED_CURRENT_PAGE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restReadingStatusMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedReadingStatusEntity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedReadingStatusEntity))
            )
            .andExpect(status().isOk());

        // Validate the ReadingStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedReadingStatusEntityToMatchAllProperties(updatedReadingStatusEntity);
    }

    @Test
    @Transactional
    void putNonExistingReadingStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        readingStatusEntity.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReadingStatusMockMvc
            .perform(
                put(ENTITY_API_URL_ID, readingStatusEntity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(readingStatusEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReadingStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchReadingStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        readingStatusEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReadingStatusMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(readingStatusEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReadingStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamReadingStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        readingStatusEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReadingStatusMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(readingStatusEntity)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ReadingStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateReadingStatusWithPatch() throws Exception {
        // Initialize the database
        insertedReadingStatusEntity = readingStatusRepository.saveAndFlush(readingStatusEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the readingStatus using partial update
        ReadingStatusEntity partialUpdatedReadingStatusEntity = new ReadingStatusEntity();
        partialUpdatedReadingStatusEntity.setId(readingStatusEntity.getId());

        partialUpdatedReadingStatusEntity.startedDate(UPDATED_STARTED_DATE).currentPage(UPDATED_CURRENT_PAGE).createdAt(UPDATED_CREATED_AT);

        restReadingStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReadingStatusEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReadingStatusEntity))
            )
            .andExpect(status().isOk());

        // Validate the ReadingStatus in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReadingStatusEntityUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedReadingStatusEntity, readingStatusEntity),
            getPersistedReadingStatusEntity(readingStatusEntity)
        );
    }

    @Test
    @Transactional
    void fullUpdateReadingStatusWithPatch() throws Exception {
        // Initialize the database
        insertedReadingStatusEntity = readingStatusRepository.saveAndFlush(readingStatusEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the readingStatus using partial update
        ReadingStatusEntity partialUpdatedReadingStatusEntity = new ReadingStatusEntity();
        partialUpdatedReadingStatusEntity.setId(readingStatusEntity.getId());

        partialUpdatedReadingStatusEntity
            .status(UPDATED_STATUS)
            .startedDate(UPDATED_STARTED_DATE)
            .finishedDate(UPDATED_FINISHED_DATE)
            .currentPage(UPDATED_CURRENT_PAGE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restReadingStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReadingStatusEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReadingStatusEntity))
            )
            .andExpect(status().isOk());

        // Validate the ReadingStatus in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReadingStatusEntityUpdatableFieldsEquals(
            partialUpdatedReadingStatusEntity,
            getPersistedReadingStatusEntity(partialUpdatedReadingStatusEntity)
        );
    }

    @Test
    @Transactional
    void patchNonExistingReadingStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        readingStatusEntity.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReadingStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, readingStatusEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(readingStatusEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReadingStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchReadingStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        readingStatusEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReadingStatusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(readingStatusEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReadingStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamReadingStatus() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        readingStatusEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReadingStatusMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(readingStatusEntity)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ReadingStatus in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteReadingStatus() throws Exception {
        // Initialize the database
        insertedReadingStatusEntity = readingStatusRepository.saveAndFlush(readingStatusEntity);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the readingStatus
        restReadingStatusMockMvc
            .perform(delete(ENTITY_API_URL_ID, readingStatusEntity.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return readingStatusRepository.count();
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

    protected ReadingStatusEntity getPersistedReadingStatusEntity(ReadingStatusEntity readingStatus) {
        return readingStatusRepository.findById(readingStatus.getId()).orElseThrow();
    }

    protected void assertPersistedReadingStatusEntityToMatchAllProperties(ReadingStatusEntity expectedReadingStatusEntity) {
        assertReadingStatusEntityAllPropertiesEquals(
            expectedReadingStatusEntity,
            getPersistedReadingStatusEntity(expectedReadingStatusEntity)
        );
    }

    protected void assertPersistedReadingStatusEntityToMatchUpdatableProperties(ReadingStatusEntity expectedReadingStatusEntity) {
        assertReadingStatusEntityAllUpdatablePropertiesEquals(
            expectedReadingStatusEntity,
            getPersistedReadingStatusEntity(expectedReadingStatusEntity)
        );
    }
}
