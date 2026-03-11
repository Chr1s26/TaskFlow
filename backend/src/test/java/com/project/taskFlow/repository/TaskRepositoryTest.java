package com.project.taskFlow.repository;

import com.project.taskFlow.model.Task;
import com.project.taskFlow.model.User;
import com.project.taskFlow.model.constant.Priority;
import com.project.taskFlow.model.constant.Role;
import com.project.taskFlow.model.constant.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindTasksByUser() {

        User user = new User();
        user.setName("test");
        user.setEmail("test@test.com");
        user.setPassword("pass");
        user.setRole(Role.USER);

        userRepository.save(user);

        Task task = new Task();
        task.setTitle("Task1");
        task.setUser(user);
        task.setStatus(TaskStatus.TODO);
        task.setPriority(Priority.HIGH);

        taskRepository.save(task);

        var tasks = taskRepository.findByUser(user, PageRequest.of(0,10));

        assertEquals(1, tasks.getTotalElements());
    }

    @Test
    void shouldFindTasksByStatusAndPriority() {

        User user = new User();
        user.setName("test");
        user.setEmail("status@test.com");
        user.setPassword("pass");
        user.setRole(Role.USER);

        userRepository.save(user);

        Task task = new Task();
        task.setTitle("Task1");
        task.setUser(user);
        task.setStatus(TaskStatus.TODO);
        task.setPriority(Priority.HIGH);

        taskRepository.save(task);

        var tasks = taskRepository.findByUserAndStatusAndPriority(
                user,
                TaskStatus.TODO,
                Priority.HIGH,
                PageRequest.of(0,10)
        );

        assertEquals(1, tasks.getTotalElements());
    }
}
