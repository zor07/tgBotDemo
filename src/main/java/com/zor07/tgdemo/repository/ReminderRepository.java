package com.zor07.tgdemo.repository;

import com.zor07.tgdemo.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {
}
