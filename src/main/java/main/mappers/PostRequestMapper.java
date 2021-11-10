package main.mappers;

import main.dto.requests.PostRequest;
import main.model.Post;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class PostRequestMapper {

    private final ModelMapper modelMapper;

    private final Converter<Long, Date> timestampConverter = l -> new Date(l.getSource()*1000);

    public PostRequestMapper() {
        this.modelMapper = new ModelMapper();
        modelMapper.createTypeMap(PostRequest.class, Post.class)
                .addMappings(mapper -> mapper.using(timestampConverter).map(PostRequest::getTimestamp, Post::setTime));
    }

    public Post mapToPost(PostRequest postRequest) {
        return modelMapper.map(postRequest, Post.class);
    }


}
