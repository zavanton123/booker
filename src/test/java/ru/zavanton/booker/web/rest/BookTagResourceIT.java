package ru.zavanton.booker.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.zavanton.booker.domain.BookTagEntityAsserts.*;
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
import ru.zavanton.booker.domain.BookTagEntity;
import ru.zavanton.booker.domain.TagEntity;
import ru.zavanton.booker.repository.BookTagRepository;

/**
 * Integration tests for the {@link BookTagResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BookTagResourceIT {

    private static final String ENTITY_API_URL = "/api/book-tags";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BookTagRepository bookTagRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBookTagMockMvc;

    private BookTagEntity bookTagEntity;

    private BookTagEntity insertedBookTagEntity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BookTagEntity createEntity(EntityManager em) {
        BookTagEntity bookTagEntity = new BookTagEntity();
        // Add required entity
        BookEntity book;
        if (TestUtil.findAll(em, BookEntity.class).isEmpty()) {
            book = BookResourceIT.createEntity();
            em.persist(book);
            em.flush();
        } else {
            book = TestUtil.findAll(em, BookEntity.class).get(0);
        }
        bookTagEntity.setBook(book);
        // Add required entity
        TagEntity tag;
        if (TestUtil.findAll(em, TagEntity.class).isEmpty()) {
            tag = TagResourceIT.createEntity();
            em.persist(tag);
            em.flush();
        } else {
            tag = TestUtil.findAll(em, TagEntity.class).get(0);
        }
        bookTagEntity.setTag(tag);
        return bookTagEntity;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BookTagEntity createUpdatedEntity(EntityManager em) {
        BookTagEntity updatedBookTagEntity = new BookTagEntity();
        // Add required entity
        BookEntity book;
        if (TestUtil.findAll(em, BookEntity.class).isEmpty()) {
            book = BookResourceIT.createUpdatedEntity();
            em.persist(book);
            em.flush();
        } else {
            book = TestUtil.findAll(em, BookEntity.class).get(0);
        }
        updatedBookTagEntity.setBook(book);
        // Add required entity
        TagEntity tag;
        if (TestUtil.findAll(em, TagEntity.class).isEmpty()) {
            tag = TagResourceIT.createUpdatedEntity();
            em.persist(tag);
            em.flush();
        } else {
            tag = TestUtil.findAll(em, TagEntity.class).get(0);
        }
        updatedBookTagEntity.setTag(tag);
        return updatedBookTagEntity;
    }

    @BeforeEach
    public void initTest() {
        bookTagEntity = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedBookTagEntity != null) {
            bookTagRepository.delete(insertedBookTagEntity);
            insertedBookTagEntity = null;
        }
    }

    @Test
    @Transactional
    void createBookTag() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the BookTag
        var returnedBookTagEntity = om.readValue(
            restBookTagMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookTagEntity)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            BookTagEntity.class
        );

        // Validate the BookTag in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertBookTagEntityUpdatableFieldsEquals(returnedBookTagEntity, getPersistedBookTagEntity(returnedBookTagEntity));

        insertedBookTagEntity = returnedBookTagEntity;
    }

    @Test
    @Transactional
    void createBookTagWithExistingId() throws Exception {
        // Create the BookTag with an existing ID
        bookTagEntity.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBookTagMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookTagEntity)))
            .andExpect(status().isBadRequest());

        // Validate the BookTag in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllBookTags() throws Exception {
        // Initialize the database
        insertedBookTagEntity = bookTagRepository.saveAndFlush(bookTagEntity);

        // Get all the bookTagList
        restBookTagMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookTagEntity.getId().intValue())));
    }

    @Test
    @Transactional
    void getBookTag() throws Exception {
        // Initialize the database
        insertedBookTagEntity = bookTagRepository.saveAndFlush(bookTagEntity);

        // Get the bookTag
        restBookTagMockMvc
            .perform(get(ENTITY_API_URL_ID, bookTagEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bookTagEntity.getId().intValue()));
    }

    @Test
    @Transactional
    void getBookTagsByIdFiltering() throws Exception {
        // Initialize the database
        insertedBookTagEntity = bookTagRepository.saveAndFlush(bookTagEntity);

        Long id = bookTagEntity.getId();

        defaultBookTagFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultBookTagFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultBookTagFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllBookTagsByBookIsEqualToSomething() throws Exception {
        BookEntity book;
        if (TestUtil.findAll(em, BookEntity.class).isEmpty()) {
            bookTagRepository.saveAndFlush(bookTagEntity);
            book = BookResourceIT.createEntity();
        } else {
            book = TestUtil.findAll(em, BookEntity.class).get(0);
        }
        em.persist(book);
        em.flush();
        bookTagEntity.setBook(book);
        bookTagRepository.saveAndFlush(bookTagEntity);
        Long bookId = book.getId();
        // Get all the bookTagList where book equals to bookId
        defaultBookTagShouldBeFound("bookId.equals=" + bookId);

        // Get all the bookTagList where book equals to (bookId + 1)
        defaultBookTagShouldNotBeFound("bookId.equals=" + (bookId + 1));
    }

    @Test
    @Transactional
    void getAllBookTagsByTagIsEqualToSomething() throws Exception {
        TagEntity tag;
        if (TestUtil.findAll(em, TagEntity.class).isEmpty()) {
            bookTagRepository.saveAndFlush(bookTagEntity);
            tag = TagResourceIT.createEntity();
        } else {
            tag = TestUtil.findAll(em, TagEntity.class).get(0);
        }
        em.persist(tag);
        em.flush();
        bookTagEntity.setTag(tag);
        bookTagRepository.saveAndFlush(bookTagEntity);
        Long tagId = tag.getId();
        // Get all the bookTagList where tag equals to tagId
        defaultBookTagShouldBeFound("tagId.equals=" + tagId);

        // Get all the bookTagList where tag equals to (tagId + 1)
        defaultBookTagShouldNotBeFound("tagId.equals=" + (tagId + 1));
    }

    private void defaultBookTagFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultBookTagShouldBeFound(shouldBeFound);
        defaultBookTagShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBookTagShouldBeFound(String filter) throws Exception {
        restBookTagMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookTagEntity.getId().intValue())));

        // Check, that the count call also returns 1
        restBookTagMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBookTagShouldNotBeFound(String filter) throws Exception {
        restBookTagMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBookTagMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingBookTag() throws Exception {
        // Get the bookTag
        restBookTagMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBookTag() throws Exception {
        // Initialize the database
        insertedBookTagEntity = bookTagRepository.saveAndFlush(bookTagEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bookTag
        BookTagEntity updatedBookTagEntity = bookTagRepository.findById(bookTagEntity.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBookTagEntity are not directly saved in db
        em.detach(updatedBookTagEntity);

        restBookTagMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBookTagEntity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedBookTagEntity))
            )
            .andExpect(status().isOk());

        // Validate the BookTag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBookTagEntityToMatchAllProperties(updatedBookTagEntity);
    }

    @Test
    @Transactional
    void putNonExistingBookTag() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookTagEntity.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookTagMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bookTagEntity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(bookTagEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookTag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBookTag() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookTagEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookTagMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(bookTagEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookTag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBookTag() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookTagEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookTagMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookTagEntity)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BookTag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBookTagWithPatch() throws Exception {
        // Initialize the database
        insertedBookTagEntity = bookTagRepository.saveAndFlush(bookTagEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bookTag using partial update
        BookTagEntity partialUpdatedBookTagEntity = new BookTagEntity();
        partialUpdatedBookTagEntity.setId(bookTagEntity.getId());

        restBookTagMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBookTagEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBookTagEntity))
            )
            .andExpect(status().isOk());

        // Validate the BookTag in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBookTagEntityUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedBookTagEntity, bookTagEntity),
            getPersistedBookTagEntity(bookTagEntity)
        );
    }

    @Test
    @Transactional
    void fullUpdateBookTagWithPatch() throws Exception {
        // Initialize the database
        insertedBookTagEntity = bookTagRepository.saveAndFlush(bookTagEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bookTag using partial update
        BookTagEntity partialUpdatedBookTagEntity = new BookTagEntity();
        partialUpdatedBookTagEntity.setId(bookTagEntity.getId());

        restBookTagMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBookTagEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBookTagEntity))
            )
            .andExpect(status().isOk());

        // Validate the BookTag in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBookTagEntityUpdatableFieldsEquals(partialUpdatedBookTagEntity, getPersistedBookTagEntity(partialUpdatedBookTagEntity));
    }

    @Test
    @Transactional
    void patchNonExistingBookTag() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookTagEntity.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookTagMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, bookTagEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(bookTagEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookTag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBookTag() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookTagEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookTagMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(bookTagEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookTag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBookTag() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookTagEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookTagMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(bookTagEntity)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BookTag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBookTag() throws Exception {
        // Initialize the database
        insertedBookTagEntity = bookTagRepository.saveAndFlush(bookTagEntity);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the bookTag
        restBookTagMockMvc
            .perform(delete(ENTITY_API_URL_ID, bookTagEntity.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return bookTagRepository.count();
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

    protected BookTagEntity getPersistedBookTagEntity(BookTagEntity bookTag) {
        return bookTagRepository.findById(bookTag.getId()).orElseThrow();
    }

    protected void assertPersistedBookTagEntityToMatchAllProperties(BookTagEntity expectedBookTagEntity) {
        assertBookTagEntityAllPropertiesEquals(expectedBookTagEntity, getPersistedBookTagEntity(expectedBookTagEntity));
    }

    protected void assertPersistedBookTagEntityToMatchUpdatableProperties(BookTagEntity expectedBookTagEntity) {
        assertBookTagEntityAllUpdatablePropertiesEquals(expectedBookTagEntity, getPersistedBookTagEntity(expectedBookTagEntity));
    }
}
