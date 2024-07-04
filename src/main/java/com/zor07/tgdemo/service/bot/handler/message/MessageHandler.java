package com.zor07.tgdemo.service.bot.handler.message;

import com.zor07.tgdemo.entity.Client;
import com.zor07.tgdemo.enums.RegistrationState;
import com.zor07.tgdemo.service.ClientService;
import com.zor07.tgdemo.service.bot.handler.registration.ClientRegistrationHandler;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Service
public class MessageHandler {

    private final ClientService clientService;
    private final UpdateMessageHandler updateMessageHandler;
    private final ClientRegistrationHandler clientRegistrationHandler;

    public MessageHandler(ClientService clientService,
                          UpdateMessageHandler updateMessageHandler,
                          ClientRegistrationHandler clientRegistrationHandler) {
        this.clientService = clientService;
        this.updateMessageHandler = updateMessageHandler;
        this.clientRegistrationHandler = clientRegistrationHandler;
    }

    public SendMessage handle(Update update) {
        String messageText = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        Optional<Client> clientOptional = clientService.getByChatId(chatId);
        if (isClientRegistered(clientOptional)) {
            return updateMessageHandler.handle(messageText, chatId);
        } else {
            return clientRegistrationHandler.register(messageText, chatId, clientOptional);
        }
    }

    private boolean isClientRegistered(Optional<Client> clientOptional) {
        return clientOptional.map(client -> client.getRegistrationState().equals(RegistrationState.REGISTERED.name()))
                .orElse(false);
    }
}
