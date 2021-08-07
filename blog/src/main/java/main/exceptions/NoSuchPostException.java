package main.exceptions;

import lombok.Data;

public class NoSuchPostException extends RuntimeException{
    public NoSuchPostException(Integer id) {
        super("Post with id " + id + " is not found");
    }
}
