package main.validation.validators;

import main.repositories.CommentRepository;
import main.validation.anotations.IsParentExists;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CommentParentExistsValidator implements ConstraintValidator<IsParentExists, Integer> {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public boolean isValid(Integer id, ConstraintValidatorContext constraintValidatorContext) {
        if(id == 0) return true;
        return commentRepository.existsById(id);
    }
}
