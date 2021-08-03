package main.servises;

import main.api.responses.PostResponse;
import main.api.responses.PostResponseSingle;
import main.dto.PostDTO;
import main.dto.PostDTOSingle;
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

    public PostResponse searchPosts(Map<String, String> parameters) {

        Pageable pageable = getPageable(parameters);

        String query = parameters.get("query");
        Page<Post> page = postRepository.findByTitleContainingOrTextContaining(query, query, pageable);
        List<PostDTO> postDTOList = page.getContent().stream()
                .map(postMapper::mapToDTO).collect(Collectors.toList());

        return new PostResponse(page.getTotalPages(), postDTOList);

    }

    public PostResponse getPostsByDate(Map<String, String> parameters) throws ParseException {

        Pageable pageable = getPageable(parameters);

        String strDate = parameters.get("date");
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(strDate);
        Page<Post> page = postRepository.findByTime(true, ModerationStatus.ACCEPTED, date, pageable);
        List<PostDTO> postDTOList = page.getContent().stream()
                .map(postMapper::mapToDTO).collect(Collectors.toList());

        return new PostResponse(page.getTotalPages(), postDTOList);

    }

    public PostResponse getPostsByTag(Map<String, String> parameters) {

        Pageable pageable = getPageable(parameters);
        String tagName = parameters.get("tag");

        Page<Post> page = postRepository.findByTag(tagName, pageable);
        List<PostDTO> postDTOList = page.getContent().stream()
                .map(postMapper::mapToDTO).collect(Collectors.toList());

        postDTOList.forEach(System.out::println);

        return new PostResponse(page.getTotalPages(), postDTOList);

    }

    public PostDTOSingle getPostsById(int id) {


        System.out.println("--PostService---------------------id = " + id);

        Post post = postRepository.getById(id);

        System.out.println("----------------------------------");

        PostDTOSingle postDTOSingle = postMapperSingle.mapToDTO(post);
        if(postDTOSingle != null) {

            String[] tags = {"5", "2"};

            return postDTOSingle;
        } else {
            return null;
        }
    }

    public int getPostCount() {
        return postRepository.findByIsActiveAndModerationStatusCount();
    }

}
