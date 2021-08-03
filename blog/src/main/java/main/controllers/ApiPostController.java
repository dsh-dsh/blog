package main.controllers;

import main.api.responses.PostResponse;
import main.api.responses.PostResponseSingle;
import main.dto.PostDTOSingle;
import main.servises.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
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

    @GetMapping("/search")
    public ResponseEntity<PostResponse> search(@RequestParam Map<String, String> requestParam) {

        String query = requestParam.get("query").trim();

        if(query.equals("")) {
            System.out.println("general");
            return general(requestParam);
        } else {
            PostResponse postResponse = postService.searchPosts(requestParam);
            return ResponseEntity.ok(postResponse);
        }

    }

    @GetMapping("/byDate")
    public ResponseEntity<PostResponse> getByDate(@RequestParam Map<String, String> requestParam) throws ParseException {
        PostResponse postResponse = postService.getPostsByDate(requestParam);
        return ResponseEntity.ok(postResponse);
    }

    @GetMapping("/byTag")
    public ResponseEntity<PostResponse> getByTag(@RequestParam Map<String, String> requestParam) {
        PostResponse postResponse = postService.getPostsByTag(requestParam);
        return ResponseEntity.ok(postResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTOSingle> getById(@PathVariable int id) {

        System.out.println("--ApiPostController---------------------id = " + id);

        PostDTOSingle postDTOSingle = postService.getPostsById(id);
        if(postDTOSingle == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(postDTOSingle);
    }

}
