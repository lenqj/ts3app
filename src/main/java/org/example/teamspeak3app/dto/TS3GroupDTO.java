package org.example.teamspeak3app.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class TS3GroupDTO {
    private Integer id;
    private String name;
    private boolean levelType;
    private Double minimumCoins;
    private Double maximumBonus;
}

