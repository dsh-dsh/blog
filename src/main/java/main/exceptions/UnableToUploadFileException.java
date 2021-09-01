package main.exceptions;

public class UnableToUploadFileException extends RuntimeException{
    private final String message;

    public UnableToUploadFileException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
