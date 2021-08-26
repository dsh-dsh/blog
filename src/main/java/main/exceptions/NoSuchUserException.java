package main.exceptions;

import main.Constants;

public class NoSuchUserException extends RuntimeException{
    public NoSuchUserException() {
        super(Constants.USER_NOT_FOUND);
    }
}
