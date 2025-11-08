package ru.zavanton.booker.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.zavanton.booker.domain.CommentEntityAsserts.*;
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
import ru.zavanton.booker.domain.CommentEntity;
import ru.zavanton.booker.domain.ReviewEntity;
import ru.zavanton.booker.domain.UserEntity;
import ru.zavanton.booker.repository.CommentRepository;
import ru.zavanton.booker.repository.UserRepository;

/**
 * Integration tests for the {@link CommentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CommentResourceIT {

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/comments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCommentMockMvc;

    private CommentEntity commentEntity;

    private CommentEntity insertedCommentEntity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CommentEntity createEntity(EntityManager em) {
        CommentEntity commentEntity = new CommentEntity()
            .content(DEFAULT_CONTENT)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
        // Add required entity
        UserEntity user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        commentEntity.setUser(user);
        // Add required entity
        ReviewEntity review;
        if (TestUtil.findAll(em, ReviewEntity.class).isEmpty()) {
            review = ReviewResourceIT.createEntity(em);
            em.persist(review);
            em.flush();
        } else {
            review = TestUtil.findAll(em, ReviewEntity.class).get(0);
        }
        commentEntity.setReview(review);
        return commentEntity;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CommentEntity createUpdatedEntity(EntityManager em) {
        CommentEntity updatedCommentEntity = new CommentEntity()
            .content(UPDATED_CONTENT)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        // Add required entity
        UserEntity user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedCommentEntity.setUser(user);
        // Add required entity
        ReviewEntity review;
        if (TestUtil.findAll(em, ReviewEntity.class).isEmpty()) {
            review = ReviewResourceIT.createUpdatedEntity(em);
            em.persist(review);
            em.flush();
        } else {
            review = TestUtil.findAll(em, ReviewEntity.class).get(0);
        }
        updatedCommentEntity.setReview(review);
        return updatedCommentEntity;
    }

    @BeforeEach
    public void initTest() {
        commentEntity = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedCommentEntity != null) {
            commentRepository.delete(insertedCommentEntity);
            insertedCommentEntity = null;
        }
    }

    @Test
    @Transactional
    void createComment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Comment
        var returnedCommentEntity = om.readValue(
            restCommentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(commentEntity)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CommentEntity.class
        );

        // Validate the Comment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertCommentEntityUpdatableFieldsEquals(returnedCommentEntity, getPersistedCommentEntity(returnedCommentEntity));

        insertedCommentEntity = returnedCommentEntity;
    }

    @Test
    @Transactional
    void createCommentWithExistingId() throws Exception {
        // Create the Comment with an existing ID
        commentEntity.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCommentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(commentEntity)))
            .andExpect(status().isBadRequest());

        // Validate the Comment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllComments() throws Exception {
        // Initialize the database
        insertedCommentEntity = commentRepository.saveAndFlush(commentEntity);

        // Get all the commentList
        restCommentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(commentEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getComment() throws Exception {
        // Initialize the database
        insertedCommentEntity = commentRepository.saveAndFlush(commentEntity);

        // Get the comment
        restCommentMockMvc
            .perform(get(ENTITY_API_URL_ID, commentEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(commentEntity.getId().intValue()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getCommentsByIdFiltering() throws Exception {
        // Initialize the database
        insertedCommentEntity = commentRepository.saveAndFlush(commentEntity);

        Long id = commentEntity.getId();

        defaultCommentFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCommentFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCommentFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCommentsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCommentEntity = commentRepository.saveAndFlush(commentEntity);

        // Get all the commentList where createdAt equals to
        defaultCommentFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllCommentsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCommentEntity = commentRepository.saveAndFlush(commentEntity);

        // Get all the commentList where createdAt in
        defaultCommentFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllCommentsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCommentEntity = commentRepository.saveAndFlush(commentEntity);

        // Get all the commentList where createdAt is not null
        defaultCommentFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllCommentsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCommentEntity = commentRepository.saveAndFlush(commentEntity);

        // Get all the commentList where updatedAt equals to
        defaultCommentFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllCommentsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCommentEntity = commentRepository.saveAndFlush(commentEntity);

        // Get all the commentList where updatedAt in
        defaultCommentFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllCommentsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCommentEntity = commentRepository.saveAndFlush(commentEntity);

        // Get all the commentList where updatedAt is not null
        defaultCommentFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllCommentsByUserIsEqualToSomething() throws Exception {
        UserEntity user;
        if (TestUtil.findAll(em, UserEntity.class).isEmpty()) {
            commentRepository.saveAndFlush(commentEntity);
            user = UserResourceIT.createEntity();
        } else {
            user = TestUtil.findAll(em, UserEntity.class).get(0);
        }
        em.persist(user);
        em.flush();
        commentEntity.setUser(user);
        commentRepository.saveAndFlush(commentEntity);
        Long userId = user.getId();
        // Get all the commentList where user equals to userId
        defaultCommentShouldBeFound("userId.equals=" + userId);

        // Get all the commentList where user equals to (userId + 1)
        defaultCommentShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    @Test
    @Transactional
    void getAllCommentsByReviewIsEqualToSomething() throws Exception {
        ReviewEntity review;
        if (TestUtil.findAll(em, ReviewEntity.class).isEmpty()) {
            commentRepository.saveAndFlush(commentEntity);
            review = ReviewResourceIT.createEntity(em);
        } else {
            review = TestUtil.findAll(em, ReviewEntity.class).get(0);
        }
        em.persist(review);
        em.flush();
        commentEntity.setReview(review);
        commentRepository.saveAndFlush(commentEntity);
        Long reviewId = review.getId();
        // Get all the commentList where review equals to reviewId
        defaultCommentShouldBeFound("reviewId.equals=" + reviewId);

        // Get all the commentList where review equals to (reviewId + 1)
        defaultCommentShouldNotBeFound("reviewId.equals=" + (reviewId + 1));
    }

    private void defaultCommentFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCommentShouldBeFound(shouldBeFound);
        defaultCommentShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCommentShouldBeFound(String filter) throws Exception {
        restCommentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(commentEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restCommentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCommentShouldNotBeFound(String filter) throws Exception {
        restCommentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCommentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingComment() throws Exception {
        // Get the comment
        restCommentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingComment() throws Exception {
        // Initialize the database
        insertedCommentEntity = commentRepository.saveAndFlush(commentEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the comment
        CommentEntity updatedCommentEntity = commentRepository.findById(commentEntity.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCommentEntity are not directly saved in db
        em.detach(updatedCommentEntity);
        updatedCommentEntity.content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);

        restCommentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCommentEntity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedCommentEntity))
            )
            .andExpect(status().isOk());

        // Validate the Comment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCommentEntityToMatchAllProperties(updatedCommentEntity);
    }

    @Test
    @Transactional
    void putNonExistingComment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        commentEntity.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCommentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, commentEntity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(commentEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Comment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchComment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        commentEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(commentEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Comment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamComment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        commentEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(commentEntity)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Comment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCommentWithPatch() throws Exception {
        // Initialize the database
        insertedCommentEntity = commentRepository.saveAndFlush(commentEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the comment using partial update
        CommentEntity partialUpdatedCommentEntity = new CommentEntity();
        partialUpdatedCommentEntity.setId(commentEntity.getId());

        restCommentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCommentEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCommentEntity))
            )
            .andExpect(status().isOk());

        // Validate the Comment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCommentEntityUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCommentEntity, commentEntity),
            getPersistedCommentEntity(commentEntity)
        );
    }

    @Test
    @Transactional
    void fullUpdateCommentWithPatch() throws Exception {
        // Initialize the database
        insertedCommentEntity = commentRepository.saveAndFlush(commentEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the comment using partial update
        CommentEntity partialUpdatedCommentEntity = new CommentEntity();
        partialUpdatedCommentEntity.setId(commentEntity.getId());

        partialUpdatedCommentEntity.content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);

        restCommentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCommentEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCommentEntity))
            )
            .andExpect(status().isOk());

        // Validate the Comment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCommentEntityUpdatableFieldsEquals(partialUpdatedCommentEntity, getPersistedCommentEntity(partialUpdatedCommentEntity));
    }

    @Test
    @Transactional
    void patchNonExistingComment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        commentEntity.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCommentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, commentEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(commentEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Comment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchComment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        commentEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(commentEntity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Comment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamComment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        commentEntity.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(commentEntity)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Comment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteComment() throws Exception {
        // Initialize the database
        insertedCommentEntity = commentRepository.saveAndFlush(commentEntity);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the comment
        restCommentMockMvc
            .perform(delete(ENTITY_API_URL_ID, commentEntity.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return commentRepository.count();
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

    protected CommentEntity getPersistedCommentEntity(CommentEntity comment) {
        return commentRepository.findById(comment.getId()).orElseThrow();
    }

    protected void assertPersistedCommentEntityToMatchAllProperties(CommentEntity expectedCommentEntity) {
        assertCommentEntityAllPropertiesEquals(expectedCommentEntity, getPersistedCommentEntity(expectedCommentEntity));
    }

    protected void assertPersistedCommentEntityToMatchUpdatableProperties(CommentEntity expectedCommentEntity) {
        assertCommentEntityAllUpdatablePropertiesEquals(expectedCommentEntity, getPersistedCommentEntity(expectedCommentEntity));
    }
}
