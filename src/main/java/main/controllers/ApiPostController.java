package main.controllers;

import main.api.responses.PostResponse;
import main.dto.PostDTOSingle;
import main.exceptions.NoSuchPostException;
import main.model.ModerationStatus;
import main.model.Post;
import main.repositories.PostRepository;
import main.servises.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.text.ParseException;
import java.util.List;

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
    public ResponseEntity<PostResponse> myPosts(
            @RequestParam String status,
            Pageable pageable) {

        PostResponse postResponse = postService.getMyPosts(status, pageable);
        return ResponseEntity.ok(postResponse);
    }

}
