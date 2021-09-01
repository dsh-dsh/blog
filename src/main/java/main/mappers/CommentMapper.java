package main.mappers;

import main.dto.CommentDTO;
import main.model.Comment;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class CommentMapper {
    private final ModelMapper modelMapper;

    private final Converter<Date, Long> timestampConverter =
            (date) -> (date.getSource().getTime() / 1000);

    public CommentMapper() {
        this.modelMapper = new ModelMapper();
        modelMapper.createTypeMap(Comment.class, CommentDTO.class)
                .addMappings(mapper -> mapper.using(timestampConverter)
                        .map(Comment::getTime, CommentDTO::setTimestamp));
    }

    public CommentDTO mapToDTO(Comment comment) {
        return modelMapper.map(comment, CommentDTO.class);
    }

}
