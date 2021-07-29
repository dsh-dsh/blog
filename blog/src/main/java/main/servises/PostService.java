package main.servises;

import main.api.responses.PostResponse;
import main.dto.PostDTO;
import main.model.ModerationStatus;
import main.model.Post;
import main.repositories.PostRepository;
import main.util.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostMapper postMapper;

    public PostResponse getPosts(Map<String, String> parameters) {

        int offset = Integer.parseInt(parameters.getOrDefault("offset", "0"));
        int limit = Integer.parseInt(parameters.getOrDefault("limit", "10"));
        String mode = parameters.getOrDefault("mode", "recent");

        Sort sort = getSortMode(mode);
        Pageable pageable = PageRequest.of(offset, limit, sort);

        Page<Post> page = postRepository.findByIsActiveAndModerationStatus(true, ModerationStatus.ACCEPTED, pageable);
        List<Post> posts = page.getContent();
        List<PostDTO> postDTOList = posts.stream().map(postMapper::mapToDTO).collect(Collectors.toList());

        return new PostResponse(page.getTotalPages(), postDTOList);

    }

    public int getPostCount() {
        List<Post> posts = postRepository.findByIsActiveAndModerationStatus(true, ModerationStatus.ACCEPTED);
        return posts.size();
    }

    public Sort getSortMode(String mode) {
        switch(mode) {
            case "popular":

            case "best":

            case "early":
                return Sort.by("time").ascending();
            default:
                return Sort.by("time").descending();
        }

    }

}
