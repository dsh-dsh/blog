package main.controllers;

import main.api.requests.UserRequest;
import main.api.responses.UserResponse;
import main.dto.CaptchaDTO;
import main.model.ModerationStatus;
import main.servises.CaptchaService;
import main.servises.PostService;
import main.servises.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    @Autowired
    private CaptchaService captchaService;
    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;

    @GetMapping("/check")
    public ResponseEntity<String> checkAuth() {

        int moderationCount = postService.getPostCount(true, ModerationStatus.NEW);

        return ResponseEntity.ok("{\"result\": false}");

//        return ResponseEntity.ok(
//                "{\n" +
//                "\"result\": true,\n" +
//                "\"user\": {\n" +
//                "\"id\": 576,\n" +
//                "\"name\": \"Дмитрий Петров\",\n" +
//                "\"photo\": \"/avatars/ab/cd/ef/111.png\",\n" +
//                "\"email\": \"petrov@petroff.ru\",\n" +
//                "\"moderation\": true,\n" +
//                "\"moderationCount\": 56,\n" +
//                "\"settings\": true\n" +
//                "}\n" +
//                "}");


    }

    @GetMapping("/captcha")
    public ResponseEntity<CaptchaDTO> getCaptcha() {

        CaptchaDTO captchaDTO = captchaService.getNewCaptcha();
        return ResponseEntity.ok(captchaDTO);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> newUser(
            @RequestBody UserRequest userRequest) {

        UserResponse userResponse = userService.saveNewUser(userRequest);
        return ResponseEntity.ok(userResponse);

    }

}
