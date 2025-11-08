package ru.zavanton.booker.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.zavanton.booker.domain.GenreEntityAsserts.*;
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
import ru.zavanton.booker.domain.GenreEntity;
import ru.zavanton.booker.repository.GenreRepository;

/**
 * Integration tests for the {@link GenreResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class GenreResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SLUG = "AAAAAAAAAA";
    private static final String UPDATED_SLUG = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/genres";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGenreMockMvc;

    private GenreEntity genreEntity;

    private GenreEntity insertedGenreEntity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GenreEntity createEntity() {
        return new GenreEntity()
            .name(DEFAULT_NAME)
            .slug(DEFAULT_SLUG)
            .description(DEFAULT_DESCRIPTION)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GenreEntity createUpdatedEntity() {
        return new GenreEntity()
            .name(UPDATED_NAME)
            .slug(UPDATED_SLUG)
            .description(UPDATED_DESCRIPTION)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    public void initTest() {
        genreEntity = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedGenreEntity != null) {
            genreRepository.delete(insertedGenreEntity);
            insertedGenreEntity = null;
        }
    }

    @Test
    @Transactional
    void createGenre() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Genre
        var returnedGenreEntity = om.readValue(
            restGenreMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(genreEntity)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            GenreEntity.class
        );

        // Validate the Genre in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertGenreEntityUpdatableFieldsEquals(returnedGenreEntity, getPersistedGenreEntity(returnedGenreEntity));

        insertedGenreEntity = returnedGenreEntity;
    }

    @Test
    @Transactional
    void createGenreWithExistingId() throws Exception {
        // Create the Genre with an existing ID
        genreEntity.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restGenreMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(genreEntity)))
            .andExpect(status().isBadRequest());

        // Validate the Genre in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        genreEntity.setName(null);

        // Create the Genre, which fails.

        restGenreMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(genreEntity)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSlugIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        genreEntity.setSlug(null);

        // Create the Genre, which fails.

        restGenreMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(genreEntity)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllGenres() throws Exception {
        // Initialize the database
        insertedGenreEntity = genreRepository.saveAndFlush(genreEntity);

        // Get all the genreList
        restGenreMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(genreEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].slug").value(hasItem(DEFAULT_SLUG)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getGenre() throws Exception {
        // Initialize the database
        insertedGenreEntity = genreRepository.saveAndFlush(genreEntity);

        // Get the genre
        restGenreMockMvc
            .perform(get(ENTITY_API_URL_ID, genreEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(genreEntity.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.slug").value(DEFAULT_SLUG))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getGenresByIdFiltering() throws Exception {
        // Initialize the database
        insertedGenreEntity = genreRepository.saveAndFlush(genreEntity);

        Long id = genreEntity.getId();

        defaultGenreFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultGenreFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultGenreFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllGenresByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedGenreEntity = genreRepository.saveAndFlush(genreEntity);

        // Get all the genreList where name equals to
        defaultGenreFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllGenresByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedGenreEntity = genreRepository.saveAndFlush(genreEntity);

        // Get all the genreList where name in
        defaultGenreFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllGenresByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedGenreEntity = genreRepository.saveAndFlush(genreEntity);

        // Get all the genreList where name is not null
        defaultGenreFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllGenresByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedGenreEntity = genreRepository.saveAndFlush(genreEntity);

        // Get all the genreList where name contains
        defaultGenreFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllGenresByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedGenreEntity = genreRepository.saveAndFlush(genreEntity);

        // Get all the genreList where name does not contain
        defaultGenreFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllGenresBySlugIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedGenreEntity = genreRepository.saveAndFlush(genreEntity);

        // Get all the genreList where slug equals to
        defaultGenreFiltering("slug.equals=" + DEFAULT_SLUG, "slug.equals=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    void getAllGenresBySlugIsInShouldWork() throws Exception {
        // Initialize the database
        insertedGenreEntity = genreRepository.saveAndFlush(genreEntity);

        // Get all the genreList where slug in
        defaultGenreFiltering("slug.in=" + DEFAULT_SLUG + "," + UPDATED_SLUG, "slug.in=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    void getAllGenresBySlugIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedGenreEntity = genreRepository.saveAndFlush(genreEntity);

        // Get all the genreList where slug is not null
        defaultGenreFiltering("slug.specified=true", "slug.specified=false");
    }

    @Test
    @Transactional
    void getAllGenresBySlugContainsSomething() throws Exception {
        // Initialize the database
        insertedGenreEntity = genreRepository.saveAndFlush(genreEntity);

        // Get all the genreList where slug contains
        defaultGenreFiltering("slug.contains=" + DEFAULT_SLUG, "slug.contains=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    void getAllGenresBySlugNotContainsSomething() throws Exception {
        // Initialize the database
        insertedGenreEntity = genreRepository.saveAndFlush(genreEntity);

        // Get all the genreList where slug does not contain
        defaultGenreFiltering("slug.doesNotContain=" + UPDATED_SLUG, "slug.doesNotContain=" + DEFAULT_SLUG);
    }

    @Test
    @Transactional
    void getAllGenresByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedGenreEntity = genreRepository.saveAndFlush(genreEntity);

        // Get all the genreList where createdAt equals to
        defaultGenreFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllGenresByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedGenreEntity = genreRepository.saveAndFlush(genreEntity);

        // Get all the genreList where createdAt in
        defaultGenreFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllGenresByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedGenreEntity = genreRepository.saveAndFlush(genreEntity);

        // Get all the genreList where createdAt is not null
        defaultGenreFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllGenresByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedGenreEntity = genreRepository.saveAndFlush(genreEntity);

        // Get all the genreList where updatedAt equals to
        defaultGenreFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllGenresByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedGenreEntity = genreRepository.saveAndFlush(genreEntity);

        // Get all the genreList where updatedAt in
        defaultGenreFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllGenresByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedGenreEntity = genreRepository.saveAndFlush(genreEntity);

        // Get all the genreList where updatedAt is not null
        defaultGenreFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    private void defaultGenreFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultGenreShouldBeFound(shouldBeFound);
        defaultGenreShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultGenreShouldBeFound(String filter) throws Exception {
        restGenreMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(genreEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].slug").value(hasItem(DEFAULT_SLUG)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restGenreMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultGenreShouldNotBeFound(String filter) throws Exception {
        restGenreMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restGenreMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingGenre() throws Exception {
        // Get the genre
        restGenreMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingGenre() throws Exception {
        // Initialize the database
        insertedGenreEntity = genreRepository.saveAndFlush(genreEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the genre
        GenreEntity updatedGenreEntity = genreRepository.findById(genreEntity.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedGenreEntity are not directly saved in db
        em.detach(updatedGenreEntity);
        updatedGenreEntity
            .name(UPDATED_NAME)
            .slug(UPDATED_SLUG)
            .description(UPDATED_DESCRIPTION)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restGenreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedGenreEntity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedGenreEntity))
            )
            .andExpect(status().isOk());

        // Validate the Genre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedGenreEntityToMatchAllProperties(updatedGenreEntity);
    }

    @Test
    @Transactional
    void putNonExistingGenre() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        genreEntity.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGenreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, genreEntity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(genreEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Genre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchGenre() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        genreEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGenreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(genreEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Genre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamGenre() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        genreEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGenreMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(genreEntity)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Genre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateGenreWithPatch() throws Exception {
        // Initialize the database
        insertedGenreEntity = genreRepository.saveAndFlush(genreEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the genre using partial update
        GenreEntity partialUpdatedGenreEntity = new GenreEntity();
        partialUpdatedGenreEntity.setId(genreEntity.getId());

        partialUpdatedGenreEntity
            .name(UPDATED_NAME)
            .slug(UPDATED_SLUG)
            .description(UPDATED_DESCRIPTION)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restGenreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGenreEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedGenreEntity))
            )
            .andExpect(status().isOk());

        // Validate the Genre in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertGenreEntityUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedGenreEntity, genreEntity),
            getPersistedGenreEntity(genreEntity)
        );
    }

    @Test
    @Transactional
    void fullUpdateGenreWithPatch() throws Exception {
        // Initialize the database
        insertedGenreEntity = genreRepository.saveAndFlush(genreEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the genre using partial update
        GenreEntity partialUpdatedGenreEntity = new GenreEntity();
        partialUpdatedGenreEntity.setId(genreEntity.getId());

        partialUpdatedGenreEntity
            .name(UPDATED_NAME)
            .slug(UPDATED_SLUG)
            .description(UPDATED_DESCRIPTION)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restGenreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGenreEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedGenreEntity))
            )
            .andExpect(status().isOk());

        // Validate the Genre in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertGenreEntityUpdatableFieldsEquals(partialUpdatedGenreEntity, getPersistedGenreEntity(partialUpdatedGenreEntity));
    }

    @Test
    @Transactional
    void patchNonExistingGenre() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        genreEntity.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGenreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, genreEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(genreEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Genre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchGenre() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        genreEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGenreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(genreEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Genre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamGenre() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        genreEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGenreMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(genreEntity)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Genre in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteGenre() throws Exception {
        // Initialize the database
        insertedGenreEntity = genreRepository.saveAndFlush(genreEntity);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the genre
        restGenreMockMvc
            .perform(delete(ENTITY_API_URL_ID, genreEntity.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return genreRepository.count();
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

    protected GenreEntity getPersistedGenreEntity(GenreEntity genre) {
        return genreRepository.findById(genre.getId()).orElseThrow();
    }

    protected void assertPersistedGenreEntityToMatchAllProperties(GenreEntity expectedGenreEntity) {
        assertGenreEntityAllPropertiesEquals(expectedGenreEntity, getPersistedGenreEntity(expectedGenreEntity));
    }

    protected void assertPersistedGenreEntityToMatchUpdatableProperties(GenreEntity expectedGenreEntity) {
        assertGenreEntityAllUpdatablePropertiesEquals(expectedGenreEntity, getPersistedGenreEntity(expectedGenreEntity));
    }
}
