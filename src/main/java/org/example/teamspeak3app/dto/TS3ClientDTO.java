package org.example.teamspeak3app.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class TS3ClientDTO {
    private String id;
    private String username;
    private String password;
    private Double coins;
    private Double lastPayday;
    private String lastPaydayDate;
    private String ts3LevelGroup;
}
