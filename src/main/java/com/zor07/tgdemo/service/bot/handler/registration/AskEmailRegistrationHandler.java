package com.zor07.tgdemo.service.bot.handler.registration;

import com.zor07.tgdemo.entity.Client;
import com.zor07.tgdemo.enums.RegistrationState;
import com.zor07.tgdemo.service.ClientService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AskEmailRegistrationHandler implements RegistrationHandler {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    private final ClientService clientService;

    public AskEmailRegistrationHandler(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public SendMessage handle(String email, Long chatId, Client client) {
        if (isValidEmail(email)) {
            client.setEmail(email);
            client.setRegistrationState(RegistrationState.REGISTERED.name());
            clientService.save(client);
            return SendMessage.builder()
                    .chatId(chatId)
                    .text("Все! Теперь мы знакомы. Если надо что-то напомнить - просто чиркани, лапуля")
                    .build();
        } else {
            return SendMessage.builder()
                    .chatId(chatId)
                    .text("То, что ты ввел не очень похоже на мыло( Давай еще разок...")
                    .build();
        }
    }

    private boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
