package main.repositories;

import main.model.Comment;
import org.modelmapper.spi.MappingContext;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommentRepository extends CrudRepository<Comment, Integer> {

}
