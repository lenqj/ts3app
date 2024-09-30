package org.example.teamspeak3app.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TS3Group {
    @Id
    private int id;
    private String name;
    private boolean levelType;
    private Double minimumCoins;
    private Double maximumBonus;
}