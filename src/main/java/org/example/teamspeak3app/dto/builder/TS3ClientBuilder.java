package org.example.teamspeak3app.dto.builder;


import org.example.teamspeak3app.dto.TS3ClientDTO;
import org.example.teamspeak3app.model.TS3Client;

import java.time.LocalDateTime;

public class TS3ClientBuilder {
    public static TS3Client generateEntity(TS3ClientDTO ts3ClientDTO) {
        return TS3Client.builder()
                .id(ts3ClientDTO.getId())
                .username(ts3ClientDTO.getUsername())
                .password(ts3ClientDTO.getPassword())
                .coins(ts3ClientDTO.getCoins())
                .lastPaydayDate(ts3ClientDTO.getLastPaydayDate() != null ? LocalDateTime.parse(ts3ClientDTO.getLastPaydayDate()) : null)
                .lastPayday(ts3ClientDTO.getLastPayday())
                .build();

    }

    public static TS3ClientDTO generateDTO(TS3Client ts3Client) {
        return TS3ClientDTO.builder()
                .id(ts3Client.getId())
                .username(ts3Client.getUsername())
                .password(ts3Client.getPassword())
                .coins(ts3Client.getCoins())
                .lastPayday(ts3Client.getLastPayday())
                .lastPaydayDate(ts3Client.getLastPayday() != null ? String.valueOf(ts3Client.getLastPayday()) : null)
                .ts3LevelGroup(ts3Client.getTs3LevelGroup().getName())
                .build();

    }
}
