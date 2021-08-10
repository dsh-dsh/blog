package main.controllers;

import main.api.requests.LoginRequest;
import main.api.requests.UserRequest;
import main.api.responses.LoginResponse;
import main.api.responses.RegisterResponse;
import main.dto.CaptchaDTO;
import main.dto.UserDTO;
import main.mappers.UserMapper;
import main.model.User;
import main.servises.CaptchaService;
import main.servises.PostService;
import main.servises.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

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
    public ResponseEntity<RegisterResponse> register(
            @RequestBody UserRequest userRequest) {

        RegisterResponse registerResponse = userService.saveNewUser(userRequest);
        return ResponseEntity.ok(registerResponse);

    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest loginRequest) {

        User user = userService.login(loginRequest);

        LoginResponse loginResponse = new LoginResponse(true, userMapper.mapToUserDTO(user));
        return ResponseEntity.ok(loginResponse);

    }

    @GetMapping("/logout")
    public ResponseEntity<LoginResponse> logout(HttpServletRequest request, HttpServletResponse response) {

        userService.logout(request, response);

        LoginResponse loginResponse = new LoginResponse(true, null);
        return ResponseEntity.ok(loginResponse);
    }

}
