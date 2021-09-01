package main.servises;

import main.dto.TagDTO;
import main.model.ModerationStatus;
import main.model.Tag;
import main.repositories.TagRepository;
import main.mappers.TagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    public List<TagDTO> getTagResponse() {

        List<Tag> tagList = tagRepository.findAll();
        List<TagDTO> tags = new ArrayList<>();
        int postCount = postService.getPostCount(true, ModerationStatus.ACCEPTED);

        if (postCount == 0) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        int maxPosts = tagList.stream()
                .map(t -> t.getPosts().size())
                .max(Integer::compareTo)
                .orElse(0);

        double normCoefficient = 1 / (maxPosts / (double) postCount);
        tagMapper.setPostCount(postCount);
        tagMapper.setNormCoefficient(normCoefficient);

        return tagList.stream()
                .map(tagMapper::mapToDTO)
                .collect(Collectors.toList());

    }

    public List<Tag> addIfNotExists(String[] tags) {

        List<String> tagNames = Arrays.asList(tags);
        List<Tag> tagsInDB = tagRepository.findByNameIgnoreCaseIn(tagNames);
        List<String> existingTagNames = tagsInDB.stream()
                .map(Tag::getName)
                .map(String::toUpperCase)
                .collect(Collectors.toList());

        List<Tag> newTags = tagNames.stream()
                .filter(tagName -> !existingTagNames.contains(tagName.toUpperCase()))
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
