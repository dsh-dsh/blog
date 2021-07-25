package main.repository;


import main.dto.PostDTO;
import main.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PostRepository extends CrudRepository<Post, Integer> {
    List<Post> findAll(Pageable pageable);

    @Query("SELECT new main.dto.PostDTO(p) FROM Post p")
    Page<PostDTO> findAllToDTO(Pageable pageable);
}
