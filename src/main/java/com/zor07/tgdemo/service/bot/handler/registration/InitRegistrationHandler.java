package com.zor07.tgdemo.service.bot.handler.registration;

import com.zor07.tgdemo.entity.Client;
import com.zor07.tgdemo.enums.RegistrationState;
import com.zor07.tgdemo.service.ClientService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
public class InitRegistrationHandler implements RegistrationHandler {

    private final ClientService clientService;

    public InitRegistrationHandler(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public SendMessage handle(String message, Long chatId, Client client) {
        client.setChatId(chatId);
        client.setRegistrationState(RegistrationState.ASK_NAME.name());
        clientService.save(client);
        return SendMessage
                .builder()
                .chatId(chatId)
                .text("Давайте познакомимся, я бот, а вас как зовут?")
                .build();

    }
}
