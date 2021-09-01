package main.mappers;

import lombok.Setter;
import main.dto.TagDTO;
import main.model.Post;
import main.model.Tag;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class TagMapper {

    private final ModelMapper modelMapper;
    @Setter
    private int postCount;
    @Setter
    private double normCoefficient;


    private final Converter<Set<Post>, Double> weightConverter =
            (posts) -> (posts.getSource().size() / (double) postCount) * normCoefficient;

    public TagMapper() {
        this.modelMapper = new ModelMapper();
        modelMapper.createTypeMap(Tag.class, TagDTO.class)
                .addMappings(mapper -> mapper.using(weightConverter).map(Tag::getPosts, TagDTO::setWeight));
    }

    public TagDTO mapToDTO(Tag tag) {
        return modelMapper.map(tag, TagDTO.class);
    }


}
