package main.repositories;

import main.model.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;

public interface TagRepository extends CrudRepository<Tag, Integer> {

    @Query("SELECT tag FROM Tag AS tag " +
            "JOIN tag.posts AS tagPost " +
            "JOIN tagPost.post AS post " +
            "WHERE post.isActive = true " +
            "AND post.moderationStatus = 'ACCEPTED' " +
            "GROUP BY tag")
    List<Tag> findByPostIsActiveAndAccepted();

    List<Tag> findByNameIgnoreCaseIn(Collection<String> tagNames);
}
