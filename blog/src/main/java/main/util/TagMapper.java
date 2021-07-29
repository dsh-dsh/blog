package main.util;

import lombok.Setter;
import main.dto.TagDTO;
import main.model.Post;
import main.model.Tag;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TagMapper {

    private final ModelMapper modelMapper;
    @Setter
    private int postCount;
    @Setter
    private double normKoeff;

    private Converter<List<Post>, Double> weightConverter =
            (posts) -> (posts.getSource().size() / (double) postCount) * normKoeff;

    public TagMapper() {
        this.modelMapper = new ModelMapper();
        modelMapper.createTypeMap(Tag.class, TagDTO.class)
                .addMappings(mapper -> mapper.using(weightConverter).map(Tag::getPosts, TagDTO::setWeight));
    }

    public TagDTO mapToDTO(Tag tag) {
        return modelMapper.map(tag, TagDTO.class);
    }


}
