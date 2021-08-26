package main.validation.validators;

import main.repositories.UserRepository;
import main.validation.anotations.IsCodeValid;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CodeValidator implements ConstraintValidator<IsCodeValid, String> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean isValid(String code, ConstraintValidatorContext constraintValidatorContext) {

        if(code == null) return true;

        return userRepository.existsByCode(code);
    }
}
