package main.servise;

import main.api.response.PostResponse;
import main.dto.PostDTO;
import main.model.Post;
import main.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public PostResponse getPosts(Map<String, String> parameters) {

        int offset = Integer.parseInt(parameters.getOrDefault("offset", "0"));
        int limit = Integer.parseInt(parameters.getOrDefault("limit", "10"));
        String mode = parameters.getOrDefault("mode", "recent");

        Sort sort = getSortMode(mode);
        Pageable pageable = PageRequest.of(offset, limit, sort);

        Page<PostDTO> page = postRepository.findAllToDTO(pageable);

        return new PostResponse(page.getTotalPages(), page.getContent());
    }

    public Sort getSortMode(String mode) {
        switch(mode) {
            case "popular":
                //return new Sort(Sort.Direction.DESC, "");
            case "best":
                //return new Sort(Sort.Direction.DESC, "");
            case "early":
                return new Sort(Sort.Direction.ASC, "time");
            default:
                return new Sort(Sort.Direction.DESC, "time");
        }

    }

}
