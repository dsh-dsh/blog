package main.validation.anotations;

import main.Constants;
import main.validation.validators.CommentParentExistsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CommentParentExistsValidator.class)
public @interface IsParentExists {

    String message() default Constants.ID_COMMENT_NOT_FOUND;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
