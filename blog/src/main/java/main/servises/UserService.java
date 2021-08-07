package main.servises;

import main.api.requests.UserRequest;
import main.api.responses.UserResponse;
import main.model.User;
import main.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CaptchaService captchaService;

    public UserResponse saveNewUser(UserRequest userRequest
    ) {

        UserResponse userResponse = new UserResponse();
        Map<String, String> errors = checkUserParams(userRequest);
        userResponse.setResult(errors == null);
        userResponse.setErrors(errors);

        if (errors == null) {
            User user = new User(
                    null, false, new Date(),
                    userRequest.getName(), userRequest.getEmail(), userRequest.getPassword(),
                    "", "");
            userRepository.save(user);
        }

        return userResponse;
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
}
