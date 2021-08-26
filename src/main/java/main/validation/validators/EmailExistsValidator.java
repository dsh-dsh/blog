package main.validation.validators;

import main.model.User;
import main.repositories.UserRepository;
import main.servises.UserService;
import main.validation.anotations.IsEmailExists;
import org.springframework.beans.factory.annotation.Autowired;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailExistsValidator implements ConstraintValidator<IsEmailExists, String> {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @Override
    public boolean isValid(String newEmail, ConstraintValidatorContext constraintValidatorContext) {

        User user = userService.getUserFromSecurityContext();

        if(user == null) {
            return !userRepository.existsByEmail(newEmail);
        } else {
            if(userRepository.existsByEmail(newEmail)) {
                return user.getEmail().equals(newEmail);
            } else {
                return true;
            }
        }

    }
}
