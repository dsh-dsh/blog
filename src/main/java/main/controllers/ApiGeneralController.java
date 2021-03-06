package main.controllers;

import main.dto.requests.CommentRequest;
import main.dto.requests.ModerationRequest;
import main.dto.requests.UserRequest;
import main.dto.SettingsDTO;
import main.dto.TagDTO;
import main.dto.responses.*;
import main.model.Comment;
import main.servises.*;
import main.validation.OnUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

@Validated
@RestController
@RequestMapping("/api")
public class ApiGeneralController {

    @Autowired
    private InitResponse initResponse;
    @Autowired
    private SettingsService settingsService;
    @Autowired
    private TagService tagService;
    @Autowired
    private CalendarService calendarService;
    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;
    @Autowired
    private FileService fileService;
    @Autowired
    private Validator validator;

    @Value("${images.dir}")
    private String imagesPath;

    @GetMapping("/init")
    public InitResponse init() {
        return initResponse;
    }

    @GetMapping("/settings")
    public SettingsDTO settings() {
        return settingsService.getGlobalSettings();
    }

    @GetMapping("/tag")
    public ResponseEntity<TagResponse> tags() {

        List<TagDTO> tags = tagService.getTags();
        TagResponse tagResponse = new TagResponse(tags);
        return ResponseEntity.ok(tagResponse);
    }

    @GetMapping("/calendar")
    public ResponseEntity<CalendarResponse> calendar(
            @RequestParam(required = false) Integer year) {

        CalendarResponse calendarResponse = calendarService.getCalendar(year);
        return ResponseEntity.ok(calendarResponse);
    }

    @PostMapping("/image")
    @PreAuthorize("hasAuthority('write')")
    public String addImage (
            @RequestParam MultipartFile image) throws Exception{

        fileService.setFile(image);
        fileService.setUploadPathName(imagesPath);
        fileService.uploadFile();
        return fileService.getNewFileName();
    }

    @PostMapping("/comment")
    @PreAuthorize("hasAuthority('write')")
    public ResponseEntity<CommentResponse> addComment(
            @RequestBody @Valid CommentRequest commentRequest) {

        Comment comment = commentService.add(commentRequest);
        CommentResponse response = new CommentResponse(comment.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/moderation")
    @PreAuthorize("hasAuthority('moderate')")
    public ResponseEntity<ResultResponse> moderatePost(
            @RequestBody ModerationRequest request) {

        postService.moderatePost(request.getId(), request.getDecision());
        return ResponseEntity.ok(new ResultResponse());
    }

    @PostMapping(value = "/profile/my", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('write')")
    public ResponseEntity<ResultResponse> updateFormProfile(
            @ModelAttribute UserRequest userRequest,
            @RequestPart MultipartFile photo) {

        Set<ConstraintViolation<UserRequest>> violations = validator.validate(userRequest, OnUpdate.class);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        userService.updateProfile(userRequest, photo);
        return ResponseEntity.ok(new ResultResponse());
    }

    @PostMapping(value = "/profile/my", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('write')")
    public ResponseEntity<ResultResponse> updateJsonProfile(
            @RequestBody
            @Validated(OnUpdate.class)
            UserRequest userRequest) throws Exception {

        userService.updateProfile(userRequest, null);
        return ResponseEntity.ok(new ResultResponse());
    }

    @GetMapping("/statistics/{mode}")
    @PreAuthorize("hasAuthority('write')")
    public ResponseEntity<StatisticResponse> statistic(@PathVariable String mode) {

        StatisticResponse response = postService.getStatistic(mode);

        System.out.println(response);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/settings")
    @PreAuthorize("hasAuthority('moderate')")
    public void putSettings(@RequestBody SettingsDTO settings) {

        settingsService.setGlobalSettings(settings);

    }

}

