package main.validation.validators;

import main.api.requests.UserRequest;
import main.repositories.CaptchaRepository;
import main.validation.anotations.Captcha;
import org.springframework.beans.factory.annotation.Autowired;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CaptchaValidator implements ConstraintValidator<Captcha, Object> {

    @Autowired
    private CaptchaRepository captchaRepository;

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {

        UserRequest userRequest = (UserRequest) object;

        if(userRequest.getCaptcha() == null) return true;

        return captchaRepository.existsByCodeAndSecretCode(userRequest.getCaptcha(), userRequest.getCaptchaSecret());
    }
}
