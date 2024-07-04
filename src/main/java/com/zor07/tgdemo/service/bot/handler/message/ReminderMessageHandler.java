package com.zor07.tgdemo.service.bot.handler.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zor07.tgdemo.entity.Reminder;
import com.zor07.tgdemo.service.ReminderService;
import com.zor07.tgdemo.service.bot.model.ReminderData;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

@Service
public class ReminderMessageHandler {

    private final ReminderService reminderService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ReminderMessageHandler(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    public SendMessage handle(String messageText, Long chatId) {
        Reminder reminder = new Reminder(messageText);
        Long reminderId = reminderService.save(reminder).getId();
        return SendMessage
                .builder()
                .chatId(chatId)
                .text("Через сколько напомнить?")
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboardRow(new InlineKeyboardRow(
                                reminderButton(10, reminderId),
                                reminderButton(20, reminderId),
                                reminderButton(30, reminderId)
                        ))
                        .build())
                .build();
    }

    private InlineKeyboardButton reminderButton(Integer seconds, Long id) {
        String data = reminderToJson(new ReminderData(seconds, id));
        return InlineKeyboardButton
                .builder()
                .text(String.format("%s секунд", seconds))
                .callbackData(data)
                .build();
    }

    private String reminderToJson(ReminderData reminderData) {
        try {
            return objectMapper.writeValueAsString(reminderData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
