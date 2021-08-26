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

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private TagMapper tagMapper;

    public TagResponse getTagResponse() {

        List<Tag> tagList = tagRepository.findAll();
        List<TagDTO> tags = new ArrayList<>();
        int postCount = postService.getPostCount(true, ModerationStatus.ACCEPTED);

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

    public List<Tag> addIfNotExists(String[] tags) {

        List<String> tagNames = Arrays.asList(tags);
        List<Tag> tagsInDB = tagRepository.findByNameIn(tagNames);

        List<String> existingTagNames = tagsInDB.stream().map(Tag::getName).collect(Collectors.toList());

        //tagNames.stream().filter(tagName -> !existingTagNames.contains(tagName));

        List<Tag> newTags = tagNames.stream()
                .filter(tagName -> !existingTagNames.contains(tagName))
                .map(this::addNewTag)
                .collect(Collectors.toList());

        tagsInDB.addAll(newTags);
        return tagsInDB;

    }

    public Tag addNewTag(String name) {
        Tag tag = new Tag();
        tag.setName(name);
        tagRepository.save(tag);
        return tag;
    }


}
