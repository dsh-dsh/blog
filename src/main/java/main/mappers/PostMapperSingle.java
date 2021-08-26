package main.mappers;

import main.dto.CommentDTO;
import main.dto.PostDTOSingle;
import main.model.*;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PostMapperSingle {

    @Autowired
    private CommentMapper commentMapper;

    private final ModelMapper modelMapper;

    private Converter<Date, Long> timestampConverter =
            (date) -> (date.getSource().getTime() / 1000);
    private Converter<List<PostVote>, Integer> likesConverter =
            (votes) -> Math.toIntExact(votes.getSource().stream().filter(i -> i.getValue() > 0).count());
    private Converter<List<PostVote>, Integer> dislikesConverter =
            (votes) -> Math.toIntExact(votes.getSource().stream().filter(i -> i.getValue() < 0).count());
    private Converter<List<Comment>, List<CommentDTO>> commentsConverter =
            (comments) -> comments.getSource().stream().map(commentMapper::mapToDTO).collect(Collectors.toList());
    private Converter<Set<TagPost>, String[]> tagsConverter =
            (tags) -> tags.getSource().stream().map(TagPost::getTag).map(Tag::getName).toArray(String[]::new);

    public PostMapperSingle() {
        this.modelMapper = new ModelMapper();
        modelMapper.createTypeMap(Post.class, PostDTOSingle.class)
                .addMappings(mapper -> mapper.using(timestampConverter).map(Post::getTime, PostDTOSingle::setTimestamp))
                .addMappings(mapper -> mapper.using(likesConverter).map(Post::getVotes, PostDTOSingle::setLikeCount))
                .addMappings(mapper -> mapper.using(dislikesConverter).map(Post::getVotes, PostDTOSingle::setDislikeCount))
                .addMappings(mapper -> mapper.using(commentsConverter).map(Post::getComments, PostDTOSingle::setComments))
                .addMappings(mapper -> mapper.using(tagsConverter).map(Post::getTags, PostDTOSingle::setTags));
    }

    public PostDTOSingle mapToDTO(Post post) throws EntityNotFoundException {
        return modelMapper.map(post, PostDTOSingle.class);
    }
}
