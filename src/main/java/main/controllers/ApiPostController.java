package main.controllers;

import main.api.requests.LikeRequest;
import main.api.requests.PostRequest;
import main.api.responses.PostResponse;
import main.api.responses.ResultResponse;
import main.dto.PostDTOSingle;
import main.model.ModerationStatus;
import main.repositories.PostRepository;
import main.servises.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import javax.validation.Valid;
import java.text.ParseException;

@RestController
@RequestMapping("/api/post")
public class ApiPostController {

    @Autowired
    private PostService postService;
    @Autowired
    private PostRepository postRepository;

    @GetMapping
    public ResponseEntity<PostResponse> getPosts(
            @RequestParam String mode,
            Pageable pageable) {

        PostResponse postResponse = postService.getPosts(mode, pageable);
        return ResponseEntity.ok(postResponse);

    }

    @GetMapping("/search")
    public ResponseEntity<PostResponse> search(
            @RequestParam String query,
            Pageable pageable) {

        if(query.trim().equals("")) {
            return getPosts("", pageable);
        } else {
            PostResponse postResponse = postService.searchPosts(query, pageable);
            return ResponseEntity.ok(postResponse);
        }

    }

    @GetMapping("/byDate")
    public ResponseEntity<PostResponse> getByDate(
            @RequestParam(name = "date") String requestDate,
            Pageable pageable) throws ParseException {

        PostResponse postResponse = postService.getPostsByDate(requestDate, pageable);
        return ResponseEntity.ok(postResponse);
    }

    @GetMapping("/byTag")
    public ResponseEntity<PostResponse> getByTag(
            @RequestParam String tag,
            Pageable pageable) {

        PostResponse postResponse = postService.getPostsByTag(tag, pageable);
        return ResponseEntity.ok(postResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTOSingle> getById(@PathVariable int id) throws Exception {

        PostDTOSingle postDTOSingle = postService.getPostsById(id);
        return ResponseEntity.ok(postDTOSingle);

    }

    @GetMapping("/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<PostResponse> getPostModerationList(
            @RequestParam ModerationStatus status,
            Pageable pageable) {

        PostResponse postResponse = postService.getPostsForModeration(status, pageable);
        return ResponseEntity.ok(postResponse);
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<PostResponse> myPosts(
            @RequestParam String status,
            Pageable pageable) {

        PostResponse postResponse = postService.getMyPosts(status, pageable);
        return ResponseEntity.ok(postResponse);
    }

    @PostMapping()
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ResultResponse> newPost(
            @RequestBody @Valid PostRequest postRequest) {

        postService.savePost(postRequest);

        return ResponseEntity.ok(new ResultResponse());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ResultResponse> changePost(
            @RequestBody @Valid PostRequest postRequest,
            @PathVariable int id) throws Exception{

        postRequest.setId(id);
        postService.updatePost(postRequest);

        return ResponseEntity.ok(new ResultResponse());
    }

    @PostMapping("/like")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ResultResponse> like(
            @RequestBody LikeRequest request) {

        ResultResponse resultResponse = new ResultResponse();
        resultResponse.setResult(postService.like(request.getPostId(), 1));

        return ResponseEntity.ok(resultResponse);
    }

    @PostMapping("/dislike")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ResultResponse> dislike(
            @RequestBody LikeRequest request) {

        ResultResponse resultResponse = new ResultResponse();
        resultResponse.setResult(postService.like(request.getPostId(), -1));

        return ResponseEntity.ok(resultResponse);
    }

}
