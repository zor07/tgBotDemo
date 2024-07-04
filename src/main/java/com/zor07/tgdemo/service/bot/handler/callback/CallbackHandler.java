package com.zor07.tgdemo.service.bot.handler.callback;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zor07.tgdemo.service.ReminderService;
import com.zor07.tgdemo.service.bot.MessageSender;
import com.zor07.tgdemo.service.bot.model.ReminderData;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Timer;
import java.util.TimerTask;

@Service
public class CallbackHandler {

    // теперь напоминания сохраняем в базу, а не в HashMap
    private final ReminderService reminderService;
    private final MessageSender messageSender;
    private final ObjectMapper objectMapper;

    public CallbackHandler(ReminderService reminderService,
                           MessageSender messageSender) {
        this.reminderService = reminderService;
        this.messageSender = messageSender;
        this.objectMapper = new ObjectMapper();
    }

    public SendMessage handle(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        String callbackData = update.getCallbackQuery().getData();
        ReminderData reminderInfo = jsonToReminder(callbackData);
        String reminderText = reminderService.getById(reminderInfo.getId()).getText();
        Integer seconds = reminderInfo.getSec();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SendMessage reminderMessage =  SendMessage
                        .builder()
                        .chatId(chatId)
                        .text(String.format("Напомнить: %s", reminderText))
                        .build();
                messageSender.sendMessage(reminderMessage);
            }
        }, seconds * 1000L);

        return  SendMessage
                .builder()
                .chatId(chatId)
                .text(String.format("Принято. Напомнить: %s, через %s секунд", reminderText, seconds))
                .build();
    }


    private ReminderData jsonToReminder(String json) {
        try {
            return objectMapper.readValue(json, ReminderData.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
