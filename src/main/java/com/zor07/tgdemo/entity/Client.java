package com.zor07.tgdemo.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "client")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private LocalDate birthday;
    @Column(unique = true)
    private Long chatId;
    private String registrationState;

    public Client() {
    }

    public Client(Long id, String name, String email, LocalDate birthday, Long chatId, String registrationState) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.birthday = birthday;
        this.chatId = chatId;
        this.registrationState = registrationState;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getRegistrationState() {
        return registrationState;
    }

    public void setRegistrationState(String registrationStatus) {
        this.registrationState = registrationStatus;
    }
}
