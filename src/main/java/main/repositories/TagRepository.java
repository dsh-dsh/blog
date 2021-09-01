package main.repositories;

import main.model.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;

public interface TagRepository extends CrudRepository<Tag, Integer> {

    List<Tag> findAll();

    List<Tag> findByNameIgnoreCaseIn(Collection<String> tagNames);

    @Query(value = "insert into tags (name) values (:name) where not name = :name", nativeQuery = true)
    void insertIfNoExists(String name);
}
