package main.controllers;

import main.api.responses.PostResponse;
import main.servises.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@RestController
@RequestMapping("/api/post")
public class ApiPostController {

    @Autowired
    private PostService postService;

    @GetMapping
    public ResponseEntity<PostResponse> getPosts(@RequestParam String mode, Pageable pageable) {

        PostResponse postResponse = postService.getPosts(mode, pageable);
        return ResponseEntity.ok(postResponse);

    }

}
