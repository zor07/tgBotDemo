package com.zor07.tgdemo.service;

import com.zor07.tgdemo.entity.Reminder;
import com.zor07.tgdemo.repository.ReminderRepository;
import org.springframework.stereotype.Service;

@Service
public class ReminderService {

    private final ReminderRepository reminderRepository;

    public ReminderService(ReminderRepository reminderRepository) {
        this.reminderRepository = reminderRepository;
    }

    public Reminder save(Reminder reminder) {
        return reminderRepository.save(reminder);
    }

    public Reminder getById(Long id) {
        return reminderRepository.findById(id)
                .orElse(null);
    }
}
