package main.servises;

import main.api.responses.LoginResponse;
import main.api.responses.PostResponse;
import main.dto.PostDTO;
import main.dto.PostDTOSingle;
import main.exceptions.NoSuchPostException;
import main.mappers.PostMapperSingle;
import main.model.ModerationStatus;
import main.model.Post;
import main.model.User;
import main.repositories.PostRepository;
import main.mappers.PostMapper;
import main.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private PostMapperSingle postMapperSingle;

    public PostResponse getPosts(String mode, Pageable pageable) {

        Page<Post> page;
        switch (mode) {
            case "popular":
                page = postRepository.findOrderByCommentsCount(pageable);
                break;
            case "best":
                page = postRepository.findOrderByLikes(pageable);
                break;
            case "early":
                page = postRepository.findByIsActiveAndModerationStatusOrderByTimeAsc(
                        true, ModerationStatus.ACCEPTED, pageable);
                break;
            default:
                page = postRepository.findByIsActiveAndModerationStatusOrderByTimeDesc(
                        true, ModerationStatus.ACCEPTED, pageable);
                break;
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

        Post post = postRepository.findById(id).orElseThrow(() -> new NoSuchPostException(id));
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

    public PostResponse getMyPosts(String status, Pageable pageable) {

        boolean isActive = false;
        ModerationStatus moderationStatus = ModerationStatus.NEW;
        User user;

        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (securityUser != null) {
            user = userService.getUserByEmail(securityUser.getEmail());
        } else {
            return new PostResponse(0, new ArrayList<>());
        }

        switch (status) {
            case "pending":
                isActive = true;
                break;
            case "declined":
                isActive = true;
                moderationStatus = ModerationStatus.DECLINED;
                break;
            case "published":
                isActive = true;
                moderationStatus = ModerationStatus.ACCEPTED;
                break;
        }

        Page<Post> page = postRepository.findByIsActiveAndModerationStatusAndUserOrderByTimeDesc(isActive, moderationStatus, user, pageable);

        List<PostDTO> postDTOList = page.getContent().stream()
                .map(postMapper::mapToDTO).collect(Collectors.toList());

        return new PostResponse(page.getTotalPages(), postDTOList);
    }
}
