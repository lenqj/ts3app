package org.example.teamspeak3app.dto.builder;


import org.example.teamspeak3app.dto.TS3ClientDTO;
import org.example.teamspeak3app.model.TS3Client;

public class TS3ClientBuilder {
    public static TS3Client generateEntity(TS3ClientDTO ts3ClientDTO) {
        return TS3Client.builder()
                .id(ts3ClientDTO.getId())
                .username(ts3ClientDTO.getUsername())
                .password(ts3ClientDTO.getPassword())
                .coins(0.0)
                .ignoreWelcomeNotifications(false)
                .ignorePaydayNotifications(false)
                .ignoreAutoAFKMove(false)
                .ignoreAutoMoveToHomeChannel(false)
                .build();

    }
}
