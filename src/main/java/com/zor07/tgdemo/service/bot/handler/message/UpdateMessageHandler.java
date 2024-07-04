package com.zor07.tgdemo.service.bot.handler.message;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
public class UpdateMessageHandler {

    private final ReminderMessageHandler reminderMessageHandler;
    private final StartMessageHandler startMessageHandler;

    public UpdateMessageHandler(ReminderMessageHandler reminderMessageHandler, StartMessageHandler startMessageHandler) {
        this.reminderMessageHandler = reminderMessageHandler;
        this.startMessageHandler = startMessageHandler;
    }

    public SendMessage handle(String messageText, Long chatId) {
        if ("/start".equals(messageText)) {
            return startMessageHandler.handle(chatId);
        } else {
            return reminderMessageHandler.handle(messageText, chatId);
        }
    }
}
