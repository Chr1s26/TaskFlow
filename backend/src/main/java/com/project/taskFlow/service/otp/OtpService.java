package com.project.taskFlow.service.otp;

public interface OtpService {
    public String generateOtpCode();
    public void isOtpValid(String email, String otp);
    public void sendOtp(String email);
}
