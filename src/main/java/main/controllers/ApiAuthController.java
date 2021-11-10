package main.controllers;

import main.dto.requests.LoginRequest;
import main.dto.requests.UserRequest;
import main.dto.responses.LoginResponse;
import main.dto.responses.ResultResponse;
import main.dto.CaptchaDTO;
import main.mappers.UserMapper;
import main.model.User;
import main.servises.CaptchaService;
import main.servises.PostService;
import main.servises.SettingsService;
import main.servises.UserService;
import main.validation.OnCreate;
import main.validation.OnRestore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;


@Validated
@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    @Autowired
    private CaptchaService captchaService;
    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SettingsService settingsService;

    @GetMapping("/check")
    public ResponseEntity<LoginResponse> check(Principal principal) {

        if(principal == null) {
            return ResponseEntity.ok(new LoginResponse());
        } else {
            User user = userService.getUserByEmail(principal.getName());

            LoginResponse loginResponse = new LoginResponse(true, userMapper.mapToUserDTO(user));
            return ResponseEntity.ok(loginResponse);
        }
    }

    @GetMapping("/captcha")
    public ResponseEntity<CaptchaDTO> captcha() {

        CaptchaDTO captchaDTO = captchaService.getNewCaptcha();

        return ResponseEntity.ok(captchaDTO);
    }

    @PostMapping("/register")
    public ResponseEntity<ResultResponse> register(
            @RequestBody
            @Validated(OnCreate.class)
            UserRequest userRequest) {

        if(!settingsService.getGlobalSettings().isMultiuserMode()) {
            return ResponseEntity.notFound().build();
        }

        userService.saveNewUser(userRequest);
        return ResponseEntity.ok(new ResultResponse());
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest loginRequest) {

        User user = userService.login(loginRequest);
        LoginResponse loginResponse = new LoginResponse(true, userMapper.mapToUserDTO(user));
        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/logout")
    public ResponseEntity<LoginResponse> logout(
            HttpServletRequest request, HttpServletResponse response) {

        userService.logout(request, response);
        LoginResponse loginResponse = new LoginResponse(true, null);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/restore")
    public ResponseEntity<ResultResponse> restorePassword(
            @RequestBody UserRequest request) {

        String email = request.getEmail();
        ResultResponse resultResponse = new ResultResponse();
        resultResponse.setResult(userService.sendRestoreEmail(email));
        return ResponseEntity.ok(resultResponse);
    }

    @PostMapping("/password")
    public ResponseEntity<ResultResponse> changePassword(
            @RequestBody
            @Validated(OnRestore.class)
            UserRequest userRequest) {

        userService.restorePassword(userRequest);
        return ResponseEntity.ok(new ResultResponse());
    }

}
