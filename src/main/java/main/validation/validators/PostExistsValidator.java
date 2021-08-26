package main.validation.validators;

import main.repositories.PostRepository;
import main.validation.anotations.IsPostExists;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PostExistsValidator implements ConstraintValidator<IsPostExists, Integer> {

    @Autowired
    private PostRepository postRepository;

    @Override
    public boolean isValid(Integer id, ConstraintValidatorContext constraintValidatorContext) {

        return postRepository.existsById(id);
    }
}
