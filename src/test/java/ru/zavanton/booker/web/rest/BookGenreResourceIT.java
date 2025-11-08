package ru.zavanton.booker.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.zavanton.booker.domain.BookGenreEntityAsserts.*;
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
import ru.zavanton.booker.domain.BookEntity;
import ru.zavanton.booker.domain.BookGenreEntity;
import ru.zavanton.booker.domain.GenreEntity;
import ru.zavanton.booker.repository.BookGenreRepository;

/**
 * Integration tests for the {@link BookGenreResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BookGenreResourceIT {

    private static final String ENTITY_API_URL = "/api/book-genres";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BookGenreRepository bookGenreRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBookGenreMockMvc;

    private BookGenreEntity bookGenreEntity;

    private BookGenreEntity insertedBookGenreEntity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BookGenreEntity createEntity(EntityManager em) {
        BookGenreEntity bookGenreEntity = new BookGenreEntity();
        // Add required entity
        BookEntity book;
        if (TestUtil.findAll(em, BookEntity.class).isEmpty()) {
            book = BookResourceIT.createEntity();
            em.persist(book);
            em.flush();
        } else {
            book = TestUtil.findAll(em, BookEntity.class).get(0);
        }
        bookGenreEntity.setBook(book);
        // Add required entity
        GenreEntity genre;
        if (TestUtil.findAll(em, GenreEntity.class).isEmpty()) {
            genre = GenreResourceIT.createEntity();
            em.persist(genre);
            em.flush();
        } else {
            genre = TestUtil.findAll(em, GenreEntity.class).get(0);
        }
        bookGenreEntity.setGenre(genre);
        return bookGenreEntity;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BookGenreEntity createUpdatedEntity(EntityManager em) {
        BookGenreEntity updatedBookGenreEntity = new BookGenreEntity();
        // Add required entity
        BookEntity book;
        if (TestUtil.findAll(em, BookEntity.class).isEmpty()) {
            book = BookResourceIT.createUpdatedEntity();
            em.persist(book);
            em.flush();
        } else {
            book = TestUtil.findAll(em, BookEntity.class).get(0);
        }
        updatedBookGenreEntity.setBook(book);
        // Add required entity
        GenreEntity genre;
        if (TestUtil.findAll(em, GenreEntity.class).isEmpty()) {
            genre = GenreResourceIT.createUpdatedEntity();
            em.persist(genre);
            em.flush();
        } else {
            genre = TestUtil.findAll(em, GenreEntity.class).get(0);
        }
        updatedBookGenreEntity.setGenre(genre);
        return updatedBookGenreEntity;
    }

    @BeforeEach
    public void initTest() {
        bookGenreEntity = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedBookGenreEntity != null) {
            bookGenreRepository.delete(insertedBookGenreEntity);
            insertedBookGenreEntity = null;
        }
    }

    @Test
    @Transactional
    void createBookGenre() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the BookGenre
        var returnedBookGenreEntity = om.readValue(
            restBookGenreMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookGenreEntity)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            BookGenreEntity.class
        );

        // Validate the BookGenre in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertBookGenreEntityUpdatableFieldsEquals(returnedBookGenreEntity, getPersistedBookGenreEntity(returnedBookGenreEntity));

        insertedBookGenreEntity = returnedBookGenreEntity;
    }

    @Test
    @Transactional
    void createBookGenreWithExistingId() throws Exception {
        // Create the BookGenre with an existing ID
        bookGenreEntity.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBookGenreMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookGenreEntity)))
            .andExpect(status().isBadRequest());

        // Validate the BookGenre in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllBookGenres() throws Exception {
        // Initialize the database
        insertedBookGenreEntity = bookGenreRepository.saveAndFlush(bookGenreEntity);

        // Get all the bookGenreList
        restBookGenreMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookGenreEntity.getId().intValue())));
    }

    @Test
    @Transactional
    void getBookGenre() throws Exception {
        // Initialize the database
        insertedBookGenreEntity = bookGenreRepository.saveAndFlush(bookGenreEntity);

        // Get the bookGenre
        restBookGenreMockMvc
            .perform(get(ENTITY_API_URL_ID, bookGenreEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bookGenreEntity.getId().intValue()));
    }

    @Test
    @Transactional
    void getBookGenresByIdFiltering() throws Exception {
        // Initialize the database
        insertedBookGenreEntity = bookGenreRepository.saveAndFlush(bookGenreEntity);

        Long id = bookGenreEntity.getId();

        defaultBookGenreFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultBookGenreFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultBookGenreFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllBookGenresByBookIsEqualToSomething() throws Exception {
        BookEntity book;
        if (TestUtil.findAll(em, BookEntity.class).isEmpty()) {
            bookGenreRepository.saveAndFlush(bookGenreEntity);
            book = BookResourceIT.createEntity();
        } else {
            book = TestUtil.findAll(em, BookEntity.class).get(0);
        }
        em.persist(book);
        em.flush();
        bookGenreEntity.setBook(book);
        bookGenreRepository.saveAndFlush(bookGenreEntity);
        Long bookId = book.getId();
        // Get all the bookGenreList where book equals to bookId
        defaultBookGenreShouldBeFound("bookId.equals=" + bookId);

        // Get all the bookGenreList where book equals to (bookId + 1)
        defaultBookGenreShouldNotBeFound("bookId.equals=" + (bookId + 1));
    }

    @Test
    @Transactional
    void getAllBookGenresByGenreIsEqualToSomething() throws Exception {
        GenreEntity genre;
        if (TestUtil.findAll(em, GenreEntity.class).isEmpty()) {
            bookGenreRepository.saveAndFlush(bookGenreEntity);
            genre = GenreResourceIT.createEntity();
        } else {
            genre = TestUtil.findAll(em, GenreEntity.class).get(0);
        }
        em.persist(genre);
        em.flush();
        bookGenreEntity.setGenre(genre);
        bookGenreRepository.saveAndFlush(bookGenreEntity);
        Long genreId = genre.getId();
        // Get all the bookGenreList where genre equals to genreId
        defaultBookGenreShouldBeFound("genreId.equals=" + genreId);

        // Get all the bookGenreList where genre equals to (genreId + 1)
        defaultBookGenreShouldNotBeFound("genreId.equals=" + (genreId + 1));
    }

    private void defaultBookGenreFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultBookGenreShouldBeFound(shouldBeFound);
        defaultBookGenreShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBookGenreShouldBeFound(String filter) throws Exception {
        restBookGenreMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookGenreEntity.getId().intValue())));

        // Check, that the count call also returns 1
        restBookGenreMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBookGenreShouldNotBeFound(String filter) throws Exception {
        restBookGenreMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBookGenreMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingBookGenre() throws Exception {
        // Get the bookGenre
        restBookGenreMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBookGenre() throws Exception {
        // Initialize the database
        insertedBookGenreEntity = bookGenreRepository.saveAndFlush(bookGenreEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bookGenre
        BookGenreEntity updatedBookGenreEntity = bookGenreRepository.findById(bookGenreEntity.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBookGenreEntity are not directly saved in db
        em.detach(updatedBookGenreEntity);

        restBookGenreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBookGenreEntity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedBookGenreEntity))
            )
            .andExpect(status().isOk());

        // Validate the BookGenre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBookGenreEntityToMatchAllProperties(updatedBookGenreEntity);
    }

    @Test
    @Transactional
    void putNonExistingBookGenre() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookGenreEntity.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookGenreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bookGenreEntity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(bookGenreEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookGenre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBookGenre() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookGenreEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookGenreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(bookGenreEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookGenre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBookGenre() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookGenreEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookGenreMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookGenreEntity)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BookGenre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBookGenreWithPatch() throws Exception {
        // Initialize the database
        insertedBookGenreEntity = bookGenreRepository.saveAndFlush(bookGenreEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bookGenre using partial update
        BookGenreEntity partialUpdatedBookGenreEntity = new BookGenreEntity();
        partialUpdatedBookGenreEntity.setId(bookGenreEntity.getId());

        restBookGenreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBookGenreEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBookGenreEntity))
            )
            .andExpect(status().isOk());

        // Validate the BookGenre in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBookGenreEntityUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedBookGenreEntity, bookGenreEntity),
            getPersistedBookGenreEntity(bookGenreEntity)
        );
    }

    @Test
    @Transactional
    void fullUpdateBookGenreWithPatch() throws Exception {
        // Initialize the database
        insertedBookGenreEntity = bookGenreRepository.saveAndFlush(bookGenreEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bookGenre using partial update
        BookGenreEntity partialUpdatedBookGenreEntity = new BookGenreEntity();
        partialUpdatedBookGenreEntity.setId(bookGenreEntity.getId());

        restBookGenreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBookGenreEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBookGenreEntity))
            )
            .andExpect(status().isOk());

        // Validate the BookGenre in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBookGenreEntityUpdatableFieldsEquals(
            partialUpdatedBookGenreEntity,
            getPersistedBookGenreEntity(partialUpdatedBookGenreEntity)
        );
    }

    @Test
    @Transactional
    void patchNonExistingBookGenre() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookGenreEntity.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookGenreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, bookGenreEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(bookGenreEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookGenre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBookGenre() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookGenreEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookGenreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(bookGenreEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookGenre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBookGenre() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookGenreEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookGenreMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(bookGenreEntity)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BookGenre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBookGenre() throws Exception {
        // Initialize the database
        insertedBookGenreEntity = bookGenreRepository.saveAndFlush(bookGenreEntity);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the bookGenre
        restBookGenreMockMvc
            .perform(delete(ENTITY_API_URL_ID, bookGenreEntity.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return bookGenreRepository.count();
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

    protected BookGenreEntity getPersistedBookGenreEntity(BookGenreEntity bookGenre) {
        return bookGenreRepository.findById(bookGenre.getId()).orElseThrow();
    }

    protected void assertPersistedBookGenreEntityToMatchAllProperties(BookGenreEntity expectedBookGenreEntity) {
        assertBookGenreEntityAllPropertiesEquals(expectedBookGenreEntity, getPersistedBookGenreEntity(expectedBookGenreEntity));
    }

    protected void assertPersistedBookGenreEntityToMatchUpdatableProperties(BookGenreEntity expectedBookGenreEntity) {
        assertBookGenreEntityAllUpdatablePropertiesEquals(expectedBookGenreEntity, getPersistedBookGenreEntity(expectedBookGenreEntity));
    }
}
