package main.servises;

import main.api.responses.PostResponse;
import main.dto.PostDTO;
import main.model.ModerationStatus;
import main.model.Post;
import main.repositories.PostRepository;
import main.util.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostMapper postMapper;

    public PostResponse getPosts(String mode, Pageable pageable) {

        Page<Post> page;
        if (mode.equals("popular")) {
            page = postRepository.findOrderByCommentsCount(pageable);

        } else if (mode.equals("best")) {
            page = postRepository.findOrderByLikes(pageable);

        } else if (mode.equals("early")){
            page = postRepository.findByIsActiveAndModerationStatusOrderByTimeAsc(
                    true,
                    ModerationStatus.ACCEPTED,
                    pageable);

        } else {
            page = postRepository.findByIsActiveAndModerationStatusOrderByTimeDesc(
                    true,
                    ModerationStatus.ACCEPTED,
                    pageable);

        }
        List<PostDTO> postDTOList = page.getContent().stream()
                .map(postMapper::mapToDTO).collect(Collectors.toList());

        return new PostResponse(page.getTotalPages(), postDTOList);

    }

    public int getPostCount() {
        return postRepository.findByIsActiveAndModerationStatusCount();
    }

}
