package main.repositories;

import main.dto.CalendarDTO;
import main.model.ModerationStatus;
import main.model.Post;
import main.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface PostRepository extends CrudRepository<Post, Integer> {

    Page<Post> findByIsActiveAndModerationStatusOrderByTimeDesc(
            boolean isActive,
            ModerationStatus moderationStatus,
            Pageable pageable);

    Page<Post> findByIsActiveAndModerationStatusOrderByTimeAsc(
            boolean isActive,
            ModerationStatus moderationStatus,
            Pageable pageable);

    @Query("SELECT COUNT(post) FROM Post post " +
            "WHERE post.isActive = true AND post.moderationStatus = 'ACCEPTED'")
    int findByIsActiveAndModerationStatusCount();

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
            "WHERE p.isActive = :isActive " +
            "AND p.moderationStatus = :moderationStatus " +
            "AND DATE(p.time) = :date")
    Page<Post> findByTime(
            boolean isActive,
            ModerationStatus moderationStatus,
            Date date,
            Pageable pageable);

    @Query("SELECT p FROM Post p JOIN p.tags tp JOIN tp.tag t WHERE t.name = :tagName")  //SELECT a FROM Author a JOIN FETCH a.books WHERE a.id = 1
    Page<Post> findByTag(String tagName, Pageable pageable);

    Post getById(int id);
}
