package main.controllers;

import main.api.responses.PostResponse;
import main.servises.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@RestController
@RequestMapping("/api/post")
public class ApiPostController {

    @Autowired
    private PostService postService;

    @GetMapping
    public ResponseEntity<PostResponse> general(@RequestParam Map<String, String> requestParam) {

        PostResponse postResponse = postService.getPosts(requestParam);
        return ResponseEntity.ok(postResponse);

    }

}
