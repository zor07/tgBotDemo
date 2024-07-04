package com.zor07.tgdemo.service.bot.handler.registration;

import com.zor07.tgdemo.entity.Client;
import com.zor07.tgdemo.enums.RegistrationState;
import com.zor07.tgdemo.service.ClientService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
public class AskNameRegistrationHandler implements RegistrationHandler {

    private final ClientService clientService;

    public AskNameRegistrationHandler(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public SendMessage handle(String name, Long chatId, Client client) {
        client.setName(name);
        client.setRegistrationState(RegistrationState.ASK_BIRTHDATE.name());
        clientService.save(client);
        return SendMessage
                .builder()
                .chatId(chatId)
                .text("Приятно познакомиться! Введи плз дату рождения в формате DD.MM.YYYY")
                .build();

    }
}
