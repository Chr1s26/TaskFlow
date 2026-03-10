package com.project.taskFlow.service.otp;

import com.project.taskFlow.exception.BadRequestException;
import com.project.taskFlow.exception.ResourceNotFoundException;
import com.project.taskFlow.exception.SendOtpFailedException;
import com.project.taskFlow.model.OtpToken;
import com.project.taskFlow.model.User;
import com.project.taskFlow.repository.OtpTokenRepository;
import com.project.taskFlow.repository.UserRepository;
import com.project.taskFlow.service.email.EmailServiceImpl;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final UserRepository userRepository;
    private final OtpTokenRepository otpTokenRepository;
    private final EmailServiceImpl emailService;

    @Override
    public String generateOtpCode() { return String.format("%06d", new Random().nextInt(999999)); }

    @Override
    public void isOtpValid(String email, String otp) {
        OtpToken token = otpTokenRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("OTP not found"));

        if(!token.getOtpCode().equals(otp)){
            throw new BadRequestException("Invalid OTP");
        }

        if(token.getExpiryTime().isBefore(LocalDateTime.now())){
            throw new BadRequestException("OTP expired");
        }

        otpTokenRepository.delete(token);
    }

    @Override
    public void sendOtp(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        otpTokenRepository.deleteByEmail(email);

        String otp = this.generateOtpCode();

        OtpToken token = new OtpToken();
        token.setEmail(email);

        token.setOtpCode(otp);
        token.setExpiryTime(LocalDateTime.now().plusMinutes(5));

        otpTokenRepository.save(token);
        try{
            emailService.sendOtpEmail(user.getEmail(),otp);
        } catch (MessagingException e) {
            throw new SendOtpFailedException("OTP failed to send"+ e.getMessage());
        }
    }
}
