package com.zor07.tgdemo.repository;

import com.zor07.tgdemo.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByChatId(Long chatId);

}
