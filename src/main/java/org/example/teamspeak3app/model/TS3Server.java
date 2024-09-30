package org.example.teamspeak3app.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class TS3Server {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ipAddress;
    private Integer port;
    private Integer queryPort;
    private String botName;
    private String username;
    private String password;
}
