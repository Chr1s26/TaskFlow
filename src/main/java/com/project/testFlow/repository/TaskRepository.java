package com.project.testFlow.repository;

import com.project.testFlow.model.Task;
import com.project.testFlow.model.User;
import com.project.testFlow.model.constant.Priority;
import com.project.testFlow.model.constant.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task,Long> {
    Page<Task> findByUser(User user, Pageable pageable);
    Page<Task> findByUserAndStatus(User user, TaskStatus status, Pageable pageable);
    Page<Task> findByUserAndPriority(User user, Priority priority, Pageable pageable);
    Page<Task> findByUserAndStatusAndPriority(User user, TaskStatus status, Priority priority, Pageable pageable);
}
