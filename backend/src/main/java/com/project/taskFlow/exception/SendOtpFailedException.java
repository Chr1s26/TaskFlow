package com.project.taskFlow.exception;

public class SendOtpFailedException extends RuntimeException {
    public SendOtpFailedException(String message) {
        super(message);
    }
}
