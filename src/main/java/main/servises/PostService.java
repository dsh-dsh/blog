package main.servises;

import main.Constants;
import main.api.requests.PostRequest;
import main.api.responses.PostResponse;
import main.api.responses.StatisticResponse;
import main.dto.PostDTO;
import main.dto.PostDTOSingle;
import main.exceptions.NoSuchPostException;
import main.exceptions.UnableToUploadFileException;
import main.mappers.PostMapperSingle;
import main.mappers.PostRequestMapper;
import main.model.*;
import main.repositories.PostRepository;
import main.mappers.PostMapper;
import main.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private TagService tagService;
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private PostMapperSingle postMapperSingle;
    @Autowired
    private PostRequestMapper postRequestMapper;

    @Value("${resources.path}")
    private String resourcesPathName;

    @Value("${max.file.size}")
    private int maxFileSize;

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

        return new PostResponse(page.getTotalElements(), postDTOList);

    }

    public PostResponse searchPosts(String query, Pageable pageable) {

        Page<Post> page = postRepository.findByTitleContainingOrTextContaining(query, query, pageable);
        List<PostDTO> postDTOList = page.getContent().stream()
                .map(postMapper::mapToDTO).collect(Collectors.toList());

        return new PostResponse(page.getTotalElements(), postDTOList);

    }

    public PostResponse getPostsByDate(String requestDate, Pageable pageable) throws ParseException {

        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(requestDate);
        Page<Post> page = postRepository.findByTime(date, pageable);
        List<PostDTO> postDTOList = page.getContent().stream()
                .map(postMapper::mapToDTO).collect(Collectors.toList());

        return new PostResponse(page.getTotalElements(), postDTOList);

    }

    public PostResponse getPostsByTag(String tag, Pageable pageable) {

        Page<Post> page = postRepository.findByTags(tag, pageable);
        List<PostDTO> postDTOList = page.getContent().stream()
                .map(postMapper::mapToDTO).collect(Collectors.toList());

        return new PostResponse(page.getTotalElements(), postDTOList);

    }

    public PostDTOSingle getPostsById(int id) {

        Post post = postRepository.findById(id).orElseThrow(() -> new NoSuchPostException(id));

        User user = userService.getUserFromSecurityContext();
        if(!user.isModerator() && !user.equals(post.getUser())) {
            post.setViewCount(post.getViewCount() + 1);
            postRepository.save(post);
        }

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

        return new PostResponse(page.getTotalElements(), postDTOList);
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

        return new PostResponse(page.getTotalElements(), postDTOList);
    }

    public void savePost(PostRequest postRequest) {

        Post post = postRequestMapper.mapToPost(postRequest);

        if(post.getTime().getTime() < new Date().getTime()) {
            post.setTime(new Date());
        }
        post.setModerationStatus(ModerationStatus.NEW);
        post.setUser(userService.getUserFromSecurityContext());

        List<Tag> tags = tagService.addIfNotExists(postRequest.getTags());
        Set<TagPost> tagPosts = tags.stream()
                .map(tag -> new TagPost(tag, post))
                .collect(Collectors.toSet());
        post.setTags(tagPosts);

        postRepository.save(post);

    }

    public void updatePost(PostRequest postRequest) {

        int id = postRequest.getId();
        Post post = postRepository.findById(id).orElseThrow(() -> new NoSuchPostException(id));

        long timestamp = postRequest.getTimestamp()*1000;
        if(post.getTime().getTime() < timestamp) {
            post.setTime(new Date(timestamp));
        }
        post.setActive(postRequest.isActive());
        post.setTitle(postRequest.getTitle());
        post.setText(postRequest.getText());


        List<Tag> tags = tagService.addIfNotExists(postRequest.getTags());
        Set<TagPost> newTagPosts = tags.stream()
                        .map(tag -> new TagPost(tag, post))
                        .collect(Collectors.toSet());
        post.getTags().clear();
        post.getTags().addAll(newTagPosts);

        postRepository.save(post);
    }

    public String uploadFile(MultipartFile image, String uploadPathName) throws Exception{

        if (!image.isEmpty()) {
            if(image.getSize() > maxFileSize) {
                throw new UnableToUploadFileException(Constants.FILE_SIZE_ERROR);
            }
            if(!(image.getContentType().equals("image/jpeg") || image.getContentType().equals("image/png"))) {
                throw new UnableToUploadFileException(Constants.WRONG_IMAGE_TYPE);
            }

            String uploadFileName = makeImagePath(image.getOriginalFilename(), uploadPathName);
            Path uploadFilePath = Paths.get(resourcesPathName + uploadPathName + uploadFileName);

            try(InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, uploadFilePath, StandardCopyOption.REPLACE_EXISTING);
                return uploadPathName + uploadFileName;

            } catch (Exception e) {
                throw new UnableToUploadFileException(Constants.FILE_UPLOAD_ERROR);
            }
        } else {
            throw new UnableToUploadFileException(Constants.FILE_MISSING_ERROR);
        }
    }

    public String makeImagePath(String originName, String uploadPathName) {
        String extension = originName.substring(originName.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String newDirName = "/" + uuid.substring(0, 3) + "/" + uuid.substring(3, 6) + "/" + uuid.substring(6, 9);
        try {
            System.out.println(Files.createDirectories(Paths.get(resourcesPathName + uploadPathName + newDirName)));
            return newDirName + "/" + uuid.substring(9, 18) + extension;
        } catch(IOException ex) {
            throw new UnableToUploadFileException(Constants.FILE_UPLOAD_ERROR);
        }
    }

    public void moderatePost(int postId, ModerationStatus moderationStatus) {
        User moderator = userService.getUserFromSecurityContext();
        Post post = postRepository.findById(postId).orElseThrow(() -> new NoSuchPostException(postId));
        post.setModeratorId(moderator);
        post.setModerationStatus(moderationStatus);
        postRepository.save(post);
    }

    public boolean like(int postId, int newValue) {

        User user = userService.getUserFromSecurityContext();
        Post post = postRepository.findById(postId).orElseThrow(() -> new NoSuchPostException(postId));

        System.out.println(user);

        List<PostVote> votes = post.getVotes();
        PostVote vote = votes.stream()
                .filter(v -> v.getUser() == user)
                .findAny()
                .orElse(new PostVote(0, user, post, new Date(), newValue));

        if(vote.getId() == 0) {
            post.getVotes().add(vote);
        } else {
            if(vote.getValue() == newValue) {
                return false;
            } else {
                vote.setValue(newValue);
                vote.setTime(new Date());
            }
        }

        postRepository.save(post);
        return true;
    }

    public StatisticResponse setStatistic(String mode) {

        System.out.println(mode);

        List<Post> posts = new ArrayList<>();
        User user = userService.getUserFromSecurityContext();

        if(mode.equals("my")) {
            posts = postRepository
                    .findByIsActiveAndModerationStatusAndUserOrderByTimeAsc(true, ModerationStatus.ACCEPTED, user);

        } else if(mode.equals("all")) {
            if(!user.isModerator()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
            }
            posts = postRepository
                    .findByIsActiveAndModerationStatusOrderByTimeAsc(true, ModerationStatus.ACCEPTED);
        }

        Map<Integer, Integer> likes = posts.stream()
                .flatMap(post -> post.getVotes().stream())
                .collect(Collectors.toMap(PostVote::getValue, vote -> 1, (v1, v2) -> v1 + 1));
        int viewCount = posts.stream().reduce(0, (count, post) -> count + post.getViewCount(), Integer::sum);
        long publication = posts.stream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                .getTime().getTime()/1000;

        StatisticResponse response = new StatisticResponse();
        response.setPostsCount(posts.size());
        response.setLikesCount(likes.getOrDefault(1, 0));
        response.setDislikesCount(likes.getOrDefault(-1, 0));
        response.setViewsCount(viewCount);
        response.setFirstPublication(publication);

        return response;
    }
}
