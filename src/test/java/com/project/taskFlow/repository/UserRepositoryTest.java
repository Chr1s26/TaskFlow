package com.project.taskFlow.repository;

import com.project.taskFlow.model.User;
import com.project.taskFlow.model.constant.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindUserByEmail() {

        User user = new User();
        user.setName("testUser");
        user.setEmail("test@test.com");
        user.setPassword("password");
        user.setRole(Role.USER);
        user.setProfileImageUrl(null);
        user.setEnable(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("test@test.com");

        assertTrue(found.isPresent());
        assertEquals("testUser", found.get().getName());
    }

    @Test
    void shouldCheckExistsByEmail() {

        User user = new User();
        user.setName("testUser");
        user.setEmail("exists@test.com");
        user.setPassword("password");
        user.setRole(Role.USER);
        user.setEnable(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        boolean exists = userRepository.existsByEmail("exists@test.com");

        assertTrue(exists);
    }

    @Test
    void shouldFindUserByName() {

        User user = new User();
        user.setName("username");
        user.setEmail("user@test.com");
        user.setPassword("password");
        user.setRole(Role.USER);
        user.setEnable(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        Optional<User> found = userRepository.findByName("username");

        assertTrue(found.isPresent());
    }
}
