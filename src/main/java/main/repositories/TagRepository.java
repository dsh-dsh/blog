package main.repositories;

import main.model.Tag;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface TagRepository extends CrudRepository<Tag, Integer> {

    List<Tag> findAll();

}
