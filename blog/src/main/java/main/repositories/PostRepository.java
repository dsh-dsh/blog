package main.repositories;

import main.dto.CalendarDTO;
import main.model.ModerationStatus;
import main.model.Post;
import main.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface PostRepository extends CrudRepository<Post, Integer> {

    Page<Post> findAll();

    Page<Post> findByIsActiveAndModerationStatus(
            boolean isActive,
            ModerationStatus moderationStatus,
            Pageable pageable);

    List<Post> findByIsActiveAndModerationStatus(
            boolean isActive,
            ModerationStatus moderationStatus);

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
