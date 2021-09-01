package main.exceptions;

import main.Constants;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ControllerAdvice
public class ExceptionsHandler extends ResponseEntityExceptionHandler {
    
    @ExceptionHandler(UsernameNotFoundException.class)
    protected ResponseEntity<Object> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        ErrorMessage message = new ErrorMessage(Constants.USER_NOT_FOUND, ex.getMessage(), false, null);
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({NoSuchPostException.class, EntityNotFoundException.class})
    protected ResponseEntity<Object> handleNoSuchPostExceptions(RuntimeException ex) {
        ErrorMessage message = new ErrorMessage(Constants.POST_NOT_FOUND, ex.getMessage(), false, null);
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnableToUploadFileException.class)
    protected ResponseEntity<ErrorMessage> handleUnableToUploadFileExceptions(UnableToUploadFileException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("image", ex.getMessage());
        ErrorMessage errorMessage = new ErrorMessage(null, null, false, errors);
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ErrorMessage> handleConstraintViolationExceptions(ConstraintViolationException ex) {

        Map<String, String> errors = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(violation -> violation.getPropertyPath().toString(), ConstraintViolation::getMessageTemplate));

        ErrorMessage errorMessage = new ErrorMessage(null, null, false, errors);

        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(
            MissingServletRequestPartException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        errors.put("image", Constants.FILE_MISSING_ERROR);
        ErrorMessage message = new ErrorMessage(null, null, false, errors);
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid
            (MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        Function<ObjectError, String> mapKeyToLowerCase = objectError -> objectError.getCode().toLowerCase(Locale.ROOT);

        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

        Map<String, String> classErrors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .filter(objectError -> Objects.equals(objectError.getCode(), "Captcha"))
                .collect(Collectors.toMap(mapKeyToLowerCase, ObjectError::getDefaultMessage));

        Map<String, String> errors = Stream.concat(fieldErrors.entrySet().stream(), classErrors.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        ErrorMessage errorMessage = new ErrorMessage(null, null, false, errors);

        return new ResponseEntity<>(errorMessage, status);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return ResponseEntity.status(301).header("redirect", "/").build();
    }


}
