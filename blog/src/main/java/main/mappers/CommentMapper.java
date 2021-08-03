package main.mappers;

import main.dto.CommentDTO;
import main.model.PostComment;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class CommentMapper {
    private final ModelMapper modelMapper;

    private Converter<Date, Long> timestampConverter =
            (date) -> (date.getSource().getTime() / 1000);

    public CommentMapper() {
        this.modelMapper = new ModelMapper();
        modelMapper.createTypeMap(PostComment.class, CommentDTO.class)
                .addMappings(mapper -> mapper.using(timestampConverter)
                        .map(PostComment::getTime, CommentDTO::setTimestamp));
    }

    public CommentDTO mapToDTO(PostComment comment) {
        return modelMapper.map(comment, CommentDTO.class);
    }

}
