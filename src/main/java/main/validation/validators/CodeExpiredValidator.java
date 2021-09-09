package main.validation.validators;

import main.repositories.UserRepository;
import main.validation.anotations.IsCodeExpired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CodeExpiredValidator implements ConstraintValidator<IsCodeExpired, String> {

    @Autowired
    private UserRepository userRepository;

    @Value("${restore.code.expired.hours}")
    private int hoursToExpired;

    @Override
    public boolean isValid(String code, ConstraintValidatorContext constraintValidatorContext) {

        if(!userRepository.existsByCode(code)) return true;

        try {
            long currentHours = System.currentTimeMillis()/(1000*60*60);
            long hours = Long.parseLong(code.substring(code.lastIndexOf("*") + 1));
            return (currentHours - hours) < hoursToExpired;

        } catch(Exception ex) {
            return false;
        }
    }
}
