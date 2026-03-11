package com.project.taskFlow.repository;

import com.project.taskFlow.model.OtpToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class OtpTokenRepositoryTest {

    @Autowired
    private OtpTokenRepository otpTokenRepository;

    @Test
    void shouldFindOtpByEmail() {

        OtpToken token = new OtpToken();
        token.setEmail("otp@test.com");
        token.setOtpCode("123456");
        token.setExpiryTime(LocalDateTime.now().plusMinutes(5));

        otpTokenRepository.save(token);

        Optional<OtpToken> found = otpTokenRepository.findByEmail("otp@test.com");

        assertTrue(found.isPresent());
        assertEquals("123456", found.get().getOtpCode());
    }

    @Test
    void shouldDeleteByEmail() {

        OtpToken token = new OtpToken();
        token.setEmail("delete@test.com");
        token.setOtpCode("123456");
        token.setExpiryTime(LocalDateTime.now().plusMinutes(5));

        otpTokenRepository.save(token);

        otpTokenRepository.deleteByEmail("delete@test.com");

        Optional<OtpToken> found = otpTokenRepository.findByEmail("delete@test.com");

        assertFalse(found.isPresent());
    }
}
