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
}
