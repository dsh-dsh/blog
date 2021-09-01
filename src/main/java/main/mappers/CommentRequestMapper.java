package main.mappers;

import main.api.requests.CommentRequest;
import main.exceptions.NoSuchPostException;
import main.model.Comment;
import main.model.Post;
import main.repositories.CommentRepository;
import main.repositories.PostRepository;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;

@Component
public class CommentRequestMapper {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;

    private final ModelMapper modelMapper;

    private final Converter<Integer, Comment> commentConverter =
            id -> (id.getSource() != 0) ? commentRepository.findById(id.getSource()).orElseThrow(EntityNotFoundException::new) : null;

    private final Converter<Integer, Post> postConverter =
            id -> postRepository.findById(id.getSource()).orElseThrow(() -> new NoSuchPostException(id.getSource()));

    public CommentRequestMapper() {
        this.modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.createTypeMap(CommentRequest.class, Comment.class)
                .addMappings(mapper -> mapper.using(commentConverter).map(CommentRequest::getParentId, Comment::setParent))
                .addMappings(mapper -> mapper.using(postConverter).map(CommentRequest::getPostId, Comment::setPost));
    }

    public Comment mapToPostComment(CommentRequest commentRequest) {
        return modelMapper.map(commentRequest, Comment.class);
    }
}
