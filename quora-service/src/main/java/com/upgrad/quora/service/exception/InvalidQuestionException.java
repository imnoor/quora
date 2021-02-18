package com.upgrad.quora.service.exception;

import org.springframework.http.HttpStatus;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * InvalidQuestionException is thrown when the question is not found in the database.
 */
public class InvalidQuestionException extends Exception {
    private final String code;
    private final String errorMessage;

    public InvalidQuestionException(final String code, final String errorMessage) {
        this.code = code;
        this.errorMessage = errorMessage;
    }

    @Override
    public void printStackTrace() {
        super.printStackTrace();
    }

    @Override
    public void printStackTrace(PrintStream s) {
        super.printStackTrace(s);
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        super.printStackTrace(s);
    }

    public String getCode() {
        return code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    // To send the right HTTP Code based on the error code and the Exception Classs
    public HttpStatus getHttpCode() {
        return HttpStatus.NOT_FOUND;
    }

}

