package main.servises;

import main.api.responses.TagResponse;
import main.dto.TagDTO;
import main.model.ModerationStatus;
import main.model.Tag;
import main.repositories.TagRepository;
import main.mappers.TagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TagService {

    private int postCount;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private TagMapper tagMapper;

    public TagResponse getTags() {

        List<Tag> tagList = tagRepository.findAll();
        List<TagDTO> tags = new ArrayList<>();
        postCount = postService.getPostCount(true, ModerationStatus.ACCEPTED);

        try{
            int maxPosts = tagList.stream()
                    .map(t -> t.getPosts().size())
                    .max(Integer::compareTo)
                    .get();

            double normKoeff = 1 / (maxPosts / (double) postCount);
            tagMapper.setPostCount(postCount);
            tagMapper.setNormKoeff(normKoeff);

            tags = tagList.stream()
                    .map(tagMapper::mapToDTO)
                    .collect(Collectors.toList());

        } catch(ArithmeticException ex) {
            ex.getStackTrace();
        }

        return new TagResponse(tags);

    }


}
