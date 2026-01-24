package de.skyslycer.hmcwraps.commands.exception;

public class InvalidWrapException extends RuntimeException {

    private String wrap;

    public InvalidWrapException(String wrap) {
        super(wrap);
    }

}
