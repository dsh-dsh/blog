package main.exceptions;

public class UnableToUploadFileException extends RuntimeException{
    private String message;

    public UnableToUploadFileException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
