package main.servises;

import main.api.responses.PostResponse;
import main.dto.PostDTO;
import main.dto.PostDTOSingle;
import main.exceptions.NoSuchPostException;
import main.mappers.PostMapperSingle;
import main.model.ModerationStatus;
import main.model.Post;
import main.repositories.PostRepository;
import main.mappers.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostMapper postMapper;
    @Autowired
    private PostMapperSingle postMapperSingle;

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

    public PostResponse searchPosts(String query, Pageable pageable) {

        Page<Post> page = postRepository.findByTitleContainingOrTextContaining(query, query, pageable);
        List<PostDTO> postDTOList = page.getContent().stream()
                .map(postMapper::mapToDTO).collect(Collectors.toList());

        return new PostResponse(page.getTotalPages(), postDTOList);

    }

    public PostResponse getPostsByDate(String requestDate, Pageable pageable) throws ParseException {

        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(requestDate);
        Page<Post> page = postRepository.findByTime(date, pageable);
        List<PostDTO> postDTOList = page.getContent().stream()
                .map(postMapper::mapToDTO).collect(Collectors.toList());

        return new PostResponse(page.getTotalPages(), postDTOList);

    }

    public PostResponse getPostsByTag(String tag, Pageable pageable) {

        Page<Post> page = postRepository.findByTags(tag, pageable);
        List<PostDTO> postDTOList = page.getContent().stream()
                .map(postMapper::mapToDTO).collect(Collectors.toList());

        return new PostResponse(page.getTotalPages(), postDTOList);

    }

    public PostDTOSingle getPostsById(int id) throws Exception {

        Post post = postRepository.getById(id).orElseThrow(() -> new NoSuchPostException(id));
        //System.out.println(post);
        return postMapperSingle.mapToDTO(post);

    }

    public int getPostCount(boolean isActive, ModerationStatus moderationStatus) {
        return postRepository.countByIsActiveAndModerationStatus(isActive, moderationStatus);
    }

    public PostResponse getPostsForModeration(ModerationStatus status, Pageable pageable) {

        Page<Post> page = postRepository.findByIsActiveAndModerationStatusOrderByTimeDesc(
                true,
                status,
                pageable);

        List<PostDTO> postDTOList = page.getContent().stream()
                .map(postMapper::mapToDTO).collect(Collectors.toList());

        return new PostResponse(page.getTotalPages(), postDTOList);
    }
}
