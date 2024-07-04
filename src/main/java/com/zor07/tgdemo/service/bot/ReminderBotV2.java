package com.zor07.tgdemo.service.bot;

import com.zor07.tgdemo.service.bot.handler.callback.CallbackHandler;
import com.zor07.tgdemo.service.bot.handler.message.MessageHandler;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class ReminderBotV2 implements LongPollingSingleThreadUpdateConsumer {

    private final MessageHandler messageHandler;
    private final CallbackHandler callbackHandler;
    private final MessageSender messageSender;

    public ReminderBotV2(MessageHandler messageHandler,
                         CallbackHandler callbackHandler, MessageSender messageSender) {
        this.messageHandler = messageHandler;
        this.callbackHandler = callbackHandler;
        this.messageSender = messageSender;
    }

    @Override
    public void consume(Update update) {
        SendMessage message = null;
        if (update.hasMessage() && update.getMessage().hasText()) {
            message = messageHandler.handle(update);
        } else if (update.hasCallbackQuery()) {
            message = callbackHandler.handle(update);
        }

        if (message != null) {
            messageSender.sendMessage(message);
        }
    }
}
