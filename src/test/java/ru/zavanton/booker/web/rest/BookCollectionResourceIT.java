package ru.zavanton.booker.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.zavanton.booker.domain.BookCollectionEntityAsserts.*;
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
import ru.zavanton.booker.domain.BookCollectionEntity;
import ru.zavanton.booker.domain.BookEntity;
import ru.zavanton.booker.domain.CollectionEntity;
import ru.zavanton.booker.repository.BookCollectionRepository;

/**
 * Integration tests for the {@link BookCollectionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BookCollectionResourceIT {

    private static final Integer DEFAULT_POSITION = 1;
    private static final Integer UPDATED_POSITION = 2;
    private static final Integer SMALLER_POSITION = 1 - 1;

    private static final Instant DEFAULT_ADDED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ADDED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/book-collections";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BookCollectionRepository bookCollectionRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBookCollectionMockMvc;

    private BookCollectionEntity bookCollectionEntity;

    private BookCollectionEntity insertedBookCollectionEntity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BookCollectionEntity createEntity(EntityManager em) {
        BookCollectionEntity bookCollectionEntity = new BookCollectionEntity().position(DEFAULT_POSITION).addedAt(DEFAULT_ADDED_AT);
        // Add required entity
        BookEntity book;
        if (TestUtil.findAll(em, BookEntity.class).isEmpty()) {
            book = BookResourceIT.createEntity();
            em.persist(book);
            em.flush();
        } else {
            book = TestUtil.findAll(em, BookEntity.class).get(0);
        }
        bookCollectionEntity.setBook(book);
        // Add required entity
        CollectionEntity collection;
        if (TestUtil.findAll(em, CollectionEntity.class).isEmpty()) {
            collection = CollectionResourceIT.createEntity(em);
            em.persist(collection);
            em.flush();
        } else {
            collection = TestUtil.findAll(em, CollectionEntity.class).get(0);
        }
        bookCollectionEntity.setCollection(collection);
        return bookCollectionEntity;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BookCollectionEntity createUpdatedEntity(EntityManager em) {
        BookCollectionEntity updatedBookCollectionEntity = new BookCollectionEntity().position(UPDATED_POSITION).addedAt(UPDATED_ADDED_AT);
        // Add required entity
        BookEntity book;
        if (TestUtil.findAll(em, BookEntity.class).isEmpty()) {
            book = BookResourceIT.createUpdatedEntity();
            em.persist(book);
            em.flush();
        } else {
            book = TestUtil.findAll(em, BookEntity.class).get(0);
        }
        updatedBookCollectionEntity.setBook(book);
        // Add required entity
        CollectionEntity collection;
        if (TestUtil.findAll(em, CollectionEntity.class).isEmpty()) {
            collection = CollectionResourceIT.createUpdatedEntity(em);
            em.persist(collection);
            em.flush();
        } else {
            collection = TestUtil.findAll(em, CollectionEntity.class).get(0);
        }
        updatedBookCollectionEntity.setCollection(collection);
        return updatedBookCollectionEntity;
    }

    @BeforeEach
    public void initTest() {
        bookCollectionEntity = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedBookCollectionEntity != null) {
            bookCollectionRepository.delete(insertedBookCollectionEntity);
            insertedBookCollectionEntity = null;
        }
    }

    @Test
    @Transactional
    void createBookCollection() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the BookCollection
        var returnedBookCollectionEntity = om.readValue(
            restBookCollectionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookCollectionEntity)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            BookCollectionEntity.class
        );

        // Validate the BookCollection in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertBookCollectionEntityUpdatableFieldsEquals(
            returnedBookCollectionEntity,
            getPersistedBookCollectionEntity(returnedBookCollectionEntity)
        );

        insertedBookCollectionEntity = returnedBookCollectionEntity;
    }

    @Test
    @Transactional
    void createBookCollectionWithExistingId() throws Exception {
        // Create the BookCollection with an existing ID
        bookCollectionEntity.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBookCollectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookCollectionEntity)))
            .andExpect(status().isBadRequest());

        // Validate the BookCollection in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllBookCollections() throws Exception {
        // Initialize the database
        insertedBookCollectionEntity = bookCollectionRepository.saveAndFlush(bookCollectionEntity);

        // Get all the bookCollectionList
        restBookCollectionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookCollectionEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].position").value(hasItem(DEFAULT_POSITION)))
            .andExpect(jsonPath("$.[*].addedAt").value(hasItem(DEFAULT_ADDED_AT.toString())));
    }

    @Test
    @Transactional
    void getBookCollection() throws Exception {
        // Initialize the database
        insertedBookCollectionEntity = bookCollectionRepository.saveAndFlush(bookCollectionEntity);

        // Get the bookCollection
        restBookCollectionMockMvc
            .perform(get(ENTITY_API_URL_ID, bookCollectionEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bookCollectionEntity.getId().intValue()))
            .andExpect(jsonPath("$.position").value(DEFAULT_POSITION))
            .andExpect(jsonPath("$.addedAt").value(DEFAULT_ADDED_AT.toString()));
    }

    @Test
    @Transactional
    void getBookCollectionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedBookCollectionEntity = bookCollectionRepository.saveAndFlush(bookCollectionEntity);

        Long id = bookCollectionEntity.getId();

        defaultBookCollectionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultBookCollectionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultBookCollectionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllBookCollectionsByPositionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookCollectionEntity = bookCollectionRepository.saveAndFlush(bookCollectionEntity);

        // Get all the bookCollectionList where position equals to
        defaultBookCollectionFiltering("position.equals=" + DEFAULT_POSITION, "position.equals=" + UPDATED_POSITION);
    }

    @Test
    @Transactional
    void getAllBookCollectionsByPositionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBookCollectionEntity = bookCollectionRepository.saveAndFlush(bookCollectionEntity);

        // Get all the bookCollectionList where position in
        defaultBookCollectionFiltering("position.in=" + DEFAULT_POSITION + "," + UPDATED_POSITION, "position.in=" + UPDATED_POSITION);
    }

    @Test
    @Transactional
    void getAllBookCollectionsByPositionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBookCollectionEntity = bookCollectionRepository.saveAndFlush(bookCollectionEntity);

        // Get all the bookCollectionList where position is not null
        defaultBookCollectionFiltering("position.specified=true", "position.specified=false");
    }

    @Test
    @Transactional
    void getAllBookCollectionsByPositionIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookCollectionEntity = bookCollectionRepository.saveAndFlush(bookCollectionEntity);

        // Get all the bookCollectionList where position is greater than or equal to
        defaultBookCollectionFiltering(
            "position.greaterThanOrEqual=" + DEFAULT_POSITION,
            "position.greaterThanOrEqual=" + UPDATED_POSITION
        );
    }

    @Test
    @Transactional
    void getAllBookCollectionsByPositionIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookCollectionEntity = bookCollectionRepository.saveAndFlush(bookCollectionEntity);

        // Get all the bookCollectionList where position is less than or equal to
        defaultBookCollectionFiltering("position.lessThanOrEqual=" + DEFAULT_POSITION, "position.lessThanOrEqual=" + SMALLER_POSITION);
    }

    @Test
    @Transactional
    void getAllBookCollectionsByPositionIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedBookCollectionEntity = bookCollectionRepository.saveAndFlush(bookCollectionEntity);

        // Get all the bookCollectionList where position is less than
        defaultBookCollectionFiltering("position.lessThan=" + UPDATED_POSITION, "position.lessThan=" + DEFAULT_POSITION);
    }

    @Test
    @Transactional
    void getAllBookCollectionsByPositionIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedBookCollectionEntity = bookCollectionRepository.saveAndFlush(bookCollectionEntity);

        // Get all the bookCollectionList where position is greater than
        defaultBookCollectionFiltering("position.greaterThan=" + SMALLER_POSITION, "position.greaterThan=" + DEFAULT_POSITION);
    }

    @Test
    @Transactional
    void getAllBookCollectionsByAddedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookCollectionEntity = bookCollectionRepository.saveAndFlush(bookCollectionEntity);

        // Get all the bookCollectionList where addedAt equals to
        defaultBookCollectionFiltering("addedAt.equals=" + DEFAULT_ADDED_AT, "addedAt.equals=" + UPDATED_ADDED_AT);
    }

    @Test
    @Transactional
    void getAllBookCollectionsByAddedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBookCollectionEntity = bookCollectionRepository.saveAndFlush(bookCollectionEntity);

        // Get all the bookCollectionList where addedAt in
        defaultBookCollectionFiltering("addedAt.in=" + DEFAULT_ADDED_AT + "," + UPDATED_ADDED_AT, "addedAt.in=" + UPDATED_ADDED_AT);
    }

    @Test
    @Transactional
    void getAllBookCollectionsByAddedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBookCollectionEntity = bookCollectionRepository.saveAndFlush(bookCollectionEntity);

        // Get all the bookCollectionList where addedAt is not null
        defaultBookCollectionFiltering("addedAt.specified=true", "addedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllBookCollectionsByBookIsEqualToSomething() throws Exception {
        BookEntity book;
        if (TestUtil.findAll(em, BookEntity.class).isEmpty()) {
            bookCollectionRepository.saveAndFlush(bookCollectionEntity);
            book = BookResourceIT.createEntity();
        } else {
            book = TestUtil.findAll(em, BookEntity.class).get(0);
        }
        em.persist(book);
        em.flush();
        bookCollectionEntity.setBook(book);
        bookCollectionRepository.saveAndFlush(bookCollectionEntity);
        Long bookId = book.getId();
        // Get all the bookCollectionList where book equals to bookId
        defaultBookCollectionShouldBeFound("bookId.equals=" + bookId);

        // Get all the bookCollectionList where book equals to (bookId + 1)
        defaultBookCollectionShouldNotBeFound("bookId.equals=" + (bookId + 1));
    }

    @Test
    @Transactional
    void getAllBookCollectionsByCollectionIsEqualToSomething() throws Exception {
        CollectionEntity collection;
        if (TestUtil.findAll(em, CollectionEntity.class).isEmpty()) {
            bookCollectionRepository.saveAndFlush(bookCollectionEntity);
            collection = CollectionResourceIT.createEntity(em);
        } else {
            collection = TestUtil.findAll(em, CollectionEntity.class).get(0);
        }
        em.persist(collection);
        em.flush();
        bookCollectionEntity.setCollection(collection);
        bookCollectionRepository.saveAndFlush(bookCollectionEntity);
        Long collectionId = collection.getId();
        // Get all the bookCollectionList where collection equals to collectionId
        defaultBookCollectionShouldBeFound("collectionId.equals=" + collectionId);

        // Get all the bookCollectionList where collection equals to (collectionId + 1)
        defaultBookCollectionShouldNotBeFound("collectionId.equals=" + (collectionId + 1));
    }

    private void defaultBookCollectionFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultBookCollectionShouldBeFound(shouldBeFound);
        defaultBookCollectionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBookCollectionShouldBeFound(String filter) throws Exception {
        restBookCollectionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookCollectionEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].position").value(hasItem(DEFAULT_POSITION)))
            .andExpect(jsonPath("$.[*].addedAt").value(hasItem(DEFAULT_ADDED_AT.toString())));

        // Check, that the count call also returns 1
        restBookCollectionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBookCollectionShouldNotBeFound(String filter) throws Exception {
        restBookCollectionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBookCollectionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingBookCollection() throws Exception {
        // Get the bookCollection
        restBookCollectionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBookCollection() throws Exception {
        // Initialize the database
        insertedBookCollectionEntity = bookCollectionRepository.saveAndFlush(bookCollectionEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bookCollection
        BookCollectionEntity updatedBookCollectionEntity = bookCollectionRepository.findById(bookCollectionEntity.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBookCollectionEntity are not directly saved in db
        em.detach(updatedBookCollectionEntity);
        updatedBookCollectionEntity.position(UPDATED_POSITION).addedAt(UPDATED_ADDED_AT);

        restBookCollectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBookCollectionEntity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedBookCollectionEntity))
            )
            .andExpect(status().isOk());

        // Validate the BookCollection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBookCollectionEntityToMatchAllProperties(updatedBookCollectionEntity);
    }

    @Test
    @Transactional
    void putNonExistingBookCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookCollectionEntity.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookCollectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bookCollectionEntity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(bookCollectionEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookCollection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBookCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookCollectionEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookCollectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(bookCollectionEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookCollection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBookCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookCollectionEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookCollectionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookCollectionEntity)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BookCollection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBookCollectionWithPatch() throws Exception {
        // Initialize the database
        insertedBookCollectionEntity = bookCollectionRepository.saveAndFlush(bookCollectionEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bookCollection using partial update
        BookCollectionEntity partialUpdatedBookCollectionEntity = new BookCollectionEntity();
        partialUpdatedBookCollectionEntity.setId(bookCollectionEntity.getId());

        partialUpdatedBookCollectionEntity.position(UPDATED_POSITION).addedAt(UPDATED_ADDED_AT);

        restBookCollectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBookCollectionEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBookCollectionEntity))
            )
            .andExpect(status().isOk());

        // Validate the BookCollection in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBookCollectionEntityUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedBookCollectionEntity, bookCollectionEntity),
            getPersistedBookCollectionEntity(bookCollectionEntity)
        );
    }

    @Test
    @Transactional
    void fullUpdateBookCollectionWithPatch() throws Exception {
        // Initialize the database
        insertedBookCollectionEntity = bookCollectionRepository.saveAndFlush(bookCollectionEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bookCollection using partial update
        BookCollectionEntity partialUpdatedBookCollectionEntity = new BookCollectionEntity();
        partialUpdatedBookCollectionEntity.setId(bookCollectionEntity.getId());

        partialUpdatedBookCollectionEntity.position(UPDATED_POSITION).addedAt(UPDATED_ADDED_AT);

        restBookCollectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBookCollectionEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBookCollectionEntity))
            )
            .andExpect(status().isOk());

        // Validate the BookCollection in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBookCollectionEntityUpdatableFieldsEquals(
            partialUpdatedBookCollectionEntity,
            getPersistedBookCollectionEntity(partialUpdatedBookCollectionEntity)
        );
    }

    @Test
    @Transactional
    void patchNonExistingBookCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookCollectionEntity.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookCollectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, bookCollectionEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(bookCollectionEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookCollection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBookCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookCollectionEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookCollectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(bookCollectionEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookCollection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBookCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookCollectionEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookCollectionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(bookCollectionEntity)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BookCollection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBookCollection() throws Exception {
        // Initialize the database
        insertedBookCollectionEntity = bookCollectionRepository.saveAndFlush(bookCollectionEntity);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the bookCollection
        restBookCollectionMockMvc
            .perform(delete(ENTITY_API_URL_ID, bookCollectionEntity.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return bookCollectionRepository.count();
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

    protected BookCollectionEntity getPersistedBookCollectionEntity(BookCollectionEntity bookCollection) {
        return bookCollectionRepository.findById(bookCollection.getId()).orElseThrow();
    }

    protected void assertPersistedBookCollectionEntityToMatchAllProperties(BookCollectionEntity expectedBookCollectionEntity) {
        assertBookCollectionEntityAllPropertiesEquals(
            expectedBookCollectionEntity,
            getPersistedBookCollectionEntity(expectedBookCollectionEntity)
        );
    }

    protected void assertPersistedBookCollectionEntityToMatchUpdatableProperties(BookCollectionEntity expectedBookCollectionEntity) {
        assertBookCollectionEntityAllUpdatablePropertiesEquals(
            expectedBookCollectionEntity,
            getPersistedBookCollectionEntity(expectedBookCollectionEntity)
        );
    }
}
