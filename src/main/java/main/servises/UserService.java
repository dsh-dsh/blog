package main.servises;

import main.api.requests.LoginRequest;
import main.api.requests.UserRequest;
import main.api.responses.RegisterResponse;
import main.dto.UserDTO;
import main.mappers.UserMapper;
import main.model.User;
import main.repositories.UserRepository;
import main.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CaptchaService captchaService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;


    public RegisterResponse saveNewUser(UserRequest userRequest
    ) {

        RegisterResponse registerResponse = new RegisterResponse();
        Map<String, String> errors = checkUserParams(userRequest);
        registerResponse.setResult(errors == null);
        registerResponse.setErrors(errors);

        if (errors == null) {

            User user = User.builder()
                    .isModerator(false)
                    .regTime(new Date())
                    .name(userRequest.getName())
                    .email(userRequest.getEmail())
                    .password(passwordEncoder.encode(userRequest.getPassword()))
                    .code("")
                    .photo("")
                    .build();

            userRepository.save(user);
        }

        return registerResponse;
    }

    public Map<String, String> checkUserParams(UserRequest userRequest) {

        Map<String, String> errors = new HashMap<>();

        if(!captchaService.isCaptchaValid(userRequest.getCaptcha(), userRequest.getCaptchaSecret())) {
            errors.put("captcha", "Код с картинки введён неверно");
        }
        if(userRequest.getPassword().length() < 6) {
            errors.put("password", "Пароль короче 6-ти символов");
        }
        if(!userRequest.getName().matches("[\\w\\s@]+")) {
            errors.put("name", "Имя указано неверно");
        }
        if(userRepository.existsByEmail(userRequest.getEmail())) {
            errors.put("email", "Этот e-mail уже зарегистрирован");
        }

        if(errors.isEmpty()) {
            return null;
        } else {
            return errors;
        }
    }

    public User getUserByEmail(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("user with " + email + " not found"));
    }

    public User login(LoginRequest loginRequest) {

        Authentication auth = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);

        SecurityUser securityUser = (SecurityUser) auth.getPrincipal();
        return getUserByEmail(securityUser.getEmail());
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null){
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
    }
}
