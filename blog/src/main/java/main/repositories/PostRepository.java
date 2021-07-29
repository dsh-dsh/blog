package main.repositories;

import main.model.ModerationStatus;
import main.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PostRepository extends CrudRepository<Post, Integer> {

    Page<Post> findAll(Pageable pageable);

    Page<Post> findByIsActiveAndModerationStatus(boolean isActive, ModerationStatus moderationStatus, Pageable pageable);

    List<Post> findByIsActiveAndModerationStatus(boolean isActive, ModerationStatus moderationStatus);

}
