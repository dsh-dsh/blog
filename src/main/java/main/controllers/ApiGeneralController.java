package main.controllers;

import main.api.requests.CommentRequest;
import main.api.requests.ModerationRequest;
import main.api.requests.UserRequest;
import main.api.responses.*;
import main.dto.SettingsDTO;
import main.model.Comment;
import main.servises.*;
import main.validation.OnUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
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
    @Value("${upload.path}")
    private String uploadPathName;

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

        TagResponse tagResponse = tagService.getTagResponse();
        return ResponseEntity.ok(tagResponse);

    }

    @GetMapping("/calendar")
    public ResponseEntity<CalendarResponse> calendar(@RequestParam int year) {

        CalendarResponse calendarResponse = calendarService.getCalendar();
        return ResponseEntity.ok(calendarResponse);

    }

    @PostMapping("/image")
    @PreAuthorize("hasAuthority('user:write')")
    public String addImage (
            @RequestParam MultipartFile image) throws Exception{

        fileService.setFile(image);
        fileService.setUploadPathName(uploadPathName);
        fileService.uploadFile();

        return fileService.getNewFileName();

    }

    @PostMapping("/comment")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<CommentResponse> addComment(
            @RequestBody @Valid CommentRequest commentRequest) {

        Comment comment = commentService.add(commentRequest);
        CommentResponse response = new CommentResponse(comment.getId());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<ResultResponse> moderatePost(
            @RequestBody ModerationRequest request) throws Exception{

        postService.moderatePost(request.getId(), request.getDecision());

        return ResponseEntity.ok(new ResultResponse());
    }

    @PostMapping(value = "/profile/my", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('user:write')")
    @Validated(OnUpdate.class)
    public ResponseEntity<ResultResponse> updateFormProfile(
            @Valid @ModelAttribute UserRequest userRequest,
            BindingResult result,
            @RequestPart MultipartFile photo) throws Exception {

        Set<ConstraintViolation<UserRequest>> violations = validator.validate(userRequest);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        userService.updateProfile(userRequest, photo);

        return ResponseEntity.ok(new ResultResponse());
    }

    @PostMapping(value = "/profile/my", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('user:write')")
    @Validated(OnUpdate.class)
    public ResponseEntity<ResultResponse> updateJsonProfile(
            @RequestBody @Valid UserRequest userRequest) throws Exception {

        userService.updateProfile(userRequest, null);

        return ResponseEntity.ok(new ResultResponse());
    }

    @GetMapping("/statistics/{mode}")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<StatisticResponse> statistic(@PathVariable String mode) {

        if(!settingsService.getGlobalSettings().isStatisticIsPublic()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        StatisticResponse response = postService.setStatistic(mode);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/settings")
    @PreAuthorize("hasAuthority('user:moderate')")
    public void putSettings(@RequestBody SettingsDTO settings) {

        settingsService.setGlobalSettings(settings);

    }

}

