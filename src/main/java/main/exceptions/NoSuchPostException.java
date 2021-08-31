package main.exceptions;

import main.Constants;

public class NoSuchPostException extends RuntimeException{
    public NoSuchPostException(Integer id) {
        super(String.format(Constants.ID_POST_NOT_FOUND, id));
    }
    public NoSuchPostException(String message) {
        super(message);
    }
}
