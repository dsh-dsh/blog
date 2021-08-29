package main.repositories;

import main.model.ModerationStatus;
import main.model.Post;
import main.model.TagPost;
import main.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PostRepository extends CrudRepository<Post, Integer> {

    Page<Post> findByIsActiveAndModerationStatusOrderByTimeDesc(
            boolean isActive,
            ModerationStatus moderationStatus,
            Pageable pageable);

    Page<Post> findByIsActiveAndModerationStatusOrderByTimeAsc(
            boolean isActive,
            ModerationStatus moderationStatus,
            Pageable pageable);

    Page<Post> findByIsActiveAndModerationStatusAndUserOrderByTimeDesc(
            boolean isActive,
            ModerationStatus moderationStatus,
            User user,
            Pageable pageable);

    int countByIsActiveAndModerationStatus(boolean isActive, ModerationStatus moderationStatus);

    @Query("SELECT post FROM Post AS post " +
            "LEFT JOIN post.comments AS comments " +
            "WHERE post.isActive = true AND post.moderationStatus = 'ACCEPTED' " +
            "GROUP BY post " +
            "ORDER BY COUNT(comments) DESC")
    Page<Post> findOrderByCommentsCount(Pageable pageable);

    @Query("SELECT post FROM Post AS post " +
            "LEFT JOIN post.votes AS votes " +
            "WHERE post.isActive = true AND post.moderationStatus = 'ACCEPTED' " +
            "GROUP BY post " +
            "ORDER BY SUM(CASE WHEN votes.value > 0 THEN 1 ELSE 0 END) DESC, " +
            "SUM(CASE WHEN votes.value < 0 THEN 1 ELSE 0 END) ASC")
    Page<Post> findOrderByLikes(Pageable pageable);

    Page<Post> findByTitleContainingOrTextContaining(
            String title,
            String text,
            Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "WHERE p.isActive = true " +
            "AND p.moderationStatus = 'ACCEPTED' " +
            "AND DATE(p.time) = :date")
    Page<Post> findByTime(Date date, Pageable pageable);

    @Query("SELECT post FROM Post AS post JOIN post.tags AS tagPost JOIN tagPost.tag AS tag WHERE tag.name = :tagName")
    Page<Post> findByTags(String tagName, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Post p " +
            "SET p.isActive = :isActive, p.time = :time, p.title = :title, p.text = :text, p.tags = :tags " +
            "WHERE p.id = :id")
    void updatePost(int id, boolean isActive, Date time, String title, String text, Set<TagPost> tags);

    List<Post> findByIsActiveAndModerationStatusAndUserOrderByTimeAsc(
            boolean isActive,
            ModerationStatus moderationStatus,
            User user);

    List<Post> findByIsActiveAndModerationStatusOrderByTimeAsc(
            boolean isActive,
            ModerationStatus moderationStatus);
}
