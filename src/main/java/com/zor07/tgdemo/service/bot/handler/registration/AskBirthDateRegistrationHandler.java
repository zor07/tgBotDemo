package com.zor07.tgdemo.service.bot.handler.registration;

import com.zor07.tgdemo.entity.Client;
import com.zor07.tgdemo.enums.RegistrationState;
import com.zor07.tgdemo.service.ClientService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
public class AskBirthDateRegistrationHandler implements RegistrationHandler {

    private final ClientService clientService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public AskBirthDateRegistrationHandler(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public SendMessage handle(String dateString, Long chatId, Client client) {
        try {
            LocalDate birthDate = LocalDate.parse(dateString, formatter);
            client.setBirthday(birthDate);
            client.setRegistrationState(RegistrationState.ASK_EMAIL.name());
            clientService.save(client);
            return SendMessage.builder()
                    .chatId(chatId)
                    .text("Круто! Осталось ввести только твой email ;")
                    .build();
        } catch (DateTimeParseException e) {
            return SendMessage
                    .builder()
                    .chatId(chatId)
                    .text("Воу... Чет не очень похоже на дату...Попробуй еще раз в формате DD.MM.YYYY")
                    .build();
        }
    }
}
