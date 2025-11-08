package ru.zavanton.booker.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.zavanton.booker.domain.TagEntityAsserts.*;
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
import ru.zavanton.booker.domain.TagEntity;
import ru.zavanton.booker.repository.TagRepository;

/**
 * Integration tests for the {@link TagResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TagResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SLUG = "AAAAAAAAAA";
    private static final String UPDATED_SLUG = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/tags";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTagMockMvc;

    private TagEntity tagEntity;

    private TagEntity insertedTagEntity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TagEntity createEntity() {
        return new TagEntity().name(DEFAULT_NAME).slug(DEFAULT_SLUG).createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TagEntity createUpdatedEntity() {
        return new TagEntity().name(UPDATED_NAME).slug(UPDATED_SLUG).createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    public void initTest() {
        tagEntity = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedTagEntity != null) {
            tagRepository.delete(insertedTagEntity);
            insertedTagEntity = null;
        }
    }

    @Test
    @Transactional
    void createTag() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Tag
        var returnedTagEntity = om.readValue(
            restTagMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tagEntity)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TagEntity.class
        );

        // Validate the Tag in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertTagEntityUpdatableFieldsEquals(returnedTagEntity, getPersistedTagEntity(returnedTagEntity));

        insertedTagEntity = returnedTagEntity;
    }

    @Test
    @Transactional
    void createTagWithExistingId() throws Exception {
        // Create the Tag with an existing ID
        tagEntity.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTagMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tagEntity)))
            .andExpect(status().isBadRequest());

        // Validate the Tag in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tagEntity.setName(null);

        // Create the Tag, which fails.

        restTagMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tagEntity)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSlugIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tagEntity.setSlug(null);

        // Create the Tag, which fails.

        restTagMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tagEntity)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTags() throws Exception {
        // Initialize the database
        insertedTagEntity = tagRepository.saveAndFlush(tagEntity);

        // Get all the tagList
        restTagMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tagEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].slug").value(hasItem(DEFAULT_SLUG)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getTag() throws Exception {
        // Initialize the database
        insertedTagEntity = tagRepository.saveAndFlush(tagEntity);

        // Get the tag
        restTagMockMvc
            .perform(get(ENTITY_API_URL_ID, tagEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tagEntity.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.slug").value(DEFAULT_SLUG))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getTagsByIdFiltering() throws Exception {
        // Initialize the database
        insertedTagEntity = tagRepository.saveAndFlush(tagEntity);

        Long id = tagEntity.getId();

        defaultTagFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTagFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTagFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTagsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTagEntity = tagRepository.saveAndFlush(tagEntity);

        // Get all the tagList where name equals to
        defaultTagFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTagsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTagEntity = tagRepository.saveAndFlush(tagEntity);

        // Get all the tagList where name in
        defaultTagFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTagsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTagEntity = tagRepository.saveAndFlush(tagEntity);

        // Get all the tagList where name is not null
        defaultTagFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllTagsByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedTagEntity = tagRepository.saveAndFlush(tagEntity);

        // Get all the tagList where name contains
        defaultTagFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTagsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTagEntity = tagRepository.saveAndFlush(tagEntity);

        // Get all the tagList where name does not contain
        defaultTagFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllTagsBySlugIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTagEntity = tagRepository.saveAndFlush(tagEntity);

        // Get all the tagList where slug equals to
        defaultTagFiltering("slug.equals=" + DEFAULT_SLUG, "slug.equals=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    void getAllTagsBySlugIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTagEntity = tagRepository.saveAndFlush(tagEntity);

        // Get all the tagList where slug in
        defaultTagFiltering("slug.in=" + DEFAULT_SLUG + "," + UPDATED_SLUG, "slug.in=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    void getAllTagsBySlugIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTagEntity = tagRepository.saveAndFlush(tagEntity);

        // Get all the tagList where slug is not null
        defaultTagFiltering("slug.specified=true", "slug.specified=false");
    }

    @Test
    @Transactional
    void getAllTagsBySlugContainsSomething() throws Exception {
        // Initialize the database
        insertedTagEntity = tagRepository.saveAndFlush(tagEntity);

        // Get all the tagList where slug contains
        defaultTagFiltering("slug.contains=" + DEFAULT_SLUG, "slug.contains=" + UPDATED_SLUG);
    }

    @Test
    @Transactional
    void getAllTagsBySlugNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTagEntity = tagRepository.saveAndFlush(tagEntity);

        // Get all the tagList where slug does not contain
        defaultTagFiltering("slug.doesNotContain=" + UPDATED_SLUG, "slug.doesNotContain=" + DEFAULT_SLUG);
    }

    @Test
    @Transactional
    void getAllTagsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTagEntity = tagRepository.saveAndFlush(tagEntity);

        // Get all the tagList where createdAt equals to
        defaultTagFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllTagsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTagEntity = tagRepository.saveAndFlush(tagEntity);

        // Get all the tagList where createdAt in
        defaultTagFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllTagsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTagEntity = tagRepository.saveAndFlush(tagEntity);

        // Get all the tagList where createdAt is not null
        defaultTagFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    private void defaultTagFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultTagShouldBeFound(shouldBeFound);
        defaultTagShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTagShouldBeFound(String filter) throws Exception {
        restTagMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tagEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].slug").value(hasItem(DEFAULT_SLUG)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));

        // Check, that the count call also returns 1
        restTagMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTagShouldNotBeFound(String filter) throws Exception {
        restTagMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTagMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTag() throws Exception {
        // Get the tag
        restTagMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTag() throws Exception {
        // Initialize the database
        insertedTagEntity = tagRepository.saveAndFlush(tagEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tag
        TagEntity updatedTagEntity = tagRepository.findById(tagEntity.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTagEntity are not directly saved in db
        em.detach(updatedTagEntity);
        updatedTagEntity.name(UPDATED_NAME).slug(UPDATED_SLUG).createdAt(UPDATED_CREATED_AT);

        restTagMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTagEntity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedTagEntity))
            )
            .andExpect(status().isOk());

        // Validate the Tag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTagEntityToMatchAllProperties(updatedTagEntity);
    }

    @Test
    @Transactional
    void putNonExistingTag() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tagEntity.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTagMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tagEntity.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tagEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTag() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tagEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTagMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tagEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTag() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tagEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTagMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tagEntity)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTagWithPatch() throws Exception {
        // Initialize the database
        insertedTagEntity = tagRepository.saveAndFlush(tagEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tag using partial update
        TagEntity partialUpdatedTagEntity = new TagEntity();
        partialUpdatedTagEntity.setId(tagEntity.getId());

        partialUpdatedTagEntity.slug(UPDATED_SLUG).createdAt(UPDATED_CREATED_AT);

        restTagMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTagEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTagEntity))
            )
            .andExpect(status().isOk());

        // Validate the Tag in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTagEntityUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTagEntity, tagEntity),
            getPersistedTagEntity(tagEntity)
        );
    }

    @Test
    @Transactional
    void fullUpdateTagWithPatch() throws Exception {
        // Initialize the database
        insertedTagEntity = tagRepository.saveAndFlush(tagEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tag using partial update
        TagEntity partialUpdatedTagEntity = new TagEntity();
        partialUpdatedTagEntity.setId(tagEntity.getId());

        partialUpdatedTagEntity.name(UPDATED_NAME).slug(UPDATED_SLUG).createdAt(UPDATED_CREATED_AT);

        restTagMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTagEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTagEntity))
            )
            .andExpect(status().isOk());

        // Validate the Tag in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTagEntityUpdatableFieldsEquals(partialUpdatedTagEntity, getPersistedTagEntity(partialUpdatedTagEntity));
    }

    @Test
    @Transactional
    void patchNonExistingTag() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tagEntity.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTagMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tagEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tagEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTag() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tagEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTagMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tagEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTag() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tagEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTagMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(tagEntity)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTag() throws Exception {
        // Initialize the database
        insertedTagEntity = tagRepository.saveAndFlush(tagEntity);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the tag
        restTagMockMvc
            .perform(delete(ENTITY_API_URL_ID, tagEntity.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return tagRepository.count();
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

    protected TagEntity getPersistedTagEntity(TagEntity tag) {
        return tagRepository.findById(tag.getId()).orElseThrow();
    }

    protected void assertPersistedTagEntityToMatchAllProperties(TagEntity expectedTagEntity) {
        assertTagEntityAllPropertiesEquals(expectedTagEntity, getPersistedTagEntity(expectedTagEntity));
    }

    protected void assertPersistedTagEntityToMatchUpdatableProperties(TagEntity expectedTagEntity) {
        assertTagEntityAllUpdatablePropertiesEquals(expectedTagEntity, getPersistedTagEntity(expectedTagEntity));
    }
}
