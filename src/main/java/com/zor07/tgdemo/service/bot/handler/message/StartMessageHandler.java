package com.zor07.tgdemo.service.bot.handler.message;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
public class StartMessageHandler  {

    public SendMessage handle(Long chatId) {
        return SendMessage
                .builder()
                .chatId(chatId)
                .text("Привет! Я помогу тебе не забыть важные вещи. Просто напиши о чем тебе нужно напомнить.")
                .build();
    }
}
