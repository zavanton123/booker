package ru.zavanton.booker.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.zavanton.booker.domain.BookAuthorEntityAsserts.*;
import static ru.zavanton.booker.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
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
import ru.zavanton.booker.domain.BookAuthorEntity;
import ru.zavanton.booker.domain.BookEntity;
import ru.zavanton.booker.repository.BookAuthorRepository;

/**
 * Integration tests for the {@link BookAuthorResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BookAuthorResourceIT {

    private static final Boolean DEFAULT_IS_PRIMARY = false;
    private static final Boolean UPDATED_IS_PRIMARY = true;

    private static final Integer DEFAULT_ORDER = 1;
    private static final Integer UPDATED_ORDER = 2;
    private static final Integer SMALLER_ORDER = 1 - 1;

    private static final String ENTITY_API_URL = "/api/book-authors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BookAuthorRepository bookAuthorRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBookAuthorMockMvc;

    private BookAuthorEntity bookAuthorEntity;

    private BookAuthorEntity insertedBookAuthorEntity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BookAuthorEntity createEntity(EntityManager em) {
        BookAuthorEntity bookAuthorEntity = new BookAuthorEntity().isPrimary(DEFAULT_IS_PRIMARY).order(DEFAULT_ORDER);
        // Add required entity
        BookEntity book;
        if (TestUtil.findAll(em, BookEntity.class).isEmpty()) {
            book = BookResourceIT.createEntity();
            em.persist(book);
            em.flush();
        } else {
            book = TestUtil.findAll(em, BookEntity.class).get(0);
        }
        bookAuthorEntity.setBook(book);
        // Add required entity
        AuthorEntity author;
        if (TestUtil.findAll(em, AuthorEntity.class).isEmpty()) {
            author = AuthorResourceIT.createEntity();
            em.persist(author);
            em.flush();
        } else {
            author = TestUtil.findAll(em, AuthorEntity.class).get(0);
        }
        bookAuthorEntity.setAuthor(author);
        return bookAuthorEntity;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BookAuthorEntity createUpdatedEntity(EntityManager em) {
        BookAuthorEntity updatedBookAuthorEntity = new BookAuthorEntity().isPrimary(UPDATED_IS_PRIMARY).order(UPDATED_ORDER);
        // Add required entity
        BookEntity book;
        if (TestUtil.findAll(em, BookEntity.class).isEmpty()) {
            book = BookResourceIT.createUpdatedEntity();
            em.persist(book);
            em.flush();
        } else {
            book = TestUtil.findAll(em, BookEntity.class).get(0);
        }
        updatedBookAuthorEntity.setBook(book);
        // Add required entity
        AuthorEntity author;
        if (TestUtil.findAll(em, AuthorEntity.class).isEmpty()) {
            author = AuthorResourceIT.createUpdatedEntity();
            em.persist(author);
            em.flush();
        } else {
            author = TestUtil.findAll(em, AuthorEntity.class).get(0);
        }
        updatedBookAuthorEntity.setAuthor(author);
        return updatedBookAuthorEntity;
    }

    @BeforeEach
    public void initTest() {
        bookAuthorEntity = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedBookAuthorEntity != null) {
            bookAuthorRepository.delete(insertedBookAuthorEntity);
            insertedBookAuthorEntity = null;
        }
    }

    @Test
    @Transactional
    void createBookAuthor() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the BookAuthor
        var returnedBookAuthorEntity = om.readValue(
            restBookAuthorMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookAuthorEntity)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            BookAuthorEntity.class
        );

        // Validate the BookAuthor in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertBookAuthorEntityUpdatableFieldsEquals(returnedBookAuthorEntity, getPersistedBookAuthorEntity(returnedBookAuthorEntity));

        insertedBookAuthorEntity = returnedBookAuthorEntity;
    }

    @Test
    @Transactional
    void createBookAuthorWithExistingId() throws Exception {
        // Create the BookAuthor with an existing ID
        bookAuthorEntity.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBookAuthorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookAuthorEntity)))
            .andExpect(status().isBadRequest());

        // Validate the BookAuthor in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllBookAuthors() throws Exception {
        // Initialize the database
        insertedBookAuthorEntity = bookAuthorRepository.saveAndFlush(bookAuthorEntity);

        // Get all the bookAuthorList
        restBookAuthorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookAuthorEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].isPrimary").value(hasItem(DEFAULT_IS_PRIMARY)))
            .andExpect(jsonPath("$.[*].order").value(hasItem(DEFAULT_ORDER)));
    }

    @Test
    @Transactional
    void getBookAuthor() throws Exception {
        // Initialize the database
        insertedBookAuthorEntity = bookAuthorRepository.saveAndFlush(bookAuthorEntity);

        // Get the bookAuthor
        restBookAuthorMockMvc
            .perform(get(ENTITY_API_URL_ID, bookAuthorEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bookAuthorEntity.getId().intValue()))
            .andExpect(jsonPath("$.isPrimary").value(DEFAULT_IS_PRIMARY))
            .andExpect(jsonPath("$.order").value(DEFAULT_ORDER));
    }

    @Test
    @Transactional
    void getBookAuthorsByIdFiltering() throws Exception {
        // Initialize the database
        insertedBookAuthorEntity = bookAuthorRepository.saveAndFlush(bookAuthorEntity);

        Long id = bookAuthorEntity.getId();

        defaultBookAuthorFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultBookAuthorFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultBookAuthorFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllBookAuthorsByIsPrimaryIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookAuthorEntity = bookAuthorRepository.saveAndFlush(bookAuthorEntity);

        // Get all the bookAuthorList where isPrimary equals to
        defaultBookAuthorFiltering("isPrimary.equals=" + DEFAULT_IS_PRIMARY, "isPrimary.equals=" + UPDATED_IS_PRIMARY);
    }

    @Test
    @Transactional
    void getAllBookAuthorsByIsPrimaryIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBookAuthorEntity = bookAuthorRepository.saveAndFlush(bookAuthorEntity);

        // Get all the bookAuthorList where isPrimary in
        defaultBookAuthorFiltering("isPrimary.in=" + DEFAULT_IS_PRIMARY + "," + UPDATED_IS_PRIMARY, "isPrimary.in=" + UPDATED_IS_PRIMARY);
    }

    @Test
    @Transactional
    void getAllBookAuthorsByIsPrimaryIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBookAuthorEntity = bookAuthorRepository.saveAndFlush(bookAuthorEntity);

        // Get all the bookAuthorList where isPrimary is not null
        defaultBookAuthorFiltering("isPrimary.specified=true", "isPrimary.specified=false");
    }

    @Test
    @Transactional
    void getAllBookAuthorsByOrderIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookAuthorEntity = bookAuthorRepository.saveAndFlush(bookAuthorEntity);

        // Get all the bookAuthorList where order equals to
        defaultBookAuthorFiltering("order.equals=" + DEFAULT_ORDER, "order.equals=" + UPDATED_ORDER);
    }

    @Test
    @Transactional
    void getAllBookAuthorsByOrderIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBookAuthorEntity = bookAuthorRepository.saveAndFlush(bookAuthorEntity);

        // Get all the bookAuthorList where order in
        defaultBookAuthorFiltering("order.in=" + DEFAULT_ORDER + "," + UPDATED_ORDER, "order.in=" + UPDATED_ORDER);
    }

    @Test
    @Transactional
    void getAllBookAuthorsByOrderIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBookAuthorEntity = bookAuthorRepository.saveAndFlush(bookAuthorEntity);

        // Get all the bookAuthorList where order is not null
        defaultBookAuthorFiltering("order.specified=true", "order.specified=false");
    }

    @Test
    @Transactional
    void getAllBookAuthorsByOrderIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookAuthorEntity = bookAuthorRepository.saveAndFlush(bookAuthorEntity);

        // Get all the bookAuthorList where order is greater than or equal to
        defaultBookAuthorFiltering("order.greaterThanOrEqual=" + DEFAULT_ORDER, "order.greaterThanOrEqual=" + UPDATED_ORDER);
    }

    @Test
    @Transactional
    void getAllBookAuthorsByOrderIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedBookAuthorEntity = bookAuthorRepository.saveAndFlush(bookAuthorEntity);

        // Get all the bookAuthorList where order is less than or equal to
        defaultBookAuthorFiltering("order.lessThanOrEqual=" + DEFAULT_ORDER, "order.lessThanOrEqual=" + SMALLER_ORDER);
    }

    @Test
    @Transactional
    void getAllBookAuthorsByOrderIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedBookAuthorEntity = bookAuthorRepository.saveAndFlush(bookAuthorEntity);

        // Get all the bookAuthorList where order is less than
        defaultBookAuthorFiltering("order.lessThan=" + UPDATED_ORDER, "order.lessThan=" + DEFAULT_ORDER);
    }

    @Test
    @Transactional
    void getAllBookAuthorsByOrderIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedBookAuthorEntity = bookAuthorRepository.saveAndFlush(bookAuthorEntity);

        // Get all the bookAuthorList where order is greater than
        defaultBookAuthorFiltering("order.greaterThan=" + SMALLER_ORDER, "order.greaterThan=" + DEFAULT_ORDER);
    }

    @Test
    @Transactional
    void getAllBookAuthorsByBookIsEqualToSomething() throws Exception {
        BookEntity book;
        if (TestUtil.findAll(em, BookEntity.class).isEmpty()) {
            bookAuthorRepository.saveAndFlush(bookAuthorEntity);
            book = BookResourceIT.createEntity();
        } else {
            book = TestUtil.findAll(em, BookEntity.class).get(0);
        }
        em.persist(book);
        em.flush();
        bookAuthorEntity.setBook(book);
        bookAuthorRepository.saveAndFlush(bookAuthorEntity);
        Long bookId = book.getId();
        // Get all the bookAuthorList where book equals to bookId
        defaultBookAuthorShouldBeFound("bookId.equals=" + bookId);

        // Get all the bookAuthorList where book equals to (bookId + 1)
        defaultBookAuthorShouldNotBeFound("bookId.equals=" + (bookId + 1));
    }

    @Test
    @Transactional
    void getAllBookAuthorsByAuthorIsEqualToSomething() throws Exception {
        AuthorEntity author;
        if (TestUtil.findAll(em, AuthorEntity.class).isEmpty()) {
            bookAuthorRepository.saveAndFlush(bookAuthorEntity);
            author = AuthorResourceIT.createEntity();
        } else {
            author = TestUtil.findAll(em, AuthorEntity.class).get(0);
        }
        em.persist(author);
        em.flush();
        bookAuthorEntity.setAuthor(author);
        bookAuthorRepository.saveAndFlush(bookAuthorEntity);
        Long authorId = author.getId();
        // Get all the bookAuthorList where author equals to authorId
        defaultBookAuthorShouldBeFound("authorId.equals=" + authorId);

        // Get all the bookAuthorList where author equals to (authorId + 1)
        defaultBookAuthorShouldNotBeFound("authorId.equals=" + (authorId + 1));
    }

    private void defaultBookAuthorFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultBookAuthorShouldBeFound(shouldBeFound);
        defaultBookAuthorShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBookAuthorShouldBeFound(String filter) throws Exception {
        restBookAuthorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookAuthorEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].isPrimary").value(hasItem(DEFAULT_IS_PRIMARY)))
            .andExpect(jsonPath("$.[*].order").value(hasItem(DEFAULT_ORDER)));

        // Check, that the count call also returns 1
        restBookAuthorMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBookAuthorShouldNotBeFound(String filter) throws Exception {
        restBookAuthorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBookAuthorMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingBookAuthor() throws Exception {
        // Get the bookAuthor
        restBookAuthorMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBookAuthor() throws Exception {
        // Initialize the database
        insertedBookAuthorEntity = bookAuthorRepository.saveAndFlush(bookAuthorEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bookAuthor
        BookAuthorEntity updatedBookAuthorEntity = bookAuthorRepository.findById(bookAuthorEntity.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBookAuthorEntity are not directly saved in db
        em.detach(updatedBookAuthorEntity);
        updatedBookAuthorEntity.isPrimary(UPDATED_IS_PRIMARY).order(UPDATED_ORDER);

        restBookAuthorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBookAuthorEntity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedBookAuthorEntity))
            )
            .andExpect(status().isOk());

        // Validate the BookAuthor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBookAuthorEntityToMatchAllProperties(updatedBookAuthorEntity);
    }

    @Test
    @Transactional
    void putNonExistingBookAuthor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookAuthorEntity.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookAuthorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bookAuthorEntity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(bookAuthorEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookAuthor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBookAuthor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookAuthorEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookAuthorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(bookAuthorEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookAuthor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBookAuthor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookAuthorEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookAuthorMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookAuthorEntity)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BookAuthor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBookAuthorWithPatch() throws Exception {
        // Initialize the database
        insertedBookAuthorEntity = bookAuthorRepository.saveAndFlush(bookAuthorEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bookAuthor using partial update
        BookAuthorEntity partialUpdatedBookAuthorEntity = new BookAuthorEntity();
        partialUpdatedBookAuthorEntity.setId(bookAuthorEntity.getId());

        partialUpdatedBookAuthorEntity.isPrimary(UPDATED_IS_PRIMARY);

        restBookAuthorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBookAuthorEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBookAuthorEntity))
            )
            .andExpect(status().isOk());

        // Validate the BookAuthor in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBookAuthorEntityUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedBookAuthorEntity, bookAuthorEntity),
            getPersistedBookAuthorEntity(bookAuthorEntity)
        );
    }

    @Test
    @Transactional
    void fullUpdateBookAuthorWithPatch() throws Exception {
        // Initialize the database
        insertedBookAuthorEntity = bookAuthorRepository.saveAndFlush(bookAuthorEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bookAuthor using partial update
        BookAuthorEntity partialUpdatedBookAuthorEntity = new BookAuthorEntity();
        partialUpdatedBookAuthorEntity.setId(bookAuthorEntity.getId());

        partialUpdatedBookAuthorEntity.isPrimary(UPDATED_IS_PRIMARY).order(UPDATED_ORDER);

        restBookAuthorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBookAuthorEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBookAuthorEntity))
            )
            .andExpect(status().isOk());

        // Validate the BookAuthor in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBookAuthorEntityUpdatableFieldsEquals(
            partialUpdatedBookAuthorEntity,
            getPersistedBookAuthorEntity(partialUpdatedBookAuthorEntity)
        );
    }

    @Test
    @Transactional
    void patchNonExistingBookAuthor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookAuthorEntity.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookAuthorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, bookAuthorEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(bookAuthorEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookAuthor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBookAuthor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookAuthorEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookAuthorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(bookAuthorEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookAuthor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBookAuthor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookAuthorEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookAuthorMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(bookAuthorEntity)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BookAuthor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBookAuthor() throws Exception {
        // Initialize the database
        insertedBookAuthorEntity = bookAuthorRepository.saveAndFlush(bookAuthorEntity);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the bookAuthor
        restBookAuthorMockMvc
            .perform(delete(ENTITY_API_URL_ID, bookAuthorEntity.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return bookAuthorRepository.count();
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

    protected BookAuthorEntity getPersistedBookAuthorEntity(BookAuthorEntity bookAuthor) {
        return bookAuthorRepository.findById(bookAuthor.getId()).orElseThrow();
    }

    protected void assertPersistedBookAuthorEntityToMatchAllProperties(BookAuthorEntity expectedBookAuthorEntity) {
        assertBookAuthorEntityAllPropertiesEquals(expectedBookAuthorEntity, getPersistedBookAuthorEntity(expectedBookAuthorEntity));
    }

    protected void assertPersistedBookAuthorEntityToMatchUpdatableProperties(BookAuthorEntity expectedBookAuthorEntity) {
        assertBookAuthorEntityAllUpdatablePropertiesEquals(
            expectedBookAuthorEntity,
            getPersistedBookAuthorEntity(expectedBookAuthorEntity)
        );
    }
}
